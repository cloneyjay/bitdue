package com.bitdue.financeapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bitdue.financeapp.FinanceApp
import com.bitdue.financeapp.data.local.entity.BudgetEntity
import com.bitdue.financeapp.data.local.entity.CategoryEntity
import com.bitdue.financeapp.data.local.entity.TransactionEntity
import com.bitdue.financeapp.data.models.TransactionType
import com.bitdue.financeapp.data.repository.BudgetRepository
import com.bitdue.financeapp.data.repository.CategoryRepository
import com.bitdue.financeapp.data.repository.TransactionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

data class BudgetWithSpent(
    val budget: BudgetEntity,
    val category: CategoryEntity?,
    val spent: Double,
    val percentage: Float
)

data class BudgetUiState(
    val budgets: List<BudgetWithSpent> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class BudgetViewModel(
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(BudgetUiState())
    val uiState: StateFlow<BudgetUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            combine(
                budgetRepository.activeBudgets,
                categoryRepository.allCategories,
                transactionRepository.allTransactions
            ) { budgets, categories, transactions ->
                val budgetsWithSpent = budgets.map { budget ->
                    val category = categories.find { it.id == budget.categoryId }
                    val spent = transactions
                        .filter { 
                            it.categoryId == budget.categoryId && 
                            it.type == TransactionType.EXPENSE &&
                            it.date >= budget.startDate && 
                            it.date <= budget.endDate 
                        }
                        .sumOf { it.amount }
                    
                    val percentage = if (budget.limitAmount > 0) {
                        (spent / budget.limitAmount).toFloat().coerceIn(0f, 1f)
                    } else 0f
                    
                    BudgetWithSpent(
                        budget = budget,
                        category = category,
                        spent = spent,
                        percentage = percentage
                    )
                }
                
                BudgetUiState(
                    budgets = budgetsWithSpent,
                    categories = categories.filter { it.type == TransactionType.EXPENSE },
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
    
    fun addBudget(
        categoryId: String,
        limit: Double,
        period: String = "monthly",
        alertThreshold: Float = 0.8f,
        name: String? = null,
        customStartDate: Long? = null,
        customEndDate: Long? = null
    ) {
        viewModelScope.launch {
            // Check for existing active budget for this category
            val existingBudget = budgetRepository.getActiveBudgetForCategory(categoryId)
            if (existingBudget != null) {
                _uiState.value = _uiState.value.copy(
                    error = "An active budget already exists for this category"
                )
                return@launch
            }
            
            val now = LocalDateTime.now()
            val startDate: Long
            val endDate: Long
            
            if (customStartDate != null && customEndDate != null) {
                startDate = customStartDate
                endDate = customEndDate
            } else {
                when (period.lowercase()) {
                    "weekly" -> {
                        val start = now.with(java.time.DayOfWeek.MONDAY)
                            .withHour(0).withMinute(0).withSecond(0)
                        val end = start.plusWeeks(1).minusSeconds(1)
                        startDate = start.toEpochSecond(ZoneOffset.UTC) * 1000
                        endDate = end.toEpochSecond(ZoneOffset.UTC) * 1000
                    }
                    "quarterly" -> {
                        val month = now.monthValue
                        val quarterStart = ((month - 1) / 3) * 3 + 1
                        val start = now.withMonth(quarterStart).withDayOfMonth(1)
                            .withHour(0).withMinute(0).withSecond(0)
                        val end = start.plusMonths(3).minusSeconds(1)
                        startDate = start.toEpochSecond(ZoneOffset.UTC) * 1000
                        endDate = end.toEpochSecond(ZoneOffset.UTC) * 1000
                    }
                    "yearly" -> {
                        val start = now.withMonth(1).withDayOfMonth(1)
                            .withHour(0).withMinute(0).withSecond(0)
                        val end = start.plusYears(1).minusSeconds(1)
                        startDate = start.toEpochSecond(ZoneOffset.UTC) * 1000
                        endDate = end.toEpochSecond(ZoneOffset.UTC) * 1000
                    }
                    else -> { // monthly (default)
                        val start = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
                        val end = now.withDayOfMonth(now.toLocalDate().lengthOfMonth())
                            .withHour(23).withMinute(59).withSecond(59)
                        startDate = start.toEpochSecond(ZoneOffset.UTC) * 1000
                        endDate = end.toEpochSecond(ZoneOffset.UTC) * 1000
                    }
                }
            }
            
            val budget = BudgetEntity(
                id = UUID.randomUUID().toString(),
                categoryId = categoryId,
                name = name,
                limitAmount = limit,
                period = period,
                startDate = startDate,
                endDate = endDate,
                alertThreshold = alertThreshold
            )
            budgetRepository.insertBudget(budget)
            
            // Clear error on successful add
            _uiState.value = _uiState.value.copy(error = null)
        }
    }
    
    fun updateBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            budgetRepository.updateBudget(
                budget.copy(updatedAt = System.currentTimeMillis())
            )
        }
    }
    
    fun deleteBudget(id: String) {
        viewModelScope.launch {
            budgetRepository.deleteBudgetById(id)
        }
    }
    
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = FinanceApp.instance
                return BudgetViewModel(
                    app.budgetRepository,
                    app.categoryRepository,
                    app.transactionRepository
                ) as T
            }
        }
    }
}
