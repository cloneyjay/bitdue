package com.bitdue.financeapp

import android.app.Application
import android.util.Log
import com.bitdue.financeapp.data.local.FinanceDatabase
import com.bitdue.financeapp.data.repository.*
import com.bitdue.financeapp.data.firebase.FirebaseAuthManager
import com.bitdue.financeapp.data.firebase.FirebaseStorageManager
import com.bitdue.financeapp.data.firebase.FirestoreSyncManager
import com.bitdue.financeapp.data.sync.DataSyncManager
import com.google.firebase.FirebaseApp

class FinanceApp : Application() {
    
    val database by lazy { FinanceDatabase.getDatabase(this) }
    
    // Local repositories
    val transactionRepository by lazy { TransactionRepository(database.transactionDao()) }
    val categoryRepository by lazy { CategoryRepository(database.categoryDao()) }
    val budgetRepository by lazy { BudgetRepository(database.budgetDao()) }
    val goalRepository by lazy { GoalRepository(database.goalDao()) }
    val debtRepository by lazy { DebtRepository(database.debtDao()) }
    
    // Firebase managers
    val authManager by lazy { FirebaseAuthManager() }
    val firestoreSyncManager by lazy { FirestoreSyncManager() }
    val storageManager by lazy { FirebaseStorageManager() }
    
    // Data sync manager
    val dataSyncManager by lazy {
        DataSyncManager(
            firestoreSyncManager = firestoreSyncManager,
            transactionRepository = transactionRepository,
            budgetRepository = budgetRepository,
            goalRepository = goalRepository,
            categoryRepository = categoryRepository
        )
    }
    
    companion object {
        lateinit var instance: FinanceApp
            private set
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // Initialize Firebase
        try {
            FirebaseApp.initializeApp(this)
            Log.d("FirebaseApp", "Firebase initialized successfully")
        } catch (e: Exception) {
            Log.e("FirebaseApp", "Failed to initialize Firebase: ${e.message}", e)
        }
    }
}
