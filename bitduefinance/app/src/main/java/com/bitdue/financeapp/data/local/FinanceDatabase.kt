package com.bitdue.financeapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bitdue.financeapp.data.local.dao.*
import com.bitdue.financeapp.data.local.entity.*
import com.bitdue.financeapp.data.models.TransactionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        BudgetEntity::class,
        GoalEntity::class,
        DebtEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FinanceDatabase : RoomDatabase() {
    
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun goalDao(): GoalDao
    abstract fun debtDao(): DebtDao
    
    companion object {
        @Volatile
        private var INSTANCE: FinanceDatabase? = null
        
        fun getDatabase(context: Context): FinanceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FinanceDatabase::class.java,
                    "finance_database"
                )
                .addCallback(DatabaseCallback())
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
        
        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Use a separate scope and get the database instance directly to avoid race conditions with the @Volatile INSTANCE
                CoroutineScope(Dispatchers.IO).launch {
                    INSTANCE?.let { database ->
                        populateDefaultCategories(database.categoryDao())
                    }
                }
            }
        }
        
        private suspend fun populateDefaultCategories(categoryDao: CategoryDao) {
            val defaultCategories = listOf(
                // Expense categories
                CategoryEntity("cat_food", "Food & Dining", "🍔", 0xFFFF6B6B, TransactionType.EXPENSE, true),
                CategoryEntity("cat_transport", "Transport", "🚗", 0xFF4ECDC4, TransactionType.EXPENSE, true),
                CategoryEntity("cat_shopping", "Shopping", "🛍️", 0xFFFFA07A, TransactionType.EXPENSE, true),
                CategoryEntity("cat_entertainment", "Entertainment", "🎬", 0xFF9B59B6, TransactionType.EXPENSE, true),
                CategoryEntity("cat_utilities", "Utilities", "💡", 0xFF3498DB, TransactionType.EXPENSE, true),
                CategoryEntity("cat_health", "Health", "⚕️", 0xFF2ECC71, TransactionType.EXPENSE, true),
                CategoryEntity("cat_education", "Education", "📚", 0xFFF39C12, TransactionType.EXPENSE, true),
                CategoryEntity("cat_rent", "Rent", "🏠", 0xFFE74C3C, TransactionType.EXPENSE, true),
                CategoryEntity("cat_insurance", "Insurance", "🛡️", 0xFF1ABC9C, TransactionType.EXPENSE, true),
                CategoryEntity("cat_personal", "Personal Care", "💅", 0xFFE67E22, TransactionType.EXPENSE, true),
                CategoryEntity("cat_other_expense", "Other", "📦", 0xFF95A5A6, TransactionType.EXPENSE, true),
                
                // Income categories
                CategoryEntity("cat_salary", "Salary", "💰", 0xFF27AE60, TransactionType.INCOME, true),
                CategoryEntity("cat_freelance", "Freelance", "💼", 0xFF8E44AD, TransactionType.INCOME, true),
                CategoryEntity("cat_investment", "Investment", "📈", 0xFF16A085, TransactionType.INCOME, true),
                CategoryEntity("cat_gift", "Gift", "🎁", 0xFFE91E63, TransactionType.INCOME, true),
                CategoryEntity("cat_refund", "Refund", "💵", 0xFF00BCD4, TransactionType.INCOME, true),
                CategoryEntity("cat_other_income", "Other Income", "💸", 0xFF9E9E9E, TransactionType.INCOME, true),
            )
            categoryDao.insertCategories(defaultCategories)
        }
    }
}
