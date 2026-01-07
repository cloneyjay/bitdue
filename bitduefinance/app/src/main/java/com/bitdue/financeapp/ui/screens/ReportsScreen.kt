package com.bitdue.financeapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bitdue.financeapp.ui.components.charts.PieChartData
import com.bitdue.financeapp.ui.components.charts.PieChartWithLegend
import com.bitdue.financeapp.ui.theme.NegativeRed
import com.bitdue.financeapp.ui.theme.PositiveGreen
import com.bitdue.financeapp.ui.viewmodel.ReportsViewModel
import com.bitdue.financeapp.ui.viewmodel.DateRangeFilter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    reportsViewModel: ReportsViewModel,
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by reportsViewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Reports & Analytics",
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
        modifier = modifier
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // Date Range Filter Chips
                item {
                    var showDatePicker by remember { mutableStateOf(false) }
                    
                    Column {
                        Text(
                            text = "Time Period",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                FilterChip(
                                    selected = uiState.selectedFilter == DateRangeFilter.MONTHLY,
                                    onClick = { reportsViewModel.updateDateFilter(DateRangeFilter.MONTHLY) },
                                    label = { Text("Monthly") }
                                )
                            }
                            item {
                                FilterChip(
                                    selected = uiState.selectedFilter == DateRangeFilter.YEARLY,
                                    onClick = { reportsViewModel.updateDateFilter(DateRangeFilter.YEARLY) },
                                    label = { Text("Yearly") }
                                )
                            }
                            item {
                                FilterChip(
                                    selected = uiState.selectedFilter == DateRangeFilter.CUSTOM,
                                    onClick = { showDatePicker = true },
                                    label = { Text("Custom") }
                                )
                            }
                        }
                        
                        if (uiState.selectedFilter == DateRangeFilter.CUSTOM) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${uiState.startDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))} - ${uiState.endDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    
                    if (showDatePicker) {
                        DateRangePickerDialog(
                            onDismiss = { showDatePicker = false },
                            onDateRangeSelected = { start, end ->
                                reportsViewModel.updateDateFilter(
                                    DateRangeFilter.CUSTOM,
                                    customStart = start,
                                    customEnd = end
                                )
                                showDatePicker = false
                            },
                            initialStartDate = uiState.startDate,
                            initialEndDate = uiState.endDate
                        )
                    }
                }
                
                // Summary Cards
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = PositiveGreen.copy(alpha = 0.15f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Income",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = PositiveGreen
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Ksh ${String.format("%,.2f", uiState.totalIncome)}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = PositiveGreen
                                )
                            }
                        }
                        
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = NegativeRed.copy(alpha = 0.15f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Expenses",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = NegativeRed
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Ksh ${String.format("%,.2f", uiState.totalExpense)}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = NegativeRed
                                )
                            }
                        }
                    }
                }
                
                // Net Income
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (uiState.netIncome >= 0) PositiveGreen.copy(alpha = 0.1f) else NegativeRed.copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Net Income",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Ksh ${String.format("%,.2f", uiState.netIncome)}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (uiState.netIncome >= 0) PositiveGreen else NegativeRed
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = if (uiState.netIncome >= 0) PositiveGreen else NegativeRed,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                }
                
                // Spending Breakdown Pie Chart
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Spending Breakdown",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            if (uiState.categorySpending.isNotEmpty()) {
                                PieChartWithLegend(
                                    data = uiState.categorySpending.map { spending ->
                                        PieChartData(
                                            label = spending.category.name,
                                            value = spending.amount.toFloat(),
                                            color = Color(spending.category.color.toInt() or 0xFF000000.toInt())
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    centerText = "Ksh ${String.format("%,.0f", uiState.totalExpense)}"
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No expense data available",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Category Details
                item {
                    Text(
                        text = "Category Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                items(uiState.categorySpending) { spending ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(spending.category.color.toInt() or 0xFF000000.toInt()),
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Text(
                                                text = spending.category.icon,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = spending.category.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "${spending.transactionCount} transactions • ${String.format("%.1f", spending.percentage)}%",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Text(
                                    text = "Ksh ${String.format("%,.2f", spending.amount)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            LinearProgressIndicator(
                                progress = { spending.percentage / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp),
                                color = Color(spending.category.color.toInt() or 0xFF000000.toInt()),
                                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                            )
                        }
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateRangePickerDialog(
    onDismiss: () -> Unit,
    onDateRangeSelected: (LocalDateTime, LocalDateTime) -> Unit,
    initialStartDate: LocalDateTime,
    initialEndDate: LocalDateTime
) {
    var selectedStartDate by remember { mutableStateOf(initialStartDate) }
    var selectedEndDate by remember { mutableStateOf(initialEndDate) }
    var selectingStart by remember { mutableStateOf(true) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(if (selectingStart) "Select Start Date" else "Select End Date") 
        },
        text = {
            Column {
                OutlinedTextField(
                    value = selectedStartDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                    onValueChange = {},
                    label = { Text("Start Date") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (selectingStart) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = selectedEndDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                    onValueChange = {},
                    label = { Text("End Date") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (!selectingStart) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (selectingStart) "Tap a date below to set start date" else "Tap a date below to set end date",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onDateRangeSelected(selectedStartDate, selectedEndDate) }) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
