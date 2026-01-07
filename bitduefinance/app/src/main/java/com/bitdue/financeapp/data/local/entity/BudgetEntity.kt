package com.bitdue.financeapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey
    val id: String,
    val categoryId: String,
    val limitAmount: Double,
    val period: String, // "monthly", "weekly"
    val startDate: Long,
    val endDate: Long,
    val alertThreshold: Float = 0.8f, // Alert when 80% of budget is used
    val isActive: Boolean = true,
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
