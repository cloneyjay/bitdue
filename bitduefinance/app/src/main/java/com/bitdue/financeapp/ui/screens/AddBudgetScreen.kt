package com.bitdue.financeapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bitdue.financeapp.ui.viewmodel.BudgetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetScreen(
    budgetViewModel: BudgetViewModel,
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by budgetViewModel.uiState.collectAsState()
    
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var budgetName by remember { mutableStateOf("") }
    var limitAmount by remember { mutableStateOf("") }
    var alertThreshold by remember { mutableStateOf(0.8f) }
    var selectedPeriod by remember { mutableStateOf("monthly") }
    var showPeriodDropdown by remember { mutableStateOf(false) }
    
    val selectedCategory = uiState.categories.find { it.id == selectedCategoryId }
    val periods = listOf("weekly", "monthly", "quarterly", "yearly", "custom")
    
    // Show error if duplicate budget
    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            // Error will be shown via SnackbarHost
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Add Budget",
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
        snackbarHost = {
            if (uiState.error != null) {
                Snackbar(
                    action = {
                        TextButton(onClick = { /* Clear error */ }) {
                            Text("Dismiss")
                        }
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(uiState.error ?: "")
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp
            ) {
                Button(
                    onClick = {
                        val amount = limitAmount.toDoubleOrNull()
                        if (selectedCategoryId != null && amount != null && amount > 0) {
                            budgetViewModel.addBudget(
                                categoryId = selectedCategoryId!!,
                                limit = amount,
                                period = selectedPeriod,
                                alertThreshold = alertThreshold,
                                name = budgetName.ifBlank { null }
                            )
                            if (uiState.error == null) {
                                onNavigateBack()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    enabled = selectedCategoryId != null && limitAmount.toDoubleOrNull() != null && limitAmount.toDoubleOrNull()!! > 0
                ) {
                    Text("Create Budget", modifier = Modifier.padding(8.dp))
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Category Selection
            Text(
                text = "Select Category",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.categories) { category ->
                    val isSelected = category.id == selectedCategoryId
                    
                    Card(
                        modifier = Modifier
                            .clickable { selectedCategoryId = category.id },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) 
                                MaterialTheme.colorScheme.primaryContainer 
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box {
                                Text(
                                    text = category.icon,
                                    style = MaterialTheme.typography.headlineMedium
                                )
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "Selected",
                                            modifier = Modifier.size(12.dp),
                                            tint = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
            
            // Budget Name (Optional)
            OutlinedTextField(
                value = budgetName,
                onValueChange = { budgetName = it },
                label = { Text("Budget Name (Optional)") },
                placeholder = { Text("e.g., \"Monthly Groceries\"") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Period Selection
            Text(
                text = "Budget Period",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            ExposedDropdownMenuBox(
                expanded = showPeriodDropdown,
                onExpandedChange = { showPeriodDropdown = it }
            ) {
                OutlinedTextField(
                    value = selectedPeriod.replaceFirstChar { it.uppercase() },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Period") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showPeriodDropdown) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = showPeriodDropdown,
                    onDismissRequest = { showPeriodDropdown = false }
                ) {
                    periods.forEach { period ->
                        DropdownMenuItem(
                            text = { Text(period.replaceFirstChar { it.uppercase() }) },
                            onClick = {
                                selectedPeriod = period
                                showPeriodDropdown = false
                            }
                        )
                    }
                }
            }
            
            // Budget Limit
            OutlinedTextField(
                value = limitAmount,
                onValueChange = { limitAmount = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("${selectedPeriod.replaceFirstChar { it.uppercase() }} Budget Limit") },
                prefix = { Text("Ksh") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = {
                    if (limitAmount.toDoubleOrNull() != null && limitAmount.toDoubleOrNull()!! <= 0) {
                        Text("Amount must be greater than 0", color = MaterialTheme.colorScheme.error)
                    }
                },
                isError = limitAmount.toDoubleOrNull() != null && limitAmount.toDoubleOrNull()!! <= 0
            )
            
            // Alert Threshold
            Text(
                text = "Alert Threshold: ${(alertThreshold * 100).toInt()}%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Text(
                text = "You'll be notified when spending reaches this percentage of your budget",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Slider(
                value = alertThreshold,
                onValueChange = { alertThreshold = it },
                valueRange = 0.5f..1f,
                steps = 4
            )
            
            // Preview
            if (selectedCategory != null && limitAmount.toDoubleOrNull() != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Preview",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedCategory.icon,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = selectedCategory.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "${selectedPeriod.replaceFirstChar { it.uppercase() }} limit: Ksh ${limitAmount}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Alert at ${(alertThreshold * 100).toInt()}% (Ksh ${
                                        String.format("%.2f", (limitAmount.toDoubleOrNull() ?: 0.0) * alertThreshold)
                                    })",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
