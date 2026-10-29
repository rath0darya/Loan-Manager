package com.fundniti

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
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Apps
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
import androidx.compose.ui.window.DialogProperties
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.res.painterResource
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

private fun modernTypography(): androidx.compose.material3.Typography {
    val base = androidx.compose.material3.Typography()
    val font = FontFamily.SansSerif
    val displayFont = FontFamily.Serif
    return base.copy(
        displayLarge = base.displayLarge.copy(fontFamily = displayFont),
        displayMedium = base.displayMedium.copy(fontFamily = displayFont),
        displaySmall = base.displaySmall.copy(fontFamily = displayFont),
        headlineLarge = base.headlineLarge.copy(fontFamily = displayFont),
        headlineMedium = base.headlineMedium.copy(fontFamily = displayFont),
        headlineSmall = base.headlineSmall.copy(fontFamily = displayFont),
        titleLarge = base.titleLarge.copy(fontFamily = font),
        titleMedium = base.titleMedium.copy(fontFamily = font),
        titleSmall = base.titleSmall.copy(fontFamily = font),
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
    Backup("Backup", Icons.Filled.Settings),
    PaddySeasons("Paddy seasons", Icons.Filled.BarChart),
    More("More", Icons.Filled.Apps)
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
    val appColorScheme = when {
        darkTheme -> androidx.compose.material3.darkColorScheme(
            primary = Color(0xFFB4C998), onPrimary = Color(0xFF243526),
            primaryContainer = Color(0xFF41503A), onPrimaryContainer = Color(0xFFE0E9CE),
            secondary = Color(0xFFE1BD75), onSecondary = Color(0xFF35280F),
            secondaryContainer = Color(0xFF55451F), onSecondaryContainer = Color(0xFFF4DCA7),
            tertiary = Color(0xFF9FC9F4), onTertiary = Color(0xFF15324D),
            background = Color(0xFF191A16), onBackground = Color(0xFFE9E1CE),
            surface = Color(0xFF24251F), onSurface = Color(0xFFE9E1CE),
            surfaceVariant = Color(0xFF34342B), onSurfaceVariant = Color(0xFFC7BFA9),
            outline = Color(0xFF9C9278), outlineVariant = Color(0xFF514D3F)
        )
        else -> androidx.compose.material3.lightColorScheme(
            primary = Color(0xFF315B43), onPrimary = Color(0xFFFFF8E9),
            primaryContainer = Color(0xFFD6E0C8), onPrimaryContainer = Color(0xFF1C3828),
            secondary = Color(0xFF9A6B21), onSecondary = Color.White,
            secondaryContainer = Color(0xFFF2D99F), onSecondaryContainer = Color(0xFF3B2A10),
            tertiary = Color(0xFF386B9E), onTertiary = Color.White,
            background = Color(0xFFE9E1CE), onBackground = Color(0xFF29291F),
            surface = Color(0xFFF7F1E3), onSurface = Color(0xFF29291F),
            surfaceVariant = Color(0xFFE2D8C1), onSurfaceVariant = Color(0xFF5D5A4B),
            outline = Color(0xFF8B8067), outlineVariant = Color(0xFFC9BDA4)
        )
    }
    val db = remember { LoanDatabase.get(context) }
    val dao = db.loanDao()
    val loans by dao.observeLoans().collectAsState(initial = emptyList())
    val payments by dao.observePayments().collectAsState(initial = emptyList())
    val paddyRecords by dao.observeSeasonPaddy().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var page by remember { mutableStateOf(AppPage.Home) }
    var themeMenuExpanded by remember { mutableStateOf(false) }
    var showExitConfirmation by remember { mutableStateOf(false) }
    var showAddLoan by remember { mutableStateOf(false) }
    var editLoan by remember { mutableStateOf<LoanEntity?>(null) }
    var paymentFor by remember { mutableStateOf<LoanEntity?>(null) }
    var editingPayment by remember { mutableStateOf<PaymentEntity?>(null) }
    var reopenStatementAfterPayment by remember { mutableStateOf(false) }
    var deleteConfirmPayment by remember { mutableStateOf<PaymentEntity?>(null) }
    var detailLoan by remember { mutableStateOf<LoanEntity?>(null) }
    var deleteConfirmLoan by remember { mutableStateOf<LoanEntity?>(null) }
    var confirmLoanDeleteFinal by remember { mutableStateOf(false) }
    var confirmPaymentDeleteFinal by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }
    BackHandler(enabled = !appLocked) {
        when {
            deleteConfirmPayment != null -> { deleteConfirmPayment = null; confirmPaymentDeleteFinal = false }
            deleteConfirmLoan != null -> { deleteConfirmLoan = null; confirmLoanDeleteFinal = false }
            editingPayment != null -> editingPayment = null
            paymentFor != null -> paymentFor = null
            showAddLoan || editLoan != null -> { showAddLoan = false; editLoan = null }
            detailLoan != null -> detailLoan = null
            page in listOf(AppPage.Calculators, AppPage.Statistics, AppPage.Backup, AppPage.PaddySeasons) -> page = AppPage.More
            page == AppPage.More -> page = AppPage.Home
            page != AppPage.Home -> page = AppPage.Home
            else -> showExitConfirmation = true
        }
    }
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
                backupStatus = "Restored ${dao.allLoans().size} loans, ${dao.allPayments().size} repayments and ${dao.allSeasonPaddy().size} paddy records. Device backup refreshed."
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
                val existingPaddy = dao.allSeasonPaddy()
                val status = if (existingLoans.isEmpty() && existingPayments.isEmpty() && existingPaddy.isEmpty()) {
                    val saved = DurableBackup.readAutomatic(context)
                    if (saved != null) {
                        restoreBackupJson(db, dao, saved)
                        "Recovered ${dao.allLoans().size} loans and ${dao.allPayments().size} repayments from the Android folder."
                    } else {
                        "No existing Android folder backup was found yet."
                    }
                } else {
                    "Found ${existingLoans.size} loans, ${existingPayments.size} repayments and ${existingPaddy.size} paddy records on this device."
                }
                if (existingLoans.isNotEmpty() || existingPayments.isNotEmpty() || existingPaddy.isNotEmpty()) {
                    DurableBackup.writeAutomatic(context, createBackupJson(dao))
                }
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

    MaterialTheme(colorScheme = appColorScheme, typography = modernTypography()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                            androidx.compose.foundation.Image(
                                painter = painterResource(id = R.drawable.ic_app_logo),
                                contentDescription = "FundNiti logo",
                                modifier = Modifier.size(38.dp)
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                                Text("FundNiti", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.titleMedium)
                                Text(page.label, style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { themeMenuExpanded = true }) {
                            val themeIcon = when (themeMode) {
                                "LIGHT" -> Icons.Filled.LightMode
                                "DARK" -> Icons.Filled.DarkMode
                                else -> Icons.Filled.BrightnessAuto
                            }
                            Icon(themeIcon, tint = MaterialTheme.colorScheme.primary,
                                contentDescription = "Appearance: ${themeMode.lowercase()}")
                        }
                        DropdownMenu(expanded = themeMenuExpanded, onDismissRequest = { themeMenuExpanded = false }) {
                            listOf("SYSTEM" to "System", "LIGHT" to "Light", "DARK" to "Dark").forEach { (mode, label) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    leadingIcon = {
                                        Icon(when (mode) {
                                            "SYSTEM" -> Icons.Filled.BrightnessAuto
                                            "LIGHT" -> Icons.Filled.LightMode
                                            else -> Icons.Filled.DarkMode
                                        }, contentDescription = null)
                                    },
                                    trailingIcon = if (themeMode == mode) ({ Text("✓", color = MaterialTheme.colorScheme.primary) }) else null,
                                    onClick = {
                                        themeMode = mode
                                        preferences.edit().putString("theme_mode", mode).apply()
                                        themeMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                )
            },
            bottomBar = {
                Column {
                    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                        listOf(AppPage.Home, AppPage.Loans, AppPage.People, AppPage.More).forEach { item ->
                            NavigationBarItem(
                                selected = if (item == AppPage.More) page in listOf(AppPage.More, AppPage.Calculators, AppPage.Statistics, AppPage.Backup, AppPage.PaddySeasons) else page == item,
                                onClick = { page = item },
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label) }
                            )
                        }
                    }
                }
            },
            snackbarHost = { androidx.compose.material3.SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddLoan = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add a new loan")
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
                    Modifier.padding(padding), loans, payments, ::balance, totalOutstanding, totalPrincipal, paddyRecords
                )
                AppPage.PaddySeasons -> PaddySeasonScreen(
                    modifier = Modifier.padding(padding), records = paddyRecords,
                    onSave = { record -> scope.launch { if (record.id == 0L) dao.addSeasonPaddy(record) else dao.updateSeasonPaddy(record) } },
                    onDelete = { record -> scope.launch { dao.deleteSeasonPaddy(record.id) } }
                )
                AppPage.More -> MoreScreen(
                    modifier = Modifier.padding(padding),
                    onNavigate = { destination -> page = destination }
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
                                    Triple(file.toString(), root.getJSONArray("loans").length(), root.getJSONArray("payments").length() + (root.optJSONArray("seasonPaddy")?.length() ?: 0))
                                }
                                backupStatus = "Backup refreshed with ${result.second} loans and ${result.third} repayments + paddy records. File: ${result.first}"
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
                                    val existingPaddy = dao.allSeasonPaddy()
                                    if (existingLoans.isNotEmpty() || existingPayments.isNotEmpty() || existingPaddy.isNotEmpty()) {
                                        "Recovery skipped: this app already has ${existingLoans.size} loans, ${existingPayments.size} repayments and ${existingPaddy.size} paddy records. Current records were not replaced."
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
        PaymentDialog(existing = null, onDismiss = {
            paymentFor = null
            if (reopenStatementAfterPayment) { detailLoan = loan; reopenStatementAfterPayment = false }
        }, onSave = { amount, paidAt, method, reference, note ->
            scope.launch {
                dao.addPayment(PaymentEntity(loanId = loan.id, amount = amount,
                    paidAt = paidAt, note = note.trim(), paymentMethod = method, referenceNumber = reference.trim()))
                withContext(Dispatchers.IO) { DurableBackup.writeAutomatic(context, createBackupJson(dao)) }
                paymentFor = null
                if (reopenStatementAfterPayment) { detailLoan = loan; reopenStatementAfterPayment = false }
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
            onRecordPayment = { reopenStatementAfterPayment = true; paymentFor = loan; detailLoan = null },
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
    if (showExitConfirmation) {
        AlertDialog(
            onDismissRequest = { showExitConfirmation = false },
            title = { Text("Close FundNiti?") },
            text = { Text("Your saved records are safe. You can return to this screen anytime.") },
            confirmButton = { TextButton(onClick = {
                showExitConfirmation = false
                (context as? Activity)?.finish()
            }) { Text("Close app") } },
            dismissButton = { TextButton(onClick = { showExitConfirmation = false }) { Text("Stay") } }
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
    val grossTotal = loan.principal + interest
    val paid = payments.sumOf { it.amount }
    val start = Instant.ofEpochMilli(loan.startDate).atZone(ZoneId.systemDefault()).toLocalDate()
    val today = LocalDate.now()
    val end = if (today.isBefore(start)) start else today
    val period = Period.between(start, end)
    val months = period.years * 12 + period.months
    val rows = mutableListOf(
        LoanReportLine("People", listOf(loan.personName, loan.personName2, loan.personName3).filter { it.isNotBlank() }.joinToString(" → ").ifBlank { "Not specified" }),
        LoanReportLine("Loan direction", when (loan.direction) { "LENT" -> "You lent money"; "BORROWED_FOR_OTHER" -> "Borrowed for someone else"; else -> "You borrowed money" }),
        LoanReportLine("LOAN BREAKDOWN", "", true),
        LoanReportLine("Loan date", formatDate(loan.startDate)),
        LoanReportLine("Duration", "$months months, ${period.days} days (${ChronoUnit.DAYS.between(start, end)} days total)"),
        LoanReportLine("Principal amount", money(loan.principal)),
        LoanReportLine("Interest rate", "${loan.monthlyRate}% per month"),
        LoanReportLine("Accrued interest", money(interest)),
        LoanReportLine("Total amount (principal + interest)", money(grossTotal)),
        LoanReportLine("Payments recorded", money(paid)),
        LoanReportLine("Amount still due", money(outstanding)),
        LoanReportLine("Status", if (outstanding <= 0.009) "Fully paid" else "Still to be paid")
    )
    loan.dueDate?.let { rows.add(LoanReportLine("Reminder date", formatDate(it))) }
    if (loan.note.isNotBlank()) rows.add(LoanReportLine("Loan notes", loan.note))
    rows.add(LoanReportLine("PAYMENT HISTORY  ·  ${payments.size} records", "", true))
    if (payments.isEmpty()) rows.add(LoanReportLine("Payments", "No payments recorded"))
    payments.forEachIndexed { index, payment ->
        rows.add(LoanReportLine("Payment ${index + 1}  ·  ${money(payment.amount)}", "", true))
        rows.add(LoanReportLine("Date and time", formatDate(payment.paidAt) + " · " + DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(payment.paidAt))))
        rows.add(LoanReportLine("Method", payment.paymentMethod))
        if (payment.referenceNumber.isNotBlank()) rows.add(LoanReportLine(if (payment.paymentMethod == "UPI") "UPI UTR number" else "Transaction reference", payment.referenceNumber))
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
    val allRows = loanReportLines(loan, payments, outstanding)
    val imageRows = loanReportLines(loan, emptyList(), outstanding)
        .filterNot { it.label.startsWith("PAYMENT HISTORY") }
        .toMutableList()
    imageRows.add(LoanReportLine("RECENT PAYMENTS  ·  ${payments.size} total", "", true))
    if (payments.isEmpty()) imageRows.add(LoanReportLine("Payments", "No payments recorded"))
    payments.takeLast(5).forEachIndexed { index, payment ->
        val paymentNumber = payments.size - minOf(5, payments.size) + index + 1
        imageRows.add(LoanReportLine("Payment $paymentNumber  ·  ${money(payment.amount)}", "", true))
        imageRows.add(LoanReportLine("Date and method", "${formatDate(payment.paidAt)} · ${payment.paymentMethod}"))
        if (payment.referenceNumber.isNotBlank()) imageRows.add(LoanReportLine("Reference", payment.referenceNumber))
    }
    val rows = if (asPdf) allRows else imageRows
    val deepGreen = android.graphics.Color.rgb(8, 62, 47)
    val emerald = android.graphics.Color.rgb(8, 121, 90)
    val mint = android.graphics.Color.rgb(225, 244, 234)
    val gold = android.graphics.Color.rgb(234, 196, 111)
    val ink = android.graphics.Color.rgb(26, 44, 36)
    val muted = android.graphics.Color.rgb(91, 109, 98)
    val paper = android.graphics.Color.rgb(246, 249, 246)
    val white = android.graphics.Color.WHITE
    val width = 1200
    val headerHeight = 250f
    val summaryHeight = 250f
    val left = 52f
    val right = width - 52f
    val rowHeights = rows.map { if (it.section) 70f else 78f }
    val rowHeight = rowHeights.sum()
    val imageHeight = (headerHeight + summaryHeight + rowHeight + 150f).toInt().coerceAtLeast(1000)
    val bitmap = android.graphics.Bitmap.createBitmap(width, imageHeight, android.graphics.Bitmap.Config.ARGB_8888)
    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
    val interest = accruedInterest(loan.principal, loan.monthlyRate, loan.startDate, System.currentTimeMillis())
    val grossTotal = loan.principal + interest

    fun drawHeader(canvas: android.graphics.Canvas, compact: Boolean = false) {
        paint.shader = android.graphics.LinearGradient(0f, 0f, width.toFloat(), headerHeight,
            intArrayOf(deepGreen, emerald), null, android.graphics.Shader.TileMode.CLAMP)
        canvas.drawRect(0f, 0f, width.toFloat(), headerHeight, paint)
        paint.shader = null
        paint.color = gold
        canvas.drawRoundRect(48f, 44f, 58f, 202f, 5f, 5f, paint)
        try {
            val logo = context.packageManager.getApplicationIcon(context.packageName)
            logo.setBounds(82, 46, 172, 136)
            logo.draw(canvas)
        } catch (_: Exception) {}
        paint.color = white
        paint.typeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.BOLD)
        paint.textSize = 52f
        canvas.drawText("FundNiti", 194f, 96f, paint)
        paint.textSize = 24f
        paint.typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
        canvas.drawText("LOAN ACCOUNT STATEMENT", 194f, 137f, paint)
        paint.color = android.graphics.Color.rgb(210, 233, 220)
        paint.textSize = 20f
        paint.typeface = android.graphics.Typeface.DEFAULT
        canvas.drawText(if (compact) "Private lending, clearly organised" else "A clear record of principal, interest and repayments", 82f, 194f, paint)
        paint.textAlign = android.graphics.Paint.Align.RIGHT
        paint.textSize = 18f
        canvas.drawText(formatDate(System.currentTimeMillis()), right, 48f, paint)
        paint.textAlign = android.graphics.Paint.Align.LEFT
    }

    fun drawSummary(canvas: android.graphics.Canvas, top: Float) {
        paint.color = white
        canvas.drawRoundRect(left, top, right, top + summaryHeight - 18f, 26f, 26f, paint)
        paint.color = muted
        paint.textSize = 19f
        paint.typeface = android.graphics.Typeface.DEFAULT_BOLD
        canvas.drawText("CURRENT AMOUNT DUE", left + 28f, top + 42f, paint)
        paint.color = deepGreen
        paint.textSize = 54f
        paint.typeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.BOLD)
        canvas.drawText(money(outstanding), left + 28f, top + 106f, paint)
        val labels = listOf("PRINCIPAL", "ACCRUED INTEREST", "TOTAL  ·  PRINCIPAL + INTEREST")
        val values = listOf(money(loan.principal), money(interest), money(grossTotal))
        val gap = 14f
        val cardWidth = (right - left - 56f - gap * 2f) / 3f
        labels.forEachIndexed { index, label ->
            val x = left + 28f + index * (cardWidth + gap)
            val tileColor = if (index == 2) mint else paper
            paint.color = tileColor
            canvas.drawRoundRect(x, top + 132f, x + cardWidth, top + summaryHeight - 38f, 15f, 15f, paint)
            paint.color = if (index == 2) emerald else muted
            paint.textSize = 15f
            paint.typeface = android.graphics.Typeface.DEFAULT_BOLD
            canvas.drawText(labels[index], x + 15f, top + 160f, paint)
            paint.color = ink
            paint.textSize = if (index == 2) 23f else 22f
            paint.typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
            canvas.drawText(values[index], x + 15f, top + 194f, paint)
        }
    }

    fun drawRows(canvas: android.graphics.Canvas, startIndex: Int, startY: Float, bottomY: Float): Pair<Int, Float> {
        var index = startIndex
        var y = startY
        while (index < rows.size) {
            val row = rows[index]
            val h = rowHeights[index]
            if (y + h > bottomY) break
            if (row.section) {
                paint.color = deepGreen
                canvas.drawRoundRect(left, y, right, y + h - 8f, 18f, 18f, paint)
                paint.color = gold
                canvas.drawRoundRect(left + 18f, y + 17f, left + 25f, y + h - 25f, 4f, 4f, paint)
                paint.color = white
                paint.textSize = 22f
                paint.typeface = android.graphics.Typeface.DEFAULT_BOLD
                canvas.drawText(row.label, left + 43f, y + 40f, paint)
            } else {
                val emphasized = row.label.contains("total amount", true) || row.label.contains("still due", true)
                paint.color = if (emphasized) mint else white
                canvas.drawRoundRect(left, y, right, y + h - 7f, 16f, 16f, paint)
                paint.color = if (emphasized) emerald else muted
                paint.textSize = 18f
                paint.typeface = android.graphics.Typeface.DEFAULT
                canvas.drawText(row.label.take(72), left + 22f, y + 29f, paint)
                paint.color = ink
                paint.textSize = if (emphasized) 25f else 22f
                paint.typeface = if (emphasized) android.graphics.Typeface.DEFAULT_BOLD else android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
                canvas.drawText(row.value.take(96), left + 22f, y + 59f, paint)
            }
            y += h
            index++
        }
        return index to y
    }

    try {
        val canvas = android.graphics.Canvas(bitmap)
        canvas.drawColor(paper)
        drawHeader(canvas)
        drawSummary(canvas, headerHeight + 20f)
        val (imageEndIndex, endY) = drawRows(canvas, 0, headerHeight + summaryHeight + 12f, imageHeight - 105f)
        if (imageEndIndex < rows.size) {
            paint.color = muted
            paint.textSize = 19f
            paint.typeface = android.graphics.Typeface.DEFAULT
            canvas.drawText("Continued in the PDF report for the complete payment history.", left + 4f, endY + 30f, paint)
        }
        paint.color = deepGreen
        canvas.drawRoundRect(left, imageHeight - 76f, right, imageHeight - 24f, 16f, 16f, paint)
        paint.color = white
        paint.textSize = 17f
        paint.typeface = android.graphics.Typeface.DEFAULT_BOLD
        canvas.drawText("FundNiti  ·  Personal loan statement", left + 20f, imageHeight - 43f, paint)
        paint.textSize = 15f
        paint.typeface = android.graphics.Typeface.DEFAULT
        canvas.drawText("For reference only. Verify details before sharing.", left + 20f, imageHeight - 22f, paint)

        val dir = java.io.File(context.cacheDir, "shared_reports").apply { mkdirs() }
        val file = java.io.File(dir, if (asPdf) "FundNiti-Loan-Report.pdf" else "FundNiti-Loan-Report.png")
        if (asPdf) {
            val doc = android.graphics.pdf.PdfDocument()
            val pageWidth = 595
            val pageHeight = 842
            val scale = pageWidth.toFloat() / width
            val logicalPageHeight = pageHeight / scale
            var rowIndex = 0
            var pageNo = 1
            do {
                val page = doc.startPage(android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNo).create())
                val pageCanvas = page.canvas
                pageCanvas.save()
                pageCanvas.scale(scale, scale)
                pageCanvas.drawColor(paper)
                drawHeader(pageCanvas, compact = true)
                drawSummary(pageCanvas, headerHeight + 12f)
                val (nextIndex, endRowY) = drawRows(pageCanvas, rowIndex, headerHeight + summaryHeight + 4f, logicalPageHeight - 90f)
                if (nextIndex == rowIndex && rowIndex < rows.size) {
                    pageCanvas.restore()
                    doc.finishPage(page)
                    doc.close()
                    error("Could not fit a report row on the PDF page")
                }
                rowIndex = nextIndex
                paint.color = deepGreen
                pageCanvas.drawRoundRect(38f, logicalPageHeight - 76f, width - 38f, logicalPageHeight - 28f, 15f, 15f, paint)
                paint.color = white
                paint.textSize = 17f
                paint.typeface = android.graphics.Typeface.DEFAULT
                pageCanvas.drawText("FundNiti  ·  Verify details before use", 56f, logicalPageHeight - 46f, paint)
                paint.textAlign = android.graphics.Paint.Align.RIGHT
                pageCanvas.drawText("PAGE $pageNo", width - 56f, logicalPageHeight - 46f, paint)
                paint.textAlign = android.graphics.Paint.Align.LEFT
                if (rowIndex < rows.size) {
                    paint.color = muted
                    paint.textSize = 14f
                    pageCanvas.drawText("Continued on next page", width - 270f, endRowY - 8f, paint)
                }
                pageCanvas.restore()
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
            putExtra(android.content.Intent.EXTRA_SUBJECT, "FundNiti — Loan statement")
            putExtra(android.content.Intent.EXTRA_TEXT, "FundNiti loan statement · Principal plus accrued interest: ${money(grossTotal)} · Current amount due: ${money(outstanding)}. For reference only; verify details before sharing.")
            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(android.content.Intent.createChooser(intent, if (asPdf) "Share loan statement PDF" else "Share loan statement image"))
    } catch (e: Exception) {
        Toast.makeText(context, "Could not create report: " + (e.message ?: "unknown error"), Toast.LENGTH_LONG).show()
    } finally {
        bitmap.recycle()
    }
}

@Composable
private fun ReportMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
     , elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface, maxLines = 2)
        }
    }
}

@Composable
private fun LoanDetailsDialog(
    loan: LoanEntity,
    payments: List<PaymentEntity>,
    outstanding: Double,
    onDismiss: () -> Unit,
    onRecordPayment: () -> Unit,
    onEditPayment: (PaymentEntity) -> Unit,
    onDeletePayment: (PaymentEntity) -> Unit
) {
    val context = LocalContext.current
    val interest = accruedInterest(loan.principal, loan.monthlyRate, loan.startDate, System.currentTimeMillis())
    val totalAmount = loan.principal + interest
    val totalPaid = payments.sumOf { it.amount }
    val loanStartDate = Instant.ofEpochMilli(loan.startDate).atZone(ZoneId.systemDefault()).toLocalDate()
    val today = LocalDate.now()
    val durationEndDate = if (today.isBefore(loanStartDate)) loanStartDate else today
    val totalDays = ChronoUnit.DAYS.between(loanStartDate, durationEndDate)
    val elapsedPeriod = Period.between(loanStartDate, durationEndDate)
    val totalMonths = elapsedPeriod.years * 12 + elapsedPeriod.months
    val people = listOf(loan.personName, loan.personName2, loan.personName3).filter { it.isNotBlank() }.joinToString(" → ")
    val direction = when (loan.direction) {
        "LENT" -> "You lent money"
        "BORROWED_FOR_OTHER" -> "Borrowed for someone else"
        else -> "You borrowed money"
    }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.96f).fillMaxHeight(0.94f),
            shape = RoundedCornerShape(30.dp),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 8.dp
        ) {
            Column {
                Row(
                    Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(start = 20.dp, end = 12.dp, top = 15.dp, bottom = 15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(13.dp)
                ) {
                    Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                        Icon(Icons.Filled.AccountBalance, contentDescription = null,
                            modifier = Modifier.padding(11.dp).size(26.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Loan statement", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(people.ifBlank { "Loan details" }, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                    }
                    TextButton(onClick = onDismiss) { Text("Close") }
                }
                Column(
                    Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Card(shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary) , elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
                        Column(Modifier.fillMaxWidth().padding(22.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                            Text("CURRENT AMOUNT DUE", style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.78f), fontWeight = FontWeight.Bold)
                            Text(money(outstanding), style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                            Text("${if (outstanding <= 0.009) "Fully paid" else direction} · ${formatDate(loan.startDate)}",
                                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.86f))
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ReportMetric("Principal", money(loan.principal), Modifier.weight(1f))
                        ReportMetric("Accrued interest", money(interest), Modifier.weight(1f))
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.22f), RoundedCornerShape(22.dp)),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                     , elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
                        Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Text("TOTAL AMOUNT  ·  PRINCIPAL + INTEREST", style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text(money(totalAmount), style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text("${money(loan.principal)} principal + ${money(interest)} accrued interest",
                                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                    GlassPanel("Loan details", "Key dates, rate and saved information") {
                        DetailLine("Loan direction", direction)
                        DetailLine("Loan date", formatDate(loan.startDate))
                        DetailLine("Duration", "$totalMonths months, ${elapsedPeriod.days} days · $totalDays days total")
                        DetailLine("Interest rate", "${loan.monthlyRate}% per month")
                        DetailLine("Payments recorded", money(totalPaid))
                        DetailLine("Amount still due", money(outstanding))
                        DetailLine("Status", if (outstanding <= 0.009) "Fully paid" else "Outstanding")
                        loan.dueDate?.let { DetailLine("Reminder date", formatDate(it)) }
                        if (loan.note.isNotBlank()) DetailLine("Notes", loan.note)
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp)),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                     , elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
                        Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text("Payment activity", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text("${payments.size} recorded ${if (payments.size == 1) "payment" else "payments"}",
                                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(money(totalPaid), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary)
                            }
                            if (payments.isEmpty()) {
                                Text("No repayments have been recorded yet.", style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            payments.forEachIndexed { index, payment ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(18.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                 , elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
                                    Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("Payment ${index + 1}", modifier = Modifier.weight(1f),
                                                style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                            Text(money(payment.amount), style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        }
                                        DetailLine("Date", formatDate(payment.paidAt) + " · " + DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(payment.paidAt)))
                                        DetailLine("Method", payment.paymentMethod)
                                        if (payment.referenceNumber.isNotBlank()) DetailLine(if (payment.paymentMethod == "UPI") "UPI UTR" else "Reference", payment.referenceNumber)
                                        if (payment.note.isNotBlank()) DetailLine("Note", payment.note)
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            TextButton(onClick = { onEditPayment(payment) }) { Text("Edit payment") }
                                            TextButton(onClick = { onDeletePayment(payment) }) { Text("Delete") }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Row(
                    Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(9.dp), verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = onRecordPayment, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.Payments, null, Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text("Record")
                    }
                    androidx.compose.material3.OutlinedButton(onClick = { shareLoanReport(context, loan, payments, outstanding, true) }, modifier = Modifier.weight(1f)) {
                        Text("Share PDF")
                    }
                    androidx.compose.material3.OutlinedButton(onClick = { shareLoanReport(context, loan, payments, outstanding, false) }, modifier = Modifier.weight(1f)) {
                        Text("Share image")
                    }
                }
            }
        }
    }
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
    val heroDecoration = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.09f)
    LazyColumn(modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Column(Modifier.padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text("Good to see you", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Text("Your finances, at a glance", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("A calm, clear view of every loan and repayment.", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(28.dp), modifier = Modifier.fillMaxWidth() , elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
                Box {
                    Canvas(Modifier.align(Alignment.TopEnd).size(150.dp)) {
                        drawCircle(heroDecoration, size.minDimension * 0.48f, androidx.compose.ui.geometry.Offset(size.width * 0.82f, size.height * 0.08f))
                        drawCircle(heroDecoration, size.minDimension * 0.27f, androidx.compose.ui.geometry.Offset(size.width * 0.82f, size.height * 0.08f))
                    }
                    Column(Modifier.fillMaxWidth().padding(22.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("PORTFOLIO OVERVIEW", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.78f),
                            style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Text("Current outstanding", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.92f),
                            style = MaterialTheme.typography.bodyMedium)
                        Text(money(outstanding), color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                        Text("${loans.size} active loan ${if (loans.size == 1) "record" else "records"}",
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.78f), style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(2.dp))
                        androidx.compose.material3.TextButton(onClick = onViewLoans) {
                            Text("Review loans  →", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
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
        item {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text("Recent loans", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Tap a record to open its complete statement.", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        if (loans.isEmpty()) item { EmptyState("Your first loan starts here", "Tap the + button to add a clear, private record.") }
        items(loans.take(5), key = { it.id }) {
            loan -> LoanCard(loan, balance(loan), payments.filter { it.loanId == loan.id }.sumOf { it.amount }, {}, null, null, { onViewDetails(loan) }, compact = true)
        }
        item { androidx.compose.material3.OutlinedButton(onClick = onViewLoans, modifier = Modifier.fillMaxWidth()) { Text("View all loan records") } }
        item { Text("Interest uses simple monthly calculations on the original principal.",
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 10.dp)) }
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
    loan: LoanEntity, balance: Double, paid: Double, onAddPayment: () -> Unit, onEdit: (() -> Unit)?, onDelete: (() -> Unit)?,
    onViewDetails: (() -> Unit)? = null, compact: Boolean = false
) {
    Card(onClick = { onViewDetails?.invoke() }, enabled = onViewDetails != null,
        modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(Modifier.padding(if (compact) 15.dp else 16.dp), verticalArrangement = Arrangement.spacedBy(if (compact) 5.dp else 7.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(Modifier.weight(1f)) {
                    Text(listOf(loan.personName, loan.personName2, loan.personName3).filter { it.isNotBlank() }.joinToString(" → "),
                        style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, maxLines = 2)
                    if (!compact) Text(when (loan.direction) { "LENT" -> "You lent money"; "BORROWED_FOR_OTHER" -> "Borrowed for someone else"; else -> "You borrowed money" },
                        style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                }
                Column(horizontalAlignment = Alignment.End, modifier = Modifier.width(if (compact) 112.dp else 150.dp)) {
                    Text(money(balance), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                        color = if (compact) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                    Text(if (compact) "Outstanding" else amountInWords(balance), style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = if (compact) 1 else 2)
                }
            }
            if (compact) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.primaryContainer) {
                        Text(when (loan.direction) { "LENT" -> "LENT"; "BORROWED_FOR_OTHER" -> "FOR SOMEONE"; else -> "BORROWED" },
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    Text("Started ${formatDate(loan.startDate)}", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (paid > 0.0) Text("${money(paid)} paid so far", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
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
}

@Composable
private fun CalculatorsScreen(modifier: Modifier) {
    var selected by remember { mutableStateOf("INTEREST") }
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Card(modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(26.dp)),
            shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) , elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
            Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(15.dp)) {
                Surface(shape = RoundedCornerShape(18.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                    Icon(Icons.Filled.Calculate, null, Modifier.padding(13.dp).size(26.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Calculators", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Fast estimates for lending, dates and harvests.",
                        style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        Text("CHOOSE A TOOL", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            androidx.compose.material3.FilterChip(selected = selected == "INTEREST",
                onClick = { selected = "INTEREST" }, label = { Text("Interest") })
            androidx.compose.material3.FilterChip(selected = selected == "TENURE",
                onClick = { selected = "TENURE" }, label = { Text("Tenure") })
            androidx.compose.material3.FilterChip(selected = selected == "PADDY",
                onClick = { selected = "PADDY" }, label = { Text("Paddy") })
        }
        when (selected) {
            "INTEREST" -> InterestCalculatorCard()
            "TENURE" -> TenureCalculatorCard()
            else -> PaddyCalculatorCard()
        }
        Text("Calculations are estimates only and do not change saved loan records.",
            style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
     , elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
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
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { shareCalculatorResult(context, "Interest Calculator", "INTEREST CALCULATION RESULT\nPrincipal: ${money(principal!!)}\nMonthly interest rate: ${rateText}%\nDuration: ${monthsText.toLongOrNull() ?: 0} months, ${daysText.toLongOrNull() ?: 0} days\nInterest amount: ${money(interest)}\nPrincipal + interest: ${money(principal + interest)}") }, modifier = Modifier.weight(1f)) { Text("Share image") }
                    Button(onClick = { shareCalculatorResultPdf(context, "Interest Calculator", "INTEREST CALCULATION RESULT\nPrincipal: ${money(principal!!)}\nMonthly interest rate: ${rateText}%\nDuration: ${monthsText.toLongOrNull() ?: 0} months, ${daysText.toLongOrNull() ?: 0} days\nInterest amount: ${money(interest)}\nPrincipal + interest: ${money(principal + interest)}") }, modifier = Modifier.weight(1f)) { Text("Share PDF") }
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
     , elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
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
                 , elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Calculated duration", style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer)
                        Text("$totalMonths months, ${period.days} days", style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        HorizontalResultLine("Total days", "$totalDays days")
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { shareCalculatorResult(context, "Tenure Calculator", "DATE DURATION RESULT\nFrom: ${formatDate(fromDateMillis)}\nTo: ${formatDate(toDateMillis)}\nDuration: $totalMonths months, ${period.days} days\nTotal days: $totalDays") }, modifier = Modifier.weight(1f)) { Text("Share image") }
                    Button(onClick = { shareCalculatorResultPdf(context, "Tenure Calculator", "DATE DURATION RESULT\nFrom: ${formatDate(fromDateMillis)}\nTo: ${formatDate(toDateMillis)}\nDuration: $totalMonths months, ${period.days} days\nTotal days: $totalDays") }, modifier = Modifier.weight(1f)) { Text("Share PDF") }
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

@Composable
private fun PaddyCalculatorCard() {
    val context = LocalContext.current
    var calculationType by remember { mutableStateOf("TYPE1") }
    var bagsText by remember { mutableStateOf("131") }
    var bagWeightText by remember { mutableStateOf("40") }
    var cuttingMode by remember { mutableStateOf("BAG") }
    var cuttingText by remember { mutableStateOf("1") }
    var priceText by remember { mutableStateOf("") }

    val bags = bagsText.toLongOrNull()
    val bagWeightKg = bagWeightText.replace(",", "").trim().toDoubleOrNull()
    val cuttingKg = cuttingText.replace(",", "").trim().toDoubleOrNull()
    val pricePerQuintal = priceText.replace(",", "").trim().toDoubleOrNull()
    val validBase = bags != null && bags > 0 && bags <= 1_000_000_000L &&
        bagWeightKg != null && bagWeightKg > 0 && bagWeightKg <= 100_000.0
    val grossQuintals = if (validBase) bags!!.toDouble() * bagWeightKg!! / 100.0 else null
    val validCutting = calculationType == "TYPE1" ||
        (cuttingKg != null && cuttingKg >= 0 && cuttingKg <= 100_000.0)
    val cuttingQuintals = if (calculationType == "TYPE2" && grossQuintals != null && validCutting) {
        if (cuttingMode == "BAG") bags!!.toDouble() * cuttingKg!! / 100.0
        else grossQuintals * cuttingKg!! / 100.0
    } else 0.0
    val finalQuintals = grossQuintals?.let { (it - cuttingQuintals).coerceAtLeast(0.0) }
    val validPrice = priceText.isBlank() || (pricePerQuintal != null && pricePerQuintal >= 0 && pricePerQuintal <= 1_000_000_000.0)
    val amount = if (validPrice && priceText.isNotBlank() && finalQuintals != null) finalQuintals * pricePerQuintal!! else null
    val valid = validBase && validCutting && validPrice &&
        (calculationType != "TYPE2" || (cuttingQuintals <= (grossQuintals ?: 0.0) + 1e-9))

    fun number(value: Double): String = DecimalFormat(
        "#,##0.##", DecimalFormatSymbols(Locale.US)
    ).format(value)

    val resultText = if (valid && grossQuintals != null && finalQuintals != null) buildString {
        appendLine("PADDY CALCULATION RESULT")
        appendLine("Calculation: ${if (calculationType == "TYPE1") "Type 1 — Gross paddy" else "Type 2 — Paddy after cutting"}")
        appendLine("Total bags: $bags")
        appendLine("Weight per bag: ${number(bagWeightKg!!)} kg")
        appendLine("Total paddy: ${number(grossQuintals)} quintals")
        if (calculationType == "TYPE2") {
            appendLine("Cutting method: ${if (cuttingMode == "BAG") "Per bag" else "Per quintal"}")
            appendLine("Cutting rate: ${number(cuttingKg!!)} kg ${if (cuttingMode == "BAG") "per bag" else "per quintal"}")
            appendLine("Total cutting: ${number(cuttingQuintals * 100.0)} kg (${number(cuttingQuintals)} quintals)")
            appendLine("Final paddy: ${number(finalQuintals)} quintals")
        }
        if (priceText.isNotBlank() && amount != null) {
            appendLine("Price per quintal: ${money(pricePerQuintal!!)}")
            appendLine("Total amount: ${money(amount)}")
        } else {
            appendLine("Price per quintal: Not provided")
            appendLine("Total amount: Not calculated")
        }
        append("Formula: ${bags} bags × ${number(bagWeightKg!!)} kg ÷ 100 = ${number(grossQuintals)} quintals")
    } else ""

    Card(
        Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
     , elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Paddy Calculator", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                "Convert bag weights into quintals and optionally calculate the amount at your price per quintal.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                androidx.compose.material3.FilterChip(
                    selected = calculationType == "TYPE1",
                    onClick = { calculationType = "TYPE1" },
                    label = { Text("Type 1 · Total") }
                )
                androidx.compose.material3.FilterChip(
                    selected = calculationType == "TYPE2",
                    onClick = { calculationType = "TYPE2" },
                    label = { Text("Type 2 · Cutting") }
                )
            }
            OutlinedTextField(
                value = bagsText,
                onValueChange = { bagsText = it.filter(Char::isDigit).take(10) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Total bags") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            OutlinedTextField(
                value = bagWeightText,
                onValueChange = { bagWeightText = it.filter { c -> c.isDigit() || c == '.' }.take(12) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Weight of single bag (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )
            if (calculationType == "TYPE2") {
                Text("Cutting unit", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    androidx.compose.material3.FilterChip(
                        selected = cuttingMode == "BAG",
                        onClick = { cuttingMode = "BAG" },
                        label = { Text("Per bag (kg)") }
                    )
                    androidx.compose.material3.FilterChip(
                        selected = cuttingMode == "QUINTAL",
                        onClick = { cuttingMode = "QUINTAL" },
                        label = { Text("Per quintal (kg)") }
                    )
                }
                OutlinedTextField(
                    value = cuttingText,
                    onValueChange = { cuttingText = it.filter { c -> c.isDigit() || c == '.' }.take(12) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(if (cuttingMode == "BAG") "Cutting for each bag (kg)" else "Cutting for each quintal (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }
            OutlinedTextField(
                value = priceText,
                onValueChange = { priceText = it.filter { c -> c.isDigit() || c == '.' || c == ',' }.take(16) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Price per quintal (₹, optional)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )

            if (valid && grossQuintals != null && finalQuintals != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                 , elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
                    Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Calculation result", style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer, fontWeight = FontWeight.Bold)
                        HorizontalResultLine("Total paddy", "${number(grossQuintals)} quintals")
                        if (calculationType == "TYPE2") {
                            HorizontalResultLine("Total cutting", "${number(cuttingQuintals * 100.0)} kg")
                            HorizontalResultLine("Final paddy", "${number(finalQuintals)} quintals")
                        }
                        if (priceText.isNotBlank() && amount != null) {
                            HorizontalResultLine("Price per quintal", money(pricePerQuintal!!))
                            HorizontalResultLine("Total amount", money(amount))
                        } else {
                            Text("Add a price per quintal to calculate the total amount.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                Text(
                    if (calculationType == "TYPE1")
                        "${bags} bags × ${number(bagWeightKg!!)} kg ÷ 100 = ${number(grossQuintals)} quintals"
                    else if (cuttingMode == "BAG")
                        "${number(grossQuintals)} quintals − (${bags} bags × ${number(cuttingKg!!)} kg ÷ 100) = ${number(finalQuintals)} quintals"
                    else
                        "${number(grossQuintals)} quintals − (${number(grossQuintals)} × ${number(cuttingKg!!)} kg ÷ 100) = ${number(finalQuintals)} quintals",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { shareCalculatorResult(context, "Paddy Calculator", resultText) },
                        modifier = Modifier.weight(1f)
                    ) { Text("Share image") }
                    Button(
                        onClick = { shareCalculatorResultPdf(context, "Paddy Calculator", resultText) },
                        modifier = Modifier.weight(1f)
                    ) { Text("Share PDF") }
                }
            } else {
                Text(
                    "Enter a positive bag count and bag weight. Cutting must be non-negative, and the final paddy weight cannot be negative. Price is optional.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun shareCalculatorResult(context: android.content.Context, title: String, result: String) {
    val navy = android.graphics.Color.rgb(18, 35, 66)
    val teal = android.graphics.Color.rgb(0, 126, 115)
    val gold = android.graphics.Color.rgb(224, 177, 83)
    val ink = android.graphics.Color.rgb(35, 48, 68)
    val muted = android.graphics.Color.rgb(104, 117, 136)
    val canvasWhite = android.graphics.Color.WHITE
    val pale = android.graphics.Color.rgb(244, 248, 252)
    val lines = result.lines().filter { it.isNotBlank() }
    val dataRows = lines.drop(1)
    val width = 1080
    val margin = 64f
    val headerHeight = 300f
    val rowHeights = dataRows.map { if (it.length > 58) 112f else 88f }
    val height = (headerHeight + 38f + rowHeights.sum() + 180f).toInt().coerceAtLeast(900)
    val bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888)
    try {
        val canvas = android.graphics.Canvas(bitmap)
        canvas.drawColor(canvasWhite)
        val p = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
        p.color = navy
        canvas.drawRect(0f, 0f, width.toFloat(), headerHeight, p)
        p.color = gold
        canvas.drawRoundRect(64f, 42f, 76f, 220f, 6f, 6f, p)
        try {
            val logo = context.packageManager.getApplicationIcon(context.packageName)
            logo.setBounds(100, 44, 190, 134)
            logo.draw(canvas)
        } catch (_: Exception) {}
        p.color = canvasWhite
        p.typeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.BOLD)
        p.textSize = 54f
        canvas.drawText("FundNiti", 100f, 180f, p)
        p.textSize = 32f
        p.typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
        canvas.drawText(title, 100f, 228f, p)
        p.color = android.graphics.Color.rgb(193, 211, 231)
        p.textSize = 21f
        p.typeface = android.graphics.Typeface.DEFAULT
        canvas.drawText("PROFESSIONAL CALCULATION SUMMARY", 100f, 268f, p)

        var y = headerHeight + 24f
        dataRows.forEachIndexed { index, line ->
            val h = rowHeights[index]
            val isEmphasis = line.contains("Total amount", true) ||
                line.contains("Final paddy", true) ||
                line.contains("Interest amount", true) ||
                line.contains("Principal + interest", true) ||
                line.contains("Calculated duration", true)
            p.color = if (isEmphasis) android.graphics.Color.rgb(231, 246, 241) else if (index % 2 == 0) pale else canvasWhite
            canvas.drawRoundRect(margin, y, width - margin, y + h - 10f, 22f, 22f, p)
            p.color = if (isEmphasis) teal else muted
            p.textSize = 22f
            p.typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
            val separator = line.indexOf(":")
            val label = if (separator > 0) line.substring(0, separator).trim() else line
            val value = if (separator > 0) line.substring(separator + 1).trim() else ""
            canvas.drawText(label.take(70), margin + 26f, y + 34f, p)
            p.color = if (isEmphasis) navy else ink
            p.textSize = if (isEmphasis) 32f else 27f
            p.typeface = if (isEmphasis) android.graphics.Typeface.DEFAULT_BOLD else android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
            val valueText = if (value.isBlank()) "" else value
            val maxChars = 54
            if (valueText.isNotBlank()) {
                val chunks = valueText.chunked(maxChars)
                chunks.take(2).forEachIndexed { j, chunk ->
                    canvas.drawText(chunk, margin + 26f, y + 68f + j * 30f, p)
                }
            }
            y += h
        }
        val footerTop = height - 132f
        p.color = navy
        canvas.drawRoundRect(38f, footerTop, width - 38f, height - 28f, 24f, 24f, p)
        p.color = gold
        canvas.drawRoundRect(64f, footerTop + 24f, 72f, height - 52f, 4f, 4f, p)
        p.color = canvasWhite
        p.textSize = 22f
        p.typeface = android.graphics.Typeface.DEFAULT_BOLD
        canvas.drawText("FundNiti  •  Personal finance, clearly organised", 94f, footerTop + 45f, p)
        p.textSize = 18f
        p.typeface = android.graphics.Typeface.DEFAULT
        canvas.drawText("For reference only  •  Please verify figures before use", 94f, footerTop + 78f, p)
        val directory = java.io.File(context.cacheDir, "shared_reports").apply { mkdirs() }
        val file = java.io.File(directory, "FundNiti-Calculator-Result.png")
        java.io.FileOutputStream(file).use { bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
        shareCalculatorFile(context, title, file, false)
    } catch (e: Exception) {
        Toast.makeText(context, "Could not share calculation: " + (e.message ?: "unknown error"), Toast.LENGTH_LONG).show()
    } finally {
        bitmap.recycle()
    }
}

private fun shareCalculatorResultPdf(context: android.content.Context, title: String, result: String) {
    val navy = android.graphics.Color.rgb(18, 35, 66)
    val teal = android.graphics.Color.rgb(0, 126, 115)
    val gold = android.graphics.Color.rgb(224, 177, 83)
    val ink = android.graphics.Color.rgb(35, 48, 68)
    val muted = android.graphics.Color.rgb(104, 117, 136)
    val pale = android.graphics.Color.rgb(244, 248, 252)
    val rows = result.lines().filter { it.isNotBlank() }.drop(1)
    val directory = java.io.File(context.cacheDir, "shared_reports").apply { mkdirs() }
    val file = java.io.File(directory, "FundNiti-Calculator-Result.pdf")
    val doc = android.graphics.pdf.PdfDocument()
    try {
        val pageWidth = 595
        val pageHeight = 842
        val margin = 32f
        var index = 0
        var pageNo = 1
        do {
            val page = doc.startPage(android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNo).create())
            val canvas = page.canvas
            canvas.drawColor(android.graphics.Color.WHITE)
            val p = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
            p.color = navy
            canvas.drawRoundRect(18f, 18f, pageWidth - 18f, 132f, 14f, 14f, p)
            p.color = gold
            canvas.drawRoundRect(34f, 34f, 39f, 112f, 3f, 3f, p)
            try {
                val drawable = context.packageManager.getApplicationIcon(context.packageName)
                val logo = android.graphics.Bitmap.createBitmap(36, 36, android.graphics.Bitmap.Config.ARGB_8888)
                val logoCanvas = android.graphics.Canvas(logo)
                drawable.setBounds(0, 0, 36, 36)
                drawable.draw(logoCanvas)
                canvas.drawBitmap(logo, 52f, 31f, p)
                logo.recycle()
            } catch (_: Exception) {}
            p.color = android.graphics.Color.WHITE
            p.typeface = android.graphics.Typeface.DEFAULT_BOLD
            p.textSize = 23f
            canvas.drawText("FundNiti", 96f, 53f, p)
            p.textSize = 14f
            p.typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
            canvas.drawText(title.take(48), 52f, 84f, p)
            p.color = android.graphics.Color.rgb(202, 218, 237)
            p.textSize = 8.5f
            canvas.drawText("PROFESSIONAL CALCULATION SUMMARY", 52f, 108f, p)

            var y = 148f
            val maxY = pageHeight - 62f
            while (index < rows.size) {
                val line = rows[index]
                val colon = line.indexOf(":")
                val label = if (colon > 0) line.substring(0, colon).trim() else line
                val value = if (colon > 0) line.substring(colon + 1).trim() else ""
                val chunks = value.chunked(52).ifEmpty { listOf("") }
                val cardHeight = maxOf(42f, 23f + chunks.size * 14f)
                if (y + cardHeight > maxY) break
                val emphasized = line.contains("Total amount", true) ||
                    line.contains("Final paddy", true) ||
                    line.contains("Interest amount", true) ||
                    line.contains("Principal + interest", true)
                p.color = if (emphasized) android.graphics.Color.rgb(231, 246, 241) else if (index % 2 == 0) pale else android.graphics.Color.WHITE
                canvas.drawRoundRect(margin, y, pageWidth - margin, y + cardHeight - 4f, 8f, 8f, p)
                p.color = if (emphasized) teal else muted
                p.textSize = 8.5f
                p.typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
                canvas.drawText(label.take(72), margin + 12f, y + 14f, p)
                p.color = if (emphasized) navy else ink
                p.textSize = if (emphasized) 12.5f else 10.5f
                p.typeface = if (emphasized) android.graphics.Typeface.DEFAULT_BOLD else android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
                chunks.take(3).forEachIndexed { j, chunk -> canvas.drawText(chunk, margin + 12f, y + 30f + j * 13f, p) }
                y += cardHeight + 5f
                index++
            }
            p.color = navy
            canvas.drawRoundRect(18f, pageHeight - 49f, pageWidth - 18f, pageHeight - 16f, 8f, 8f, p)
            p.color = android.graphics.Color.WHITE
            p.textSize = 8f
            p.typeface = android.graphics.Typeface.DEFAULT
            canvas.drawText("FundNiti  •  Verify figures before use", 30f, pageHeight - 29f, p)
            p.textAlign = android.graphics.Paint.Align.RIGHT
            canvas.drawText("Page $pageNo", pageWidth - 30f, pageHeight - 29f, p)
            p.textAlign = android.graphics.Paint.Align.LEFT
            doc.finishPage(page)
            pageNo++
        } while (index < rows.size)
        java.io.FileOutputStream(file).use { doc.writeTo(it) }
        shareCalculatorFile(context, title, file, true)
    } catch (e: Exception) {
        Toast.makeText(context, "Could not create calculator PDF: " + (e.message ?: "unknown error"), Toast.LENGTH_LONG).show()
    } finally {
        doc.close()
    }
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

private fun titleCaseName(name: String): String =
    name.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.joinToString(" ") { word ->
        word.lowercase(Locale.ROOT).replaceFirstChar { it.titlecase(Locale.ROOT) }
    }

@Composable
private fun PeopleScreen(modifier: Modifier, loans: List<LoanEntity>, balance: (LoanEntity) -> Double) {
    var searchQuery by remember { mutableStateOf("") }
    val people = loans.flatMap { loan ->
        listOf(loan.personName, loan.personName2, loan.personName3)
            .filter { it.isNotBlank() }
            .map { it.trim() to loan }
    }.groupBy({ it.first.lowercase(Locale.ROOT) }, { it }).toSortedMap()
    val visiblePeople = people.filter { (key, entries) ->
        key.contains(searchQuery.trim(), ignoreCase = true) ||
            titleCaseName(entries.firstOrNull()?.first ?: key).contains(searchQuery.trim(), ignoreCase = true)
    }
    val totalPeopleOutstanding = loans.distinctBy { it.id }.sumOf(balance)
    LazyColumn(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("People", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("A clear view of every relationship and the balance across their loan records.",
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item {
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer) , elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
                Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("PEOPLE TRACKED", style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(people.size.toString(), style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Total outstanding", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(money(totalPeopleOutstanding), style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }
        }
        item {
            OutlinedTextField(searchQuery, { searchQuery = it }, modifier = Modifier.fillMaxWidth(),
                label = { Text("Search people") }, leadingIcon = { Icon(Icons.Filled.Search, null) },
                singleLine = true)
        }
        if (visiblePeople.isEmpty()) item {
            if (people.isEmpty()) EmptyState("No people yet", "Your borrower and lender list will appear here.")
            else EmptyState("No matching people", "Try another name.")
        }
        items(visiblePeople.keys.toList(), key = { it }) { personKey ->
            val entries = visiblePeople[personKey].orEmpty()
            val name = titleCaseName(entries.firstOrNull()?.first ?: personKey)
            val records = entries.map { it.second }.distinctBy { it.id }
            val lentTotal = records.filter { it.direction == "LENT" }.sumOf(balance)
            val borrowedTotal = records.filter { it.direction == "BORROWED" || it.direction == "BORROWED_FOR_OTHER" }.sumOf(balance)
            val combinedTotal = lentTotal + borrowedTotal
            Card(Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                            Text(name.split(" ").take(2).mapNotNull { it.firstOrNull()?.toString() }.joinToString(""),
                                Modifier.padding(horizontal = 13.dp, vertical = 11.dp), fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                        Column(Modifier.weight(1f)) {
                            Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("${records.size} loan ${if (records.size == 1) "record" else "records"}",
                                style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(money(combinedTotal), style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    DetailLine("You lent", money(lentTotal))
                    DetailLine("You borrowed / for someone", money(borrowedTotal))
                    Text(amountInWords(combinedTotal), style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun StatisticsScreen(
    modifier: Modifier, loans: List<LoanEntity>, payments: List<PaymentEntity>,
    balance: (LoanEntity) -> Double, outstanding: Double, principal: Double,
    paddyRecords: List<SeasonPaddyEntity>
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
    val maxPersonName = maxPerson?.let { entry ->
        entry.value.distinctBy { it.id }.asSequence()
            .flatMap { loan -> sequenceOf(loan.personName, loan.personName2, loan.personName3) }
            .firstOrNull { it.trim().equals(entry.key, ignoreCase = true) }
    }?.let(::titleCaseName) ?: "—"
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
        Text("Statistics & reports", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("A simple overview of what you lent, what you borrowed, how much has been paid, and what is still due.",
            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        GlassPanel(title = "Your money at a glance", subtitle = "These are the main numbers to check first") {
            DetailLine("Still to be paid / repaid", money(outstanding))
            DetailLine("Original loan amounts", money(principal))
            DetailLine("Payments recorded so far", money(paymentSum))
            DetailLine("People in your records", "$peopleCount")
            Text("Outstanding means the estimated balance remaining after recorded payments and accrued interest.",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        StatCard("Total outstanding", money(outstanding), Modifier.fillMaxWidth())
        val totalInterestNow = loans.sumOf { accruedInterest(it.principal, it.monthlyRate, it.startDate, System.currentTimeMillis()) }
        GlassPanel(title = "How the balance is calculated", subtitle = "Principal is the original amount. Interest is added using each loan's saved rate and dates.") {
            DetailLine("Total principal", money(principal))
            ComparisonBar("Principal", principal, maxOf(principal, totalInterestNow, 1.0), MaterialTheme.colorScheme.primary)
            DetailLine("Interest added", money(totalInterestNow))
            ComparisonBar("Interest", totalInterestNow, maxOf(principal, totalInterestNow, 1.0), MaterialTheme.colorScheme.tertiary)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard("Money lent", money(lentBalance), Modifier.weight(1f))
            StatCard("Money borrowed", money(borrowedBalance), Modifier.weight(1f))
        }
        GlassPanel(title = "Who owes whom?", subtitle = "Compare the balance owed to you with the balance you owe others") {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                PortfolioDonut(lentBalance, borrowedBalance, Modifier.size(132.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                    LegendRow("Money lent", MaterialTheme.colorScheme.primary, money(lentBalance))
                    LegendRow("Money borrowed", MaterialTheme.colorScheme.tertiary, money(borrowedBalance))
                }
            }
        }
        GlassPanel(title = "Where payments are allocated", subtitle = "Estimated principal versus interest portions for the last 6 months") {
            StackedPaymentChart(stackedPayments)
            Text("This split is an estimate. The app records the full payment amount, not a lender-provided split.",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        GlassPanel(title = "Is the outstanding balance changing?", subtitle = "Estimated amount remaining at each of the last 6 month ends") {
            TrendLineChart(balanceTrend)
            Text("Uses loan dates, interest added, and payments recorded by each month end.",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        GlassPanel(title = "Loan reminders", subtitle = "Loans that are due soon, overdue, or have no reminder date") {
            HealthDonut(currentCount, overdueCount, noDueDateCount, Modifier.size(136.dp))
            LegendRow("Current / not yet due", MaterialTheme.colorScheme.primary, "$currentCount loans")
            LegendRow("Overdue", MaterialTheme.colorScheme.secondary, "$overdueCount loans")
            LegendRow("No due date set", MaterialTheme.colorScheme.tertiary, "$noDueDateCount loans")
            Text("A late payment is not automatically marked as a default.",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        GlassPanel(title = "Money lent and borrowed each month", subtitle = "Original amount recorded each month") {
            GroupedDirectionChart(grouped)
            LegendRow("You lent", MaterialTheme.colorScheme.primary, "Lent principal")
            LegendRow("You borrowed", MaterialTheme.colorScheme.tertiary, "Borrowed principal")
        }
        GlassPanel(title = "New loans and payments", subtitle = "Compare new loan amounts with payments received or made") {
            CashFlowBridgeChart(originationsVsPayments)
            Text("Payments include both principal and interest.",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        GlassPanel(title = "Money lent vs money borrowed", subtitle = "Compare amounts still due") {
            ComparisonBar("Lent", lentBalance, maxOf(lentBalance, borrowedBalance), MaterialTheme.colorScheme.primary)
            ComparisonBar("Borrowed", borrowedBalance, maxOf(lentBalance, borrowedBalance), MaterialTheme.colorScheme.tertiary)
        }
        GlassPanel(title = "Repayment activity", subtitle = "Payments recorded in the last 6 months") {
            PaymentBars(paymentBars)
        }
        Text("Breakdown by person", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Open each person below to compare the original amount, estimated interest and amount still due.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                ComparisonBar("Principal", personPrincipal, max, MaterialTheme.colorScheme.primary)
                ComparisonBar("Interest", personInterest, max, MaterialTheme.colorScheme.tertiary)
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
        val paddyBags = paddyRecords.sumOf { it.bags }
        val paddyQuintals = paddyRecords.sumOf { it.bags * it.kgPerBag / 100.0 }
        val paddyValue = paddyRecords.sumOf { it.bags * it.kgPerBag / 100.0 * it.ratePerQuintal }
        val paddyPaid = paddyRecords.sumOf { it.paidAmount }
        val storageSummary = paddyRecords.groupBy { it.storageLocation.trim().ifBlank { "Not specified" } }
            .mapValues { (_, lots) -> lots.sumOf { it.bags * it.kgPerBag / 100.0 } }
            .toList().sortedByDescending { it.second }.take(4)
        GlassPanel("Paddy season & storage report", "Harvest, stock locations and settlement totals from saved paddy records") {
            DetailLine("Recorded lots", paddyRecords.size.toString())
            DetailLine("Paddy bags", paddyBags.toString())
            DetailLine("Harvest weight", "${paddyNumber(paddyQuintals)} quintals")
            DetailLine("Estimated value", money(paddyValue))
            DetailLine("Paid / remaining", "${money(paddyPaid)} / ${money((paddyValue - paddyPaid).coerceAtLeast(0.0))}")
            if (storageSummary.isNotEmpty()) {
                Text("Storage locations", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                storageSummary.forEach { (location, quintals) ->
                    DetailLine(location, "${paddyNumber(quintals)} quintals")
                }
            }
            paddyRecords.groupBy { it.seasonName.trim() }.toSortedMap().forEach { (season, lots) ->
                val quintals = lots.sumOf { it.bags * it.kgPerBag / 100.0 }
                DetailLine(season, "${lots.sumOf { it.bags }} bags · ${paddyNumber(quintals)} quintals")
            }
            if (paddyRecords.isEmpty()) Text("Add paddy lots to include harvest and storage details in this report.",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        StatCard("People tracked", peopleCount.toString(), Modifier.fillMaxWidth())
        StatCard("Highest outstanding person", maxPersonName, Modifier.fillMaxWidth())
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
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
    val colors = MaterialTheme.colorScheme
    Canvas(modifier) {
        val stroke = 24.dp.toPx()
        val diameter = size.minDimension - stroke
        val topLeft = androidx.compose.ui.geometry.Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
        val arcSize = androidx.compose.ui.geometry.Size(diameter, diameter)
        drawArc(colors.surfaceVariant, -90f, 360f, false, topLeft, arcSize, style = Stroke(stroke, cap = StrokeCap.Round))
        if (total > 0.0) {
            val lentSweep = (lent / total * 360.0).toFloat()
            drawArc(colors.primary, -90f, lentSweep, false, topLeft, arcSize, style = Stroke(stroke, cap = StrokeCap.Round))
            drawArc(colors.tertiary, -90f + lentSweep, (borrowed / total * 360.0).toFloat(), false,
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
            Modifier.fillMaxWidth().height(12.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(50))
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
                        androidx.compose.ui.graphics.Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.72f), MaterialTheme.colorScheme.primary)),
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
        shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) , elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.BarChart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            }
            Text("Needs more data", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
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
                                    .background(MaterialTheme.colorScheme.tertiary))
                                androidx.compose.foundation.layout.Box(Modifier.fillMaxWidth()
                                    .height((100f * (principalPart / maxValue).toFloat()).dp)
                                    .background(MaterialTheme.colorScheme.primary))
                            }
                        }
                    }
                    Spacer(Modifier.height(5.dp))
                    Text(label, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LegendRow("Principal estimate", MaterialTheme.colorScheme.primary, "Allocated")
            LegendRow("Interest estimate", MaterialTheme.colorScheme.tertiary, "Allocated")
        }
    }
}

@Composable
private fun TrendLineChart(data: List<Pair<String, Double>>) {
    val maxValue = data.maxOfOrNull { it.second } ?: 0.0
    val colors = MaterialTheme.colorScheme
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
                    drawLine(colors.outlineVariant, androidx.compose.ui.geometry.Offset(pad, y),
                        androidx.compose.ui.geometry.Offset(size.width - pad, y), strokeWidth = 1.dp.toPx())
                }
                pts.zipWithNext().forEach { (a, b) -> drawLine(colors.primary, a, b, 3.dp.toPx(), cap = StrokeCap.Round) }
                pts.forEach { drawCircle(colors.primary, 4.dp.toPx(), it) }
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
    val colors = MaterialTheme.colorScheme
    Canvas(modifier) {
        val stroke = 24.dp.toPx()
        val diameter = size.minDimension - stroke
        val topLeft = androidx.compose.ui.geometry.Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
        val arcSize = androidx.compose.ui.geometry.Size(diameter, diameter)
        drawArc(colors.surfaceVariant, -90f, 360f, false, topLeft, arcSize, style = Stroke(stroke, cap = StrokeCap.Butt))
        if (total > 0) {
            val a = current.toFloat() / total * 360f
            val b = overdue.toFloat() / total * 360f
            val c = noDueDate.toFloat() / total * 360f
            drawArc(colors.primary, -90f, a, false, topLeft, arcSize, style = Stroke(stroke, cap = StrokeCap.Butt))
            drawArc(colors.secondary, -90f + a, b, false, topLeft, arcSize, style = Stroke(stroke, cap = StrokeCap.Butt))
            drawArc(colors.tertiary, -90f + a + b, c, false, topLeft, arcSize, style = Stroke(stroke, cap = StrokeCap.Butt))
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
                    androidx.compose.foundation.layout.Box(Modifier.width(12.dp).height(h1).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)))
                    Spacer(Modifier.width(3.dp))
                    androidx.compose.foundation.layout.Box(Modifier.width(12.dp).height(h2).background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)))
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
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)))
                Spacer(Modifier.height(4.dp))
                val h2 = if (maxValue <= 0.0) 2.dp else (78f * (repayments / maxValue).toFloat()).dp
                androidx.compose.foundation.layout.Box(Modifier.fillMaxWidth(0.62f).height(h2)
                    .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)))
                Spacer(Modifier.height(4.dp))
                Text(label, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        LegendRow("Originations", MaterialTheme.colorScheme.primary, "New principal")
        LegendRow("Repayments", MaterialTheme.colorScheme.secondary, "Recorded payments")
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
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Filled.FileDownload, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text("Back up records", style = MaterialTheme.typography.titleMedium)
                Text("One tap saves every loan and repayment to Download/FundNiti/Backup/Lendora-Data.json. On Android 10 and later, this shared Downloads copy is designed to remain after uninstall. On reinstall, FundNiti checks it and restores records automatically when the in-app database is empty.")
                Button(onClick = onBackupNow, modifier = Modifier.fillMaxWidth()) { Text("Back up all data now") }
                Button(onClick = onExport, modifier = Modifier.fillMaxWidth()) { Text("Export encrypted backup file") }
            }
        }
        Card(Modifier.fillMaxWidth() , elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Filled.FileUpload, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text("Restore records", style = MaterialTheme.typography.titleMedium)
                Text("Load the saved Android folder snapshot directly into the app when the local database is empty. Existing records are never replaced by this one-tap recovery action.")
                Button(onClick = onRecoverDeviceBackup, modifier = Modifier.fillMaxWidth()) { Text("Load device backup into app") }
                Text("Or choose a separate backup file. Restoring a selected file replaces current records.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Button(onClick = onImport, modifier = Modifier.fillMaxWidth()) { Text("Choose backup file") }
            }
        }
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant) , elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
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
                    Icon(Icons.Filled.AccountBalance, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(30.dp))
                    Column {
                        Text("About FundNiti", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Personal lending, clearly organised", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
                Text("FundNiti is an offline-first loan ledger for tracking money lent and borrowed, people, repayments, due dates and portfolio trends. Your records are stored locally on this device.", style = MaterialTheme.typography.bodyMedium)
                Text("Interest method", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text("Simple interest on the original principal at the fixed monthly rate. Full monthly anniversary periods receive the full monthly rate; partial periods are prorated by elapsed calendar days divided by the actual days in that monthly period. Recorded payments are deducted from accrued interest plus principal in the displayed outstanding balance.", style = MaterialTheme.typography.bodySmall)
                Text("Privacy by design", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text("No account, external server, or cloud sync is required. FundNiti keeps a portable JSON snapshot in Android/media/com.fundniti/Lendora/backup and restores it on a fresh install when the local database is empty. This automatic snapshot is not encrypted so reinstalling can read it; protect the file because it contains financial records. Manual encrypted exports use this installation's Android Keystore key and generally cannot be restored after reinstalling or on another device.", style = MaterialTheme.typography.bodySmall)
                Text("FundNiti · Native Android · Kotlin + Jetpack Compose · Version 1.2.0", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun LoanDirectionCard(
    label: String,
    description: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val container = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    val content = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
    Card(
        onClick = onClick,
        modifier = modifier.border(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
            shape = RoundedCornerShape(18.dp)
        ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = container)
     , elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
        Column(Modifier.fillMaxWidth().padding(13.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = content)
            Text(description, style = MaterialTheme.typography.labelSmall, color = content.copy(alpha = 0.78f))
        }
    }
}

@Composable
private fun LoanFormHeading(number: String, title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.secondaryContainer) {
            Text(number, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                color = MaterialTheme.colorScheme.onSecondaryContainer, fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelMedium)
        }
        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
    val interestRate = rate.toDoubleOrNull()
    val parsedDate = runCatching { LocalDate.parse(startDateText) }.getOrNull()
    val validStartDate = parsedDate != null && !parsedDate.isAfter(LocalDate.now())
    val startMillis = if (validStartDate) parsedDate!!.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() else null
    val canSave = name1.isNotBlank() &&
        (direction != "BORROWED_FOR_OTHER" || (name2.isNotBlank() && name3.isNotBlank())) &&
        amount != null && amount > 0 && interestRate != null && interestRate >= 0 && startMillis != null
    val monthlyInterest = if (amount != null && interestRate != null && interestRate >= 0) amount * interestRate / 100.0 else null

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.96f).fillMaxHeight(0.94f),
            shape = RoundedCornerShape(30.dp),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 8.dp
        ) {
            Column {
                Row(
                    Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(start = 19.dp, end = 12.dp, top = 15.dp, bottom = 15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.padding(11.dp).size(26.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(if (existing == null) "Add a loan" else "Edit loan", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("A clear record, ready when you need it", style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                }
                Column(
                    Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) , elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
                        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            LoanFormHeading("01", "Loan direction", "Choose how this money is being tracked")
                            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                                LoanDirectionCard("I lent", "You gave money", direction == "LENT", Modifier.weight(1f)) { direction = "LENT" }
                                LoanDirectionCard("I borrowed", "You received money", direction == "BORROWED", Modifier.weight(1f)) { direction = "BORROWED" }
                            }
                            LoanDirectionCard("Borrowed for someone", "You borrowed money and passed it on", direction == "BORROWED_FOR_OTHER", Modifier.fillMaxWidth()) { direction = "BORROWED_FOR_OTHER" }
                        }
                    }
                    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) , elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
                        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
                            LoanFormHeading("02", "People", "Keep everyone linked to this loan")
                            OutlinedTextField(
                                value = name1, onValueChange = { name1 = it }, modifier = Modifier.fillMaxWidth(),
                                label = { Text(if (direction == "LENT") "Borrower / person who received money" else "Lender / person you borrowed from") },
                                supportingText = { Text(if (direction == "LENT") "Who did you give the money to?" else "Who gave you the money?") },
                                singleLine = true, shape = RoundedCornerShape(16.dp)
                            )
                            OutlinedTextField(
                                value = name2, onValueChange = { name2 = it }, modifier = Modifier.fillMaxWidth(),
                                label = { Text(if (direction == "BORROWED_FOR_OTHER") "Your name (borrower)" else if (direction == "LENT") "Your name / lender name" else "Your name (optional)") },
                                supportingText = { Text(if (direction == "BORROWED_FOR_OTHER") "Name used when you borrowed the money" else "Optional name for your side of this record") },
                                singleLine = true, shape = RoundedCornerShape(16.dp)
                            )
                            if (direction == "BORROWED_FOR_OTHER") {
                                OutlinedTextField(
                                    value = name3, onValueChange = { name3 = it }, modifier = Modifier.fillMaxWidth(),
                                    label = { Text("Person you gave the money to") },
                                    supportingText = { Text("The person who asked you for money") }, singleLine = true,
                                    shape = RoundedCornerShape(16.dp)
                                )
                            }
                        }
                    }
                    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) , elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
                        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
                            LoanFormHeading("03", "Amount and interest", "Interest is calculated on the original principal")
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedTextField(
                                    value = principal, onValueChange = { principal = amountInputFormat(it) },
                                    modifier = Modifier.weight(1.35f), label = { Text("Principal (₹)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                OutlinedTextField(
                                    value = rate, onValueChange = { rate = it.filter { ch -> ch.isDigit() || ch == '.' }.take(8) },
                                    modifier = Modifier.weight(1f), label = { Text("Rate / month (%)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true,
                                    shape = RoundedCornerShape(16.dp)
                                )
                            }
                            if (monthlyInterest != null) {
                                Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
                                    Row(Modifier.fillMaxWidth().padding(horizontal = 13.dp, vertical = 11.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text("Estimated interest per full month", modifier = Modifier.weight(1f),
                                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                        Text(money(monthlyInterest), fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer)
                                    }
                                }
                            }
                        }
                    }
                    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) , elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
                        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
                            LoanFormHeading("04", "Dates and reminder", "Use the actual loan date and set a reminder if needed")
                            OutlinedTextField(
                                value = startDateText, onValueChange = { startDateText = it }, modifier = Modifier.fillMaxWidth(),
                                label = { Text("Loan date") }, supportingText = { Text("Choose a date today or earlier") },
                                trailingIcon = {
                                    TextButton(onClick = {
                                        val date = parsedDate ?: LocalDate.now()
                                        DatePickerDialog(
                                            context,
                                            { _, year, month, day -> startDateText = LocalDate.of(year, month + 1, day).toString() },
                                            date.year, date.monthValue - 1, date.dayOfMonth
                                        ).apply { datePicker.maxDate = System.currentTimeMillis() }.show()
                                    }) { Text("Choose date") }
                                },
                                isError = !validStartDate, singleLine = true, shape = RoundedCornerShape(16.dp)
                            )
                            if (!validStartDate) Text("Enter a valid date that is not in the future.",
                                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                            OutlinedTextField(
                                value = dueDays, onValueChange = { dueDays = it.filter(Char::isDigit).take(4) },
                                modifier = Modifier.fillMaxWidth(), label = { Text("Reminder after (days)") },
                                supportingText = { Text("Enter 0 if you do not want a reminder") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true, shape = RoundedCornerShape(16.dp)
                            )
                            OutlinedTextField(
                                value = note, onValueChange = { note = it }, modifier = Modifier.fillMaxWidth(),
                                label = { Text("Note (optional)") }, minLines = 2, maxLines = 4,
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                    }
                }
                Row(
                    Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }
                    Button(
                        onClick = {
                            if (canSave && amount != null && interestRate != null && startMillis != null) {
                                onSave(name1, name2, name3, direction, amount, interestRate, startMillis, dueDays.toIntOrNull() ?: 0, note)
                            }
                        },
                        enabled = canSave,
                        modifier = Modifier.weight(1.35f), shape = RoundedCornerShape(16.dp)
                    ) { Text(if (existing == null) "Save loan" else "Save changes") }
                }
            }
        }
    }
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
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(modifier = Modifier.fillMaxWidth(0.94f).fillMaxHeight(0.9f),
            shape = RoundedCornerShape(30.dp), color = MaterialTheme.colorScheme.background, tonalElevation = 8.dp) {
            Column {
                Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)
                    .padding(start = 18.dp, end = 10.dp, top = 14.dp, bottom = 14.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(shape = RoundedCornerShape(15.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                        Icon(Icons.Filled.Payments, null, Modifier.padding(11.dp).size(24.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(if (existing == null) "Record payment" else "Edit payment",
                            style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Capture the amount, method and payment reference.",
                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    TextButton(onClick = onDismiss) { Text("Close") }
                }
                Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer) , elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
                        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("PAYMENT AMOUNT", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer)
                            OutlinedTextField(value = amountText, onValueChange = { amountText = amountInputFormat(it) },
                                modifier = Modifier.fillMaxWidth(), label = { Text("Amount (₹)") },
                                supportingText = { amount?.takeIf { it > 0 }?.let { Text(amountInWords(it)) } },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                        }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Payment method", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Cash", "UPI", "NEFT").forEach { option ->
                                androidx.compose.material3.FilterChip(selected = method == option,
                                    onClick = { method = option; if (option == "Cash") reference = "" },
                                    label = { Text(option) })
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("IMPS", "RTGS", "Other").forEach { option ->
                                androidx.compose.material3.FilterChip(selected = method == option,
                                    onClick = { method = option }, label = { Text(option) })
                            }
                        }
                    }
                    if (method != "Cash") OutlinedTextField(reference, { reference = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(if (method == "UPI") "UPI UTR number" else "Transaction / reference number") },
                        singleLine = true)
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) , elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
                        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Payment date & time", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                androidx.compose.material3.OutlinedButton(onClick = {
                                    val selected = java.util.Calendar.getInstance().apply { timeInMillis = paidAt }
                                    DatePickerDialog(context, { _, year, month, day ->
                                        paidAt = java.util.Calendar.getInstance().apply {
                                            timeInMillis = paidAt
                                            set(java.util.Calendar.YEAR, year)
                                            set(java.util.Calendar.MONTH, month)
                                            set(java.util.Calendar.DAY_OF_MONTH, day)
                                        }.timeInMillis
                                    }, selected.get(java.util.Calendar.YEAR), selected.get(java.util.Calendar.MONTH),
                                        selected.get(java.util.Calendar.DAY_OF_MONTH)).show()
                                }, modifier = Modifier.weight(1f)) { Text(formatDate(paidAt)) }
                                androidx.compose.material3.OutlinedButton(onClick = {
                                    val selected = java.util.Calendar.getInstance().apply { timeInMillis = paidAt }
                                    TimePickerDialog(context, { _, hour, minute ->
                                        selected.set(java.util.Calendar.HOUR_OF_DAY, hour)
                                        selected.set(java.util.Calendar.MINUTE, minute)
                                        paidAt = selected.timeInMillis
                                    }, selected.get(java.util.Calendar.HOUR_OF_DAY), selected.get(java.util.Calendar.MINUTE), false).show()
                                }, modifier = Modifier.weight(1f)) {
                                    Text(DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(paidAt)))
                                }
                            }
                        }
                    }
                    OutlinedTextField(note, { note = it }, modifier = Modifier.fillMaxWidth(),
                        label = { Text("Notes (optional)") }, minLines = 2)
                }
                Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Button(onClick = { if (amount != null && amount > 0) onSave(amount, paidAt, method, reference, note) },
                        modifier = Modifier.weight(1f),
                        enabled = amount != null && amount > 0 && (method == "Cash" || reference.isNotBlank())) {
                        Text(if (existing == null) "Save payment" else "Save changes")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.AccountBalance, null, Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}



@Composable
private fun paddyNumber(value: Double): String = DecimalFormat("#,##0.##", DecimalFormatSymbols(Locale("en", "IN"))).format(value)

@Composable
private fun PaddySeasonScreen(
    modifier: Modifier,
    records: List<SeasonPaddyEntity>,
    onSave: (SeasonPaddyEntity) -> Unit,
    onDelete: (SeasonPaddyEntity) -> Unit
) {
    var editing by remember { mutableStateOf<SeasonPaddyEntity?>(null) }
    var showEditor by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf<SeasonPaddyEntity?>(null) }
    var seasonFilter by remember { mutableStateOf("All seasons") }
    val visible = records.filter { seasonFilter == "All seasons" || it.seasonName.contains(seasonFilter, true) }
    val totalBags = visible.sumOf { it.bags }
    val totalQuintals = visible.sumOf { it.bags * it.kgPerBag / 100.0 }
    val totalValue = visible.sumOf { it.bags * it.kgPerBag / 100.0 * it.ratePerQuintal }
    val totalPaid = visible.sumOf { it.paidAmount }
    val storageSummary = visible.groupBy { it.storageLocation.trim().ifBlank { "Not specified" } }
        .mapValues { (_, lots) -> lots.sumOf { it.bags * it.kgPerBag / 100.0 } }
        .toList().sortedByDescending { it.second }
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Paddy seasons & storage", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("A season-by-season view of harvest, stock locations and settlement.",
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Button(onClick = { editing = null; showEditor = true }) {
                Icon(Icons.Filled.Add, null, Modifier.size(18.dp)); Spacer(Modifier.width(5.dp)); Text("Add lot")
            }
        }
        OutlinedTextField(value = seasonFilter, onValueChange = { seasonFilter = it.ifBlank { "All seasons" } },
            label = { Text("Filter by season") }, modifier = Modifier.fillMaxWidth(),
            singleLine = true, supportingText = { Text("Type a season name, or All seasons") })
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard("Paddy bags", totalBags.toString(), Modifier.weight(1f))
            StatCard("Harvest weight", "${paddyNumber(totalQuintals)} qtl", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard("Estimated value", money(totalValue), Modifier.weight(1f))
            StatCard("Paid", money(totalPaid), Modifier.weight(1f))
        }
        Card(Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) , elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
            Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text("Storage overview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("${storageSummary.size} locations · ${visible.size} recorded lots",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (storageSummary.isEmpty()) Text("Storage details will appear here when you add a paddy lot.",
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                storageSummary.take(5).forEach { (location, quintals) ->
                    DetailLine(location, "${paddyNumber(quintals)} quintals")
                }
                DetailLine("Balance to pay", money((totalValue - totalPaid).coerceAtLeast(0.0)))
            }
        }
        Text("1 quintal = 100 kg. Value = bags × kg per bag ÷ 100 × rate per quintal.",
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (visible.isEmpty()) {
            EmptyState("No paddy records yet", "Add a lot to track its season, harvest weight, payment and storage location.")
            Button(onClick = { editing = null; showEditor = true }, modifier = Modifier.fillMaxWidth()) { Text("Add first paddy lot") }
        } else visible.forEach { record ->
            val quintals = record.bags * record.kgPerBag / 100.0
            val value = quintals * record.ratePerQuintal
            Card(Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                .clickable { editing = record; showEditor = true },
                shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                Column(Modifier.padding(17.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text(titleCaseName(record.farmerName), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(record.seasonName + if (record.paddyVariety.isBlank()) "" else " · " + record.paddyVariety,
                                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = { confirmDelete = record }) { Icon(Icons.Filled.Delete, contentDescription = "Delete paddy record") }
                    }
                    DetailLine("Harvest", "${record.bags} bags · ${paddyNumber(quintals)} quintals")
                    DetailLine("Rate / quintal", money(record.ratePerQuintal))
                    DetailLine("Lot value", money(value))
                    DetailLine("Paid / remaining", "${money(record.paidAmount)} / ${money((value - record.paidAmount).coerceAtLeast(0.0))}")
                    DetailLine("Storage location", record.storageLocation.ifBlank { "Not specified" })
                    if (record.notes.isNotBlank()) Text(record.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Tap lot to edit details", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
    if (showEditor) PaddyRecordDialog(existing = editing, onDismiss = { showEditor = false },
        onSave = { record -> onSave(record); showEditor = false })
    confirmDelete?.let { record ->
        AlertDialog(onDismissRequest = { confirmDelete = null },
            title = { Text("Delete paddy lot?") },
            text = { Text("Delete ${record.farmerName} · ${record.seasonName}? This cannot be undone.") },
            confirmButton = { TextButton(onClick = { onDelete(record); confirmDelete = null }) { Text("Delete") } },
            dismissButton = { TextButton(onClick = { confirmDelete = null }) { Text("Cancel") } })
    }
}

@Composable
private fun PaddyRecordDialog(
    existing: SeasonPaddyEntity?,
    onDismiss: () -> Unit,
    onSave: (SeasonPaddyEntity) -> Unit
) {
    var season by remember(existing?.id) { mutableStateOf(existing?.seasonName ?: LocalDate.now().year.toString() + " season") }
    var farmer by remember(existing?.id) { mutableStateOf(existing?.farmerName ?: "") }
    var variety by remember(existing?.id) { mutableStateOf(existing?.paddyVariety ?: "") }
    var storage by remember(existing?.id) { mutableStateOf(existing?.storageLocation ?: "") }
    var bags by remember(existing?.id) { mutableStateOf(existing?.bags?.toString() ?: "") }
    var kgPerBag by remember(existing?.id) { mutableStateOf(existing?.kgPerBag?.toString() ?: "75") }
    var rate by remember(existing?.id) { mutableStateOf(existing?.ratePerQuintal?.toString() ?: "") }
    var paid by remember(existing?.id) { mutableStateOf(existing?.paidAmount?.toString() ?: "0") }
    var notes by remember(existing?.id) { mutableStateOf(existing?.notes ?: "") }
    val bagCount = bags.toIntOrNull() ?: 0
    val kg = kgPerBag.toDoubleOrNull() ?: 0.0
    val quintals = bagCount * kg / 100.0
    val total = quintals * (rate.toDoubleOrNull() ?: 0.0)
    AlertDialog(onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Add paddy record" else "Edit paddy record") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(season, { season = it }, label = { Text("Season (e.g. Kharif 2026)") }, singleLine = true)
                OutlinedTextField(farmer, { farmer = it }, label = { Text("Farmer / owner name") }, singleLine = true)
                OutlinedTextField(variety, { variety = it }, label = { Text("Paddy variety (optional)") }, singleLine = true)
                OutlinedTextField(storage, { storage = it }, label = { Text("Storage / godown location") }, singleLine = true)
                OutlinedTextField(bags, { bags = it.filter(Char::isDigit) }, label = { Text("Number of bags") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                OutlinedTextField(kgPerBag, { kgPerBag = it.filter { c -> c.isDigit() || c == '.' } }, label = { Text("Kg per bag") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                OutlinedTextField(rate, { rate = it.filter { c -> c.isDigit() || c == '.' } }, label = { Text("Price per quintal (₹)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                OutlinedTextField(paid, { paid = it.filter { c -> c.isDigit() || c == '.' } }, label = { Text("Amount already paid (₹)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                Text("Calculated: ${paddyNumber(quintals)} quintals · ${money(total)} total value · ${money((total - (paid.toDoubleOrNull() ?: 0.0)).coerceAtLeast(0.0))} remaining",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                OutlinedTextField(notes, { notes = it }, label = { Text("Notes (optional)") }, minLines = 2)
            }
        },
        confirmButton = {
            TextButton(enabled = season.isNotBlank() && farmer.isNotBlank() && bagCount > 0 && kg > 0.0 &&
                (rate.toDoubleOrNull() ?: 0.0) >= 0.0 && (paid.toDoubleOrNull() ?: -1.0) >= 0.0,
                onClick = {
                    onSave(SeasonPaddyEntity(id = existing?.id ?: 0L, seasonName = season.trim(),
                        farmerName = farmer.trim(), paddyVariety = variety.trim(), storageLocation = storage.trim(),
                        bags = bagCount, kgPerBag = kg, ratePerQuintal = rate.toDoubleOrNull() ?: 0.0,
                        paidAmount = paid.toDoubleOrNull() ?: 0.0, recordedAt = existing?.recordedAt ?: System.currentTimeMillis(),
                        notes = notes.trim()))
                }) { Text("Save record") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun MoreScreen(
    modifier: Modifier = Modifier,
    onNavigate: (AppPage) -> Unit
) {
    val destinations = listOf(
        Triple(AppPage.Calculators, Icons.Filled.Calculate, "Calculators" to "Loan, interest and paddy calculations"),
        Triple(AppPage.Statistics, Icons.Filled.BarChart, "Statistics & reports" to "Portfolio totals, repayment trends and insights"),
        Triple(AppPage.PaddySeasons, Icons.Filled.BarChart, "Paddy seasons & storage" to "Track farmers, bags, quintals, payments and storage"),
        Triple(AppPage.Backup, Icons.Filled.Settings, "Backup & security" to "Protect, export and restore your records")
    )
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("More tools", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(
            "Calculations, insights and data protection in one place.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        destinations.forEach { destination ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onNavigate(destination.first) },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Icon(
                            destination.second,
                            contentDescription = null,
                            modifier = Modifier.padding(12.dp).size(26.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(destination.third.first, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(destination.third.second, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(
                        "›",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
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