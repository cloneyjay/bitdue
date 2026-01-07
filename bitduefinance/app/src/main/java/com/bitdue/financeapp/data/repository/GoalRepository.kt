package com.bitdue.financeapp.data.repository

import com.bitdue.financeapp.data.local.dao.GoalDao
import com.bitdue.financeapp.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

class GoalRepository(private val goalDao: GoalDao) {
    
    val activeGoals: Flow<List<GoalEntity>> = goalDao.getActiveGoals()
    val allGoals: Flow<List<GoalEntity>> = goalDao.getAllGoals()
    
    suspend fun getAllGoalsList(): List<GoalEntity> {
        return goalDao.getAllGoalsList()
    }
    val completedGoals: Flow<List<GoalEntity>> = goalDao.getCompletedGoals()
    
    suspend fun getGoalById(id: String): GoalEntity? {
        return goalDao.getGoalById(id)
    }
    
    suspend fun insertGoal(goal: GoalEntity) {
        goalDao.insertGoal(goal)
    }
    
    suspend fun insertGoals(goals: List<GoalEntity>) {
        goalDao.insertGoals(goals)
    }
    
    suspend fun updateGoal(goal: GoalEntity) {
        goalDao.updateGoal(goal)
    }
    
    suspend fun updateGoalProgress(id: String, amount: Double) {
        goalDao.updateGoalProgress(id, amount)
    }
    
    suspend fun markGoalAsCompleted(id: String) {
        goalDao.markGoalAsCompleted(id)
    }
    
    suspend fun deleteGoal(goal: GoalEntity) {
        goalDao.deleteGoal(goal)
    }
    
    suspend fun deleteGoalById(id: String) {
        goalDao.deleteGoalById(id)
    }
    
    suspend fun getUnsyncedGoals(): List<GoalEntity> {
        return goalDao.getUnsyncedGoals()
    }
    
    suspend fun markAsSynced(id: String) {
        goalDao.markAsSynced(id)
    }
}
