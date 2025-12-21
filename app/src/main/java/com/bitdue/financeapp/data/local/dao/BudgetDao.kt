package com.bitdue.financeapp.data.local.dao

import androidx.room.*
import com.bitdue.financeapp.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    
    @Query("SELECT * FROM budgets WHERE isActive = 1 ORDER BY startDate DESC")
    fun getActiveBudgets(): Flow<List<BudgetEntity>>
    
    @Query("SELECT * FROM budgets ORDER BY startDate DESC")
    fun getAllBudgets(): Flow<List<BudgetEntity>>
    
    @Query("SELECT * FROM budgets ORDER BY startDate DESC")
    suspend fun getAllBudgetsList(): List<BudgetEntity>
    
    @Query("SELECT * FROM budgets WHERE id = :id")
    suspend fun getBudgetById(id: String): BudgetEntity?
    
    @Query("SELECT * FROM budgets WHERE categoryId = :categoryId AND isActive = 1")
    suspend fun getActiveBudgetForCategory(categoryId: String): BudgetEntity?
    
    @Query("SELECT * FROM budgets WHERE categoryId = :categoryId AND startDate <= :date AND endDate >= :date AND isActive = 1")
    suspend fun getBudgetForCategoryAtDate(categoryId: String, date: Long): BudgetEntity?
    
    @Query("SELECT * FROM budgets WHERE isSynced = 0")
    suspend fun getUnsyncedBudgets(): List<BudgetEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudgets(budgets: List<BudgetEntity>)
    
    @Update
    suspend fun updateBudget(budget: BudgetEntity)
    
    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)
    
    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun deleteBudgetById(id: String)
    
    @Query("UPDATE budgets SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)
}
