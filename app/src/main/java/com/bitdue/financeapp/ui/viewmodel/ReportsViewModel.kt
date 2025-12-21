package com.bitdue.financeapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bitdue.financeapp.FinanceApp
import com.bitdue.financeapp.data.local.entity.CategoryEntity
import com.bitdue.financeapp.data.local.entity.TransactionEntity
import com.bitdue.financeapp.data.models.TransactionType
import com.bitdue.financeapp.data.repository.CategoryRepository
import com.bitdue.financeapp.data.repository.TransactionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset

enum class DateRangeFilter {
    MONTHLY,
    YEARLY,
    CUSTOM
}

data class CategorySpending(
    val category: CategoryEntity,
    val amount: Double,
    val percentage: Float,
    val transactionCount: Int
)

data class MonthlyData(
    val month: String,
    val income: Double,
    val expense: Double
)

data class ReportsUiState(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val netIncome: Double = 0.0,
    val categorySpending: List<CategorySpending> = emptyList(),
    val monthlyData: List<MonthlyData> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val selectedPeriod: String = "This Month",
    val selectedFilter: DateRangeFilter = DateRangeFilter.MONTHLY,
    val startDate: LocalDateTime = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0),
    val endDate: LocalDateTime = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59)
)

class ReportsViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            combine(
                transactionRepository.allTransactions,
                categoryRepository.allCategories
            ) { transactions, categories ->
                calculateReportsData(
                    transactions, 
                    categories, 
                    _uiState.value.startDate, 
                    _uiState.value.endDate
                )
            }.collect { state ->
                _uiState.value = state.copy(
                    selectedFilter = _uiState.value.selectedFilter,
                    startDate = _uiState.value.startDate,
                    endDate = _uiState.value.endDate,
                    selectedPeriod = _uiState.value.selectedPeriod
                )
            }
        }
    }
    
    fun updateDateFilter(filter: DateRangeFilter, customStart: LocalDateTime? = null, customEnd: LocalDateTime? = null) {
        val now = LocalDateTime.now()
        val (startDate, endDate, periodLabel) = when (filter) {
            DateRangeFilter.MONTHLY -> {
                val start = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
                val end = now.withHour(23).withMinute(59).withSecond(59)
                Triple(start, end, "This Month")
            }
            DateRangeFilter.YEARLY -> {
                val start = now.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0)
                val end = now.withHour(23).withMinute(59).withSecond(59)
                Triple(start, end, "This Year")
            }
            DateRangeFilter.CUSTOM -> {
                val start = customStart ?: now.minusMonths(1).withHour(0).withMinute(0).withSecond(0)
                val end = customEnd ?: now.withHour(23).withMinute(59).withSecond(59)
                Triple(start, end, "Custom Range")
            }
        }
        
        _uiState.value = _uiState.value.copy(
            selectedFilter = filter,
            startDate = startDate,
            endDate = endDate,
            selectedPeriod = periodLabel,
            isLoading = true
        )
        
        loadData()
    }
    
    private fun calculateReportsData(
        transactions: List<TransactionEntity>,
        categories: List<CategoryEntity>,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): ReportsUiState {
        val startMillis = startDate.toEpochSecond(ZoneOffset.UTC) * 1000
        val endMillis = endDate.toEpochSecond(ZoneOffset.UTC) * 1000
        
        val filteredTransactions = transactions.filter { it.date in startMillis..endMillis }
        
        val totalIncome = filteredTransactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }
        
        val totalExpense = filteredTransactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }
        
        val categorySpending = filteredTransactions
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.categoryId }
            .mapNotNull { (categoryId, trans) ->
                val category = categories.find { it.id == categoryId } ?: return@mapNotNull null
                val amount = trans.sumOf { it.amount }
                val percentage = if (totalExpense > 0) (amount / totalExpense * 100).toFloat() else 0f
                CategorySpending(
                    category = category,
                    amount = amount,
                    percentage = percentage,
                    transactionCount = trans.size
                )
            }
            .sortedByDescending { it.amount }
        
        // Calculate monthly data for last 6 months
        val now = LocalDateTime.now()
        val monthlyData = (0..5).map { monthsAgo ->
            val monthStart = now.minusMonths(monthsAgo.toLong())
                .withDayOfMonth(1)
                .withHour(0).withMinute(0).withSecond(0)
            val monthEnd = monthStart.plusMonths(1).minusSeconds(1)
            
            val monthStartMillis = monthStart.toEpochSecond(ZoneOffset.UTC) * 1000
            val monthEndMillis = monthEnd.toEpochSecond(ZoneOffset.UTC) * 1000
            
            val monthTrans = transactions.filter { it.date in monthStartMillis..monthEndMillis }
            val income = monthTrans.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
            val expense = monthTrans.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
            
            MonthlyData(
                month = monthStart.month.name.take(3),
                income = income,
                expense = expense
            )
        }.reversed()
        
        return ReportsUiState(
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            netIncome = totalIncome - totalExpense,
            categorySpending = categorySpending,
            monthlyData = monthlyData,
            isLoading = false
        )
    }
    
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = FinanceApp.instance
                return ReportsViewModel(
                    app.transactionRepository,
                    app.categoryRepository
                ) as T
            }
        }
    }
}
