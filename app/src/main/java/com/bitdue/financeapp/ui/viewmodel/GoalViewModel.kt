package com.bitdue.financeapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bitdue.financeapp.FinanceApp
import com.bitdue.financeapp.data.local.entity.GoalEntity
import com.bitdue.financeapp.data.repository.GoalRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

data class GoalUiState(
    val activeGoals: List<GoalEntity> = emptyList(),
    val completedGoals: List<GoalEntity> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val totalSaved: Double = 0.0,
    val totalTarget: Double = 0.0
)

class GoalViewModel(
    private val goalRepository: GoalRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(GoalUiState())
    val uiState: StateFlow<GoalUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            combine(
                goalRepository.activeGoals,
                goalRepository.completedGoals
            ) { active, completed ->
                val totalSaved = active.sumOf { it.currentAmount }
                val totalTarget = active.sumOf { it.targetAmount }
                
                GoalUiState(
                    activeGoals = active,
                    completedGoals = completed,
                    isLoading = false,
                    totalSaved = totalSaved,
                    totalTarget = totalTarget
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
    
    fun addGoal(
        name: String,
        targetAmount: Double,
        deadline: LocalDateTime,
        icon: String = "🎯",
        color: Long = 0xFF4CAF50
    ) {
        viewModelScope.launch {
            val goal = GoalEntity(
                id = UUID.randomUUID().toString(),
                name = name,
                targetAmount = targetAmount,
                currentAmount = 0.0,
                deadline = deadline.toEpochSecond(ZoneOffset.UTC) * 1000,
                icon = icon,
                color = color
            )
            goalRepository.insertGoal(goal)
        }
    }
    
    fun updateGoalProgress(id: String, amount: Double) {
        viewModelScope.launch {
            val goal = goalRepository.getGoalById(id)
            if (goal != null) {
                val newAmount = (goal.currentAmount + amount).coerceAtLeast(0.0)
                goalRepository.updateGoalProgress(id, newAmount)
                
                if (newAmount >= goal.targetAmount) {
                    goalRepository.markGoalAsCompleted(id)
                }
            }
        }
    }
    
    fun updateGoal(goal: GoalEntity) {
        viewModelScope.launch {
            goalRepository.updateGoal(
                goal.copy(updatedAt = System.currentTimeMillis())
            )
        }
    }
    
    fun deleteGoal(id: String) {
        viewModelScope.launch {
            goalRepository.deleteGoalById(id)
        }
    }
    
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = FinanceApp.instance
                return GoalViewModel(app.goalRepository) as T
            }
        }
    }
}
