package com.bitdue.financeapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Home : Screen("home")
    object Transactions : Screen("transactions")
    object Budgets : Screen("budgets")
    object Reports : Screen("reports")
    object Goals : Screen("goals")
    object Settings : Screen("settings")
    object Profile : Screen("profile")
    object AddTransaction : Screen("add_transaction?type={type}&transactionId={transactionId}") {
        fun createRoute(type: String? = null, transactionId: String? = null): String {
            var route = "add_transaction"
            val params = mutableListOf<String>()
            if (type != null) params.add("type=$type")
            if (transactionId != null) params.add("transactionId=$transactionId")
            if (params.isNotEmpty()) route += "?" + params.joinToString("&")
            return route
        }
    }
    object AddBudget : Screen("add_budget")
    object AddGoal : Screen("add_goal")
    object AddSavingsProgress : Screen("add_savings_progress/{goalId}") {
        fun createRoute(goalId: String) = "add_savings_progress/$goalId"
    }
}

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(Screen.Home.route, "Home", Icons.Filled.Home)
    object Reports : BottomNavItem(Screen.Reports.route, "Reports", Icons.Filled.BarChart)
    object Goals : BottomNavItem(Screen.Goals.route, "Savings", Icons.Filled.Stars)
    object Settings : BottomNavItem(Screen.Settings.route, "Settings", Icons.Filled.Settings)
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Reports,
    BottomNavItem.Goals,
    BottomNavItem.Settings
)
