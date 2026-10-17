package com.rath0darya.loanmanager

import android.content.Context
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
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(128, iv))
        return cipher.doFinal(encrypted).toString(Charsets.UTF_8)
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
            .put("note", payment.note))
    }
    root.put("formatVersion", 2)
        .put("exportedAt", System.currentTimeMillis())
        .put("loans", loanArray)
        .put("payments", paymentArray)
    return root.toString(2)
}
