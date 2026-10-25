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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.window.Dialog
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import java.time.Period
import java.time.temporal.ChronoUnit

private fun modernTypography(colors: androidx.compose.material3.ColorScheme): androidx.compose.material3.Typography {
    val base = androidx.compose.material3.Typography()
    val font = FontFamily.SansSerif
    return base.copy(
        displayLarge = base.displayLarge.copy(fontFamily = font, color = colors.primary),
        displayMedium = base.displayMedium.copy(fontFamily = font, color = colors.primary),
        displaySmall = base.displaySmall.copy(fontFamily = font, color = colors.primary),
        headlineLarge = base.headlineLarge.copy(fontFamily = font, color = colors.primary),
        headlineMedium = base.headlineMedium.copy(fontFamily = font, color = colors.primary),
        headlineSmall = base.headlineSmall.copy(fontFamily = font, color = colors.primary),
        titleLarge = base.titleLarge.copy(fontFamily = font, color = colors.primary),
        titleMedium = base.titleMedium.copy(fontFamily = font, color = colors.primary),
        titleSmall = base.titleSmall.copy(fontFamily = font, color = colors.primary),
        bodyLarge = base.bodyLarge.copy(fontFamily = font),
        bodyMedium = base.bodyMedium.copy(fontFamily = font),
        bodySmall = base.bodySmall.copy(fontFamily = font),
        labelLarge = base.labelLarge.copy(fontFamily = font),
        labelMedium = base.labelMedium.copy(fontFamily = font),
        labelSmall = base.labelSmall.copy(fontFamily = font)
    )
}

private enum class AppPage(val label: String, val icon: ImageVector) {
    Home("Home", Icons.Filled.Home),
    Loans("Loans", Icons.Filled.AccountBalance),
    People("People", Icons.Filled.People),
    Calculators("Calculators", Icons.Filled.Calculate),
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
    var fingerprintFailures by remember { mutableStateOf(0) }
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

    MaterialTheme(colorScheme = appColorScheme, typography = modernTypography(appColorScheme)) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    title = {
                        Column {
                            Text("FundNiti", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                            Text(page.label, style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary)
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
                AppPage.Calculators -> CalculatorsScreen(Modifier.padding(padding))
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
                                    Triple(file.toString(), root.getJSONArray("loans").length(), root.getJSONArray("payments").length())
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
            fingerprintFailures = fingerprintFailures,
            onUnlock = { pin ->
                val salt = preferences.getString("pin_salt", "") ?: ""
                val savedHash = preferences.getString("pin_hash", "") ?: ""
                if (hashAppPin(pin, salt) == savedHash) {
                    appLocked = false
                    fingerprintFailures = 0
                    true
                } else false
            },
            onFingerprintFailureCount = { fingerprintFailures = it },
            onFingerprint = {
                val activity = context as? androidx.fragment.app.FragmentActivity
                if (activity != null) requestFingerprintUnlock(
                    activity,
                    { appLocked = false; fingerprintFailures = 0 },
                    { count -> fingerprintFailures = maxOf(fingerprintFailures, count) },
                    { message ->
                        fingerprintFailures = 3
                        scope.launch { snackbarHostState.showSnackbar(message) }
                    }
                )
            }
        )
    }
}

private data class LoanReportLine(val label: String, val value: String, val section: Boolean = false)

private fun loanReportLines(loan: LoanEntity, payments: List<PaymentEntity>, outstanding: Double): List<LoanReportLine> {
    val now = System.currentTimeMillis()
    val interest = accruedInterest(loan.principal, loan.monthlyRate, loan.startDate, now)
    val paid = payments.sumOf { it.amount }
    val start = Instant.ofEpochMilli(loan.startDate).atZone(ZoneId.systemDefault()).toLocalDate()
    val today = LocalDate.now()
    val end = if (today.isBefore(start)) start else today
    val period = Period.between(start, end)
    val months = period.years * 12 + period.months
    val rows = mutableListOf(
        LoanReportLine("People", listOf(loan.personName, loan.personName2, loan.personName3).filter { it.isNotBlank() }.joinToString(" → ").ifBlank { "Not specified" }),
        LoanReportLine("Loan direction", when (loan.direction) { "LENT" -> "You gave money to someone"; "BORROWED_FOR_OTHER" -> "Borrowed from one person for another"; else -> "Borrowed for yourself" }),
        LoanReportLine("Loan details", "", true),
        LoanReportLine("Loan date", formatDate(loan.startDate)),
        LoanReportLine("Duration", "$months months, ${period.days} days (${ChronoUnit.DAYS.between(start, end)} days total)"),
        LoanReportLine("Principal amount", money(loan.principal)),
        LoanReportLine("Interest rate", "${loan.monthlyRate}% per month"),
        LoanReportLine("Interest till now", money(interest)),
        LoanReportLine("Payments made", money(paid)),
        LoanReportLine("Amount still due", money(outstanding)),
        LoanReportLine("Status", if (outstanding <= 0.009) "Fully paid" else "Still to be paid")
    )
    loan.dueDate?.let { rows.add(LoanReportLine("Reminder date", formatDate(it))) }
    if (loan.note.isNotBlank()) rows.add(LoanReportLine("Loan notes", loan.note))
    rows.add(LoanReportLine("Payment history (${payments.size})", "", true))
    if (payments.isEmpty()) rows.add(LoanReportLine("Payments", "No payments recorded"))
    payments.forEachIndexed { index, payment ->
        rows.add(LoanReportLine("Payment ${index + 1}: ${money(payment.amount)}", "", true))
        rows.add(LoanReportLine("Date", formatDate(payment.paidAt)))
        rows.add(LoanReportLine("Time", DateFormat.getTimeInstance(DateFormat.MEDIUM).format(Date(payment.paidAt))))
        rows.add(LoanReportLine("Method", payment.paymentMethod))
        if (payment.referenceNumber.isNotBlank()) rows.add(LoanReportLine(if (payment.paymentMethod == "UPI") "UPI UTR number" else "Transaction number", payment.referenceNumber))
        if (payment.note.isNotBlank()) rows.add(LoanReportLine("Payment notes", payment.note))
    }
    return rows.flatMap { row ->
        if (row.section || row.value.length <= 54) listOf(row)
        else row.value.chunked(54).mapIndexed { index, chunk ->
            LoanReportLine(if (index == 0) row.label else row.label + " (cont.)", chunk)
        }
    }
}

private fun shareLoanReport(context: android.content.Context, loan: LoanEntity, payments: List<PaymentEntity>, outstanding: Double, asPdf: Boolean) {
    val rows = loanReportLines(loan, payments, outstanding)
    val navy = android.graphics.Color.rgb(24, 43, 85)
    val teal = android.graphics.Color.rgb(0, 137, 123)
    val ink = android.graphics.Color.rgb(35, 45, 62)
    val muted = android.graphics.Color.rgb(95, 108, 126)
    val pale = android.graphics.Color.rgb(240, 246, 252)
    val width = 1080
    val left = 64f
    val right = width - 64f
    val headerHeight = 250f
    val heights = rows.map { if (it.section) 62f else 82f }
    val imageHeight = (headerHeight + heights.sum() + 150f).toInt().coerceAtLeast(1000)
    val bitmap = android.graphics.Bitmap.createBitmap(width, imageHeight, android.graphics.Bitmap.Config.ARGB_8888)
    try {
        val canvas = android.graphics.Canvas(bitmap)
        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
        canvas.drawColor(android.graphics.Color.WHITE)
        paint.color = navy
        canvas.drawRect(0f, 0f, width.toFloat(), headerHeight, paint)
        try {
            val logo = context.packageManager.getApplicationIcon(context.packageName)
            logo.setBounds(64, 48, 164, 148)
            logo.draw(canvas)
        } catch (_: Exception) {}
        paint.color = android.graphics.Color.WHITE
        paint.textSize = 48f
        paint.typeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.BOLD)
        canvas.drawText("FundNiti", 190f, 102f, paint)
        paint.textSize = 28f
        canvas.drawText("FULL LOAN REPORT", 190f, 145f, paint)
        paint.textSize = 23f
        paint.typeface = android.graphics.Typeface.DEFAULT
        canvas.drawText("Personal lending, clearly organised", 64f, 205f, paint)
        var y = headerHeight + 24f
        rows.forEachIndexed { index, row ->
            val h = heights[index]
            if (row.section) {
                paint.color = teal
                canvas.drawRoundRect(left, y, right, y + h - 8f, 14f, 14f, paint)
                paint.color = android.graphics.Color.WHITE
                paint.textSize = 27f
                paint.typeface = android.graphics.Typeface.DEFAULT_BOLD
                canvas.drawText(row.label, left + 20f, y + 38f, paint)
            } else {
                paint.color = if (index % 2 == 0) pale else android.graphics.Color.WHITE
                canvas.drawRoundRect(left, y, right, y + h - 6f, 12f, 12f, paint)
                paint.color = muted
                paint.textSize = 22f
                paint.typeface = android.graphics.Typeface.DEFAULT
                canvas.drawText(row.label.take(58), left + 18f, y + 29f, paint)
                paint.color = ink
                paint.textSize = 27f
                paint.typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
                canvas.drawText(row.value, left + 18f, y + 59f, paint)
            }
            y += h
        }
        val footer = (y + 15f).coerceAtMost(imageHeight - 120f)
        paint.color = navy
        canvas.drawRect(0f, footer, width.toFloat(), imageHeight.toFloat(), paint)
        paint.color = android.graphics.Color.WHITE
        paint.textSize = 23f
        paint.typeface = android.graphics.Typeface.DEFAULT_BOLD
        canvas.drawText("FundNiti • Personal lending, clearly organised", left, footer + 42f, paint)
        paint.textSize = 19f
        paint.typeface = android.graphics.Typeface.DEFAULT
        canvas.drawText("For reference only • Verify details before sharing", left, footer + 78f, paint)

        val dir = java.io.File(context.cacheDir, "shared_reports").apply { mkdirs() }
        val file = java.io.File(dir, if (asPdf) "FundNiti-Loan-Report.pdf" else "FundNiti-Loan-Report.png")
        if (asPdf) {
            val doc = android.graphics.pdf.PdfDocument()
            val pageW = 595
            val pageH = 842
            val scale = pageW.toFloat() / width
            var rowIndex = 0
            var pageNo = 1
            do {
                val page = doc.startPage(android.graphics.pdf.PdfDocument.PageInfo.Builder(pageW, pageH, pageNo).create())
                val pc = page.canvas
                pc.drawColor(android.graphics.Color.WHITE)
                val hp = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply { color = navy }
                pc.drawRect(0f, 0f, pageW.toFloat(), headerHeight * scale, hp)
                val title = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply { color = android.graphics.Color.WHITE; textSize = 25f; typeface = android.graphics.Typeface.DEFAULT_BOLD }
                try {
                    val drawable = context.packageManager.getApplicationIcon(context.packageName)
                    val logo = android.graphics.Bitmap.createBitmap(48, 48, android.graphics.Bitmap.Config.ARGB_8888)
                    val logoCanvas = android.graphics.Canvas(logo)
                    drawable.setBounds(0, 0, 48, 48)
                    drawable.draw(logoCanvas)
                    pc.drawBitmap(logo, 32f, 18f, title)
                    logo.recycle()
                } catch (_: Exception) {}
                pc.drawText("FundNiti", 90f, 42f, title)
                title.textSize = 14f
                pc.drawText("FULL LOAN REPORT", 90f, 64f, title)
                val p = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
                var py = headerHeight * scale + 18f
                val maxY = pageH - 45f
                while (rowIndex < rows.size && py + heights[rowIndex] * scale < maxY) {
                    val row = rows[rowIndex]
                    val rh = heights[rowIndex] * scale
                    if (row.section) {
                        p.color = teal
                        pc.drawRoundRect(32f, py, pageW - 32f, py + rh - 3f, 6f, 6f, p)
                        p.color = android.graphics.Color.WHITE; p.textSize = 12f; p.typeface = android.graphics.Typeface.DEFAULT_BOLD
                        pc.drawText(row.label.take(65), 40f, py + rh / 2f + 4f, p)
                    } else {
                        p.color = if (rowIndex % 2 == 0) pale else android.graphics.Color.WHITE
                        pc.drawRoundRect(32f, py, pageW - 32f, py + rh - 3f, 5f, 5f, p)
                        p.color = muted; p.textSize = 9f; p.typeface = android.graphics.Typeface.DEFAULT
                        pc.drawText(row.label.take(34), 40f, py + 14f, p)
                        p.color = ink; p.textSize = 11f; p.typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
                        pc.drawText(row.value.take(66), 40f, py + 29f, p)
                    }
                    py += rh
                    rowIndex++
                }
                val foot = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply { color = navy; textSize = 9f }
                pc.drawText("FundNiti • For reference only • Page $pageNo", 32f, pageH - 18f, foot)
                doc.finishPage(page)
                pageNo++
            } while (rowIndex < rows.size)
            java.io.FileOutputStream(file).use { doc.writeTo(it) }
            doc.close()
        } else {
            java.io.FileOutputStream(file).use { bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
        }
        val uri = androidx.core.content.FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
            type = if (asPdf) "application/pdf" else "image/png"
            putExtra(android.content.Intent.EXTRA_STREAM, uri)
            putExtra(android.content.Intent.EXTRA_SUBJECT, "FundNiti — Full Loan Report")
            putExtra(android.content.Intent.EXTRA_TEXT, "FundNiti • Full Loan Report\nFor reference only. Verify details before sharing.")
            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(android.content.Intent.createChooser(intent, if (asPdf) "Share loan report PDF" else "Share loan report image"))
    } catch (e: Exception) {
        Toast.makeText(context, "Could not create report: " + (e.message ?: "unknown error"), Toast.LENGTH_LONG).show()
    } finally {
        bitmap.recycle()
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
    val context = LocalContext.current
    val interest = accruedInterest(loan.principal, loan.monthlyRate, loan.startDate, System.currentTimeMillis())
    val totalPaid = payments.sumOf { it.amount }
    val loanStartDate = Instant.ofEpochMilli(loan.startDate).atZone(ZoneId.systemDefault()).toLocalDate()
    val today = LocalDate.now()
    val durationEndDate = if (today.isBefore(loanStartDate)) loanStartDate else today
    val totalDays = ChronoUnit.DAYS.between(loanStartDate, durationEndDate)
    val elapsedPeriod = Period.between(loanStartDate, durationEndDate)
    val totalMonths = elapsedPeriod.years * 12 + elapsedPeriod.months
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Full loan report", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) },
        text = {
            Column(Modifier.fillMaxWidth().height(520.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text("People", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(listOf(loan.personName, loan.personName2, loan.personName3).filter { it.isNotBlank() }.joinToString(" → "))
                Text(when (loan.direction) { "LENT" -> "You gave money to someone."; "BORROWED_FOR_OTHER" -> "You borrowed money from one person and gave it to someone else."; else -> "You borrowed money for yourself." })
                androidx.compose.material3.HorizontalDivider()
                Text("Loan details", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                DetailLine("Loan date", formatDate(loan.startDate))
                DetailLine("Total Days", "$totalDays Days")
                DetailLine("In Months", String.format(Locale.getDefault(), "%02d Months %02d Days", totalMonths, elapsedPeriod.days))
                DetailLine("Principal amount", money(loan.principal))
                DetailLine("Interest rate", "${loan.monthlyRate}% per month")
                DetailLine("Interest till now", money(interest))
                DetailLine("Payments made", money(totalPaid))
                DetailLine("Amount still due", money(outstanding))
                DetailLine("Status", if (outstanding <= 0.009) "Fully paid" else "Still to be paid")
                loan.dueDate?.let { DetailLine("Reminder date", formatDate(it)) }
                if (loan.note.isNotBlank()) DetailLine("Notes", loan.note)
                androidx.compose.material3.HorizontalDivider()
                Text("Payments made (${payments.size})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
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
                Text("Share full report", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { shareLoanReport(context = context, loan = loan, payments = payments, outstanding = outstanding, asPdf = true) }, modifier = Modifier.weight(1f)) {
                        Text("Share PDF")
                    }
                    Button(onClick = { shareLoanReport(context = context, loan = loan, payments = payments, outstanding = outstanding, asPdf = false) }, modifier = Modifier.weight(1f)) {
                        Text("Share image")
                    }
                }
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
private fun CalculatorsScreen(modifier: Modifier) {
    var selected by remember { mutableStateOf("INTEREST") }
    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Loan calculators", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(
            "Estimate interest or calculate the exact calendar duration between two dates. Share a clear, ready-to-send result directly to your favourite social app.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            androidx.compose.material3.FilterChip(
                selected = selected == "INTEREST",
                onClick = { selected = "INTEREST" },
                label = { Text("Interest Calculator") }
            )
            androidx.compose.material3.FilterChip(
                selected = selected == "TENURE",
                onClick = { selected = "TENURE" },
                label = { Text("Tenure Calculator") }
            )
        }
        if (selected == "INTEREST") InterestCalculatorCard() else TenureCalculatorCard()
        Text(
            "These are estimates only and do not change saved loan records.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun InterestCalculatorCard() {
    val context = LocalContext.current
    var principalText by remember { mutableStateOf("") }
    var rateText by remember { mutableStateOf("") }
    var monthsText by remember { mutableStateOf("1") }
    var daysText by remember { mutableStateOf("0") }
    val principal = parseAmount(principalText)
    val rate = rateText.replace(",", "").trim().toDoubleOrNull()
    val months = monthsText.toLongOrNull()
    val days = daysText.toLongOrNull()
    val valid = principal != null && principal > 0 && rate != null && rate >= 0 &&
        months != null && months >= 0 && months <= 12000 && days != null && days >= 0 && days <= 36500
    val interest = if (valid) {
        val start = LocalDate.now()
        val end = start.plusMonths(months!!).plusDays(days!!)
        accruedInterest(
            principal!!, rate!!,
            start.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            end.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
    } else null
    Card(
        Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Interest Calculator", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            OutlinedTextField(principalText, { principalText = amountInputFormat(it) },
                modifier = Modifier.fillMaxWidth(), label = { Text("Principal amount (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
            OutlinedTextField(rateText, { rateText = it.filter { c -> c.isDigit() || c == '.' }.take(8) },
                modifier = Modifier.fillMaxWidth(), label = { Text("Fixed monthly interest rate (%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(monthsText, { monthsText = it.filter(Char::isDigit).take(5) },
                    modifier = Modifier.weight(1f), label = { Text("Months") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                OutlinedTextField(daysText, { daysText = it.filter(Char::isDigit).take(5) },
                    modifier = Modifier.weight(1f), label = { Text("Extra days") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
            }
            if (interest != null) {
                HorizontalResultLine("Interest amount", money(interest))
                HorizontalResultLine("Principal + interest", money(principal!! + interest))
                HorizontalResultLine("Duration", "${monthsText.toLongOrNull() ?: 0} months, ${daysText.toLongOrNull() ?: 0} days")
                androidx.compose.material3.                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { shareCalculatorResult(context, "Interest Calculator", "INTEREST CALCULATION RESULT\\nPrincipal: ${money(principal!!)}\\nMonthly interest rate: ${rateText}%\\nDuration: ${monthsText.toLongOrNull() ?: 0} months, ${daysText.toLongOrNull() ?: 0} days\\nInterest amount: ${money(interest)}\\nPrincipal + interest: ${money(principal + interest)}") }, modifier = Modifier.weight(1f)) { Text("Share image") }
                    Button(onClick = { shareCalculatorResultPdf(context, "Interest Calculator", "INTEREST CALCULATION RESULT\\nPrincipal: ${money(principal!!)}\\nMonthly interest rate: ${rateText}%\\nDuration: ${monthsText.toLongOrNull() ?: 0} months, ${daysText.toLongOrNull() ?: 0} days\\nInterest amount: ${money(interest)}\\nPrincipal + interest: ${money(principal + interest)}") }, modifier = Modifier.weight(1f)) { Text("Share PDF") }
                }
            } else {
                Text("Enter a positive principal, a non-negative rate, and a valid duration.",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun TenureCalculatorCard() {
    val context = LocalContext.current
    val zone = ZoneId.systemDefault()
    var fromDateMillis by remember {
        mutableStateOf(LocalDate.now().atStartOfDay(zone).toInstant().toEpochMilli())
    }
    var toDateMillis by remember {
        mutableStateOf(LocalDate.now().atStartOfDay(zone).toInstant().toEpochMilli())
    }
    val fromDate = Instant.ofEpochMilli(fromDateMillis).atZone(zone).toLocalDate()
    val toDate = Instant.ofEpochMilli(toDateMillis).atZone(zone).toLocalDate()
    val valid = !toDate.isBefore(fromDate)
    val period = if (valid) Period.between(fromDate, toDate) else null
    val totalDays = if (valid) ChronoUnit.DAYS.between(fromDate, toDate) else 0L
    val totalMonths = period?.let { it.years * 12 + it.months } ?: 0

    Card(
        Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Tenure Calculator", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                "Select only the From date and To date. FundNiti calculates the exact calendar months and remaining days, plus the total number of days.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = {
                val selected = java.util.Calendar.getInstance().apply { timeInMillis = fromDateMillis }
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        val selectedDate = LocalDate.of(year, month + 1, day)
                        fromDateMillis = selectedDate.atStartOfDay(zone).toInstant().toEpochMilli()
                        if (toDate.isBefore(selectedDate)) toDateMillis = fromDateMillis
                    },
                    selected.get(java.util.Calendar.YEAR),
                    selected.get(java.util.Calendar.MONTH),
                    selected.get(java.util.Calendar.DAY_OF_MONTH)
                ).apply { datePicker.maxDate = toDateMillis }.show()
            }, modifier = Modifier.fillMaxWidth()) {
                Text("From date: ${formatDate(fromDateMillis)}")
            }
            TextButton(onClick = {
                val selected = java.util.Calendar.getInstance().apply { timeInMillis = toDateMillis }
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        toDateMillis = LocalDate.of(year, month + 1, day)
                            .atStartOfDay(zone).toInstant().toEpochMilli()
                    },
                    selected.get(java.util.Calendar.YEAR),
                    selected.get(java.util.Calendar.MONTH),
                    selected.get(java.util.Calendar.DAY_OF_MONTH)
                ).apply { datePicker.minDate = fromDateMillis }.show()
            }, modifier = Modifier.fillMaxWidth()) {
                Text("To date: ${formatDate(toDateMillis)}")
            }

            if (valid && period != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Calculated duration", style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer)
                        Text("$totalMonths months, ${period.days} days", style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        HorizontalResultLine("Total days", "$totalDays days")
                    }
                }
                androidx.compose.material3.                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { shareCalculatorResult(context, "Tenure Calculator", "DATE DURATION RESULT\\nFrom: ${formatDate(fromDateMillis)}\\nTo: ${formatDate(toDateMillis)}\\nDuration: $totalMonths months, ${period.days} days\\nTotal days: $totalDays") }, modifier = Modifier.weight(1f)) { Text("Share image") }
                    Button(onClick = { shareCalculatorResultPdf(context, "Tenure Calculator", "DATE DURATION RESULT\\nFrom: ${formatDate(fromDateMillis)}\\nTo: ${formatDate(toDateMillis)}\\nDuration: $totalMonths months, ${period.days} days\\nTotal days: $totalDays") }, modifier = Modifier.weight(1f)) { Text("Share PDF") }
                }
                Text(
                    "Calendar months follow the selected dates, including different month lengths and leap years. This calculation does not change saved loan records.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun shareCalculatorResult(context: android.content.Context, title: String, result: String) {
    val navy = android.graphics.Color.rgb(20, 45, 66)
    val teal = android.graphics.Color.rgb(18, 107, 85)
    val gold = android.graphics.Color.rgb(197, 155, 67)
    val ink = android.graphics.Color.rgb(32, 48, 43)
    val muted = android.graphics.Color.rgb(91, 111, 102)
    val pale = android.graphics.Color.rgb(239, 247, 243)
    val width = 1080; val margin = 64f
    val lines = result.lines().filter { it.isNotBlank() }
    val height = (300 + lines.size * 90).coerceAtLeast(850)
    val bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888)
    try {
        val canvas = android.graphics.Canvas(bitmap); canvas.drawColor(android.graphics.Color.WHITE)
        val p = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply { color = navy }
        canvas.drawRect(0f, 0f, width.toFloat(), 220f, p)
        p.color = gold; canvas.drawRoundRect(42f, 34f, 50f, 178f, 4f, 4f, p)
        p.color = android.graphics.Color.WHITE; p.textSize = 48f; p.typeface = android.graphics.Typeface.DEFAULT_BOLD; canvas.drawText("FundNiti", 76f, 84f, p)
        p.textSize = 28f; p.typeface = android.graphics.Typeface.DEFAULT; canvas.drawText(title, 76f, 130f, p)
        p.textSize = 19f; canvas.drawText("CALCULATION SUMMARY", 76f, 172f, p)
        var y = 246f
        lines.forEachIndexed { index, line ->
            val rowHeight = 66f
            p.color = if (index % 2 == 0) pale else android.graphics.Color.WHITE
            canvas.drawRoundRect(margin, y, width - margin, y + rowHeight, 14f, 14f, p)
            p.color = if (index == 0) teal else ink; p.textSize = if (index == 0) 29f else 25f
            p.typeface = if (index == 0) android.graphics.Typeface.DEFAULT_BOLD else android.graphics.Typeface.DEFAULT
            val chunks = line.chunked(52)
            chunks.forEachIndexed { j, chunk -> canvas.drawText(chunk, margin + 20f, y + 39f + j * 29f, p) }
            y += rowHeight + 8f
        }
        p.color = navy; canvas.drawRect(0f, height - 70f, width.toFloat(), height.toFloat(), p)
        p.color = android.graphics.Color.WHITE; p.textSize = 18f; p.typeface = android.graphics.Typeface.DEFAULT
        canvas.drawText("For reference only · No saved loan records changed", margin, height - 38f, p)
        val directory = java.io.File(context.cacheDir, "shared_reports").apply { mkdirs() }
        val file = java.io.File(directory, "FundNiti-Calculator-Result.png")
        java.io.FileOutputStream(file).use { bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
        shareCalculatorFile(context, title, file, false)
    } catch (e: Exception) { Toast.makeText(context, "Could not share calculation: " + (e.message ?: "unknown error"), Toast.LENGTH_LONG).show() }
    finally { bitmap.recycle() }
}

private fun shareCalculatorResultPdf(context: android.content.Context, title: String, result: String) {
    val navy = android.graphics.Color.rgb(20, 45, 66); val teal = android.graphics.Color.rgb(18, 107, 85)
    val ink = android.graphics.Color.rgb(32, 48, 43); val muted = android.graphics.Color.rgb(91, 111, 102)
    val pale = android.graphics.Color.rgb(239, 247, 243)
    val lines = result.lines().filter { it.isNotBlank() }
    val directory = java.io.File(context.cacheDir, "shared_reports").apply { mkdirs() }
    val file = java.io.File(directory, "FundNiti-Calculator-Result.pdf")
    val doc = android.graphics.pdf.PdfDocument()
    try {
        val pageWidth = 595; val pageHeight = 842
        val page = doc.startPage(android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create())
        val canvas = page.canvas; canvas.drawColor(android.graphics.Color.WHITE)
        val p = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply { color = navy }; canvas.drawRect(0f, 0f, pageWidth.toFloat(), 140f, p)
        p.color = android.graphics.Color.WHITE; p.textSize = 27f; p.typeface = android.graphics.Typeface.DEFAULT_BOLD; canvas.drawText("FundNiti", 38f, 55f, p)
        p.textSize = 15f; p.typeface = android.graphics.Typeface.DEFAULT; canvas.drawText(title, 38f, 86f, p); p.textSize = 10f; canvas.drawText("CALCULATION SUMMARY", 38f, 112f, p)
        var y = 164f
        lines.forEachIndexed { index, line ->
            p.textSize = 12f; p.typeface = if (index == 0) android.graphics.Typeface.DEFAULT_BOLD else android.graphics.Typeface.DEFAULT
            val chunks = line.chunked(62); val h = maxOf(38f, chunks.size * 18f + 16f)
            p.color = if (index % 2 == 0) pale else android.graphics.Color.WHITE; canvas.drawRoundRect(28f, y, pageWidth - 28f, y + h, 7f, 7f, p)
            p.color = if (index == 0) teal else ink; chunks.forEachIndexed { j, chunk -> canvas.drawText(chunk, 40f, y + 24f + j * 18f, p) }; y += h + 8f
        }
        p.color = muted; p.textSize = 9f; canvas.drawText("For reference only · No saved loan records changed", 30f, pageHeight - 24f, p)
        doc.finishPage(page); java.io.FileOutputStream(file).use { doc.writeTo(it) }
        shareCalculatorFile(context, title, file, true)
    } catch (e: Exception) { Toast.makeText(context, "Could not create calculator PDF: " + (e.message ?: "unknown error"), Toast.LENGTH_LONG).show() }
    finally { doc.close() }
}

private fun shareCalculatorFile(context: android.content.Context, title: String, file: java.io.File, asPdf: Boolean) {
    val uri = androidx.core.content.FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
    val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
        type = if (asPdf) "application/pdf" else "image/png"
        putExtra(android.content.Intent.EXTRA_STREAM, uri); putExtra(android.content.Intent.EXTRA_SUBJECT, "FundNiti — " + title)
        putExtra(android.content.Intent.EXTRA_TEXT, "FundNiti " + title + " result. For reference only; no saved loan records changed.")
        addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(android.content.Intent.createChooser(intent, if (asPdf) "Share calculator PDF" else "Share calculator image"))
}
@Composable
private fun HorizontalResultLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Spacer(Modifier.width(10.dp))
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary, textAlign = androidx.compose.ui.text.style.TextAlign.End)
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
    fingerprintFailures: Int,
    onUnlock: (String) -> Boolean,
    onFingerprintFailureCount: (Int) -> Unit,
    onFingerprint: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    val pinFocus = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    val showPin = !fingerprintEnabled || fingerprintFailures >= 1
    LaunchedEffect(Unit) {
        if (fingerprintEnabled) onFingerprint()
    }
    LaunchedEffect(fingerprintFailures) {
        if (fingerprintFailures >= 3) {
            pinFocus.requestFocus()
            keyboard?.show()
        }
    }
    LaunchedEffect(pin) {
        if (pin.length == 6) {
            if (onUnlock(pin)) {
                error = ""
                pin = ""
                onFingerprintFailureCount(0)
            } else {
                pin = ""
                error = "That PIN is not correct. Try again."
            }
        }
    }
    Dialog(onDismissRequest = {}) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            shadowElevation = 18.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(76.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Icon(Icons.Filled.AccountBalance, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(34.dp))
                    }
                }
                Text("Welcome back", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("FundNiti is locked", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Text(
                    if (fingerprintEnabled && fingerprintFailures < 3) "Use your fingerprint to open your private loan records." else "Enter your 6-digit PIN to continue.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                if (showPin) {
                    OutlinedTextField(
                        value = pin,
                        onValueChange = { value ->
                            if (value.all(Char::isDigit) && value.length <= 6) {
                                pin = value
                                error = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth().focusRequester(pinFocus),
                        label = { Text("6-digit PIN") },
                        placeholder = { Text("••••••") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp)
                    )
                    if (error.isNotBlank()) {
                        Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                    Text("Your PIN is checked automatically when you enter the last digit.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
                if (fingerprintEnabled) {
                    Button(
                        onClick = {
                            error = ""
                            onFingerprint()
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Try fingerprint again")
                    }
                }
                if (!showPin && fingerprintEnabled) {
                    Text("If fingerprint does not work, choose Use PIN on the fingerprint prompt.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
                if (showPin && !fingerprintEnabled) {
                    Text("Enter your PIN above. No extra button is needed.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
        }
    }
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
                Text("One tap saves every loan and repayment to Download/FundNiti/Backup/Lendora-Data.json. On Android 10 and later, this shared Downloads copy is designed to remain after uninstall. On reinstall, FundNiti checks it and restores records automatically when the in-app database is empty.")
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
 * Each completed month earns one full monthly interest amount. Partial-month
 * days earn monthly interest divided by 30, regardless of leap years.
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

    // Partial-month daily interest is always 1/30 of the monthly interest.
    var day = start.plusMonths(completedPeriods)
    val dailyInterest = monthlyInterest.divide(BigDecimal.valueOf(30L), scale, rounding)
    while (day.isBefore(end)) {
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
