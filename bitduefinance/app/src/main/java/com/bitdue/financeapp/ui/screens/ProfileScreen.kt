package com.bitdue.financeapp.ui.screens

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.bitdue.financeapp.FinanceApp
import com.bitdue.financeapp.data.preferences.UserPreferencesManager
import com.bitdue.financeapp.data.preferences.UserPreferences
import com.bitdue.financeapp.ui.components.ProfilePictureDialog
import com.bitdue.financeapp.ui.components.getAvatarEmoji
import com.bitdue.financeapp.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onSignOut: () -> Unit,
    authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory)
) {
    val authManager = FinanceApp.instance.authManager
    val syncManager = FinanceApp.instance.dataSyncManager
    val preferencesManager = remember { UserPreferencesManager(FinanceApp.instance.applicationContext) }
    val userPreferences by preferencesManager.userPreferencesFlow.collectAsState(initial = UserPreferences())
    val syncState by syncManager.syncState.collectAsState()
    val currentUser = authManager.currentUser
    
    val scope = rememberCoroutineScope()
    
    var showSignOutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditNickname by remember { mutableStateOf(false) }
    var showProfilePictureDialog by remember { mutableStateOf(false) }
    var nicknameInput by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Text("←", style = MaterialTheme.typography.headlineMedium)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Picture with Edit Button
            Box(
                modifier = Modifier.size(120.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .clickable { showProfilePictureDialog = true },
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        when {
                            userPreferences.profilePictureUrl.startsWith("avatar_") -> {
                                Text(
                                    text = getAvatarEmoji(userPreferences.profilePictureUrl),
                                    style = MaterialTheme.typography.displayLarge
                                )
                            }
                            userPreferences.profilePictureUrl.isNotBlank() -> {
                                AsyncImage(
                                    model = userPreferences.profilePictureUrl,
                                    contentDescription = "Profile picture",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            else -> {
                                Text(
                                    text = currentUser?.displayName?.firstOrNull()?.uppercase() ?: "👤",
                                    style = MaterialTheme.typography.displayLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
                
                // Edit Button
                SmallFloatingActionButton(
                    onClick = { showProfilePictureDialog = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-4).dp, y = (-4).dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit profile picture",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // User Name
            Text(
                text = currentUser?.displayName ?: "User",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            // Email
            Text(
                text = currentUser?.email ?: "",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Nickname Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Nickname",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (userPreferences.userName.isNotBlank()) 
                                userPreferences.userName 
                            else "Tap to set your nickname",
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (userPreferences.userName.isNotBlank())
                                MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                    IconButton(
                        onClick = {
                            nicknameInput = userPreferences.userName
                            showEditNickname = true
                        }
                    ) {
                        Text("✏️")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Sync Status Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (syncState.isSyncing)
                        MaterialTheme.colorScheme.secondaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Sync Status",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        if (syncState.isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("✓", style = MaterialTheme.typography.titleLarge)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (syncState.lastSyncTime > 0) {
                        val timeDiff = System.currentTimeMillis() - syncState.lastSyncTime
                        val minutesAgo = (timeDiff / (1000 * 60)).toInt()
                        
                        Text(
                            text = when {
                                minutesAgo < 1 -> "Just now"
                                minutesAgo < 60 -> "$minutesAgo minute${if (minutesAgo > 1) "s" else ""} ago"
                                else -> "${minutesAgo / 60} hour${if (minutesAgo / 60 > 1) "s" else ""} ago"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    if (syncState.error != null) {
                        Text(
                            text = "Error: ${syncState.error}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Account Actions
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    // Sync Now
                    TextButton(
                        onClick = {
                            scope.launch {
                                syncManager.syncAllData()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !syncState.isSyncing
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Sync Data Now")
                            Text("🔄")
                        }
                    }
                    
                    HorizontalDivider()
                    
                    // Change Password
                    TextButton(
                        onClick = { /* TODO: Navigate to change password screen */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Change Password")
                            Text("🔑")
                        }
                    }
                    
                    HorizontalDivider()
                    
                    // Privacy Policy
                    TextButton(
                        onClick = { /* TODO: Navigate to privacy policy */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Privacy Policy")
                            Text("📜")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Sign Out Button
            OutlinedButton(
                onClick = { showSignOutDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Sign Out")
            }
            
            // Delete Account Button
            TextButton(
                onClick = { showDeleteDialog = true },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete Account")
            }
        }
    }
    
    // Sign Out Dialog
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text("Sign Out") },
            text = { Text("Are you sure you want to sign out?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        authViewModel.signOut()
                        showSignOutDialog = false
                        onSignOut()
                    }
                ) {
                    Text("Sign Out", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Delete Account Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Account") },
            text = { Text("This action cannot be undone. All your data will be permanently deleted.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        authViewModel.deleteAccount()
                        showDeleteDialog = false
                        onSignOut()
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Edit Nickname Dialog
    if (showEditNickname) {
        AlertDialog(
            onDismissRequest = { showEditNickname = false },
            title = { Text("Edit Nickname") },
            text = {
                OutlinedTextField(
                    value = nicknameInput,
                    onValueChange = { nicknameInput = it },
                    label = { Text("Nickname") },
                    singleLine = true,
                    placeholder = { Text("Enter your nickname") }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            preferencesManager.updateUserName(nicknameInput)
                        }
                        showEditNickname = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNickname = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Profile Picture Dialog
    if (showProfilePictureDialog) {
        ProfilePictureDialog(
            currentProfilePicture = userPreferences.profilePictureUrl,
            onDismiss = { showProfilePictureDialog = false },
            onAvatarSelected = { avatarId ->
                scope.launch {
                    preferencesManager.updateProfilePictureUrl(avatarId)
                }
            },
            onImageSelected = { uri ->
                scope.launch {
                    // Save the URI as string - in production, you'd upload to Firebase Storage
                    // and save the download URL, but for now we'll just save the URI
                    preferencesManager.updateProfilePictureUrl(uri.toString())
                }
            }
        )
    }
}
