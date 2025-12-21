package com.bitdue.financeapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey
    val id: String,
    val creditor: String,
    val totalAmount: Double,
    val paidAmount: Double = 0.0,
    val interestRate: Double = 0.0,
    val dueDate: Long,
    val description: String = "",
    val isCompleted: Boolean = false,
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
