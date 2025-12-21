package com.bitdue.financeapp.data.repository

import com.bitdue.financeapp.data.local.dao.TransactionDao
import com.bitdue.financeapp.data.local.entity.TransactionEntity
import com.bitdue.financeapp.data.models.TransactionType
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {
    
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    
    suspend fun getAllTransactionsList(): List<TransactionEntity> {
        return transactionDao.getAllTransactionsList()
    }
    
    fun getTransactionsByType(type: TransactionType): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsByType(type)
    }
    
    fun getTransactionsByCategory(categoryId: String): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsByCategory(categoryId)
    }
    
    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsByDateRange(startDate, endDate)
    }
    
    fun getTransactionsByCategoryAndDateRange(
        categoryId: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsByCategoryAndDateRange(categoryId, startDate, endDate)
    }
    
    fun searchTransactions(query: String): Flow<List<TransactionEntity>> {
        return transactionDao.searchTransactions(query)
    }
    
    fun getTotalByTypeAndDateRange(
        type: TransactionType,
        startDate: Long,
        endDate: Long
    ): Flow<Double?> {
        return transactionDao.getTotalByTypeAndDateRange(type, startDate, endDate)
    }
    
    suspend fun getTransactionById(id: String): TransactionEntity? {
        return transactionDao.getTransactionById(id)
    }
    
    suspend fun insertTransaction(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
    }
    
    suspend fun insertTransactions(transactions: List<TransactionEntity>) {
        transactionDao.insertTransactions(transactions)
    }
    
    suspend fun updateTransaction(transaction: TransactionEntity) {
        transactionDao.updateTransaction(transaction)
    }
    
    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
    }
    
    suspend fun deleteTransactionById(id: String) {
        transactionDao.deleteTransactionById(id)
    }
    
    suspend fun getUnsyncedTransactions(): List<TransactionEntity> {
        return transactionDao.getUnsyncedTransactions()
    }
    
    suspend fun markAsSynced(id: String) {
        transactionDao.markAsSynced(id)
    }
}
