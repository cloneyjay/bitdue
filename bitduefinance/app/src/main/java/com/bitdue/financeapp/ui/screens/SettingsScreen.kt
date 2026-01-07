package com.bitdue.financeapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.bitdue.financeapp.FinanceApp
import com.bitdue.financeapp.data.preferences.UserPreferencesManager
import com.bitdue.financeapp.data.preferences.UserPreferences
import com.bitdue.financeapp.utils.PermissionUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToManageCategories: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val preferencesManager = remember { UserPreferencesManager(FinanceApp.instance.applicationContext) }
    val userPreferences by preferencesManager.userPreferencesFlow.collectAsState(initial = UserPreferences())
    val scope = rememberCoroutineScope()
    
    var showBiometricRationale by remember { mutableStateOf(false) }
    var showNotificationRationale by remember { mutableStateOf(false) }
    
    // Biometric permission launcher
    val biometricPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        scope.launch {
            if (isGranted) {
                preferencesManager.updateBiometricEnabled(true)
            } else {
                preferencesManager.updateBiometricEnabled(false)
                showBiometricRationale = true
            }
        }
    }
    
    // Notification permission launcher
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        scope.launch {
            if (isGranted) {
                preferencesManager.updateNotificationsEnabled(true)
            } else {
                preferencesManager.updateNotificationsEnabled(false)
                showNotificationRationale = true
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Account Section
            item {
                Text(
                    text = "Account",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Person,
                    title = "Profile",
                    subtitle = "Manage your account and preferences",
                    onClick = onNavigateToProfile
                )
            }
            
            // Appearance Section
            item {
                Text(
                    text = "Appearance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }
            
            item {
                SettingsSwitchItem(
                    icon = Icons.Default.DarkMode,
                    title = "Dark Mode",
                    subtitle = "Toggle dark/light theme",
                    checked = userPreferences.darkTheme,
                    onCheckedChange = { enabled ->
                        scope.launch {
                            preferencesManager.updateDarkTheme(enabled)
                        }
                    }
                )
            }
            
            item {
                SettingsSwitchItem(
                    icon = Icons.Default.Palette,
                    title = "Dynamic Colors",
                    subtitle = "Use system color scheme (Android 12+)",
                    checked = userPreferences.dynamicColors,
                    onCheckedChange = { enabled ->
                        scope.launch {
                            preferencesManager.updateDynamicColors(enabled)
                        }
                    }
                )
            }
            
            // Customization Section
            item {
                Text(
                    text = "Customization",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Category,
                    title = "Manage Categories",
                    subtitle = "Create and customize your own categories",
                    onClick = onNavigateToManageCategories
                )
            }
            
            // Security Section
            item {
                Text(
                    text = "Security",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }
            
            item {
                SettingsSwitchItem(
                    icon = Icons.Default.Fingerprint,
                    title = "Biometric Authentication",
                    subtitle = "Use fingerprint or face ID",
                    checked = userPreferences.biometricEnabled,
                    onCheckedChange = { enabled ->
                        if (enabled) {
                            // Check if permission is already granted
                            if (PermissionUtils.checkBiometricPermission(context)) {
                                scope.launch {
                                    preferencesManager.updateBiometricEnabled(true)
                                }
                            } else {
                                // Request permission
                                biometricPermissionLauncher.launch(PermissionUtils.getBiometricPermission())
                            }
                        } else {
                            scope.launch {
                                preferencesManager.updateBiometricEnabled(false)
                            }
                        }
                    }
                )
            }
            
            // Notifications Section
            item {
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }
            
            item {
                SettingsSwitchItem(
                    icon = Icons.Default.Notifications,
                    title = "Push Notifications",
                    subtitle = "Get alerts and reminders",
                    checked = userPreferences.notificationsEnabled,
                    onCheckedChange = { enabled ->
                        if (enabled) {
                            // Check if permission is granted
                            if (PermissionUtils.checkNotificationPermission(context)) {
                                scope.launch {
                                    preferencesManager.updateNotificationsEnabled(true)
                                }
                            } else {
                                // Request permission (Android 13+)
                                val permission = PermissionUtils.getNotificationPermission()
                                if (permission != null) {
                                    notificationPermissionLauncher.launch(permission)
                                } else {
                                    // Pre-Android 13, no permission needed
                                    scope.launch {
                                        preferencesManager.updateNotificationsEnabled(true)
                                    }
                                }
                            }
                        } else {
                            scope.launch {
                                preferencesManager.updateNotificationsEnabled(false)
                            }
                        }
                    }
                )
            }
            
            item {
                SettingsSwitchItem(
                    icon = Icons.Default.Warning,
                    title = "Budget Alerts",
                    subtitle = "Notify when approaching budget limits",
                    checked = userPreferences.budgetAlertsEnabled,
                    onCheckedChange = { enabled ->
                        scope.launch {
                            preferencesManager.updateBudgetAlertsEnabled(enabled)
                        }
                    }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        
        // Biometric permission rationale dialog
        if (showBiometricRationale) {
            AlertDialog(
                onDismissRequest = { showBiometricRationale = false },
                title = { Text("Biometric Permission Required") },
                text = { Text("Biometric authentication requires permission to use your device's fingerprint or face recognition. Please enable it in your device settings.") },
                confirmButton = {
                    TextButton(onClick = { showBiometricRationale = false }) {
                        Text("OK")
                    }
                }
            )
        }
        
        // Notification permission rationale dialog
        if (showNotificationRationale) {
            AlertDialog(
                onDismissRequest = { showNotificationRationale = false },
                title = { Text("Notification Permission Required") },
                text = { Text("Push notifications require permission to send you alerts and reminders. Please enable it in your device settings.") },
                confirmButton = {
                    TextButton(onClick = { showNotificationRationale = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingsSwitchItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}
