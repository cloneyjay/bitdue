package com.bitdue.financeapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bitdue.financeapp.ui.components.*
import com.bitdue.financeapp.ui.viewmodel.TransactionViewModel
import com.bitdue.financeapp.ui.viewmodel.GoalViewModel
import com.bitdue.financeapp.data.preferences.UserPreferencesManager
import com.bitdue.financeapp.data.preferences.UserPreferences
import com.bitdue.financeapp.FinanceApp
import java.time.LocalTime

@Composable
fun HomeScreen(
    transactionViewModel: TransactionViewModel,
    goalViewModel: GoalViewModel,
    notificationViewModel: com.bitdue.financeapp.ui.viewmodel.NotificationViewModel,
    onNavigateToTransactions: () -> Unit = {},
    onNavigateToAddTransaction: () -> Unit = {},
    onNavigateToBudgets: () -> Unit = {},
    onNavigateToGoals: () -> Unit = {},
    onNavigateToAddIncome: () -> Unit = {},
    onNavigateToAddExpense: () -> Unit = {},
    onNavigateToEditTransaction: (String) -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by transactionViewModel.uiState.collectAsState()
    val goalUiState by goalViewModel.uiState.collectAsState()
    val notificationUiState by notificationViewModel.uiState.collectAsState()
    val preferencesManager = remember { UserPreferencesManager(FinanceApp.instance.applicationContext) }
    val userPreferences by preferencesManager.userPreferencesFlow.collectAsState(initial = UserPreferences())
    
    val currentHour = LocalTime.now().hour
    val greeting = when (currentHour) {
        in 0..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }
    
    val userName = userPreferences.userName.ifBlank { "User" }
    
    Scaffold(
        modifier = modifier
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Top Bar with Profile Picture, Greeting, Username, Notifications, and Logout
                item {
                    HomeTopBar(
                        greeting = greeting,
                        userName = userName,
                        profilePictureUrl = userPreferences.profilePictureUrl,
                        unreadNotificationCount = notificationUiState.unreadCount,
                        onNotificationClick = onNavigateToNotifications,
                        onLogoutClick = onLogout
                    )
                }
                
                // Balance Card
                item {
                    BalanceCard(
                        cardName = "Total Balance",
                        balance = uiState.balance,
                        income = uiState.totalIncome,
                        expense = uiState.totalExpense,
                        savings = goalUiState.totalSaved
                    )
                }
                
                // Quick Actions
                item {
                    QuickActionsRow(
                        onAddIncomeClick = onNavigateToAddIncome,
                        onAddExpenseClick = onNavigateToAddExpense,
                        onBudgetsClick = onNavigateToBudgets,
                        onGoalsClick = onNavigateToGoals
                    )
                }
                
                // Transaction Statements Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Recent Transactions",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        TextButton(onClick = onNavigateToTransactions) {
                            Text("See All")
                        }
                    }
                }
                
                // Recent Transactions from Room database
                if (uiState.transactions.isEmpty()) {
                    item {
                        EmptyState(
                            emoji = "💸",
                            title = "No transactions yet",
                            description = "Tap + to add your first transaction"
                        )
                    }
                } else {
                    items(uiState.transactions.take(5)) { transaction ->
                        val category = uiState.categories.find { it.id == transaction.categoryId }
                        TransactionItemFromEntity(
                            transaction = transaction,
                            category = category,
                            onClick = { onNavigateToEditTransaction(transaction.id) }
                        )
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
