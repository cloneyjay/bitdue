package com.bitdue.financeapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Fingerprint
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
import androidx.fragment.app.FragmentActivity
import com.bitdue.financeapp.utils.BiometricAuthManager

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Trigger sync if user is logged in
        if (FinanceApp.instance.authManager.isLoggedIn) {
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                FinanceApp.instance.dataSyncManager.syncAllData()
            }
        }
        
        setContent {
            val preferencesManager = remember { 
                com.bitdue.financeapp.data.preferences.UserPreferencesManager(applicationContext) 
            }
            val userPreferences by preferencesManager.userPreferencesFlow.collectAsState(
                initial = com.bitdue.financeapp.data.preferences.UserPreferences()
            )
            
            // Biometric authentication state
            var biometricAuthenticated by remember { mutableStateOf(false) }
            var showBiometricError by remember { mutableStateOf<String?>(null) }
            
            // Check if biometric should be shown
            val shouldShowBiometric = userPreferences.biometricEnabled && !biometricAuthenticated
            
            LaunchedEffect(shouldShowBiometric) {
                if (shouldShowBiometric) {
                    val biometricManager = BiometricAuthManager(this@MainActivity)
                    val status = biometricManager.isBiometricAvailable()
                    
                    if (status.isAvailable()) {
                        biometricManager.authenticate(
                            title = "Unlock BitDue Finance",
                            subtitle = "Authenticate to access your financial data",
                            onSuccess = {
                                biometricAuthenticated = true
                                showBiometricError = null
                            },
                            onError = { errorCode, errorMessage ->
                                if (errorCode == androidx.biometric.BiometricPrompt.ERROR_USER_CANCELED ||
                                    errorCode == androidx.biometric.BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                                    // User cancelled, exit app
                                    finish()
                                } else {
                                    showBiometricError = errorMessage
                                }
                            },
                            onFailed = {
                                showBiometricError = "Authentication failed. Please try again."
                            }
                        )
                    } else {
                        // Biometric not available, disable it
                        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                            preferencesManager.updateBiometricEnabled(false)
                        }
                        biometricAuthenticated = true
                    }
                }
            }
            
            FinanceAppTheme(
                darkTheme = userPreferences.darkTheme,
                dynamicColor = userPreferences.dynamicColors
            ) {
                if (shouldShowBiometric) {
                    // Show authentication screen
                    BiometricAuthScreen(
                        errorMessage = showBiometricError,
                        onRetry = {
                            showBiometricError = null
                            biometricAuthenticated = false
                        },
                        onCancel = { finish() }
                    )
                } else {
                    MainScreen()
                }
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
    
    // Get onboarding status
    val preferencesManager = remember { 
        com.bitdue.financeapp.data.preferences.UserPreferencesManager(FinanceApp.instance.applicationContext) 
    }
    val userPreferences by preferencesManager.userPreferencesFlow.collectAsState(
        initial = com.bitdue.financeapp.data.preferences.UserPreferences()
    )
    
    // Determine start destination based on onboarding and auth status
    val startDestination = when {
        !userPreferences.onboardingCompleted -> Screen.Onboarding.route
        FinanceApp.instance.authManager.isLoggedIn -> Screen.Home.route
        else -> Screen.Login.route
    }
    
    // Determine if bottom bar should be shown
    // Hide bottom bar on auth screens and onboarding
    val hideBottomBarRoutes = listOf(
        Screen.Onboarding.route,
        Screen.Login.route,
        Screen.SignUp.route
    )
    
    val showBottomBar = currentRoute in bottomNavItems.map { it.route } && 
                        currentRoute !in hideBottomBarRoutes
    
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                // Floating Bottom Navigation Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    androidx.compose.material3.Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                        tonalElevation = 0.dp,
                        shadowElevation = 0.dp,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            tonalElevation = 0.dp,
                            modifier = Modifier.fillMaxWidth()
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
            }
        }
    ) { paddingValues ->
        AppNavGraph(
            navController = navController,
            modifier = Modifier.padding(paddingValues),
            isUserAuthenticated = authUiState.isAuthenticated,
            startDestination = startDestination
        )
    }
}

@Composable
fun BiometricAuthScreen(
    errorMessage: String?,
    onRetry: () -> Unit,
    onCancel: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Filled.Fingerprint,
                    contentDescription = "Biometric Authentication",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "Biometric Authentication",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                if (errorMessage != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onCancel,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }
                        
                        Button(
                            onClick = onRetry,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Retry")
                        }
                    }
                } else {
                    Text(
                        text = "Authenticating...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

