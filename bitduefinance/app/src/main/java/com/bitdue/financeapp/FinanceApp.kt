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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        
        // Ensure default categories exist
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val existingCategories = categoryRepository.getAllCategoriesList()
                if (existingCategories.isEmpty()) {
                    Log.d("FinanceApp", "No categories found, seeding default categories")
                    seedDefaultCategories()
                } else {
                    Log.d("FinanceApp", "Found ${existingCategories.size} existing categories")
                }
            } catch (e: Exception) {
                Log.e("FinanceApp", "Error checking/seeding categories: ${e.message}", e)
            }
        }
    }
    
    private suspend fun seedDefaultCategories() {
        val defaultCategories = listOf(
            // Expense categories
            com.bitdue.financeapp.data.local.entity.CategoryEntity("cat_food", "Food & Dining", "🍔", 0xFFFF6B6B, com.bitdue.financeapp.data.models.TransactionType.EXPENSE, true),
            com.bitdue.financeapp.data.local.entity.CategoryEntity("cat_transport", "Transport", "🚗", 0xFF4ECDC4, com.bitdue.financeapp.data.models.TransactionType.EXPENSE, true),
            com.bitdue.financeapp.data.local.entity.CategoryEntity("cat_shopping", "Shopping", "🛍️", 0xFFFFA07A, com.bitdue.financeapp.data.models.TransactionType.EXPENSE, true),
            com.bitdue.financeapp.data.local.entity.CategoryEntity("cat_entertainment", "Entertainment", "🎬", 0xFF9B59B6, com.bitdue.financeapp.data.models.TransactionType.EXPENSE, true),
            com.bitdue.financeapp.data.local.entity.CategoryEntity("cat_utilities", "Utilities", "💡", 0xFF3498DB, com.bitdue.financeapp.data.models.TransactionType.EXPENSE, true),
            com.bitdue.financeapp.data.local.entity.CategoryEntity("cat_health", "Health", "⚕️", 0xFF2ECC71, com.bitdue.financeapp.data.models.TransactionType.EXPENSE, true),
            com.bitdue.financeapp.data.local.entity.CategoryEntity("cat_education", "Education", "📚", 0xFFF39C12, com.bitdue.financeapp.data.models.TransactionType.EXPENSE, true),
            com.bitdue.financeapp.data.local.entity.CategoryEntity("cat_rent", "Rent", "🏠", 0xFFE74C3C, com.bitdue.financeapp.data.models.TransactionType.EXPENSE, true),
            com.bitdue.financeapp.data.local.entity.CategoryEntity("cat_insurance", "Insurance", "🛡️", 0xFF1ABC9C, com.bitdue.financeapp.data.models.TransactionType.EXPENSE, true),
            com.bitdue.financeapp.data.local.entity.CategoryEntity("cat_personal", "Personal Care", "💅", 0xFFE67E22, com.bitdue.financeapp.data.models.TransactionType.EXPENSE, true),
            com.bitdue.financeapp.data.local.entity.CategoryEntity("cat_other_expense", "Other", "📦", 0xFF95A5A6, com.bitdue.financeapp.data.models.TransactionType.EXPENSE, true),
            
            // Income categories
            com.bitdue.financeapp.data.local.entity.CategoryEntity("cat_salary", "Salary", "💰", 0xFF27AE60, com.bitdue.financeapp.data.models.TransactionType.INCOME, true),
            com.bitdue.financeapp.data.local.entity.CategoryEntity("cat_freelance", "Freelance", "💼", 0xFF8E44AD, com.bitdue.financeapp.data.models.TransactionType.INCOME, true),
            com.bitdue.financeapp.data.local.entity.CategoryEntity("cat_investment", "Investment", "📈", 0xFF16A085, com.bitdue.financeapp.data.models.TransactionType.INCOME, true),
            com.bitdue.financeapp.data.local.entity.CategoryEntity("cat_gift", "Gift", "🎁", 0xFFE91E63, com.bitdue.financeapp.data.models.TransactionType.INCOME, true),
            com.bitdue.financeapp.data.local.entity.CategoryEntity("cat_refund", "Refund", "💵", 0xFF00BCD4, com.bitdue.financeapp.data.models.TransactionType.INCOME, true),
            com.bitdue.financeapp.data.local.entity.CategoryEntity("cat_other_income", "Other Income", "💸", 0xFF9E9E9E, com.bitdue.financeapp.data.models.TransactionType.INCOME, true),
        )
        categoryRepository.insertCategories(defaultCategories)
        Log.d("FinanceApp", "Successfully seeded ${defaultCategories.size} default categories")
    }
}
