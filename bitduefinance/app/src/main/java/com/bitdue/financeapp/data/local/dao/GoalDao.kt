package com.bitdue.financeapp.data.local.dao

import androidx.room.*
import com.bitdue.financeapp.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    
    @Query("SELECT * FROM goals WHERE isCompleted = 0 ORDER BY deadline ASC")
    fun getActiveGoals(): Flow<List<GoalEntity>>
    
    @Query("SELECT * FROM goals ORDER BY deadline ASC")
    fun getAllGoals(): Flow<List<GoalEntity>>
    
    @Query("SELECT * FROM goals ORDER BY deadline ASC")
    suspend fun getAllGoalsList(): List<GoalEntity>
    
    @Query("SELECT * FROM goals WHERE id = :id")
    suspend fun getGoalById(id: String): GoalEntity?
    
    @Query("SELECT * FROM goals WHERE isCompleted = 1 ORDER BY deadline DESC")
    fun getCompletedGoals(): Flow<List<GoalEntity>>
    
    @Query("SELECT * FROM goals WHERE isSynced = 0")
    suspend fun getUnsyncedGoals(): List<GoalEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoals(goals: List<GoalEntity>)
    
    @Update
    suspend fun updateGoal(goal: GoalEntity)
    
    @Query("UPDATE goals SET currentAmount = :amount, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateGoalProgress(id: String, amount: Double, updatedAt: Long = System.currentTimeMillis())
    
    @Query("UPDATE goals SET isCompleted = 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun markGoalAsCompleted(id: String, updatedAt: Long = System.currentTimeMillis())
    
    @Delete
    suspend fun deleteGoal(goal: GoalEntity)
    
    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun deleteGoalById(id: String)
    
    @Query("UPDATE goals SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)
}
