package com.bitdue.financeapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bitdue.financeapp.data.models.PaymentMethod
import com.bitdue.financeapp.data.models.TransactionType

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val amount: Double,
    val type: TransactionType,
    val categoryId: String,
    val title: String,
    val description: String = "",
    val date: Long, // Stored as epoch millis
    val paymentMethod: PaymentMethod,
    val tags: String = "", // Comma-separated tags
    val isRecurring: Boolean = false,
    val recurringPeriod: String? = null,
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
