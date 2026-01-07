package com.bitdue.financeapp.data.local.dao

import androidx.room.*
import com.bitdue.financeapp.data.local.entity.DebtEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtDao {
    
    @Query("SELECT * FROM debts WHERE isCompleted = 0 ORDER BY dueDate ASC")
    fun getActiveDebts(): Flow<List<DebtEntity>>
    
    @Query("SELECT * FROM debts ORDER BY dueDate ASC")
    fun getAllDebts(): Flow<List<DebtEntity>>
    
    @Query("SELECT * FROM debts WHERE id = :id")
    suspend fun getDebtById(id: String): DebtEntity?
    
    @Query("SELECT * FROM debts WHERE isCompleted = 1 ORDER BY dueDate DESC")
    fun getCompletedDebts(): Flow<List<DebtEntity>>
    
    @Query("SELECT SUM(totalAmount - paidAmount) FROM debts WHERE isCompleted = 0")
    fun getTotalOutstandingDebt(): Flow<Double?>
    
    @Query("SELECT * FROM debts WHERE isSynced = 0")
    suspend fun getUnsyncedDebts(): List<DebtEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebt(debt: DebtEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebts(debts: List<DebtEntity>)
    
    @Update
    suspend fun updateDebt(debt: DebtEntity)
    
    @Query("UPDATE debts SET paidAmount = :amount, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateDebtPayment(id: String, amount: Double, updatedAt: Long = System.currentTimeMillis())
    
    @Query("UPDATE debts SET isCompleted = 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun markDebtAsCompleted(id: String, updatedAt: Long = System.currentTimeMillis())
    
    @Delete
    suspend fun deleteDebt(debt: DebtEntity)
    
    @Query("DELETE FROM debts WHERE id = :id")
    suspend fun deleteDebtById(id: String)
    
    @Query("UPDATE debts SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)
}
