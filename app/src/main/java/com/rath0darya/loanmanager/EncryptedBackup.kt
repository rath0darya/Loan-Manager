package com.rath0darya.loanmanager

import android.content.Context
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.room.withTransaction
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Local encrypted backup storage. The AES key is held in Android Keystore and is
 * not written alongside the backup file.
 */
object EncryptedBackup {
    private const val KEY_ALIAS = "lendora_backup_aes_v1"
    private val MAGIC = "LENDORA1".toByteArray(Charsets.US_ASCII)
    private const val IV_LENGTH = 12

    fun encrypt(plainText: String): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        return MAGIC + cipher.iv + encrypted
    }

    fun decrypt(bytes: ByteArray): String {
        if (!isEncrypted(bytes)) {
            // Preserve compatibility with older user-created JSON backups.
            return bytes.toString(Charsets.UTF_8)
        }
        if (bytes.size <= MAGIC.size + IV_LENGTH) throw IOException("Encrypted backup is incomplete")
        val iv = bytes.copyOfRange(MAGIC.size, MAGIC.size + IV_LENGTH)
        val encrypted = bytes.copyOfRange(MAGIC.size + IV_LENGTH, bytes.size)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        try {
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(128, iv))
            return cipher.doFinal(encrypted).toString(Charsets.UTF_8)
        } catch (e: Exception) {
            throw IOException(
                "This encrypted backup cannot be opened with this installation's key. If this is from a reinstall or another phone, choose the portable Lendora-Data.json backup from Android/media/com.rath0darya.loanmanager/Lendora/backup instead.",
                e
            )
        }
    }

    fun isEncrypted(bytes: ByteArray): Boolean =
        bytes.size >= MAGIC.size && bytes.copyOfRange(0, MAGIC.size).contentEquals(MAGIC)

    fun automaticBackupFile(context: Context): File {
        // Mirrors WhatsApp's Android/media/<package>/<app>/... folder pattern.
        val root = context.externalMediaDirs.firstOrNull()
            ?: context.getExternalFilesDir(null)
            ?: context.filesDir
        return File(root, "Lendora/backup/Lendora-Backup.lendora")
    }

    fun writeAutomatic(context: Context, plainText: String): File {
        val target = automaticBackupFile(context)
        target.parentFile?.mkdirs()
        val temporary = File(target.parentFile, target.name + ".tmp")
        temporary.writeBytes(encrypt(plainText))
        if (target.exists() && !target.delete()) throw IOException("Could not replace previous backup")
        if (!temporary.renameTo(target)) {
            temporary.copyTo(target, overwrite = true)
            temporary.delete()
        }
        return target
    }

    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (keyStore.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        generator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(true)
                .build()
        )
        return generator.generateKey()
    }
}

internal suspend fun createBackupJson(dao: LoanDao): String {
    val root = JSONObject()
    val loanArray = JSONArray()
    dao.allLoans().forEach { loan ->
        loanArray.put(JSONObject()
            .put("id", loan.id)
            .put("personName", loan.personName)
            .put("personName2", loan.personName2)
            .put("personName3", loan.personName3)
            .put("direction", loan.direction)
            .put("principal", loan.principal)
            .put("monthlyRate", loan.monthlyRate)
            .put("startDate", loan.startDate)
            .put("dueDate", loan.dueDate ?: JSONObject.NULL)
            .put("note", loan.note))
    }
    val paymentArray = JSONArray()
    dao.allPayments().forEach { payment ->
        paymentArray.put(JSONObject()
            .put("id", payment.id)
            .put("loanId", payment.loanId)
            .put("amount", payment.amount)
            .put("paidAt", payment.paidAt)
            .put("note", payment.note)
            .put("paymentMethod", payment.paymentMethod)
            .put("referenceNumber", payment.referenceNumber))
    }
    root.put("formatVersion", 2)
        .put("exportedAt", System.currentTimeMillis())
        .put("loans", loanArray)
        .put("payments", paymentArray)
    return root.toString(2)
}


/**
 * Portable automatic snapshot in shared Android/media storage.
 *
 * This copy is intentionally JSON so a fresh installation can recover it after the
 * Android Keystore key from a previous installation is gone. It contains personal
 * financial information and is therefore readable by anyone who can access the file.
 */
object DurableBackup {
    private const val BACKUP_NAME = "Lendora-Data.json"
    private const val RELATIVE_PATH = "Download/FundNiti/Backup/"

    // Android 10+ uses shared Downloads storage so the file is not removed with the app.
    // Older Android versions retain the previous app-specific fallback.
    fun automaticBackupFile(context: Context): File {
        val root = context.externalMediaDirs.firstOrNull()
            ?: throw IOException("Shared storage is unavailable on this device")
        return File(root, "Lendora/backup/$BACKUP_NAME")
    }

    private fun findSharedBackup(context: Context): Uri? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return null
        val collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Downloads._ID)
        val selection = "${MediaStore.Downloads.DISPLAY_NAME} = ? AND ${MediaStore.Downloads.RELATIVE_PATH} = ?"
        context.contentResolver.query(collection, projection, selection, arrayOf(BACKUP_NAME, RELATIVE_PATH), null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID))
                return Uri.withAppendedPath(collection, id.toString())
            }
        }
        return null
    }

    private fun readShared(context: Context): String? {
        val uri = findSharedBackup(context) ?: return null
        return context.contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }
    }

    private fun countRecords(json: String): Pair<Int, Int> {
        val root = JSONObject(json)
        return (root.optJSONArray("loans")?.length() ?: 0) to
            (root.optJSONArray("payments")?.length() ?: 0)
    }

    fun writeAutomatic(context: Context, plainText: String): Uri {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val existing = findSharedBackup(context)
            val uri = existing ?: resolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, BACKUP_NAME)
                    put(MediaStore.Downloads.MIME_TYPE, "application/json")
                    put(MediaStore.Downloads.RELATIVE_PATH, RELATIVE_PATH)
                }
            ) ?: throw IOException("Could not create Download/FundNiti/Backup folder")
            try {
                resolver.openOutputStream(uri, "wt")?.bufferedWriter(Charsets.UTF_8)?.use { writer ->
                    writer.write(plainText)
                    writer.flush()
                } ?: throw IOException("Could not write the shared backup file")
            } catch (e: Exception) {
                if (existing == null) runCatching { resolver.delete(uri, null, null) }
                throw e
            }
            return uri
        }

        val target = automaticBackupFile(context)
        val parent = target.parentFile ?: throw IOException("Backup folder is unavailable")
        if (!parent.exists() && !parent.mkdirs()) throw IOException("Could not create the backup folder")
        val temporary = File(parent, target.name + ".tmp")
        temporary.outputStream().bufferedWriter(Charsets.UTF_8).use { writer ->
            writer.write(plainText)
            writer.flush()
        }
        if (!temporary.renameTo(target)) {
            temporary.copyTo(target, overwrite = true)
            temporary.delete()
        }
        return Uri.fromFile(target)
    }

    fun readAutomatic(context: Context): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            readShared(context)?.let { return it }
            // Migrate a previous app-specific backup into shared Downloads on first run after update.
            val legacy = runCatching {
                val file = automaticBackupFile(context)
                if (file.isFile && file.length() > 0L) file.readText(Charsets.UTF_8) else null
            }.getOrNull()
            if (legacy != null) {
                runCatching { writeAutomatic(context, legacy) }
                return legacy
            }
            return null
        }
        val file = automaticBackupFile(context)
        return if (file.isFile && file.length() > 0L) file.readText(Charsets.UTF_8) else null
    }
}

internal suspend fun restoreBackupJson(db: LoanDatabase, dao: LoanDao, text: String) {
    val cleaned = text.trim().removePrefix("\uFEFF").trim()
    val root = try {
        JSONObject(cleaned)
    } catch (e: Exception) {
        throw IOException("This file is not a readable FundNiti backup. Choose the exported .lendora backup or the Lendora-Data.json device backup.", e)
    }
    // Accept both current portable JSON and older backup field names.
    val loanArray = root.optJSONArray("loans")
        ?: root.optJSONArray("loanRecords")
        ?: throw IOException("Backup is missing its loans list. Choose FundNiti-Backup.lendora or Lendora-Data.json.")
    val paymentArray = root.optJSONArray("payments") ?: root.optJSONArray("repayments") ?: JSONArray()
    if (loanArray.length() == 0 && paymentArray.length() == 0) {
        throw IOException("This backup has no loans or repayments. No data was changed.")
    }
    for (i in 0 until loanArray.length()) {
        val item = loanArray.optJSONObject(i) ?: throw IOException("Backup loan record " + (i + 1) + " is damaged. No data was changed.")
        if (!item.has("personName") || !item.has("principal") || !item.has("startDate")) {
            throw IOException("Backup loan record " + (i + 1) + " is missing required details. No data was changed.")
        }
    }
    for (i in 0 until paymentArray.length()) {
        val item = paymentArray.optJSONObject(i) ?: throw IOException("Backup payment record " + (i + 1) + " is damaged. No data was changed.")
        if (!item.has("loanId") || !item.has("amount") || !item.has("paidAt")) {
            throw IOException("Backup payment record " + (i + 1) + " is missing required details. No data was changed.")
        }
    }
    db.withTransaction {
        dao.clearPayments()
        dao.clearLoans()
        val ids = mutableMapOf<Long, Long>()
        for (i in 0 until loanArray.length()) {
            val o = loanArray.getJSONObject(i)
            val oldId = o.getLong("id")
            val due = o.opt("dueDate")
            val newId = dao.addLoan(LoanEntity(
                personName = o.getString("personName"),
                personName2 = o.optString("personName2", ""),
                personName3 = o.optString("personName3", ""),
                direction = o.getString("direction"),
                principal = o.getDouble("principal"),
                monthlyRate = o.getDouble("monthlyRate"),
                startDate = o.getLong("startDate"),
                dueDate = if (due == null || due == JSONObject.NULL) null else (due as Number).toLong(),
                note = o.optString("note", "")
            ))
            ids[oldId] = newId
        }
        for (i in 0 until paymentArray.length()) {
            val o = paymentArray.getJSONObject(i)
            val newLoanId = ids[o.getLong("loanId")] ?: continue
            dao.addPayment(PaymentEntity(
                loanId = newLoanId,
                amount = o.getDouble("amount"),
                paidAt = o.getLong("paidAt"),
                note = o.optString("note", ""),
                paymentMethod = o.optString("paymentMethod", "Cash"),
                referenceNumber = o.optString("referenceNumber", "")
            ))
        }
    }
}
