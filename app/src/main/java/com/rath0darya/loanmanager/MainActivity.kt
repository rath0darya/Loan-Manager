package com.rath0darya.loanmanager

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.room.withTransaction
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import androidx.work.ExistingWorkPolicy
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.DateFormat
import java.util.Date
import java.util.Locale
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId

private enum class AppPage(val label: String, val icon: ImageVector) {
    Home("Home", Icons.Filled.Home),
    Loans("Loans", Icons.Filled.AccountBalance),
    People("People", Icons.Filled.People),
    Statistics("Stats", Icons.Filled.BarChart),
    Backup("Backup", Icons.Filled.Settings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { LoanManagerApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoanManagerApp() {
    val context = LocalContext.current
    val db = remember { LoanDatabase.get(context) }
    val dao = db.loanDao()
    val loans by dao.observeLoans().collectAsState(initial = emptyList())
    val payments by dao.observePayments().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var page by remember { mutableStateOf(AppPage.Home) }
    var showAddLoan by remember { mutableStateOf(false) }
    var editLoan by remember { mutableStateOf<LoanEntity?>(null) }
    var paymentFor by remember { mutableStateOf<LoanEntity?>(null) }
    var search by remember { mutableStateOf("") }
    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }
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
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        if (uri != null) scope.launch {
            try {
                val root = JSONObject()
                val loanArray = JSONArray()
                dao.allLoans().forEach { loan ->
                    loanArray.put(JSONObject().put("id", loan.id).put("personName", loan.personName).put("personName2", loan.personName2)
                        .put("direction", loan.direction).put("principal", loan.principal)
                        .put("monthlyRate", loan.monthlyRate).put("startDate", loan.startDate)
                        .put("dueDate", loan.dueDate ?: JSONObject.NULL).put("note", loan.note))
                }
                val paymentArray = JSONArray()
                dao.allPayments().forEach { payment ->
                    paymentArray.put(JSONObject().put("id", payment.id).put("loanId", payment.loanId)
                        .put("amount", payment.amount).put("paidAt", payment.paidAt).put("note", payment.note))
                }
                root.put("formatVersion", 2).put("exportedAt", System.currentTimeMillis())
                    .put("loans", loanArray).put("payments", paymentArray)
                context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { it.write(root.toString(2)) }
                snackbarHostState.showSnackbar("Backup saved")
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
                val text = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                    ?: error("Could not read the selected file")
                val root = JSONObject(text)
                val loanArray = root.getJSONArray("loans")
                val paymentArray = root.getJSONArray("payments")
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
                            note = o.optString("note", "")
                        ))
                    }
                }
                snackbarHostState.showSnackbar("Backup restored")
            } catch (e: Exception) {
                snackbarHostState.showSnackbar("Restore failed: ${e.localizedMessage ?: "invalid backup"}")
            }
        }
    }

    val paymentTotals = payments.groupBy { it.loanId }.mapValues { (_, list) -> list.sumOf { it.amount } }
    fun balance(loan: LoanEntity): Double {
        val months = completedMonths(loan.startDate, System.currentTimeMillis())
        val interest = loan.principal * loan.monthlyRate / 100.0 * months
        return (loan.principal + interest - (paymentTotals[loan.id] ?: 0.0)).coerceAtLeast(0.0)
    }
    val totalOutstanding = loans.sumOf { balance(it) }
    val totalPrincipal = loans.sumOf { it.principal }
    val totalPaid = payments.sumOf { it.amount }
    val monthlyInterest = loans.sumOf { it.principal * it.monthlyRate / 100.0 }

    MaterialTheme(
        colorScheme = androidx.compose.material3.lightColorScheme(
            primary = androidx.compose.ui.graphics.Color(0xFF145A46),
            onPrimary = androidx.compose.ui.graphics.Color.White,
            secondary = androidx.compose.ui.graphics.Color(0xFFB5D8C8),
            background = androidx.compose.ui.graphics.Color(0xFFF6F8F6),
            surface = androidx.compose.ui.graphics.Color(0xFFFFFFFF)
        )
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("Loan Manager", fontWeight = FontWeight.Bold)
                            Text(page.label, style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary)
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    AppPage.entries.forEach { item ->
                        NavigationBarItem(
                            selected = page == item,
                            onClick = { page = item },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
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
                    monthlyInterest, ::balance, { page = AppPage.Loans }
                )
                AppPage.Loans -> LoansScreen(
                    Modifier.padding(padding), loans, ::balance, paymentTotals, search, { search = it },
                    onAddPayment = { paymentFor = it },
                    onEdit = { editLoan = it },
                    onDelete = { loan -> scope.launch {
                        dao.deletePaymentsForLoan(loan.id); dao.deleteLoan(loan.id)
                        snackbarHostState.showSnackbar("Loan deleted")
                    } }
                )
                AppPage.People -> PeopleScreen(Modifier.padding(padding), loans, ::balance)
                AppPage.Statistics -> StatisticsScreen(
                    Modifier.padding(padding), loans, payments, ::balance, totalOutstanding, totalPrincipal
                )
                AppPage.Backup -> BackupScreen(
                    Modifier.padding(padding),
                    onExport = { exportLauncher.launch("LoanManager-Backup.json") },
                    onImport = { importLauncher.launch(arrayOf("application/json", "text/*")) }
                )
            }
        }
    }

    if (showAddLoan || editLoan != null) {
        val editing = editLoan
        LoanDialog(
            existing = editing,
            onDismiss = { showAddLoan = false; editLoan = null },
            onSave = { person1, person2, direction, principal, rate, startDate, dueDays, note ->
                scope.launch {
                    val due = if (dueDays > 0) System.currentTimeMillis() + dueDays * 86_400_000L else null
                    val savedLoan = LoanEntity(
                        id = editing?.id ?: 0L,
                        personName = person1.trim(),
                        personName2 = person2.trim(),
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
                    if (due != null) {
                        val delay = (due - System.currentTimeMillis()).coerceAtLeast(0L)
                        val request = OneTimeWorkRequestBuilder<LoanReminderWorker>()
                            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                            .setInputData(workDataOf("loanId" to savedId, "personName" to listOf(person1.trim(), person2.trim()).filter { it.isNotBlank() }.joinToString(" & ")))
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
        PaymentDialog(onDismiss = { paymentFor = null }, onSave = { amount, note ->
            scope.launch {
                dao.addPayment(PaymentEntity(loanId = loan.id, amount = amount,
                    paidAt = System.currentTimeMillis(), note = note.trim()))
                paymentFor = null
                snackbarHostState.showSnackbar("Payment recorded")
            }
        })
    }
}

@Composable
private fun DashboardScreen(
    modifier: Modifier, loans: List<LoanEntity>, outstanding: Double, principal: Double,
    paid: Double, monthlyInterest: Double, balance: (LoanEntity) -> Double, onViewLoans: () -> Unit
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
                StatCard("People", loans.flatMap { listOf(it.personName, it.personName2) }.filter { it.isNotBlank() }.map { it.trim().lowercase() }.distinct().size.toString(), Modifier.weight(1f))
            }
        }
        item { Text("Recent loans", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
        if (loans.isEmpty()) item { EmptyState("No loans yet", "Tap Loans, then + to add your first record.") }
        items(loans.take(5), key = { it.id }) { loan -> LoanCard(loan, balance(loan), 0.0, {}, null, null) }
        item { Button(onClick = onViewLoans, modifier = Modifier.fillMaxWidth()) { Text("View all loans") } }
        item { Text("Interest uses simple monthly interest on the original principal for each completed month.",
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}

@Composable
private fun LoansScreen(
    modifier: Modifier, loans: List<LoanEntity>, balance: (LoanEntity) -> Double,
    paymentTotals: Map<Long, Double>, search: String, onSearch: (String) -> Unit, onAddPayment: (LoanEntity) -> Unit, onEdit: (LoanEntity) -> Unit,
    onDelete: (LoanEntity) -> Unit
) {
    val filtered = loans.filter {
        (it.personName.contains(search, ignoreCase = true) || it.personName2.contains(search, ignoreCase = true)) ||
            it.direction.contains(search, ignoreCase = true) || it.note.contains(search, ignoreCase = true)
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
                LoanCard(loan, balance(loan), paymentTotals[loan.id] ?: 0.0, { onAddPayment(loan) }, { onEdit(loan) }, { onDelete(loan) })
            }
            item { Spacer(Modifier.height(72.dp)) }
        }
    }
}

@Composable
private fun LoanCard(
    loan: LoanEntity, balance: Double, paid: Double, onAddPayment: () -> Unit, onEdit: (() -> Unit)?, onDelete: (() -> Unit)?
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(listOf(loan.personName, loan.personName2).filter { it.isNotBlank() }.joinToString(" & "), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(if (loan.direction == "LENT") "You lent" else "You borrowed",
                        style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                }
                Text(money(balance), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("Principal: ${money(loan.principal)}", style = MaterialTheme.typography.bodySmall)
                Text("${loan.monthlyRate}% / month", style = MaterialTheme.typography.bodySmall)
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
    val people = loans.flatMap { loan -> listOf(loan.personName, loan.personName2).filter { it.isNotBlank() }.map { it.trim() to loan } }.groupBy({ it.first }, { it.second }).toSortedMap(String.CASE_INSENSITIVE_ORDER)
    LazyColumn(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("People are created automatically when you add a loan.", style = MaterialTheme.typography.bodyMedium) }
        if (people.isEmpty()) item { EmptyState("No people yet", "Your borrower and lender list will appear here.") }
        items(people.keys.toList(), key = { it }) { name ->
            val records = people[name].orEmpty().distinctBy { it.id }
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("${records.size} record(s) · Outstanding ${money(records.sumOf(balance))}",
                        style = MaterialTheme.typography.bodyMedium)
                    Text("Lent: ${money(records.filter { it.direction == "LENT" }.sumOf(balance))}   Borrowed: ${money(records.filter { it.direction == "BORROWED" }.sumOf(balance))}",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
    val borrowed = loans.filter { it.direction == "BORROWED" }
    val paymentSum = payments.sumOf { it.amount }
    val monthlyInterest = loans.sumOf { it.principal * it.monthlyRate / 100.0 }
    val maxPerson = loans.flatMap { loan -> listOf(loan.personName, loan.personName2).filter { it.isNotBlank() }.map { it to loan } }.groupBy({ it.first }, { it.second }).maxByOrNull { entry -> entry.value.distinctBy { it.id }.sumOf(balance) }
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Portfolio overview", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        StatCard("Outstanding across all records", money(outstanding), Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard("Money lent", money(lent.sumOf(balance)), Modifier.weight(1f))
            StatCard("Money borrowed", money(borrowed.sumOf(balance)), Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard("Original principal", money(principal), Modifier.weight(1f))
            StatCard("Payments received/paid", money(paymentSum), Modifier.weight(1f))
        }
        StatCard("Estimated monthly interest", money(monthlyInterest), Modifier.fillMaxWidth())
        StatCard("Loan records", loans.size.toString(), Modifier.fillMaxWidth())
        StatCard("People", loans.flatMap { listOf(it.personName, it.personName2) }.filter { it.isNotBlank() }.map { it.trim().lowercase() }.distinct().size.toString(), Modifier.fillMaxWidth())
        StatCard("Highest outstanding person", maxPerson?.key ?: "—", Modifier.fillMaxWidth())
        Text("Balances include simple interest for completed months and subtract recorded payments. Interest is calculated on the original principal; confirm that this matches your agreement before relying on the totals.",
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun BackupScreen(modifier: Modifier, onExport: () -> Unit, onImport: () -> Unit) {
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Your data stays on this device", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("No login, cloud database, or external server. Export a JSON backup to a folder you choose, or restore a previous backup using Android's document picker.")
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Filled.FileDownload, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text("Back up records", style = MaterialTheme.typography.titleMedium)
                Text("Save loans and payment history as a portable JSON file.")
                Button(onClick = onExport, modifier = Modifier.fillMaxWidth()) { Text("Export backup") }
            }
        }
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Filled.FileUpload, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text("Restore records", style = MaterialTheme.typography.titleMedium)
                Text("Restoring replaces the current local records with the selected backup.")
                Button(onClick = onImport, modifier = Modifier.fillMaxWidth()) { Text("Choose backup file") }
            }
        }
        Text("Tip: save a copy to Downloads or an SD card. Restoring a backup overwrites existing loan records.",
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun LoanDialog(
    existing: LoanEntity? = null,
    onDismiss: () -> Unit,
    onSave: (String, String, String, Double, Double, Long, Int, String) -> Unit
) {
    val context = LocalContext.current
    var name1 by remember(existing?.id) { mutableStateOf(existing?.personName ?: "") }
    var name2 by remember(existing?.id) { mutableStateOf(existing?.personName2 ?: "") }
    var direction by remember(existing?.id) { mutableStateOf(existing?.direction ?: "LENT") }
    var principal by remember(existing?.id) { mutableStateOf(existing?.principal?.toString() ?: "") }
    var rate by remember(existing?.id) { mutableStateOf(existing?.monthlyRate?.toString() ?: "") }
    var startDateText by remember(existing?.id) {
        mutableStateOf(existing?.startDate?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().toString() } ?: LocalDate.now().toString())
    }
    var dueDays by remember(existing?.id) {
        mutableStateOf(existing?.dueDate?.let { ((it - System.currentTimeMillis()) / 86_400_000L).coerceAtLeast(0).toString() } ?: "30")
    }
    var note by remember(existing?.id) { mutableStateOf(existing?.note ?: "") }
    val amount = principal.toDoubleOrNull()
    val interest = rate.toDoubleOrNull()
    val parsedDate = runCatching { LocalDate.parse(startDateText) }.getOrNull()
    val validStartDate = parsedDate != null && !parsedDate.isAfter(LocalDate.now())
    val startMillis = if (validStartDate) parsedDate!!.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() else null
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Add loan" else "Edit loan") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(name1, { name1 = it }, label = { Text(if (direction == "LENT") "Borrower 1 name" else "Lender 1 name") }, singleLine = true)
                OutlinedTextField(name2, { name2 = it }, label = { Text(if (direction == "LENT") "Borrower 2 name (optional)" else "Lender 2 name (optional)") }, singleLine = true)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    androidx.compose.material3.FilterChip(selected = direction == "LENT", onClick = { direction = "LENT" }, label = { Text("I lent") })
                    androidx.compose.material3.FilterChip(selected = direction == "BORROWED", onClick = { direction = "BORROWED" }, label = { Text("I borrowed") })
                }
                OutlinedTextField(principal, { principal = it }, label = { Text("Principal amount (₹)") }, singleLine = true)
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
                    onSave(name1, name2, direction, amount, interest, startMillis, dueDays.toIntOrNull() ?: 0, note)
                }
            }, enabled = name1.isNotBlank() && amount != null && amount > 0 && interest != null && interest >= 0 && startMillis != null) {
                Text(if (existing == null) "Save loan" else "Save changes")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun PaymentDialog(onDismiss: () -> Unit, onSave: (Double, String) -> Unit) {
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    val amount = amountText.toDoubleOrNull()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record payment") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(amountText, { amountText = it }, label = { Text("Payment amount (₹)") }, singleLine = true)
                OutlinedTextField(note, { note = it }, label = { Text("Note / receipt reference (optional)") })
            }
        },
        confirmButton = {
            TextButton(onClick = { if (amount != null && amount > 0) onSave(amount, note) },
                enabled = amount != null && amount > 0) { Text("Save payment") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun EmptyState(title: String, message: String) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.AccountBalance, null, Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun money(value: Double): String =
    java.text.NumberFormat.getCurrencyInstance(Locale("en", "IN")).format(value)

private fun formatDate(time: Long): String =
    DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(Date(time))

private fun completedMonths(startMillis: Long, endMillis: Long): Long {
    val zone = ZoneId.systemDefault()
    val start = Instant.ofEpochMilli(startMillis).atZone(zone).toLocalDate()
    val end = Instant.ofEpochMilli(endMillis).atZone(zone).toLocalDate()
    if (end.isBefore(start)) return 0
    return Period.between(start, end).toTotalMonths().coerceAtLeast(0)
}
