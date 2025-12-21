package com.bitdue.financeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bitdue.financeapp.navigation.AppNavGraph
import com.bitdue.financeapp.navigation.Screen
import com.bitdue.financeapp.navigation.bottomNavItems
import kotlinx.coroutines.launch
import com.bitdue.financeapp.ui.theme.FinanceAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Trigger sync if user is logged in
        if (FinanceApp.instance.authManager.isLoggedIn) {
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                FinanceApp.instance.dataSyncManager.syncAllData()
            }
        }
        
        setContent {
            FinanceAppTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Auth state
    val authViewModel: com.bitdue.financeapp.ui.viewmodel.AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = com.bitdue.financeapp.ui.viewmodel.AuthViewModel.Factory
    )
    val authUiState by authViewModel.uiState.collectAsState()
    
    // Determine if bottom bar should be shown
    // Hide bottom bar on auth screens
    val hideBottomBarRoutes = listOf(
        Screen.Login.route,
        Screen.SignUp.route
    )
    
    val showBottomBar = currentRoute in bottomNavItems.map { it.route } && 
                        currentRoute !in hideBottomBarRoutes
    
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (currentRoute == item.route) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            selected = currentRoute == item.route,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        AppNavGraph(
            navController = navController,
            modifier = Modifier.padding(paddingValues),
            isUserAuthenticated = authUiState.isAuthenticated,
            startDestination = if (FinanceApp.instance.authManager.isLoggedIn) Screen.Home.route else Screen.Login.route
        )
    }
}
