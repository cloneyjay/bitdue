package com.bitdue.financeapp.data.repository

import com.bitdue.financeapp.data.local.dao.BudgetDao
import com.bitdue.financeapp.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

class BudgetRepository(private val budgetDao: BudgetDao) {
    
    val activeBudgets: Flow<List<BudgetEntity>> = budgetDao.getActiveBudgets()
    val allBudgets: Flow<List<BudgetEntity>> = budgetDao.getAllBudgets()
    
    suspend fun getAllBudgetsList(): List<BudgetEntity> {
        return budgetDao.getAllBudgetsList()
    }
    
    suspend fun getBudgetById(id: String): BudgetEntity? {
        return budgetDao.getBudgetById(id)
    }
    
    suspend fun getActiveBudgetForCategory(categoryId: String): BudgetEntity? {
        return budgetDao.getActiveBudgetForCategory(categoryId)
    }
    
    suspend fun getBudgetForCategoryAtDate(categoryId: String, date: Long): BudgetEntity? {
        return budgetDao.getBudgetForCategoryAtDate(categoryId, date)
    }
    
    suspend fun insertBudget(budget: BudgetEntity) {
        budgetDao.insertBudget(budget)
    }
    
    suspend fun insertBudgets(budgets: List<BudgetEntity>) {
        budgetDao.insertBudgets(budgets)
    }
    
    suspend fun updateBudget(budget: BudgetEntity) {
        budgetDao.updateBudget(budget)
    }
    
    suspend fun deleteBudget(budget: BudgetEntity) {
        budgetDao.deleteBudget(budget)
    }
    
    suspend fun deleteBudgetById(id: String) {
        budgetDao.deleteBudgetById(id)
    }
    
    suspend fun getUnsyncedBudgets(): List<BudgetEntity> {
        return budgetDao.getUnsyncedBudgets()
    }
    
    suspend fun markAsSynced(id: String) {
        budgetDao.markAsSynced(id)
    }
}
