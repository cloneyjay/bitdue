package com.bitdue.financeapp.data.repository

import com.bitdue.financeapp.data.local.dao.DebtDao
import com.bitdue.financeapp.data.local.entity.DebtEntity
import kotlinx.coroutines.flow.Flow

class DebtRepository(private val debtDao: DebtDao) {
    
    val activeDebts: Flow<List<DebtEntity>> = debtDao.getActiveDebts()
    val allDebts: Flow<List<DebtEntity>> = debtDao.getAllDebts()
    val completedDebts: Flow<List<DebtEntity>> = debtDao.getCompletedDebts()
    val totalOutstandingDebt: Flow<Double?> = debtDao.getTotalOutstandingDebt()
    
    suspend fun getDebtById(id: String): DebtEntity? {
        return debtDao.getDebtById(id)
    }
    
    suspend fun insertDebt(debt: DebtEntity) {
        debtDao.insertDebt(debt)
    }
    
    suspend fun insertDebts(debts: List<DebtEntity>) {
        debtDao.insertDebts(debts)
    }
    
    suspend fun updateDebt(debt: DebtEntity) {
        debtDao.updateDebt(debt)
    }
    
    suspend fun updateDebtPayment(id: String, amount: Double) {
        debtDao.updateDebtPayment(id, amount)
    }
    
    suspend fun markDebtAsCompleted(id: String) {
        debtDao.markDebtAsCompleted(id)
    }
    
    suspend fun deleteDebt(debt: DebtEntity) {
        debtDao.deleteDebt(debt)
    }
    
    suspend fun deleteDebtById(id: String) {
        debtDao.deleteDebtById(id)
    }
    
    suspend fun getUnsyncedDebts(): List<DebtEntity> {
        return debtDao.getUnsyncedDebts()
    }
    
    suspend fun markAsSynced(id: String) {
        debtDao.markAsSynced(id)
    }
}
