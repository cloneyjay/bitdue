package com.bitdue.financeapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bitdue.financeapp.FinanceApp
import com.bitdue.financeapp.data.local.entity.CategoryEntity
import com.bitdue.financeapp.data.models.TransactionType
import com.bitdue.financeapp.data.repository.CategoryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class CategoryUiState(
    val categories: List<CategoryEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val operationSuccess: Boolean = false
)

class CategoryViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()
    
    init {
        loadCategories()
    }
    
    private fun loadCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            categoryRepository.allCategories
                .catch { exception ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load categories"
                        )
                    }
                }
                .collect { categories ->
                    _uiState.update { 
                        it.copy(
                            categories = categories,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }
    
    fun getUserCategories(): List<CategoryEntity> {
        return _uiState.value.categories.filter { !it.isDefault }
    }
    
    fun getDefaultCategories(): List<CategoryEntity> {
        return _uiState.value.categories.filter { it.isDefault }
    }
    
    fun createCategory(
        name: String,
        icon: String,
        color: Long,
        type: TransactionType
    ) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                
                val category = CategoryEntity(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    icon = icon,
                    color = color,
                    type = type,
                    isDefault = false,
                    userId = FinanceApp.instance.authManager.currentUser?.uid
                )
                
                categoryRepository.insertCategory(category)
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        operationSuccess = true,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to create category"
                    )
                }
            }
        }
    }
    
    fun updateCategory(category: CategoryEntity) {
        viewModelScope.launch {
            try {
                // Prevent updating default categories
                if (category.isDefault) {
                    _uiState.update { 
                        it.copy(error = "Cannot modify default categories")
                    }
                    return@launch
                }
                
                _uiState.update { it.copy(isLoading = true) }
                categoryRepository.updateCategory(category)
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        operationSuccess = true,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to update category"
                    )
                }
            }
        }
    }
    
    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            try {
                val category = _uiState.value.categories.find { it.id == categoryId }
                
                // Prevent deleting default categories
                if (category?.isDefault == true) {
                    _uiState.update { 
                        it.copy(error = "Cannot delete default categories")
                    }
                    return@launch
                }
                
                _uiState.update { it.copy(isLoading = true) }
                categoryRepository.deleteCategoryById(categoryId)
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        operationSuccess = true,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to delete category"
                    )
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    fun clearOperationSuccess() {
        _uiState.update { it.copy(operationSuccess = false) }
    }
    
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CategoryViewModel(
                    FinanceApp.instance.categoryRepository
                ) as T
            }
        }
    }
}
