package com.rath0darya.loanmanager

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Switch
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.room.withTransaction
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import androidx.work.ExistingWorkPolicy
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Date
import java.util.Locale
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

private enum class AppPage(val label: String, val icon: ImageVector) {
    Home("Home", Icons.Filled.Home),
    Loans("Loans", Icons.Filled.AccountBalance),
    People("People", Icons.Filled.People),
    Statistics("Stats", Icons.Filled.BarChart),
    Backup("Backup", Icons.Filled.Settings)
}

class MainActivity : androidx.fragment.app.FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { LoanManagerApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoanManagerApp() {
    val context = LocalContext.current
    val preferences = remember { context.getSharedPreferences("fundniti_settings", android.content.Context.MODE_PRIVATE) }
    var themeMode by remember { mutableStateOf(preferences.getString("theme_mode", "SYSTEM") ?: "SYSTEM") }
    var pinConfigured by remember { mutableStateOf(preferences.contains("pin_hash")) }
    var appLocked by remember { mutableStateOf(preferences.contains("pin_hash")) }
    var fingerprintEnabled by remember { mutableStateOf(preferences.getBoolean("fingerprint_enabled", true)) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, pinConfigured) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP && pinConfigured) appLocked = true
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    val systemDark = isSystemInDarkTheme()
    val darkTheme = when (themeMode) {
        "LIGHT" -> false
        "DARK" -> true
        else -> systemDark
    }
    val dynamicColors = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val appColorScheme = when {
        dynamicColors && darkTheme -> androidx.compose.material3.dynamicDarkColorScheme(context)
        dynamicColors -> androidx.compose.material3.dynamicLightColorScheme(context)
        darkTheme -> androidx.compose.material3.darkColorScheme(
            primary = Color(0xFF83D6BB), secondary = Color(0xFFE7C77A),
            background = Color(0xFF101714), surface = Color(0xFF171F1B),
            surfaceVariant = Color(0xFF26352E), tertiary = Color(0xFF9BBEFF)
        )
        else -> androidx.compose.material3.lightColorScheme(
            primary = Color(0xFF126B55), onPrimary = Color.White,
            secondary = Color(0xFFB5D8C8), background = Color(0xFFF2F7F5),
            surface = Color(0xFFF9FCFB), surfaceVariant = Color(0xFFE5F0EB),
            tertiary = Color(0xFF4C86D9)
        )
    }
    val db = remember { LoanDatabase.get(context) }
    val dao = db.loanDao()
    val loans by dao.observeLoans().collectAsState(initial = emptyList())
    val payments by dao.observePayments().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var page by remember { mutableStateOf(AppPage.Home) }
    var showAddLoan by remember { mutableStateOf(false) }
    var editLoan by remember { mutableStateOf<LoanEntity?>(null) }
    var paymentFor by remember { mutableStateOf<LoanEntity?>(null) }
    var editingPayment by remember { mutableStateOf<PaymentEntity?>(null) }
    var deleteConfirmPayment by remember { mutableStateOf<PaymentEntity?>(null) }
    var detailLoan by remember { mutableStateOf<LoanEntity?>(null) }
    var deleteConfirmLoan by remember { mutableStateOf<LoanEntity?>(null) }
    var confirmLoanDeleteFinal by remember { mutableStateOf(false) }
    var confirmPaymentDeleteFinal by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }
    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }
    var backupStatus by remember { mutableStateOf("Device backup will be checked automatically.") }
    val notificationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri: Uri? ->
        if (uri != null) scope.launch {
            try {
                val json = createBackupJson(dao)
                val encrypted = EncryptedBackup.encrypt(json)
                context.contentResolver.openOutputStream(uri)?.use { it.write(encrypted) }
                    ?: error("Could not write the backup file")
                snackbarHostState.showSnackbar("Encrypted backup saved")

            } catch (e: Exception) {
                snackbarHostState.showSnackbar("Backup failed: ${e.localizedMessage ?: "unknown error"}")
            }
        }
    }
    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) scope.launch {
            try {
                val text = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                        ?.let { EncryptedBackup.decrypt(it) }
                        ?: error("Could not read the selected file")
                }
                restoreBackupJson(db, dao, text)
                withContext(Dispatchers.IO) { DurableBackup.writeAutomatic(context, createBackupJson(dao)) }
                backupStatus = "Restored ${dao.allLoans().size} loans and ${dao.allPayments().size} repayments. Device backup refreshed."
                snackbarHostState.showSnackbar("Backup restored and Android folder copy refreshed")
            } catch (e: Exception) {
                snackbarHostState.showSnackbar("Restore failed: ${e.localizedMessage ?: "invalid backup"}")
            }
        }
    }

    // Recover the shared Android/media copy on a fresh installation before replacing it.
    LaunchedEffect(Unit) {
        try {
            backupStatus = withContext(Dispatchers.IO) {
                val existingLoans = dao.allLoans()
                val existingPayments = dao.allPayments()
                val status = if (existingLoans.isEmpty() && existingPayments.isEmpty()) {
                    val saved = DurableBackup.readAutomatic(context)
                    if (saved != null) {
                        restoreBackupJson(db, dao, saved)
                        "Recovered ${dao.allLoans().size} loans and ${dao.allPayments().size} repayments from the Android folder."
                    } else {
                        "No existing Android folder backup was found yet."
                    }
                } else {
                    "Found ${existingLoans.size} local loans and ${existingPayments.size} repayments."
                }
                DurableBackup.writeAutomatic(context, createBackupJson(dao))
                status
            }
        } catch (e: Exception) {
            backupStatus = "Device backup needs attention: ${e.localizedMessage ?: "storage unavailable"}"
        }
    }

    val paymentTotals = payments.groupBy { it.loanId }.mapValues { (_, list) -> list.sumOf { it.amount } }
    fun balance(loan: LoanEntity): Double {
        val interest = accruedInterest(loan.principal, loan.monthlyRate, loan.startDate, System.currentTimeMillis())
        return (loan.principal + interest - (paymentTotals[loan.id] ?: 0.0)).coerceAtLeast(0.0)
    }
    val totalOutstanding = loans.sumOf { balance(it) }
    val totalPrincipal = loans.sumOf { it.principal }
    val totalPaid = payments.sumOf { it.amount }
    val monthlyInterest = loans.sumOf { it.principal * it.monthlyRate / 100.0 }

    SideEffect {
        val activity = context as? Activity
        activity?.window?.let { window ->
            window.statusBarColor = appColorScheme.background.toArgb()
            window.navigationBarColor = appColorScheme.background.toArgb()
            val bars = WindowInsetsControllerCompat(window, window.decorView)
            bars.isAppearanceLightStatusBars = !darkTheme
            bars.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(colorScheme = appColorScheme, typography = androidx.compose.material3.Typography(defaultFontFamily = FontFamily.SansSerif)) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    title = {
                        Column {
                            Text("FundNiti", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text(page.label, style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            themeMode = when (themeMode) {
                                "SYSTEM" -> "LIGHT"
                                "LIGHT" -> "DARK"
                                else -> "SYSTEM"
                            }
                            preferences.edit().putString("theme_mode", themeMode).apply()
                        }) {
                            val themeIcon = when (themeMode) {
                                "LIGHT" -> Icons.Filled.LightMode
                                "DARK" -> Icons.Filled.DarkMode
                                else -> Icons.Filled.BrightnessAuto
                            }
                            Icon(themeIcon, contentDescription = "Theme: ${themeMode.lowercase()}. Tap to change.")
                        }
                    }
                )
            },
            bottomBar = {
                Column {
                    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                        AppPage.entries.forEach { item ->
                            NavigationBarItem(
                                selected = page == item,
                                onClick = { page = item },
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label) }
                            )
                        }
                    }
                    Text("Developed with ♥️ Aravind", modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(vertical = 5.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            snackbarHost = { androidx.compose.material3.SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                if (page == AppPage.Loans) {
                    FloatingActionButton(onClick = { showAddLoan = true }) {
                        Icon(Icons.Filled.Add, contentDescription = "Add loan")
                    }
                }
            }
        ) { padding ->
            when (page) {
                AppPage.Home -> DashboardScreen(
                    Modifier.padding(padding), loans, totalOutstanding, totalPrincipal, totalPaid,
                    monthlyInterest, ::balance, { page = AppPage.Loans }, { detailLoan = it }, payments
                )
                AppPage.Loans -> LoansScreen(
                    Modifier.padding(padding), loans, ::balance, paymentTotals, search, { search = it },
                    onAddPayment = { paymentFor = it }, onViewDetails = { detailLoan = it },
                    onEdit = { editLoan = it }, onDelete = { deleteConfirmLoan = it; confirmLoanDeleteFinal = false }
                )
                AppPage.People -> PeopleScreen(Modifier.padding(padding), loans, ::balance)
                AppPage.Statistics -> StatisticsScreen(
                    Modifier.padding(padding), loans, payments, ::balance, totalOutstanding, totalPrincipal
                )
                AppPage.Backup -> BackupScreen(
                    Modifier.padding(padding),
                    pinConfigured = pinConfigured,
                    fingerprintEnabled = fingerprintEnabled,
                    onSetPin = { pin ->
                        val salt = java.util.UUID.randomUUID().toString()
                        preferences.edit().putString("pin_salt", salt).putString("pin_hash", hashAppPin(pin, salt)).apply()
                        pinConfigured = true
                        appLocked = false
                        snackbarHostState.currentSnackbarData?.dismiss()
                    },
                    onDisablePin = {
                        preferences.edit().remove("pin_hash").remove("pin_salt").putBoolean("fingerprint_enabled", false).apply()
                        pinConfigured = false
                        fingerprintEnabled = false
                        appLocked = false
                    },
                    onFingerprintChange = { enabled ->
                        fingerprintEnabled = enabled
                        preferences.edit().putBoolean("fingerprint_enabled", enabled).apply()
                    },
                    backupStatus = backupStatus,
                    onBackupNow = {
                        scope.launch {
                            try {
                                val result = withContext(Dispatchers.IO) {
                                    val json = createBackupJson(dao)
                                    val file = DurableBackup.writeAutomatic(context, json)
                                    val root = JSONObject(json)
                                    Triple(file.absolutePath, root.getJSONArray("loans").length(), root.getJSONArray("payments").length())
                                }
                                backupStatus = "Saved ${result.second} loans and ${result.third} repayments. File: ${result.first}"
                                snackbarHostState.showSnackbar("All data backed up to the Android folder")
                            } catch (e: Exception) {
                                backupStatus = "Backup failed: ${e.localizedMessage ?: "storage unavailable"}"
                                snackbarHostState.showSnackbar(backupStatus)
                            }
                        }
                    },
                    onRecoverDeviceBackup = {
                        scope.launch {
                            try {
                                backupStatus = withContext(Dispatchers.IO) {
                                    val existingLoans = dao.allLoans()
                                    val existingPayments = dao.allPayments()
                                    if (existingLoans.isNotEmpty() || existingPayments.isNotEmpty()) {
                                        "Recovery skipped: this app already has ${existingLoans.size} loans and ${existingPayments.size} repayments. Current records were not replaced."
                                    } else {
                                        val saved = DurableBackup.readAutomatic(context)
                                            ?: error("No Android folder backup was found")
                                        restoreBackupJson(db, dao, saved)
                                        DurableBackup.writeAutomatic(context, createBackupJson(dao))
                                        "Recovered ${dao.allLoans().size} loans and ${dao.allPayments().size} repayments from the Android folder."
                                    }
                                }
                                snackbarHostState.showSnackbar(backupStatus)
                            } catch (e: Exception) {
                                backupStatus = "Recovery failed: ${e.localizedMessage ?: "backup unavailable"}"
                                snackbarHostState.showSnackbar(backupStatus)
                            }
                        }
                    },
                    onExport = { exportLauncher.launch("FundNiti-Backup.lendora") },
                    onImport = { importLauncher.launch(arrayOf("application/octet-stream", "application/json", "text/*")) }
                )
            }
        }
    }

    if (showAddLoan || editLoan != null) {
        val editing = editLoan
        LoanDialog(
            existing = editing,
            onDismiss = { showAddLoan = false; editLoan = null },
            onSave = { person1, person2, person3, direction, principal, rate, startDate, dueDays, note ->
                scope.launch {
                    val due = if (dueDays > 0) System.currentTimeMillis() + dueDays * 86_400_000L else null
                    val savedLoan = LoanEntity(
                        id = editing?.id ?: 0L,
                        personName = person1.trim(),
                        personName2 = person2.trim(),
                        personName3 = person3.trim(),
                        direction = direction,
                        principal = principal,
                        monthlyRate = rate,
                        startDate = startDate,
                        dueDate = due,
                        note = note.trim()
                    )
                    val savedId = if (editing == null) dao.addLoan(savedLoan) else {
                        dao.updateLoan(savedLoan)
                        editing.id
                    }
                    DurableBackup.writeAutomatic(context, createBackupJson(dao))
                    if (due != null) {
                        val delay = (due - System.currentTimeMillis()).coerceAtLeast(0L)
                        val request = OneTimeWorkRequestBuilder<LoanReminderWorker>()
                            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                            .setInputData(workDataOf("loanId" to savedId, "personName" to listOf(person1.trim(), person2.trim(), person3.trim()).filter { it.isNotBlank() }.joinToString(" & ")))
                            .build()
                        WorkManager.getInstance(context).enqueueUniqueWork("loan_due_$savedId", ExistingWorkPolicy.REPLACE, request)
                    } else {
                        WorkManager.getInstance(context).cancelUniqueWork("loan_due_$savedId")
                    }
                    showAddLoan = false
                    editLoan = null
                    snackbarHostState.showSnackbar(if (editing == null) "Loan saved" else "Loan updated")
                }
            }
        )
    }
    paymentFor?.let { loan ->
        PaymentDialog(existing = null, onDismiss = { paymentFor = null }, onSave = { amount, paidAt, method, reference, note ->
            scope.launch {
                dao.addPayment(PaymentEntity(loanId = loan.id, amount = amount,
                    paidAt = paidAt, note = note.trim(), paymentMethod = method, referenceNumber = reference.trim()))
                withContext(Dispatchers.IO) { DurableBackup.writeAutomatic(context, createBackupJson(dao)) }
                paymentFor = null
                snackbarHostState.showSnackbar("Payment recorded")
            }
        })
    }
    editingPayment?.let { payment ->
        PaymentDialog(existing = payment, onDismiss = { editingPayment = null }, onSave = { amount, paidAt, method, reference, note ->
            scope.launch {
                dao.updatePayment(payment.copy(amount = amount, paidAt = paidAt, paymentMethod = method,
                    referenceNumber = reference.trim(), note = note.trim()))
                withContext(Dispatchers.IO) { DurableBackup.writeAutomatic(context, createBackupJson(dao)) }
                editingPayment = null
                snackbarHostState.showSnackbar("Payment updated")
            }
        })
    }
    detailLoan?.let { loan ->
        LoanDetailsDialog(
            loan, payments.filter { it.loanId == loan.id }.sortedBy { it.paidAt }, balance(loan),
            onDismiss = { detailLoan = null },
            onEditPayment = { editingPayment = it },
            onDeletePayment = { deleteConfirmPayment = it; confirmPaymentDeleteFinal = false }
        )
    }
    deleteConfirmPayment?.let { payment ->
        AlertDialog(
            onDismissRequest = { deleteConfirmPayment = null },
            title = { Text(if (confirmPaymentDeleteFinal) "Final check: delete payment?" else "Delete this payment?") },
            text = { Text(if (confirmPaymentDeleteFinal) "This is the last confirmation. Delete ${money(payment.amount)} permanently? The outstanding balance will be recalculated." else "Are you sure you want to delete the payment of ${money(payment.amount)} recorded on ${formatDate(payment.paidAt)}?") },
            confirmButton = { TextButton(onClick = {
                if (!confirmPaymentDeleteFinal) confirmPaymentDeleteFinal = true
                else scope.launch {
                    dao.deletePayment(payment.id)
                    withContext(Dispatchers.IO) { DurableBackup.writeAutomatic(context, createBackupJson(dao)) }
                    deleteConfirmPayment = null
                    confirmPaymentDeleteFinal = false
                    snackbarHostState.showSnackbar("Payment deleted and balance updated")
                }
            }) { Text(if (confirmPaymentDeleteFinal) "Delete permanently" else "Continue") } },
            dismissButton = { TextButton(onClick = { deleteConfirmPayment = null; confirmPaymentDeleteFinal = false }) { Text("Cancel") } }
        )
    }
    deleteConfirmLoan?.let { loan ->
        AlertDialog(
            onDismissRequest = { deleteConfirmLoan = null },
            title = { Text(if (confirmLoanDeleteFinal) "Final check: delete loan?" else "Delete this loan?") },
            text = { Text(if (confirmLoanDeleteFinal) "Final confirmation: permanently delete this loan and all its payment records? This cannot be undone." else "Are you sure you want to delete the loan for " + listOf(loan.personName, loan.personName2, loan.personName3).filter { it.isNotBlank() }.joinToString(" → ") + "? You will be asked one more time before it is deleted.") },
            confirmButton = { TextButton(onClick = {
                if (!confirmLoanDeleteFinal) confirmLoanDeleteFinal = true
                else scope.launch {
                    dao.deletePaymentsForLoan(loan.id); dao.deleteLoan(loan.id)
                    withContext(Dispatchers.IO) { DurableBackup.writeAutomatic(context, createBackupJson(dao)) }
                    deleteConfirmLoan = null
                    confirmLoanDeleteFinal = false
                    snackbarHostState.showSnackbar("Loan and its payment records deleted")
                }
            }) { Text(if (confirmLoanDeleteFinal) "Delete permanently" else "Continue") } },
            dismissButton = { TextButton(onClick = { deleteConfirmLoan = null; confirmLoanDeleteFinal = false }) { Text("Cancel") } }
        )
    }
    if (pinConfigured && appLocked) {
        PinUnlockDialog(
            fingerprintEnabled = fingerprintEnabled,
            onUnlock = { pin ->
                val salt = preferences.getString("pin_salt", "") ?: ""
                val savedHash = preferences.getString("pin_hash", "") ?: ""
                if (hashAppPin(pin, salt) == savedHash) { appLocked = false; true } else false
            },
            onFingerprint = {
                val activity = context as? androidx.fragment.app.FragmentActivity
                if (activity != null) requestFingerprintUnlock(activity, { appLocked = false }, { message ->
                    scope.launch { snackbarHostState.showSnackbar(message) }
                })
            }
        )
    }
}

@Composable
private fun LoanDetailsDialog(
    loan: LoanEntity,
    payments: List<PaymentEntity>,
    outstanding: Double,
    onDismiss: () -> Unit,
    onEditPayment: (PaymentEntity) -> Unit,
    onDeletePayment: (PaymentEntity) -> Unit
) {
    val interest = accruedInterest(loan.principal, loan.monthlyRate, loan.startDate, System.currentTimeMillis())
    val totalPaid = payments.sumOf { it.amount }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Full loan report") },
        text = {
            Column(Modifier.fillMaxWidth().height(520.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text("People", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(listOf(loan.personName, loan.personName2, loan.personName3).filter { it.isNotBlank() }.joinToString(" → "))
                Text(when (loan.direction) { "LENT" -> "You gave money to someone."; "BORROWED_FOR_OTHER" -> "You borrowed money from one person and gave it to someone else."; else -> "You borrowed money for yourself." })
                androidx.compose.material3.HorizontalDivider()
                Text("Loan details", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                DetailLine("Loan date", formatDate(loan.startDate))
                DetailLine("Original amount", money(loan.principal))
                DetailLine("Interest rate", "${loan.monthlyRate}% per month")
                DetailLine("Interest added so far", money(interest))
                DetailLine("Payments made", money(totalPaid))
                DetailLine("Amount still due", money(outstanding))
                DetailLine("Status", if (outstanding <= 0.009) "Fully paid" else "Still to be paid")
                loan.dueDate?.let { DetailLine("Reminder date", formatDate(it)) }
                if (loan.note.isNotBlank()) DetailLine("Notes", loan.note)
                androidx.compose.material3.HorizontalDivider()
                Text("Payments made (${payments.size})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                if (payments.isEmpty()) Text("No payments recorded yet.")
                payments.forEachIndexed { index, payment ->
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Column(Modifier.fillMaxWidth().padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Payment ${index + 1}: ${money(payment.amount)}", fontWeight = FontWeight.SemiBold)
                            DetailLine("Date", formatDate(payment.paidAt))
                            DetailLine("Time", DateFormat.getTimeInstance(DateFormat.MEDIUM).format(Date(payment.paidAt)))
                            DetailLine("Paid by", payment.paymentMethod)
                            if (payment.referenceNumber.isNotBlank()) DetailLine(if (payment.paymentMethod == "UPI") "UPI UTR number" else "Transaction number", payment.referenceNumber)
                            if (payment.note.isNotBlank()) DetailLine("Notes", payment.note)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TextButton(onClick = { onEditPayment(payment) }) {
                                    Icon(Icons.Filled.Edit, null, Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text("Edit payment")
                                }
                                TextButton(onClick = { onDeletePayment(payment) }) {
                                    Icon(Icons.Filled.Delete, null, Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text("Delete payment")
                                }
                            }
                        }
                    }
                }
                Text("You can change or delete any payment. The amount still due updates automatically.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close report") } }
    )
}

@Composable
private fun DetailLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Spacer(Modifier.width(8.dp))
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1.2f))
    }
}
@Composable
private fun DashboardScreen(
    modifier: Modifier, loans: List<LoanEntity>, outstanding: Double, principal: Double,
    paid: Double, monthlyInterest: Double, balance: (LoanEntity) -> Double, onViewLoans: () -> Unit,
    onViewDetails: (LoanEntity) -> Unit, payments: List<PaymentEntity>
) {
    LazyColumn(modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Spacer(Modifier.height(4.dp)); Text("Your money, clearly tracked.", style = MaterialTheme.typography.bodyLarge) }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp)) {
                    Text("TOTAL OUTSTANDING", color = androidx.compose.ui.graphics.Color.White,
                        style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.height(6.dp))
                    Text(money(outstanding), color = androidx.compose.ui.graphics.Color.White,
                        style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                    Text(amountInWords(outstanding), color = Color(0xFFDDF5E9),
                        style = MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.height(8.dp))
                    Text("${loans.size} active loan records", color = androidx.compose.ui.graphics.Color.White)
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("Principal", money(principal), Modifier.weight(1f))
                StatCard("Payments", money(paid), Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("Monthly interest", money(monthlyInterest), Modifier.weight(1f))
                StatCard("People", loans.flatMap { listOf(it.personName, it.personName2, it.personName3) }.filter { it.isNotBlank() }.map { it.trim().lowercase() }.distinct().size.toString(), Modifier.weight(1f))
            }
        }
        item { Text("Recent loans", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
        if (loans.isEmpty()) item { EmptyState("No loans yet", "Tap Loans, then + to add your first record.") }
        items(loans.take(5), key = { it.id }) { loan -> LoanCard(loan, balance(loan), payments.filter { it.loanId == loan.id }.sumOf { it.amount }, {}, null, null, { onViewDetails(loan) }) }
        item { Button(onClick = onViewLoans, modifier = Modifier.fillMaxWidth()) { Text("View all loans") } }
        item { Text("Interest is worked out each month on the original amount.",
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        item { Text("Developed with ♥️ Aravind", modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}

@Composable
private fun LoansScreen(
    modifier: Modifier, loans: List<LoanEntity>, balance: (LoanEntity) -> Double,
    paymentTotals: Map<Long, Double>, search: String, onSearch: (String) -> Unit, onAddPayment: (LoanEntity) -> Unit,
    onViewDetails: (LoanEntity) -> Unit, onEdit: (LoanEntity) -> Unit, onDelete: (LoanEntity) -> Unit
) {
    val filtered = loans.filter {
        (it.personName.contains(search, ignoreCase = true) || it.personName2.contains(search, ignoreCase = true)) ||
            it.personName3.contains(search, ignoreCase = true) || it.direction.contains(search, ignoreCase = true) || it.note.contains(search, ignoreCase = true)
    }
    Column(modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        OutlinedTextField(value = search, onValueChange = onSearch, modifier = Modifier.fillMaxWidth(),
            label = { Text("Search loans or people") }, leadingIcon = { Icon(Icons.Filled.Search, null) },
            singleLine = true)
        Spacer(Modifier.height(10.dp))
        Text("${filtered.size} records", style = MaterialTheme.typography.labelLarge)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
            if (filtered.isEmpty()) item { EmptyState("Nothing found", "Add a loan or try another search.") }
            items(filtered, key = { it.id }) { loan ->
                LoanCard(loan, balance(loan), paymentTotals[loan.id] ?: 0.0, { onAddPayment(loan) }, { onEdit(loan) }, { onDelete(loan) }, { onViewDetails(loan) })
            }
            item { Spacer(Modifier.height(72.dp)) }
        }
    }
}

@Composable
private fun LoanCard(
    loan: LoanEntity, balance: Double, paid: Double, onAddPayment: () -> Unit, onEdit: (() -> Unit)?, onDelete: (() -> Unit)?, onViewDetails: (() -> Unit)? = null
) {
    Card(onClick = { onViewDetails?.invoke() }, enabled = onViewDetails != null,
        modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(listOf(loan.personName, loan.personName2, loan.personName3).filter { it.isNotBlank() }.joinToString(" → "), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(when (loan.direction) { "LENT" -> "You lent money"; "BORROWED_FOR_OTHER" -> "Borrowed for someone else"; else -> "You borrowed money" },
                        style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                }
                Column(horizontalAlignment = Alignment.End, modifier = Modifier.width(150.dp)) {
                    Text(money(balance), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(amountInWords(balance), style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                }
            }
            val elapsedDays = elapsedLoanDays(loan.startDate, System.currentTimeMillis())
            val interestAmount = accruedInterest(loan.principal, loan.monthlyRate, loan.startDate, System.currentTimeMillis())
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Principal: ${money(loan.principal)}", style = MaterialTheme.typography.bodySmall)
                    Text("Rate: ${loan.monthlyRate}% / month", style = MaterialTheme.typography.bodySmall)
                }
                Text("Interest added (${elapsedDays} days since loan started): ${money(interestAmount)}",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
            if (paid > 0.0) Text("Paid so far: ${money(paid)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            if (loan.note.isNotBlank()) Text(loan.note, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Started ${formatDate(loan.startDate)}", style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (onDelete != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = onAddPayment) {
                        Icon(Icons.Filled.Payments, null, Modifier.size(18.dp)); Spacer(Modifier.width(6.dp)); Text("Record payment")
                    }
                    if (onEdit != null) IconButton(onClick = onEdit) { Icon(Icons.Filled.Edit, contentDescription = "Edit loan") }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete loan")
                    }
                }
            }
        }
    }
}

@Composable
private fun PeopleScreen(modifier: Modifier, loans: List<LoanEntity>, balance: (LoanEntity) -> Double) {
    // Normalize names so "Ravi", "ravi", and names with extra spaces merge into one person.
    val people = loans.flatMap { loan ->
        listOf(loan.personName, loan.personName2, loan.personName3)
            .filter { it.isNotBlank() }
            .map { it.trim() to loan }
    }.groupBy({ it.first.lowercase(Locale.ROOT) }, { it })
        .toSortedMap()
    LazyColumn(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("See each person’s total amount, payments and amount still due.", style = MaterialTheme.typography.bodyMedium) }
        if (people.isEmpty()) item { EmptyState("No people yet", "Your borrower and lender list will appear here.") }
        items(people.keys.toList(), key = { it }) { personKey ->
            val entries = people[personKey].orEmpty()
            val name = entries.firstOrNull()?.first ?: personKey
            val records = entries.map { it.second }.distinctBy { it.id }
            val lentTotal = records.filter { it.direction == "LENT" }.sumOf(balance)
            val borrowedTotal = records.filter { it.direction == "BORROWED" || it.direction == "BORROWED_FOR_OTHER" }.sumOf(balance)
            val combinedTotal = lentTotal + borrowedTotal
            Card(Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("Combined outstanding across ${records.size} loan(s): ${money(combinedTotal)}",
                        style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    Text(amountInWords(combinedTotal), style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("You lent · ${money(lentTotal)}",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    Text("You borrowed / borrowed for someone · ${money(borrowedTotal)}",
                        style = MaterialTheme.typography.bodySmall, color = Color(0xFF4C86D9))
                    Text("Total = outstanding balances of this person's unique loan records, split by each record's Lent/Borrowed type.",
                        style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun StatisticsScreen(
    modifier: Modifier, loans: List<LoanEntity>, payments: List<PaymentEntity>,
    balance: (LoanEntity) -> Double, outstanding: Double, principal: Double
) {
    val lent = loans.filter { it.direction == "LENT" }
    val borrowed = loans.filter { it.direction == "BORROWED" || it.direction == "BORROWED_FOR_OTHER" }
    val lentBalance = lent.sumOf(balance)
    val borrowedBalance = borrowed.sumOf(balance)
    val paymentSum = payments.sumOf { it.amount }
    val monthlyInterest = loans.sumOf { it.principal * it.monthlyRate / 100.0 }
    val peopleCount = loans.flatMap { listOf(it.personName, it.personName2, it.personName3) }
        .filter { it.isNotBlank() }.map { it.trim().lowercase(Locale.ROOT) }.distinct().size
    val maxPerson = loans.flatMap { loan -> listOf(loan.personName, loan.personName2, loan.personName3)
        .filter { it.isNotBlank() }.map { it.trim() to loan } }
        .groupBy({ it.first.lowercase(Locale.ROOT) }, { it.second })
        .maxByOrNull { entry -> entry.value.distinctBy { it.id }.sumOf(balance) }
    val recentMonths = (5 downTo 0).map { java.time.YearMonth.now().minusMonths(it.toLong()) }
    fun endOfMonth(month: java.time.YearMonth) =
        month.atEndOfMonth().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val paymentBars = recentMonths.map { month ->
        month.month.name.take(3) to payments.filter {
            Instant.ofEpochMilli(it.paidAt).atZone(ZoneId.systemDefault())
                .let { d -> d.year == month.year && d.monthValue == month.monthValue }
        }.sumOf { it.amount }
    }
    val balanceTrend = recentMonths.map { month ->
        val cutoff = endOfMonth(month)
        val value = loans.filter { it.startDate <= cutoff }.sumOf { loan ->
            val interest = accruedInterest(loan.principal, loan.monthlyRate, loan.startDate, cutoff)
            val paid = payments.filter { it.loanId == loan.id && it.paidAt <= cutoff }.sumOf { it.amount }
            (loan.principal + interest - paid).coerceAtLeast(0.0)
        }
        month.month.name.take(3) to value
    }
    val currentCount = loans.count { balance(it) > 0 && it.dueDate != null && it.dueDate >= System.currentTimeMillis() }
    val overdueCount = loans.count { balance(it) > 0 && it.dueDate != null && it.dueDate < System.currentTimeMillis() }
    val noDueDateCount = loans.count { balance(it) > 0 && it.dueDate == null }
    val allocatedByMonth = mutableMapOf<String, Pair<Double, Double>>()
    loans.forEach { loan ->
        var interestAlreadyPaid = 0.0
        payments.filter { it.loanId == loan.id }.sortedBy { it.paidAt }.forEach { payment ->
            val interestAccrued = accruedInterest(loan.principal, loan.monthlyRate, loan.startDate, payment.paidAt)
            val interestDue = (interestAccrued - interestAlreadyPaid).coerceAtLeast(0.0)
            val interestPart = kotlin.math.min(payment.amount, interestDue)
            val principalPart = (payment.amount - interestPart).coerceAtLeast(0.0)
            interestAlreadyPaid += interestPart
            val monthKey = Instant.ofEpochMilli(payment.paidAt).atZone(ZoneId.systemDefault()).let { java.time.YearMonth.of(it.year, it.monthValue).toString() }
            val prior = allocatedByMonth[monthKey] ?: (0.0 to 0.0)
            allocatedByMonth[monthKey] = (prior.first + principalPart) to (prior.second + interestPart)
        }
    }
    val stackedPayments = recentMonths.map { month ->
        val split = allocatedByMonth[month.toString()] ?: (0.0 to 0.0)
        Triple(month.month.name.take(3), split.first, split.second)
    }
    val grouped = recentMonths.map { month ->
        val inMonth = loans.filter { loan ->
            Instant.ofEpochMilli(loan.startDate).atZone(ZoneId.systemDefault())
                .let { it.year == month.year && it.monthValue == month.monthValue }
        }
        Triple(month.month.name.take(3), inMonth.filter { it.direction == "LENT" }.sumOf { it.principal },
            inMonth.filter { it.direction == "BORROWED" }.sumOf { it.principal })
    }
    val originationsVsPayments = recentMonths.map { month ->
        val originations = loans.filter { loan ->
            Instant.ofEpochMilli(loan.startDate).atZone(ZoneId.systemDefault())
                .let { it.year == month.year && it.monthValue == month.monthValue }
        }.sumOf { it.principal }
        val paid = payments.filter {
            Instant.ofEpochMilli(it.paidAt).atZone(ZoneId.systemDefault())
                .let { it.year == month.year && it.monthValue == month.monthValue }
        }.sumOf { it.amount }
        Triple(month.month.name.take(3), originations, paid)
    }
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Money reports", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Charts and totals from your saved loan and payment records.",
            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        StatCard("Total outstanding", money(outstanding), Modifier.fillMaxWidth())
        val totalInterestNow = loans.sumOf { accruedInterest(it.principal, it.monthlyRate, it.startDate, System.currentTimeMillis()) }
        GlassPanel(title = "Total principal and interest", subtitle = "Original amounts compared with interest added so far") {
            DetailLine("Total principal", money(principal))
            ComparisonBar("Principal", principal, maxOf(principal, totalInterestNow, 1.0), Color(0xFF16866B))
            DetailLine("Interest added", money(totalInterestNow))
            ComparisonBar("Interest", totalInterestNow, maxOf(principal, totalInterestNow, 1.0), Color(0xFF5B8DE5))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard("Money lent", money(lentBalance), Modifier.weight(1f))
            StatCard("Money borrowed", money(borrowedBalance), Modifier.weight(1f))
        }
        GlassPanel(title = "Portfolio split · donut chart", subtitle = "Outstanding balance by direction") {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                PortfolioDonut(lentBalance, borrowedBalance, Modifier.size(132.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                    LegendRow("Money lent", Color(0xFF16866B), money(lentBalance))
                    LegendRow("Money borrowed", Color(0xFF5B8DE5), money(borrowedBalance))
                }
            }
        }
        GlassPanel(title = "Payments split", subtitle = "Estimated amount going to principal and interest in the last 6 months") {
            StackedPaymentChart(stackedPayments)
            Text("This split is an estimate. The app records the full payment amount, not a lender-provided split.",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        GlassPanel(title = "Amount still due over time", subtitle = "Estimated balance at the end of each of the last 6 months") {
            TrendLineChart(balanceTrend)
            Text("Uses loan dates, interest added, and payments recorded by each month end.",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        GlassPanel(title = "Loan reminders", subtitle = "Loans that are due soon, overdue, or have no reminder date") {
            HealthDonut(currentCount, overdueCount, noDueDateCount, Modifier.size(136.dp))
            LegendRow("Current / not yet due", Color(0xFF16866B), "$currentCount loans")
            LegendRow("Overdue", Color(0xFFE59B42), "$overdueCount loans")
            LegendRow("No due date set", Color(0xFF5B8DE5), "$noDueDateCount loans")
            Text("A late payment is not automatically marked as a default.",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        GlassPanel(title = "Money lent and borrowed each month", subtitle = "Original amount recorded each month") {
            GroupedDirectionChart(grouped)
            LegendRow("You lent", Color(0xFF16866B), "Lent principal")
            LegendRow("You borrowed", Color(0xFF5B8DE5), "Borrowed principal")
        }
        GlassPanel(title = "New loans and payments", subtitle = "Compare new loan amounts with payments received or made") {
            CashFlowBridgeChart(originationsVsPayments)
            Text("Payments include both principal and interest.",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        GlassPanel(title = "Money lent vs money borrowed", subtitle = "Compare amounts still due") {
            ComparisonBar("Lent", lentBalance, maxOf(lentBalance, borrowedBalance), Color(0xFF16866B))
            ComparisonBar("Borrowed", borrowedBalance, maxOf(lentBalance, borrowedBalance), Color(0xFF5B8DE5))
        }
        GlassPanel(title = "Repayment activity", subtitle = "Payments recorded in the last 6 months") {
            PaymentBars(paymentBars)
        }
        Text("Principal and interest by person", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        val personBreakdown = loans.flatMap { loan ->
            listOf(loan.personName, loan.personName2, loan.personName3).filter { it.isNotBlank() }.map { it.trim() to loan }
        }.groupBy({ it.first.lowercase(Locale.ROOT) }, { it })
        personBreakdown.toSortedMap().forEach { (_, entries) ->
            val personName = entries.first().first
            val personLoans = entries.map { it.second }.distinctBy { it.id }
            val personPrincipal = personLoans.sumOf { it.principal }
            val personInterest = personLoans.sumOf { accruedInterest(it.principal, it.monthlyRate, it.startDate, System.currentTimeMillis()) }
            GlassPanel(title = personName, subtitle = "${personLoans.size} loan record(s)") {
                DetailLine("Original amount", money(personPrincipal))
                DetailLine("Interest added so far", money(personInterest))
                DetailLine("Amount still due", money(personLoans.sumOf(balance)))
                val max = maxOf(personPrincipal, personInterest, 1.0)
                ComparisonBar("Principal", personPrincipal, max, Color(0xFF16866B))
                ComparisonBar("Interest", personInterest, max, Color(0xFF5B8DE5))
            }
        }
        Text("Key metrics", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard("Original principal", money(principal), Modifier.weight(1f))
            StatCard("Payments total", money(paymentSum), Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard("Monthly interest", money(monthlyInterest), Modifier.weight(1f))
            StatCard("Loan records", loans.size.toString(), Modifier.weight(1f))
        }
        StatCard("People tracked", peopleCount.toString(), Modifier.fillMaxWidth())
        StatCard("Highest outstanding person", maxPerson?.key ?: "—", Modifier.fillMaxWidth())
        Text("Balances use simple monthly interest on original principal for each completed month, less recorded payments. Confirm this matches each loan agreement before relying on totals.",
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun GlassPanel(title: String, subtitle: String, content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            content()
        }
    }
}

@Composable
private fun PortfolioDonut(lent: Double, borrowed: Double, modifier: Modifier = Modifier) {
    val total = lent + borrowed
    Canvas(modifier) {
        val stroke = 24.dp.toPx()
        val diameter = size.minDimension - stroke
        val topLeft = androidx.compose.ui.geometry.Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
        val arcSize = androidx.compose.ui.geometry.Size(diameter, diameter)
        drawArc(Color(0xFFE4ECE8), -90f, 360f, false, topLeft, arcSize, style = Stroke(stroke, cap = StrokeCap.Round))
        if (total > 0.0) {
            val lentSweep = (lent / total * 360.0).toFloat()
            drawArc(Color(0xFF16866B), -90f, lentSweep, false, topLeft, arcSize, style = Stroke(stroke, cap = StrokeCap.Round))
            drawArc(Color(0xFF5B8DE5), -90f + lentSweep, (borrowed / total * 360.0).toFloat(), false,
                topLeft, arcSize, style = Stroke(stroke, cap = StrokeCap.Round))
        }
    }
}

@Composable
private fun LegendRow(label: String, color: Color, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Surface(Modifier.size(10.dp), shape = RoundedCornerShape(50), color = color) {}
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ComparisonBar(label: String, value: Double, maxValue: Double, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(money(value), style = MaterialTheme.typography.labelLarge)
        }
        androidx.compose.foundation.layout.Box(
            Modifier.fillMaxWidth().height(12.dp).background(Color(0xFFE6EEEA), RoundedCornerShape(50))
        ) {
            val fraction = if (maxValue <= 0.0) 0f else (value / maxValue).toFloat().coerceIn(0f, 1f)
            androidx.compose.foundation.layout.Box(
                Modifier.fillMaxWidth(fraction).height(12.dp).background(color, RoundedCornerShape(50))
            )
        }
    }
}

@Composable
private fun PaymentBars(data: List<Pair<String, Double>>) {
    val maxValue = data.maxOfOrNull { it.second } ?: 0.0
    Row(Modifier.fillMaxWidth().height(150.dp), horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom) {
        data.forEach { (month, amount) ->
            Column(Modifier.weight(1f).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom) {
                Text(if (amount > 0) shortMoney(amount) else "—", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                Spacer(Modifier.height(5.dp))
                val fraction = if (maxValue <= 0.0) 0.025f else (amount / maxValue).toFloat().coerceIn(0.025f, 1f)
                androidx.compose.foundation.layout.Box(
                    Modifier.fillMaxWidth(0.72f).fillMaxHeight(fraction).background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color(0xFF77CDB2), Color(0xFF126B55))),
                        RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                    )
                )
                Spacer(Modifier.height(7.dp))
                Text(month, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun DataNeededPanel(title: String, description: String) {
    Card(modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.BarChart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            }
            Text("Needs more data", style = MaterialTheme.typography.labelMedium, color = Color(0xFF9A6B20))
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun StackedPaymentChart(data: List<Triple<String, Double, Double>>) {
    val maxValue = data.maxOfOrNull { it.second + it.third } ?: 0.0
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth().height(150.dp), horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalAlignment = Alignment.Bottom) {
            data.forEach { (label, principalPart, interestPart) ->
                Column(Modifier.weight(1f).fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom) {
                    Text(shortMoney(principalPart + interestPart), style = MaterialTheme.typography.labelSmall, maxLines = 1)
                    Spacer(Modifier.height(4.dp))
                    val totalHeight = if (maxValue <= 0.0) 2f else 100f * ((principalPart + interestPart) / maxValue).toFloat()
                    androidx.compose.foundation.layout.Box(Modifier.fillMaxWidth(0.65f).height(totalHeight.dp)) {
                        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
                            if (maxValue > 0.0) {
                                androidx.compose.foundation.layout.Box(Modifier.fillMaxWidth()
                                    .height((100f * (interestPart / maxValue).toFloat()).dp)
                                    .background(Color(0xFF5B8DE5)))
                                androidx.compose.foundation.layout.Box(Modifier.fillMaxWidth()
                                    .height((100f * (principalPart / maxValue).toFloat()).dp)
                                    .background(Color(0xFF16866B)))
                            }
                        }
                    }
                    Spacer(Modifier.height(5.dp))
                    Text(label, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LegendRow("Principal estimate", Color(0xFF16866B), "Allocated")
            LegendRow("Interest estimate", Color(0xFF5B8DE5), "Allocated")
        }
    }
}

@Composable
private fun TrendLineChart(data: List<Pair<String, Double>>) {
    val maxValue = data.maxOfOrNull { it.second } ?: 0.0
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Canvas(Modifier.fillMaxWidth().height(150.dp)) {
            if (data.isNotEmpty()) {
                val pad = 12.dp.toPx()
                val w = (size.width - 2 * pad).coerceAtLeast(1f)
                val h = (size.height - 2 * pad).coerceAtLeast(1f)
                val pts = data.mapIndexed { i, item ->
                    val x = if (data.size == 1) size.width / 2f else pad + w * i / (data.size - 1)
                    val y = size.height - pad - if (maxValue <= 0.0) h else h * (item.second / maxValue).toFloat()
                    androidx.compose.ui.geometry.Offset(x, y)
                }
                for (step in 0..3) {
                    val y = pad + h * step / 3f
                    drawLine(Color(0xFFE3ECE7), androidx.compose.ui.geometry.Offset(pad, y),
                        androidx.compose.ui.geometry.Offset(size.width - pad, y), strokeWidth = 1.dp.toPx())
                }
                pts.zipWithNext().forEach { (a, b) -> drawLine(Color(0xFF16866B), a, b, 3.dp.toPx(), cap = StrokeCap.Round) }
                pts.forEach { drawCircle(Color(0xFF16866B), 4.dp.toPx(), it) }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            data.forEach { (label, _) -> Text(label, style = MaterialTheme.typography.labelSmall) }
        }
        Text("Latest: " + (data.lastOrNull()?.second?.let { money(it) } ?: money(0.0)),
            style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun HealthDonut(current: Int, overdue: Int, noDueDate: Int, modifier: Modifier = Modifier) {
    val total = current + overdue + noDueDate
    Canvas(modifier) {
        val stroke = 24.dp.toPx()
        val diameter = size.minDimension - stroke
        val topLeft = androidx.compose.ui.geometry.Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
        val arcSize = androidx.compose.ui.geometry.Size(diameter, diameter)
        drawArc(Color(0xFFE4ECE8), -90f, 360f, false, topLeft, arcSize, style = Stroke(stroke, cap = StrokeCap.Butt))
        if (total > 0) {
            val a = current.toFloat() / total * 360f
            val b = overdue.toFloat() / total * 360f
            val c = noDueDate.toFloat() / total * 360f
            drawArc(Color(0xFF16866B), -90f, a, false, topLeft, arcSize, style = Stroke(stroke, cap = StrokeCap.Butt))
            drawArc(Color(0xFFE59B42), -90f + a, b, false, topLeft, arcSize, style = Stroke(stroke, cap = StrokeCap.Butt))
            drawArc(Color(0xFF5B8DE5), -90f + a + b, c, false, topLeft, arcSize, style = Stroke(stroke, cap = StrokeCap.Butt))
        }
    }
}

@Composable
private fun GroupedDirectionChart(data: List<Triple<String, Double, Double>>) {
    val maxValue = data.maxOfOrNull { maxOf(it.second, it.third) } ?: 0.0
    Row(Modifier.fillMaxWidth().height(150.dp), horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.Bottom) {
        data.forEach { (label, lent, borrowed) ->
            Column(Modifier.weight(1f).fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.Bottom) {
                    val h1 = if (maxValue <= 0.0) 2.dp else (86f * (lent / maxValue).toFloat()).dp
                    val h2 = if (maxValue <= 0.0) 2.dp else (86f * (borrowed / maxValue).toFloat()).dp
                    androidx.compose.foundation.layout.Box(Modifier.width(12.dp).height(h1).background(Color(0xFF16866B), RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)))
                    Spacer(Modifier.width(3.dp))
                    androidx.compose.foundation.layout.Box(Modifier.width(12.dp).height(h2).background(Color(0xFF5B8DE5), RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)))
                }
                Spacer(Modifier.height(6.dp))
                Text(label, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun CashFlowBridgeChart(data: List<Triple<String, Double, Double>>) {
    val maxValue = data.maxOfOrNull { maxOf(it.second, it.third) } ?: 0.0
    Row(Modifier.fillMaxWidth().height(164.dp), horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.Bottom) {
        data.forEach { (label, originations, repayments) ->
            Column(Modifier.weight(1f).fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom) {
                Text(shortMoney(originations), style = MaterialTheme.typography.labelSmall, maxLines = 1)
                val h1 = if (maxValue <= 0.0) 2.dp else (78f * (originations / maxValue).toFloat()).dp
                androidx.compose.foundation.layout.Box(Modifier.fillMaxWidth(0.62f).height(h1)
                    .background(Color(0xFF16866B), RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)))
                Spacer(Modifier.height(4.dp))
                val h2 = if (maxValue <= 0.0) 2.dp else (78f * (repayments / maxValue).toFloat()).dp
                androidx.compose.foundation.layout.Box(Modifier.fillMaxWidth(0.62f).height(h2)
                    .background(Color(0xFFE59B42), RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)))
                Spacer(Modifier.height(4.dp))
                Text(label, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        LegendRow("Originations", Color(0xFF16866B), "New principal")
        LegendRow("Repayments", Color(0xFFE59B42), "Recorded payments")
    }
}

private fun shortMoney(value: Double): String = when {
    value >= 10000000 -> "₹%.1fCr".format(Locale.getDefault(), value / 10000000)
    value >= 100000 -> "₹%.1fL".format(Locale.getDefault(), value / 100000)
    value >= 1000 -> "₹%.1fK".format(Locale.getDefault(), value / 1000)
    else -> "₹%.0f".format(Locale.getDefault(), value)
}

@Composable
private fun AppLockSettings(
    pinConfigured: Boolean,
    fingerprintEnabled: Boolean,
    onSetPin: (String) -> Unit,
    onDisablePin: () -> Unit,
    onFingerprintChange: (Boolean) -> Unit
) {
    var showPinSetup by remember { mutableStateOf(false) }
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    Card(
        Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("App lock", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(if (pinConfigured) "Your records are protected by a 6-digit PIN." else "Add a 6-digit PIN so other people cannot open your loan records.")
            if (pinConfigured) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Fingerprint unlock", fontWeight = FontWeight.SemiBold)
                        Text("Use your phone fingerprint when available.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(checked = fingerprintEnabled, onCheckedChange = onFingerprintChange)
                }
                TextButton(onClick = { pin = ""; confirmPin = ""; error = ""; showPinSetup = true }) { Text("Change 6-digit PIN") }
                TextButton(onClick = onDisablePin) { Text("Turn off app lock") }
            } else {
                Button(onClick = { pin = ""; confirmPin = ""; error = ""; showPinSetup = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Set 6-digit PIN")
                }
            }
        }
    }
    if (showPinSetup) {
        AlertDialog(
            onDismissRequest = { showPinSetup = false },
            title = { Text(if (pinConfigured) "Change app PIN" else "Set app PIN") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Choose a 6-digit number. You will need it to open FundNiti.")
                    OutlinedTextField(
                        value = pin,
                        onValueChange = { if (it.all(Char::isDigit) && it.length <= 6) pin = it },
                        label = { Text("6-digit PIN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = confirmPin,
                        onValueChange = { if (it.all(Char::isDigit) && it.length <= 6) confirmPin = it },
                        label = { Text("Enter PIN again") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )
                    if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (pin.length != 6) error = "PIN must contain exactly 6 digits."
                    else if (pin != confirmPin) error = "The PINs do not match."
                    else { onSetPin(pin); showPinSetup = false }
                }) { Text("Save PIN") }
            },
            dismissButton = { TextButton(onClick = { showPinSetup = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun PinUnlockDialog(
    fingerprintEnabled: Boolean,
    onUnlock: (String) -> Boolean,
    onFingerprint: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Unlock FundNiti") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Enter your 6-digit PIN to open your loan records.")
                OutlinedTextField(
                    value = pin,
                    onValueChange = { if (it.all(Char::isDigit) && it.length <= 6) pin = it; error = "" },
                    label = { Text("6-digit PIN") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )
                if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                if (fingerprintEnabled) {
                    TextButton(onClick = onFingerprint, modifier = Modifier.fillMaxWidth()) {
                        Text("Unlock with fingerprint")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (pin.length != 6) error = "Enter all 6 digits."
                else if (onUnlock(pin)) { pin = ""; error = "" }
                else { pin = ""; error = "Wrong PIN. Try again." }
            }, enabled = pin.length == 6) { Text("Unlock") }
        }
    )
}

@Composable
private fun BackupScreen(
    modifier: Modifier,
    pinConfigured: Boolean,
    fingerprintEnabled: Boolean,
    onSetPin: (String) -> Unit,
    onDisablePin: () -> Unit,
    onFingerprintChange: (Boolean) -> Unit,
    backupStatus: String,
    onBackupNow: () -> Unit,
    onRecoverDeviceBackup: () -> Unit,
    onExport: () -> Unit,
    onImport: () -> Unit
) {
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Your data stays on this device", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("No login, cloud database, or external server. Back up all records to the Android folder in one tap, recover a saved device snapshot, or export an encrypted backup file.")
        Card(Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(22.dp)),
            shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Filled.FileDownload, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text("Back up records", style = MaterialTheme.typography.titleMedium)
                Text("One tap saves every loan and repayment to Android/media/com.rath0darya.loanmanager/Lendora/backup/Lendora-Data.json. On reinstall, FundNiti checks this file and restores it automatically when the in-app database is empty.")
                Button(onClick = onBackupNow, modifier = Modifier.fillMaxWidth()) { Text("Back up all data now") }
                Button(onClick = onExport, modifier = Modifier.fillMaxWidth()) { Text("Export encrypted backup file") }
            }
        }
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Filled.FileUpload, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text("Restore records", style = MaterialTheme.typography.titleMedium)
                Text("Load the saved Android folder snapshot directly into the app when the local database is empty. Existing records are never replaced by this one-tap recovery action.")
                Button(onClick = onRecoverDeviceBackup, modifier = Modifier.fillMaxWidth()) { Text("Load device backup into app") }
                Text("Or choose a separate backup file. Restoring a selected file replaces current records.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Button(onClick = onImport, modifier = Modifier.fillMaxWidth()) { Text("Choose backup file") }
            }
        }
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Device backup status", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(backupStatus, style = MaterialTheme.typography.bodySmall)
                Text("The automatic JSON copy is readable in your phone's file manager so it can survive app reinstall. It contains personal financial records; protect your phone and do not share this file. Android and file managers can still delete files—the app simply does not offer a delete-backup button.",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        AppLockSettings(
            pinConfigured = pinConfigured,
            fingerprintEnabled = fingerprintEnabled,
            onSetPin = onSetPin,
            onDisablePin = onDisablePin,
            onFingerprintChange = onFingerprintChange
        )
        Spacer(Modifier.height(8.dp))
        Card(Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Filled.AccountBalance, contentDescription = null, tint = Color(0xFF126B55), modifier = Modifier.size(30.dp))
                    Column {
                        Text("About FundNiti", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Personal lending, clearly organised", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
                Text("FundNiti is an offline-first loan ledger for tracking money lent and borrowed, people, repayments, due dates and portfolio trends. Your records are stored locally on this device.", style = MaterialTheme.typography.bodyMedium)
                Text("Interest method", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text("Simple interest on the original principal at the fixed monthly rate. Full monthly anniversary periods receive the full monthly rate; partial periods are prorated by elapsed calendar days divided by the actual days in that monthly period. Recorded payments are deducted from accrued interest plus principal in the displayed outstanding balance.", style = MaterialTheme.typography.bodySmall)
                Text("Privacy by design", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text("No account, external server, or cloud sync is required. FundNiti keeps a portable JSON snapshot in Android/media/com.rath0darya.loanmanager/Lendora/backup and restores it on a fresh install when the local database is empty. This automatic snapshot is not encrypted so reinstalling can read it; protect the file because it contains financial records. Manual encrypted exports use this installation's Android Keystore key and generally cannot be restored after reinstalling or on another device.", style = MaterialTheme.typography.bodySmall)
                Text("FundNiti · Native Android · Kotlin + Jetpack Compose · Version 1.0", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun LoanDialog(
    existing: LoanEntity? = null,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, Double, Double, Long, Int, String) -> Unit
) {
    val context = LocalContext.current
    var name1 by remember(existing?.id) { mutableStateOf(existing?.personName ?: "") }
    var name2 by remember(existing?.id) { mutableStateOf(existing?.personName2 ?: "") }
    var name3 by remember(existing?.id) { mutableStateOf(existing?.personName3 ?: "") }
    var direction by remember(existing?.id) { mutableStateOf(existing?.direction ?: "LENT") }
    var principal by remember(existing?.id) { mutableStateOf(existing?.principal?.let { amountInputFormat(it.toString()) } ?: "") }
    var rate by remember(existing?.id) { mutableStateOf(existing?.monthlyRate?.toString() ?: "") }
    var startDateText by remember(existing?.id) {
        mutableStateOf(existing?.startDate?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().toString() } ?: LocalDate.now().toString())
    }
    var dueDays by remember(existing?.id) {
        mutableStateOf(existing?.dueDate?.let { ((it - System.currentTimeMillis()) / 86_400_000L).coerceAtLeast(0).toString() } ?: "30")
    }
    var note by remember(existing?.id) { mutableStateOf(existing?.note ?: "") }
    val amount = parseAmount(principal)
    val interest = rate.toDoubleOrNull()
    val parsedDate = runCatching { LocalDate.parse(startDateText) }.getOrNull()
    val validStartDate = parsedDate != null && !parsedDate.isAfter(LocalDate.now())
    val startMillis = if (validStartDate) parsedDate!!.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() else null
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Add loan" else "Edit loan") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    name1, { name1 = it },
                    label = { Text(if (direction == "LENT") "Person who received money" else "Person you borrowed money from") },
                    supportingText = { Text(if (direction == "LENT") "Who did you give the money to?" else "Who gave you the money?") },
                    singleLine = true
                )
                OutlinedTextField(
                    name2, { name2 = it },
                    label = { Text(if (direction == "BORROWED_FOR_OTHER") "Your name (person who borrowed)" else if (direction == "LENT") "Your name / lender name" else "Your name (optional)") },
                    supportingText = { Text(if (direction == "BORROWED_FOR_OTHER") "Name used when you borrowed the money" else "Optional name for your side of this record") },
                    singleLine = true
                )
                if (direction == "BORROWED_FOR_OTHER") OutlinedTextField(name3, { name3 = it }, label = { Text("Person you gave the money to") }, supportingText = { Text("The person who asked you for money") }, singleLine = true)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    androidx.compose.material3.FilterChip(selected = direction == "LENT", onClick = { direction = "LENT" }, label = { Text("I lent") })
                    androidx.compose.material3.FilterChip(selected = direction == "BORROWED", onClick = { direction = "BORROWED" }, label = { Text("I borrowed") })
                    androidx.compose.material3.FilterChip(selected = direction == "BORROWED_FOR_OTHER", onClick = { direction = "BORROWED_FOR_OTHER" }, label = { Text("Borrowed for someone") })
                }
                OutlinedTextField(
                    value = principal,
                    onValueChange = { principal = amountInputFormat(it) },
                    label = { Text("Principal amount (₹)") },
                    supportingText = { amount?.takeIf { it > 0 }?.let { Text(amountInWords(it)) } },
                    singleLine = true
                )
                OutlinedTextField(rate, { rate = it }, label = { Text("Fixed interest (% per month)") }, singleLine = true)
                OutlinedTextField(
                    value = startDateText,
                    onValueChange = { startDateText = it },
                    label = { Text("Loan taken date (yyyy-MM-dd)") },
                    supportingText = { Text("Tap Choose to select a past or current date.") },
                    trailingIcon = {
                        TextButton(onClick = {
                            val date = parsedDate ?: LocalDate.now()
                            DatePickerDialog(
                                context,
                                { _, year, month, day -> startDateText = LocalDate.of(year, month + 1, day).toString() },
                                date.year, date.monthValue - 1, date.dayOfMonth
                            ).apply { datePicker.maxDate = System.currentTimeMillis() }.show()
                        }) { Text("Choose") }
                    },
                    isError = !validStartDate,
                    singleLine = true
                )
                OutlinedTextField(dueDays, { dueDays = it.filter(Char::isDigit).take(4) },
                    label = { Text("Reminder due in days from today (0 = none)") }, singleLine = true)
                OutlinedTextField(note, { note = it }, label = { Text("Note (optional)") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name1.isNotBlank() && amount != null && amount > 0 && interest != null && interest >= 0 && startMillis != null) {
                    onSave(name1, name2, name3, direction, amount, interest, startMillis, dueDays.toIntOrNull() ?: 0, note)
                }
            }, enabled = name1.isNotBlank() && (direction != "BORROWED_FOR_OTHER" || (name2.isNotBlank() && name3.isNotBlank())) && amount != null && amount > 0 && interest != null && interest >= 0 && startMillis != null) {
                Text(if (existing == null) "Save loan" else "Save changes")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun PaymentDialog(existing: PaymentEntity?, onDismiss: () -> Unit, onSave: (Double, Long, String, String, String) -> Unit) {
    val context = LocalContext.current
    var amountText by remember { mutableStateOf(existing?.amount?.let { amountInputFormat(it.toString()) } ?: "") }
    var note by remember { mutableStateOf(existing?.note ?: "") }
    var method by remember { mutableStateOf(existing?.paymentMethod ?: "Cash") }
    var reference by remember { mutableStateOf(existing?.referenceNumber ?: "") }
    var paidAt by remember { mutableStateOf(existing?.paidAt ?: System.currentTimeMillis()) }
    val amount = parseAmount(amountText)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Record payment" else "Edit payment") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = amountInputFormat(it) },
                    label = { Text("Payment amount (₹)") },
                    supportingText = { amount?.takeIf { it > 0 }?.let { Text(amountInWords(it)) } },
                    singleLine = true
                )
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        androidx.compose.material3.FilterChip(selected = method == "Cash", onClick = { method = "Cash"; reference = "" }, label = { Text("Cash") })
                        androidx.compose.material3.FilterChip(selected = method == "UPI", onClick = { method = "UPI" }, label = { Text("UPI") })
                        androidx.compose.material3.FilterChip(selected = method == "NEFT", onClick = { method = "NEFT" }, label = { Text("NEFT") })
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        androidx.compose.material3.FilterChip(selected = method == "IMPS", onClick = { method = "IMPS" }, label = { Text("IMPS") })
                        androidx.compose.material3.FilterChip(selected = method == "RTGS", onClick = { method = "RTGS" }, label = { Text("RTGS") })
                        androidx.compose.material3.FilterChip(selected = method == "Other", onClick = { method = "Other" }, label = { Text("Other") })
                    }
                }
                if (method != "Cash") OutlinedTextField(reference, { reference = it }, label = { Text(if (method == "UPI") "UPI UTR number" else "Transaction / reference number") }, singleLine = true)
                TextButton(onClick = {
                    val selected = java.util.Calendar.getInstance().apply { timeInMillis = paidAt }
                    DatePickerDialog(context, { _, year, month, day ->
                        val date = java.util.Calendar.getInstance().apply { timeInMillis = paidAt; set(year, month, day) }
                        paidAt = date.timeInMillis
                    }, selected.get(java.util.Calendar.YEAR), selected.get(java.util.Calendar.MONTH), selected.get(java.util.Calendar.DAY_OF_MONTH)).show()
                }) { Text("Payment date: ${formatDate(paidAt)}") }
                TextButton(onClick = {
                    val selected = java.util.Calendar.getInstance().apply { timeInMillis = paidAt }
                    TimePickerDialog(context, { _, hour, minute -> selected.set(java.util.Calendar.HOUR_OF_DAY, hour); selected.set(java.util.Calendar.MINUTE, minute); paidAt = selected.timeInMillis }, selected.get(java.util.Calendar.HOUR_OF_DAY), selected.get(java.util.Calendar.MINUTE), false).show()
                }) { Text("Payment time: ${DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(paidAt))}") }
                OutlinedTextField(note, { note = it }, label = { Text("Notes (optional)") })
            }
        },
        confirmButton = {
            TextButton(onClick = { if (amount != null && amount > 0) onSave(amount, paidAt, method, reference, note) },
                enabled = amount != null && amount > 0 && (method == "Cash" || reference.isNotBlank())) { Text(if (existing == null) "Save payment" else "Save changes") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary)
            if (value.trim().startsWith("₹")) {
                value.replace("₹", "").replace(",", "").toDoubleOrNull()?.let { amount ->
                    Text(amountInWords(amount), style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                }
            }
        }
    }
}

@Composable
private fun EmptyState(title: String, message: String) {
    Card(Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.AccountBalance, null, Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun money(value: Double): String {
    val symbols = DecimalFormatSymbols(Locale("en", "IN")).apply {
        groupingSeparator = ','
        decimalSeparator = '.'
        currencySymbol = "₹"
    }
    return DecimalFormat("₹#,##,##0.00", symbols).format(value.coerceAtLeast(0.0))
}

private fun parseAmount(text: String): Double? =
    text.replace(",", "").replace("₹", "").trim().toDoubleOrNull()

/** Formats editable amounts with Indian grouping, e.g. 1,23,456.78. */
private fun amountInputFormat(raw: String): String {
    val cleaned = raw.filter { it.isDigit() || it == '.' }
    val dot = cleaned.indexOf('.')
    val integerPart = (if (dot >= 0) cleaned.substring(0, dot) else cleaned)
        .trimStart('0').ifEmpty { if (cleaned.startsWith("0")) "0" else "" }
    val decimalPart = if (dot >= 0) "." + cleaned.substring(dot + 1).filter(Char::isDigit).take(2) else ""
    val grouped = if (integerPart.length <= 3) integerPart else {
        val lastThree = integerPart.takeLast(3)
        val leading = integerPart.dropLast(3).reversed().chunked(2)
            .joinToString(",") { it }.reversed()
        "$leading,$lastThree"
    }
    return grouped + decimalPart
}

private fun amountInWords(value: Double): String {
    val totalPaise = (value.coerceAtLeast(0.0) * 100.0).toLong()
    val rupees = totalPaise / 100
    val paise = totalPaise % 100
    val rupeeWords = indianNumberWords(rupees)
    val paiseWords = if (paise > 0) " and ${indianNumberWords(paise)} Paise" else ""
    return "$rupeeWords Rupees$paiseWords Only"
}

private fun indianNumberWords(number: Long): String {
    if (number == 0L) return "Zero"
    val ones = arrayOf("", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
        "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen",
        "Eighteen", "Nineteen")
    val tens = arrayOf("", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety")
    fun underThousand(n: Int): String {
        var v = n
        val parts = mutableListOf<String>()
        if (v >= 100) { parts += ones[v / 100] + " Hundred"; v %= 100 }
        if (v >= 20) { parts += tens[v / 10]; v %= 10 }
        if (v in 1..19) parts += ones[v]
        return parts.joinToString(" ")
    }
    var n = number
    val parts = mutableListOf<String>()
    val crore = n / 10_000_000
    if (crore > 0) { parts += indianNumberWords(crore) + " Crore"; n %= 10_000_000 }
    val lakh = n / 100_000
    if (lakh > 0) { parts += underThousand(lakh.toInt()) + " Lakh"; n %= 100_000 }
    val thousand = n / 1_000
    if (thousand > 0) { parts += underThousand(thousand.toInt()) + " Thousand"; n %= 1_000 }
    if (n > 0) parts += underThousand(n.toInt())
    return parts.joinToString(" ")
}

private fun formatDate(time: Long): String =
    DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(Date(time))

/**
 * Simple interest using calendar-month anniversaries for completed periods.
 * Each completed month earns one full monthly interest amount. Remaining days
 * accrue at monthlyInterest * 12 / 365 or 366, according to each day's calendar year.
 * Example: 27 Jun 2026 to 27 Jun 2027 earns 12 full monthly charges.
 */
private fun accruedInterest(principal: Double, monthlyRate: Double, startMillis: Long, endMillis: Long): Double {
    if (principal <= 0.0 || monthlyRate <= 0.0 || endMillis <= startMillis) return 0.0
    val zone = ZoneId.systemDefault()
    val start = Instant.ofEpochMilli(startMillis).atZone(zone).toLocalDate()
    val end = Instant.ofEpochMilli(endMillis).atZone(zone).toLocalDate()
    if (end.isBefore(start)) return 0.0

    val hundred = BigDecimal.valueOf(100L)
    val scale = 12
    val rounding = RoundingMode.HALF_UP
    val monthlyInterest = BigDecimal.valueOf(principal)
        .multiply(BigDecimal.valueOf(monthlyRate))
        .divide(hundred, scale, rounding)

    var completedPeriods = 0L
    while (!start.plusMonths(completedPeriods + 1).isAfter(end)) completedPeriods++
    var interest = monthlyInterest.multiply(BigDecimal.valueOf(completedPeriods))

    // Only the remaining incomplete monthly period uses the annual daily rate.
    var day = start.plusMonths(completedPeriods)
    while (day.isBefore(end)) {
        val daysInYear = if (day.isLeapYear) 366L else 365L
        val dailyInterest = monthlyInterest.multiply(BigDecimal.valueOf(12L))
            .divide(BigDecimal.valueOf(daysInYear), scale, rounding)
        interest = interest.add(dailyInterest)
        day = day.plusDays(1)
    }

    return interest.setScale(2, rounding).toDouble().coerceAtLeast(0.0)
}
private fun elapsedLoanDays(startMillis: Long, endMillis: Long): Long {
    val zone = ZoneId.systemDefault()
    val start = Instant.ofEpochMilli(startMillis).atZone(zone).toLocalDate()
    val end = Instant.ofEpochMilli(endMillis).atZone(zone).toLocalDate()
    return java.time.temporal.ChronoUnit.DAYS.between(start, end).coerceAtLeast(0)
}
