package com.bitdue.financeapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bitdue.financeapp.data.models.TransactionType

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val icon: String,
    val color: Long,
    val type: TransactionType,
    val isDefault: Boolean = false,
    val userId: String? = null // null for default categories
)
