package com.bitdue.financeapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bitdue.financeapp.data.models.TransactionType
import com.bitdue.financeapp.ui.components.EmptyState
import com.bitdue.financeapp.ui.components.TransactionItemFromEntity
import com.bitdue.financeapp.ui.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    transactionViewModel: TransactionViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToEditTransaction: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by transactionViewModel.uiState.collectAsState()
    var selectedFilter by remember { mutableStateOf<TransactionType?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }
    
    val filteredTransactions = when (selectedFilter) {
        TransactionType.INCOME -> uiState.transactions.filter { it.type == TransactionType.INCOME }
        TransactionType.EXPENSE -> uiState.transactions.filter { it.type == TransactionType.EXPENSE }
        null -> uiState.transactions
    }.filter { transaction ->
        searchQuery.isEmpty() || 
        transaction.title.contains(searchQuery, ignoreCase = true) ||
        transaction.description.contains(searchQuery, ignoreCase = true)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (showSearch) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search transactions...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    } else {
                        Text(
                            "All Transactions",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (showSearch) {
                            showSearch = false
                            searchQuery = ""
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (!showSearch) {
                        IconButton(onClick = { showSearch = true }) {
                            Icon(Icons.Default.Search, "Search")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { selectedFilter = null },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = selectedFilter == TransactionType.INCOME,
                    onClick = { selectedFilter = TransactionType.INCOME },
                    label = { Text("Income") }
                )
                FilterChip(
                    selected = selectedFilter == TransactionType.EXPENSE,
                    onClick = { selectedFilter = TransactionType.EXPENSE },
                    label = { Text("Expenses") }
                )
            }
            
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (filteredTransactions.isEmpty()) {
                EmptyState(
                    emoji = "💸",
                    title = "No transactions found",
                    description = if (searchQuery.isNotEmpty()) "Try a different search term" else "Add your first transaction"
                )
            } else {
                // Transactions List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredTransactions) { transaction ->
                        val category = uiState.categories.find { it.id == transaction.categoryId }
                        TransactionItemFromEntity(
                            transaction = transaction,
                            category = category,
                            onClick = { onNavigateToEditTransaction(transaction.id) }
                        )
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}
