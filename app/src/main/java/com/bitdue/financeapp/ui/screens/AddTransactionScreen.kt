package com.bitdue.financeapp.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bitdue.financeapp.data.local.entity.CategoryEntity
import com.bitdue.financeapp.data.models.PaymentMethod
import com.bitdue.financeapp.data.models.TransactionType
import com.bitdue.financeapp.ui.viewmodel.TransactionViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    transactionViewModel: TransactionViewModel,
    onNavigateBack: () -> Unit = {},
    initialType: TransactionType? = null,
    transactionId: String? = null,
    modifier: Modifier = Modifier
) {
    val uiState by transactionViewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // Find existing transaction if editing
    val existingTransaction = transactionId?.let { id ->
        uiState.transactions.find { it.id == id }
    }
    val isEditing = existingTransaction != null
    
    var selectedType by remember { mutableStateOf(existingTransaction?.type ?: initialType ?: TransactionType.EXPENSE) }
    var amount by remember { mutableStateOf(existingTransaction?.amount?.toString() ?: "") }
    var title by remember { mutableStateOf(existingTransaction?.title ?: "") }
    var description by remember { mutableStateOf(existingTransaction?.description ?: "") }
    var selectedCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    var selectedPaymentMethod by remember { mutableStateOf(existingTransaction?.paymentMethod ?: PaymentMethod.CASH) }
    
    // Date & Time
    var selectedDate by remember { 
        mutableStateOf(
            existingTransaction?.let {
                LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(it.date),
                    java.time.ZoneId.systemDefault()
                ).toLocalDate()
            } ?: LocalDate.now()
        )
    }
    var selectedTime by remember { 
        mutableStateOf(
            existingTransaction?.let {
                LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(it.date),
                    java.time.ZoneId.systemDefault()
                ).toLocalTime()
            } ?: LocalTime.now()
        )
    }
    
    // Recurrence
    var isRecurring by remember { mutableStateOf(existingTransaction?.isRecurring ?: false) }
    var recurringPeriod by remember { mutableStateOf(existingTransaction?.recurringPeriod ?: "MONTHLY") }
    
    // Tags
    var tags by remember { mutableStateOf(existingTransaction?.tags ?: "") }
    
    var showCategoryDialog by remember { mutableStateOf(false) }
    
    val categories = uiState.categories.filter { it.type == selectedType }
    
    // Set category when editing or when categories load
    LaunchedEffect(existingTransaction, uiState.categories) {
        if (existingTransaction != null && selectedCategory == null) {
            selectedCategory = uiState.categories.find { it.id == existingTransaction.categoryId }
        }
    }
    
    // Reset category when type changes (but not on initial load when editing)
    LaunchedEffect(selectedType) {
        if (!isEditing || existingTransaction?.type != selectedType) {
            selectedCategory = null
        }
    }
    
    // Date Picker Dialog
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    
    // Time Picker Dialog
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            selectedTime = LocalTime.of(hourOfDay, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        false
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "Edit Transaction" else "Add Transaction",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp
            ) {
                Button(
                    onClick = {
                        val amountValue = amount.toDoubleOrNull()
                        if (amountValue != null && title.isNotEmpty() && selectedCategory != null) {
                            if (isEditing && existingTransaction != null) {
                                // Update existing transaction
                                transactionViewModel.updateTransaction(
                                    existingTransaction.copy(
                                        amount = amountValue,
                                        type = selectedType,
                                        categoryId = selectedCategory!!.id,
                                        title = title,
                                        description = description,
                                        date = LocalDateTime.of(selectedDate, selectedTime)
                                            .atZone(java.time.ZoneId.systemDefault())
                                            .toInstant()
                                            .toEpochMilli(),
                                        paymentMethod = selectedPaymentMethod,
                                        tags = tags,
                                        isRecurring = isRecurring,
                                        recurringPeriod = if (isRecurring) recurringPeriod else null
                                    )
                                )
                            } else {
                                // Add new transaction
                                transactionViewModel.addTransaction(
                                    amount = amountValue,
                                    type = selectedType,
                                    categoryId = selectedCategory!!.id,
                                    title = title,
                                    description = description,
                                    date = LocalDateTime.of(selectedDate, selectedTime),
                                    paymentMethod = selectedPaymentMethod,
                                    tags = if (tags.isNotBlank()) tags.split(",").map { it.trim() } else emptyList(),
                                    isRecurring = isRecurring,
                                    recurringPeriod = if (isRecurring) recurringPeriod else null
                                )
                            }
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    enabled = amount.toDoubleOrNull() != null && title.isNotEmpty() && selectedCategory != null
                ) {
                    Text(
                        text = if (isEditing) "Update Transaction" else "Save Transaction",
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Transaction Type Toggle
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                SegmentedButton(
                    selected = selectedType == TransactionType.EXPENSE,
                    onClick = { selectedType = TransactionType.EXPENSE },
                    shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.errorContainer,
                        activeContentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text("Expense")
                }
                SegmentedButton(
                    selected = selectedType == TransactionType.INCOME,
                    onClick = { selectedType = TransactionType.INCOME },
                    shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        activeContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Text("Income")
                }
            }
            
            // Amount Input
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                leadingIcon = {
                    Icon(Icons.Default.AttachMoney, "Amount")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                )
            )
            
            // Title Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                leadingIcon = {
                    Icon(Icons.Default.Title, "Title")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Category Selection
            OutlinedCard(
                onClick = { showCategoryDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = selectedCategory?.let { "${it.icon} ${it.name}" } ?: "Select Category",
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (selectedCategory != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(Icons.Default.ArrowDropDown, null)
                }
            }
            
            // DateTime Selection Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Date Picker
                OutlinedCard(
                    onClick = { datePickerDialog.show() },
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CalendarToday, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                        )
                    }
                }
                
                // Time Picker
                OutlinedCard(
                    onClick = { timePickerDialog.show() },
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AccessTime, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            selectedTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
                        )
                    }
                }
            }
            
            // Payment Method
            var expandedPaymentMethod by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedPaymentMethod,
                onExpandedChange = { expandedPaymentMethod = !expandedPaymentMethod }
            ) {
                OutlinedTextField(
                    value = selectedPaymentMethod.name.replace("_", " "),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Payment Method") },
                    leadingIcon = { Icon(Icons.Default.Payments, null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPaymentMethod) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedPaymentMethod,
                    onDismissRequest = { expandedPaymentMethod = false }
                ) {
                    PaymentMethod.values().forEach { method ->
                        DropdownMenuItem(
                            text = { Text(method.name.replace("_", " ")) },
                            onClick = {
                                selectedPaymentMethod = method
                                expandedPaymentMethod = false
                            }
                        )
                    }
                }
            }
            
            // Recurrence Toggle
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isRecurring = !isRecurring },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Check, null)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Recurring Transaction", style = MaterialTheme.typography.bodyLarge)
                            Text("Repeat this transaction automatically", style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(
                            checked = isRecurring,
                            onCheckedChange = { isRecurring = it }
                        )
                    }
                    
                    if (isRecurring) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        var expandedPeriod by remember { mutableStateOf(false) }
                        val periods = listOf("DAILY", "WEEKLY", "MONTHLY", "YEARLY")
                        
                        ExposedDropdownMenuBox(
                            expanded = expandedPeriod,
                            onExpandedChange = { expandedPeriod = !expandedPeriod }
                        ) {
                            OutlinedTextField(
                                value = recurringPeriod,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Frequency") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPeriod) }
                            )
                            ExposedDropdownMenu(
                                expanded = expandedPeriod,
                                onDismissRequest = { expandedPeriod = false }
                            ) {
                                periods.forEach { period ->
                                    DropdownMenuItem(
                                        text = { Text(period) },
                                        onClick = {
                                            recurringPeriod = period
                                            expandedPeriod = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Description Input
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (Optional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                maxLines = 4
            )
            
            // Tags Input
            OutlinedTextField(
                value = tags,
                onValueChange = { tags = it },
                label = { Text("Tags (comma separated, Optional)") },
                leadingIcon = { Icon(Icons.Default.Label, null) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. groceries, weekend, personal") }
            )
        }
    }
    
    // Category Selection Dialog
    if (showCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = { Text("Select Category") },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
                        Card(
                            onClick = {
                                selectedCategory = category
                                showCategoryDialog = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedCategory?.id == category.id)
                                    MaterialTheme.colorScheme.secondaryContainer
                                else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = category.icon,
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCategoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
