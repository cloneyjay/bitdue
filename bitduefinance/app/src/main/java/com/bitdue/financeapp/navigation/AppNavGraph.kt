package com.bitdue.financeapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bitdue.financeapp.FinanceApp
import com.bitdue.financeapp.ui.screens.*
import com.bitdue.financeapp.ui.screens.auth.LoginScreen
import com.bitdue.financeapp.ui.screens.auth.SignUpScreen
import com.bitdue.financeapp.ui.viewmodel.*

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Login.route,
    isUserAuthenticated: Boolean = false
) {
    // Redirect to home if authenticated
    LaunchedEffect(isUserAuthenticated) {
        if (isUserAuthenticated && 
            (navController.currentDestination?.route == Screen.Login.route ||
             navController.currentDestination?.route == Screen.SignUp.route)) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Authentication Screens
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onSignUpSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            val transactionViewModel: TransactionViewModel = viewModel(factory = TransactionViewModel.Factory)
            val goalViewModel: GoalViewModel = viewModel(factory = GoalViewModel.Factory)
            HomeScreen(
                transactionViewModel = transactionViewModel,
                goalViewModel = goalViewModel,
                onNavigateToTransactions = {
                    navController.navigate(Screen.Transactions.route)
                },
                onNavigateToAddTransaction = {
                    navController.navigate(Screen.AddTransaction.route)
                },
                onNavigateToBudgets = {
                    navController.navigate(Screen.Budgets.route)
                },
                onNavigateToGoals = {
                    navController.navigate(Screen.Goals.route)
                },
                onNavigateToAddIncome = {
                    navController.navigate(Screen.AddTransaction.createRoute("income"))
                },
                onNavigateToAddExpense = {
                    navController.navigate(Screen.AddTransaction.createRoute("expense"))
                },
                onNavigateToEditTransaction = { transactionId ->
                    navController.navigate(Screen.AddTransaction.createRoute(transactionId = transactionId))
                }
            )
        }
        
        composable(Screen.Transactions.route) {
            val transactionViewModel: TransactionViewModel = viewModel(factory = TransactionViewModel.Factory)
            TransactionsScreen(
                transactionViewModel = transactionViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToEditTransaction = { transactionId ->
                    navController.navigate(Screen.AddTransaction.createRoute(transactionId = transactionId))
                }
            )
        }
        
        composable(Screen.Budgets.route) {
            val budgetViewModel: BudgetViewModel = viewModel(factory = BudgetViewModel.Factory)
            BudgetsScreen(
                budgetViewModel = budgetViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToAddBudget = {
                    navController.navigate(Screen.AddBudget.route)
                }
            )
        }
        
        composable(Screen.Reports.route) {
            val reportsViewModel: ReportsViewModel = viewModel(factory = ReportsViewModel.Factory)
            ReportsScreen(
                reportsViewModel = reportsViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Goals.route) {
            val goalViewModel: GoalViewModel = viewModel(factory = GoalViewModel.Factory)
            GoalsScreen(
                goalViewModel = goalViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToAddGoal = {
                    navController.navigate(Screen.AddGoal.route)
                },
                onNavigateToAddSavingsProgress = { goalId ->
                    navController.navigate(Screen.AddSavingsProgress.createRoute(goalId))
                }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToManageCategories = {
                    navController.navigate(Screen.ManageCategories.route)
                }
            )
        }
        
        composable(Screen.ManageCategories.route) {
            ManageCategoriesScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSignOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.AddTransaction.route) { backStackEntry ->
            val transactionViewModel: TransactionViewModel = viewModel(factory = TransactionViewModel.Factory)
            val typeParam = backStackEntry.arguments?.getString("type")
            val transactionId = backStackEntry.arguments?.getString("transactionId")
            val initialType = when (typeParam) {
                "income" -> com.bitdue.financeapp.data.models.TransactionType.INCOME
                "expense" -> com.bitdue.financeapp.data.models.TransactionType.EXPENSE
                else -> null
            }
            AddTransactionScreen(
                transactionViewModel = transactionViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                initialType = initialType,
                transactionId = transactionId
            )
        }
        
        composable(Screen.AddBudget.route) {
            val budgetViewModel: BudgetViewModel = viewModel(factory = BudgetViewModel.Factory)
            AddBudgetScreen(
                budgetViewModel = budgetViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.AddGoal.route) {
            val goalViewModel: GoalViewModel = viewModel(factory = GoalViewModel.Factory)
            AddGoalScreen(
                goalViewModel = goalViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.AddSavingsProgress.route) { backStackEntry ->
            val goalId = backStackEntry.arguments?.getString("goalId") ?: return@composable
            val goalViewModel: GoalViewModel = viewModel(factory = GoalViewModel.Factory)
            AddSavingsProgressScreen(
                goalId = goalId,
                goalViewModel = goalViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
