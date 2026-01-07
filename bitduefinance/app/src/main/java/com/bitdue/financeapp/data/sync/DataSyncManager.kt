package com.bitdue.financeapp.data.sync

import android.util.Log
import com.bitdue.financeapp.FinanceApp
import com.bitdue.financeapp.data.firebase.FirestoreSyncManager
import com.bitdue.financeapp.data.repository.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SyncState(
    val isSyncing: Boolean = false,
    val lastSyncTime: Long = 0L,
    val error: String? = null,
    val itemsSynced: Int = 0
)

class DataSyncManager(
    private val firestoreSyncManager: FirestoreSyncManager,
    private val transactionRepository: TransactionRepository,
    private val budgetRepository: BudgetRepository,
    private val goalRepository: GoalRepository,
    private val categoryRepository: CategoryRepository
) {
    
    private val _syncState = MutableStateFlow(SyncState())
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    /**
     * Sync all data between local database and Firestore
     * Uses last-write-wins strategy for conflict resolution
     */
    suspend fun syncAllData() {
        if (_syncState.value.isSyncing) {
            Log.d(TAG, "Sync already in progress")
            return
        }
        
        _syncState.value = SyncState(isSyncing = true)
        
        try {
            var totalSynced = 0
            
            // Sync transactions
            totalSynced += syncTransactions()
            
            // Sync budgets
            totalSynced += syncBudgets()
            
            // Sync goals
            totalSynced += syncGoals()
            
            // Sync categories
            totalSynced += syncCategories()
            
            _syncState.value = SyncState(
                isSyncing = false,
                lastSyncTime = System.currentTimeMillis(),
                itemsSynced = totalSynced
            )
            
            Log.d(TAG, "Sync completed successfully. Items synced: $totalSynced")
        } catch (e: Exception) {
            Log.e(TAG, "Sync failed", e)
            _syncState.value = SyncState(
                isSyncing = false,
                error = e.message
            )
        }
    }
    
    private suspend fun syncTransactions(): Int {
        var synced = 0
        
        try {
            // Upload local transactions that haven't been synced
            val localTransactions = transactionRepository.getAllTransactionsList()
            localTransactions.forEach { transaction ->
                if (!transaction.isSynced) {
                    val result = firestoreSyncManager.syncTransaction(transaction)
                    if (result.isSuccess) {
                        // Mark as synced
                        transactionRepository.updateTransaction(
                            transaction.copy(isSynced = true)
                        )
                        synced++
                    }
                }
            }
            
            // Download transactions from Firestore
            val remoteResult = firestoreSyncManager.fetchTransactions()
            if (remoteResult.isSuccess) {
                val remoteTransactions = remoteResult.getOrNull() ?: emptyList()
                
                remoteTransactions.forEach { remoteTransaction ->
                    // Check if transaction exists locally
                    val localTransaction = localTransactions.find { it.id == remoteTransaction.id }
                    
                    if (localTransaction == null) {
                        // New transaction from server
                        transactionRepository.insertTransaction(remoteTransaction)
                        synced++
                    } else if (remoteTransaction.updatedAt > localTransaction.updatedAt) {
                        // Server version is newer (last-write-wins)
                        transactionRepository.updateTransaction(remoteTransaction)
                        synced++
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Transaction sync failed", e)
        }
        
        return synced
    }
    
    private suspend fun syncBudgets(): Int {
        var synced = 0
        
        try {
            val localBudgets = budgetRepository.getAllBudgetsList()
            
            // Upload unsynced budgets
            localBudgets.forEach { budget ->
                if (!budget.isSynced) {
                    val result = firestoreSyncManager.syncBudget(budget)
                    if (result.isSuccess) {
                        budgetRepository.updateBudget(budget.copy(isSynced = true))
                        synced++
                    }
                }
            }
            
            // Download budgets from Firestore
            val remoteResult = firestoreSyncManager.fetchBudgets()
            if (remoteResult.isSuccess) {
                val remoteBudgets = remoteResult.getOrNull() ?: emptyList()
                
                remoteBudgets.forEach { remoteBudget ->
                    val localBudget = localBudgets.find { it.id == remoteBudget.id }
                    
                    if (localBudget == null) {
                        budgetRepository.insertBudget(remoteBudget)
                        synced++
                    } else if (remoteBudget.updatedAt > localBudget.updatedAt) {
                        budgetRepository.updateBudget(remoteBudget)
                        synced++
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Budget sync failed", e)
        }
        
        return synced
    }
    
    private suspend fun syncGoals(): Int {
        var synced = 0
        
        try {
            val localGoals = goalRepository.getAllGoalsList()
            
            // Upload unsynced goals
            localGoals.forEach { goal ->
                if (!goal.isSynced) {
                    val result = firestoreSyncManager.syncGoal(goal)
                    if (result.isSuccess) {
                        goalRepository.updateGoal(goal.copy(isSynced = true))
                        synced++
                    }
                }
            }
            
            // Download goals from Firestore
            val remoteResult = firestoreSyncManager.fetchGoals()
            if (remoteResult.isSuccess) {
                val remoteGoals = remoteResult.getOrNull() ?: emptyList()
                
                remoteGoals.forEach { remoteGoal ->
                    val localGoal = localGoals.find { it.id == remoteGoal.id }
                    
                    if (localGoal == null) {
                        goalRepository.insertGoal(remoteGoal)
                        synced++
                    } else if (remoteGoal.updatedAt > localGoal.updatedAt) {
                        goalRepository.updateGoal(remoteGoal)
                        synced++
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Goal sync failed", e)
        }
        
        return synced
    }
    
    private suspend fun syncCategories(): Int {
        var synced = 0
        
        try {
            val localCategories = categoryRepository.getAllCategoriesList()
            
            // Upload unsynced categories
            localCategories.forEach { category ->
                val result = firestoreSyncManager.syncCategory(category)
                if (result.isSuccess) {
                    synced++
                }
            }
            
            // Download categories from Firestore
            val remoteResult = firestoreSyncManager.fetchCategories()
            if (remoteResult.isSuccess) {
                val remoteCategories = remoteResult.getOrNull() ?: emptyList()
                
                remoteCategories.forEach { remoteCategory ->
                    val localCategory = localCategories.find { it.id == remoteCategory.id }
                    
                    if (localCategory == null && !remoteCategory.isDefault) {
                        categoryRepository.insertCategory(remoteCategory)
                        synced++
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Category sync failed", e)
        }
        
        return synced
    }
    
    /**
     * Clear sync state
     */
    fun clearSyncState() {
        _syncState.value = SyncState()
    }
    
    companion object {
        private const val TAG = "DataSyncManager"
    }
}
