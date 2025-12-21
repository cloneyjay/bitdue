package com.bitdue.financeapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bitdue.financeapp.FinanceApp
import com.bitdue.financeapp.data.local.entity.CategoryEntity
import com.bitdue.financeapp.data.local.entity.TransactionEntity
import com.bitdue.financeapp.data.models.PaymentMethod
import com.bitdue.financeapp.data.models.TransactionType
import com.bitdue.financeapp.data.repository.CategoryRepository
import com.bitdue.financeapp.data.repository.TransactionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

data class TransactionUiState(
    val transactions: List<TransactionEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0
)

class TransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            combine(
                transactionRepository.allTransactions,
                categoryRepository.allCategories
            ) { transactions, categories ->
                val income = transactions
                    .filter { it.type == TransactionType.INCOME }
                    .sumOf { it.amount }
                val expense = transactions
                    .filter { it.type == TransactionType.EXPENSE }
                    .sumOf { it.amount }
                
                TransactionUiState(
                    transactions = transactions,
                    categories = categories,
                    isLoading = false,
                    totalIncome = income,
                    totalExpense = expense,
                    balance = income - expense
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
    
    fun addTransaction(
        amount: Double,
        type: TransactionType,
        categoryId: String,
        title: String,
        description: String = "",
        date: LocalDateTime = LocalDateTime.now(),
        paymentMethod: PaymentMethod = PaymentMethod.CASH,
        tags: List<String> = emptyList(),
        isRecurring: Boolean = false,
        recurringPeriod: String? = null
    ) {
        viewModelScope.launch {
            val transaction = TransactionEntity(
                id = UUID.randomUUID().toString(),
                amount = amount,
                type = type,
                categoryId = categoryId,
                title = title,
                description = description,
                date = date.toEpochSecond(ZoneOffset.UTC) * 1000,
                paymentMethod = paymentMethod,
                tags = tags.joinToString(","),
                isRecurring = isRecurring,
                recurringPeriod = recurringPeriod
            )
            transactionRepository.insertTransaction(transaction)
        }
    }
    
    fun updateTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            transactionRepository.updateTransaction(
                transaction.copy(updatedAt = System.currentTimeMillis())
            )
        }
    }
    
    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            transactionRepository.deleteTransactionById(id)
        }
    }
    
    fun searchTransactions(query: String): Flow<List<TransactionEntity>> {
        return transactionRepository.searchTransactions(query)
    }
    
    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<TransactionEntity>> {
        return transactionRepository.getTransactionsByDateRange(startDate, endDate)
    }
    
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = FinanceApp.instance
                return TransactionViewModel(
                    app.transactionRepository,
                    app.categoryRepository
                ) as T
            }
        }
    }
}
