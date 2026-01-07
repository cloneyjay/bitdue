package com.bitdue.financeapp.data.firebase

import com.bitdue.financeapp.data.local.entity.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class FirestoreSyncManager {
    
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    
    private val userId: String?
        get() = auth.currentUser?.uid
    
    private fun getUserCollection(collection: String) =
        userId?.let { firestore.collection("users").document(it).collection(collection) }
    
    // Transactions
    suspend fun syncTransaction(transaction: TransactionEntity): Result<Unit> {
        return try {
            getUserCollection("transactions")?.document(transaction.id)
                ?.set(transaction.toMap(), SetOptions.merge())?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun fetchTransactions(): Result<List<TransactionEntity>> {
        return try {
            val snapshot = getUserCollection("transactions")?.get()?.await()
            val transactions = snapshot?.documents?.mapNotNull { doc ->
                doc.toTransactionEntity()
            } ?: emptyList()
            Result.success(transactions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteTransaction(id: String): Result<Unit> {
        return try {
            getUserCollection("transactions")?.document(id)?.delete()?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Budgets
    suspend fun syncBudget(budget: BudgetEntity): Result<Unit> {
        return try {
            getUserCollection("budgets")?.document(budget.id)
                ?.set(budget.toMap(), SetOptions.merge())?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun fetchBudgets(): Result<List<BudgetEntity>> {
        return try {
            val snapshot = getUserCollection("budgets")?.get()?.await()
            val budgets = snapshot?.documents?.mapNotNull { doc ->
                doc.toBudgetEntity()
            } ?: emptyList()
            Result.success(budgets)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Goals
    suspend fun syncGoal(goal: GoalEntity): Result<Unit> {
        return try {
            getUserCollection("goals")?.document(goal.id)
                ?.set(goal.toMap(), SetOptions.merge())?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun fetchGoals(): Result<List<GoalEntity>> {
        return try {
            val snapshot = getUserCollection("goals")?.get()?.await()
            val goals = snapshot?.documents?.mapNotNull { doc ->
                doc.toGoalEntity()
            } ?: emptyList()
            Result.success(goals)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Categories
    suspend fun syncCategory(category: CategoryEntity): Result<Unit> {
        return try {
            getUserCollection("categories")?.document(category.id)
                ?.set(category.toMap(), SetOptions.merge())?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun fetchCategories(): Result<List<CategoryEntity>> {
        return try {
            val snapshot = getUserCollection("categories")?.get()?.await()
            val categories = snapshot?.documents?.mapNotNull { doc ->
                doc.toCategoryEntity()
            } ?: emptyList()
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Extension functions for entity conversion
private fun TransactionEntity.toMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "amount" to amount,
    "type" to type.name,
    "categoryId" to categoryId,
    "title" to title,
    "description" to description,
    "date" to date,
    "paymentMethod" to paymentMethod.name,
    "tags" to tags,
    "isRecurring" to isRecurring,
    "recurringPeriod" to recurringPeriod,
    "createdAt" to createdAt,
    "updatedAt" to updatedAt
)

private fun com.google.firebase.firestore.DocumentSnapshot.toTransactionEntity(): TransactionEntity? {
    return try {
        TransactionEntity(
            id = getString("id") ?: return null,
            amount = getDouble("amount") ?: 0.0,
            type = com.bitdue.financeapp.data.models.TransactionType.valueOf(getString("type") ?: "EXPENSE"),
            categoryId = getString("categoryId") ?: "",
            title = getString("title") ?: "",
            description = getString("description") ?: "",
            date = getLong("date") ?: 0L,
            paymentMethod = com.bitdue.financeapp.data.models.PaymentMethod.valueOf(getString("paymentMethod") ?: "CASH"),
            tags = getString("tags") ?: "",
            isRecurring = getBoolean("isRecurring") ?: false,
            recurringPeriod = getString("recurringPeriod"),
            isSynced = true,
            createdAt = getLong("createdAt") ?: System.currentTimeMillis(),
            updatedAt = getLong("updatedAt") ?: System.currentTimeMillis()
        )
    } catch (e: Exception) {
        null
    }
}

private fun BudgetEntity.toMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "categoryId" to categoryId,
    "limitAmount" to limitAmount,
    "period" to period,
    "startDate" to startDate,
    "endDate" to endDate,
    "alertThreshold" to alertThreshold,
    "isActive" to isActive,
    "createdAt" to createdAt,
    "updatedAt" to updatedAt
)

private fun com.google.firebase.firestore.DocumentSnapshot.toBudgetEntity(): BudgetEntity? {
    return try {
        BudgetEntity(
            id = getString("id") ?: return null,
            categoryId = getString("categoryId") ?: "",
            limitAmount = getDouble("limitAmount") ?: 0.0,
            period = getString("period") ?: "monthly",
            startDate = getLong("startDate") ?: 0L,
            endDate = getLong("endDate") ?: 0L,
            alertThreshold = getDouble("alertThreshold")?.toFloat() ?: 0.8f,
            isActive = getBoolean("isActive") ?: true,
            isSynced = true,
            createdAt = getLong("createdAt") ?: System.currentTimeMillis(),
            updatedAt = getLong("updatedAt") ?: System.currentTimeMillis()
        )
    } catch (e: Exception) {
        null
    }
}

private fun GoalEntity.toMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "name" to name,
    "targetAmount" to targetAmount,
    "currentAmount" to currentAmount,
    "deadline" to deadline,
    "icon" to icon,
    "color" to color,
    "isCompleted" to isCompleted,
    "createdAt" to createdAt,
    "updatedAt" to updatedAt
)

private fun com.google.firebase.firestore.DocumentSnapshot.toGoalEntity(): GoalEntity? {
    return try {
        GoalEntity(
            id = getString("id") ?: return null,
            name = getString("name") ?: "",
            targetAmount = getDouble("targetAmount") ?: 0.0,
            currentAmount = getDouble("currentAmount") ?: 0.0,
            deadline = getLong("deadline") ?: 0L,
            icon = getString("icon") ?: "🎯",
            color = getLong("color") ?: 0xFF4CAF50,
            isCompleted = getBoolean("isCompleted") ?: false,
            isSynced = true,
            createdAt = getLong("createdAt") ?: System.currentTimeMillis(),
            updatedAt = getLong("updatedAt") ?: System.currentTimeMillis()
        )
    } catch (e: Exception) {
        null
    }
}

private fun CategoryEntity.toMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "name" to name,
    "icon" to icon,
    "color" to color,
    "type" to type.name,
    "isDefault" to isDefault,
    "userId" to userId
)

private fun com.google.firebase.firestore.DocumentSnapshot.toCategoryEntity(): CategoryEntity? {
    return try {
        CategoryEntity(
            id = getString("id") ?: return null,
            name = getString("name") ?: "",
            icon = getString("icon") ?: "📦",
            color = getLong("color") ?: 0xFF95A5A6,
            type = com.bitdue.financeapp.data.models.TransactionType.valueOf(getString("type") ?: "EXPENSE"),
            isDefault = getBoolean("isDefault") ?: false,
            userId = getString("userId")
        )
    } catch (e: Exception) {
        null
    }
}
