package com.bitdue.financeapp.data.models

import java.time.LocalDateTime

enum class TransactionType {
    INCOME,
    EXPENSE
}

enum class PaymentMethod {
    CASH,
    DEBIT_CARD,
    CREDIT_CARD,
    BANK_TRANSFER,
    DIGITAL_WALLET,
    OTHER
}

data class Category(
    val id: String,
    val name: String,
    val icon: String,
    val color: Long,
    val type: TransactionType
)

data class Transaction(
    val id: String,
    val amount: Double,
    val type: TransactionType,
    val category: Category,
    val title: String,
    val description: String = "",
    val date: LocalDateTime,
    val paymentMethod: PaymentMethod,
    val tags: List<String> = emptyList(),
    val isRecurring: Boolean = false,
    val recurringPeriod: String? = null
)

data class Budget(
    val id: String,
    val category: Category,
    val limit: Double,
    val spent: Double,
    val period: String, // "monthly", "weekly"
    val startDate: LocalDateTime,
    val endDate: LocalDateTime
)

data class Debt(
    val id: String,
    val creditor: String,
    val totalAmount: Double,
    val paidAmount: Double,
    val interestRate: Double,
    val dueDate: LocalDateTime,
    val description: String
)

data class Goal(
    val id: String,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val deadline: LocalDateTime,
    val icon: String,
    val color: Long
)
