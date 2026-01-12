package com.bitdue.financeapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey
    val id: String,
    val categoryId: String,
    val name: String? = null, // Optional custom budget name
    val limitAmount: Double,
    val period: String, // "monthly", "weekly", "quarterly", "yearly", "custom"
    val startDate: Long,
    val endDate: Long,
    val alertThreshold: Float = 0.8f, // Alert when 80% of budget is used
    val alertNotificationIds: String = "", // Comma-separated list of notification IDs already sent
    val isActive: Boolean = true,
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
