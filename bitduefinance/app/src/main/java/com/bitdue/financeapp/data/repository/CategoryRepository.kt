package com.bitdue.financeapp.data.repository

import com.bitdue.financeapp.data.local.dao.CategoryDao
import com.bitdue.financeapp.data.local.entity.CategoryEntity
import com.bitdue.financeapp.data.models.TransactionType
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDao) {
    
    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()
    
    suspend fun getAllCategoriesList(): List<CategoryEntity> {
        return categoryDao.getAllCategoriesList()
    }
    
    fun getCategoriesByType(type: TransactionType): Flow<List<CategoryEntity>> {
        return categoryDao.getCategoriesByType(type)
    }
    
    fun getDefaultCategories(): Flow<List<CategoryEntity>> {
        return categoryDao.getDefaultCategories()
    }
    
    fun getCategoriesForUser(userId: String): Flow<List<CategoryEntity>> {
        return categoryDao.getCategoriesForUser(userId)
    }
    
    suspend fun getCategoryById(id: String): CategoryEntity? {
        return categoryDao.getCategoryById(id)
    }
    
    suspend fun insertCategory(category: CategoryEntity) {
        categoryDao.insertCategory(category)
    }
    
    suspend fun insertCategories(categories: List<CategoryEntity>) {
        categoryDao.insertCategories(categories)
    }
    
    suspend fun updateCategory(category: CategoryEntity) {
        categoryDao.updateCategory(category)
    }
    
    suspend fun deleteCategory(category: CategoryEntity) {
        categoryDao.deleteCategory(category)
    }
    
    suspend fun deleteCategoryById(id: String) {
        categoryDao.deleteCategoryById(id)
    }
}
