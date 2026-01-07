package com.bitdue.financeapp.data

import com.bitdue.financeapp.data.models.*
import com.bitdue.financeapp.ui.theme.*
import java.time.LocalDateTime

object SampleData {
    
    val categories = listOf(
        Category("1", "Food & Dining", "🍔", CategoryDining.value.toLong(), TransactionType.EXPENSE),
        Category("2", "Transport", "🚗", CategoryTransport.value.toLong(), TransactionType.EXPENSE),
        Category("3", "Shopping", "🛍️", CategoryShopping.value.toLong(), TransactionType.EXPENSE),
        Category("4", "Entertainment", "🎬", CategoryEntertainment.value.toLong(), TransactionType.EXPENSE),
        Category("5", "Utilities", "💡", CategoryUtilities.value.toLong(), TransactionType.EXPENSE),
        Category("6", "Health", "⚕️", CategoryHealth.value.toLong(), TransactionType.EXPENSE),
        Category("7", "Education", "📚", CategoryEducation.value.toLong(), TransactionType.EXPENSE),
        Category("8", "Salary", "💰", CategoryIncome.value.toLong(), TransactionType.INCOME),
        Category("9", "Freelance", "💼", CategoryIncome.value.toLong(), TransactionType.INCOME),
        Category("10", "Investment", "📈", CategoryInvestment.value.toLong(), TransactionType.INCOME),
    )
    
    val transactions = listOf(
        Transaction(
            id = "1",
            amount = 45.50,
            type = TransactionType.EXPENSE,
            category = categories[0],
            title = "Lunch at Restaurant",
            description = "Business lunch with client",
            date = LocalDateTime.now().minusHours(2),
            paymentMethod = PaymentMethod.CREDIT_CARD,
            tags = listOf("business", "food")
        ),
        Transaction(
            id = "2",
            amount = 3500.00,
            type = TransactionType.INCOME,
            category = categories[7],
            title = "Monthly Salary",
            description = "December salary",
            date = LocalDateTime.now().minusDays(1),
            paymentMethod = PaymentMethod.BANK_TRANSFER,
            tags = listOf("salary"),
            isRecurring = true,
            recurringPeriod = "monthly"
        ),
        Transaction(
            id = "3",
            amount = 25.00,
            type = TransactionType.EXPENSE,
            category = categories[1],
            title = "Uber Ride",
            description = "Ride to office",
            date = LocalDateTime.now().minusHours(5),
            paymentMethod = PaymentMethod.DIGITAL_WALLET,
            tags = listOf("transport")
        ),
        Transaction(
            id = "4",
            amount = 150.00,
            type = TransactionType.EXPENSE,
            category = categories[2],
            title = "New Shoes",
            description = "Nike Air Max",
            date = LocalDateTime.now().minusDays(2),
            paymentMethod = PaymentMethod.DEBIT_CARD,
            tags = listOf("shopping", "clothing")
        ),
        Transaction(
            id = "5",
            amount = 500.00,
            type = TransactionType.INCOME,
            category = categories[8],
            title = "Freelance Project",
            description = "Website development",
            date = LocalDateTime.now().minusDays(3),
            paymentMethod = PaymentMethod.BANK_TRANSFER,
            tags = listOf("freelance", "web")
        ),
        Transaction(
            id = "6",
            amount = 12.99,
            type = TransactionType.EXPENSE,
            category = categories[3],
            title = "Netflix Subscription",
            description = "Monthly subscription",
            date = LocalDateTime.now().minusDays(5),
            paymentMethod = PaymentMethod.CREDIT_CARD,
            tags = listOf("subscription"),
            isRecurring = true,
            recurringPeriod = "monthly"
        ),
        Transaction(
            id = "7",
            amount = 85.00,
            type = TransactionType.EXPENSE,
            category = categories[4],
            title = "Electricity Bill",
            description = "November bill",
            date = LocalDateTime.now().minusDays(7),
            paymentMethod = PaymentMethod.BANK_TRANSFER,
            tags = listOf("bills", "utilities")
        ),
        Transaction(
            id = "8",
            amount = 60.00,
            type = TransactionType.EXPENSE,
            category = categories[0],
            title = "Grocery Shopping",
            description = "Weekly groceries",
            date = LocalDateTime.now().minusDays(4),
            paymentMethod = PaymentMethod.CASH,
            tags = listOf("food", "groceries")
        )
    )
    
    val budgets = listOf(
        Budget(
            id = "1",
            category = categories[0],
            limit = 500.00,
            spent = 315.50,
            period = "monthly",
            startDate = LocalDateTime.now().withDayOfMonth(1),
            endDate = LocalDateTime.now().withDayOfMonth(31)
        ),
        Budget(
            id = "2",
            category = categories[1],
            limit = 200.00,
            spent = 125.00,
            period = "monthly",
            startDate = LocalDateTime.now().withDayOfMonth(1),
            endDate = LocalDateTime.now().withDayOfMonth(31)
        ),
        Budget(
            id = "3",
            category = categories[2],
            limit = 300.00,
            spent = 250.00,
            period = "monthly",
            startDate = LocalDateTime.now().withDayOfMonth(1),
            endDate = LocalDateTime.now().withDayOfMonth(31)
        ),
        Budget(
            id = "4",
            category = categories[3],
            limit = 100.00,
            spent = 42.99,
            period = "monthly",
            startDate = LocalDateTime.now().withDayOfMonth(1),
            endDate = LocalDateTime.now().withDayOfMonth(31)
        )
    )
    
    val goals = listOf(
        Goal(
            id = "1",
            name = "Emergency Fund",
            targetAmount = 10000.00,
            currentAmount = 4500.00,
            deadline = LocalDateTime.now().plusMonths(12),
            icon = "🏦",
            color = CategoryInvestment.value.toLong()
        ),
        Goal(
            id = "2",
            name = "Vacation to Japan",
            targetAmount = 5000.00,
            currentAmount = 2300.00,
            deadline = LocalDateTime.now().plusMonths(8),
            icon = "✈️",
            color = CategoryEntertainment.value.toLong()
        ),
        Goal(
            id = "3",
            name = "New Laptop",
            targetAmount = 1500.00,
            currentAmount = 900.00,
            deadline = LocalDateTime.now().plusMonths(3),
            icon = "💻",
            color = CategoryShopping.value.toLong()
        )
    )
    
    val debts = listOf(
        Debt(
            id = "1",
            creditor = "Student Loan",
            totalAmount = 15000.00,
            paidAmount = 3500.00,
            interestRate = 4.5,
            dueDate = LocalDateTime.now().plusYears(3),
            description = "Education loan"
        ),
        Debt(
            id = "2",
            creditor = "Car Loan",
            totalAmount = 20000.00,
            paidAmount = 8000.00,
            interestRate = 6.2,
            dueDate = LocalDateTime.now().plusYears(2),
            description = "Vehicle financing"
        )
    )
}
