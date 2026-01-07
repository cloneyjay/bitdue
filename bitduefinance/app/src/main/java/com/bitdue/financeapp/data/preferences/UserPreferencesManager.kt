package com.bitdue.financeapp.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferencesManager(private val context: Context) {
    
    companion object {
        private val DARK_THEME = booleanPreferencesKey("dark_theme")
        private val DYNAMIC_COLORS = booleanPreferencesKey("dynamic_colors")
        private val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val BUDGET_ALERTS_ENABLED = booleanPreferencesKey("budget_alerts_enabled")
        private val CURRENCY_CODE = stringPreferencesKey("currency_code")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val CLOUD_SYNC_ENABLED = booleanPreferencesKey("cloud_sync_enabled")
    }
    
    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserPreferences(
                darkTheme = preferences[DARK_THEME] ?: false,
                dynamicColors = preferences[DYNAMIC_COLORS] ?: true,
                biometricEnabled = preferences[BIOMETRIC_ENABLED] ?: false,
                notificationsEnabled = preferences[NOTIFICATIONS_ENABLED] ?: true,
                budgetAlertsEnabled = preferences[BUDGET_ALERTS_ENABLED] ?: true,
                currencyCode = preferences[CURRENCY_CODE] ?: "KES",
                userName = preferences[USER_NAME] ?: "",
                onboardingCompleted = preferences[ONBOARDING_COMPLETED] ?: false,
                cloudSyncEnabled = preferences[CLOUD_SYNC_ENABLED] ?: false
            )
        }
    
    suspend fun updateDarkTheme(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_THEME] = enabled
        }
    }
    
    suspend fun updateDynamicColors(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DYNAMIC_COLORS] = enabled
        }
    }
    
    suspend fun updateBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[BIOMETRIC_ENABLED] = enabled
        }
    }
    
    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }
    
    suspend fun updateBudgetAlertsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[BUDGET_ALERTS_ENABLED] = enabled
        }
    }
    
    suspend fun updateCurrencyCode(code: String) {
        context.dataStore.edit { preferences ->
            preferences[CURRENCY_CODE] = code
        }
    }
    
    suspend fun updateUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME] = name
        }
    }
    
    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = completed
        }
    }
    
    suspend fun updateCloudSyncEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[CLOUD_SYNC_ENABLED] = enabled
        }
    }
}

data class UserPreferences(
    val darkTheme: Boolean = false,
    val dynamicColors: Boolean = true,
    val biometricEnabled: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val budgetAlertsEnabled: Boolean = true,
    val currencyCode: String = "KES",
    val userName: String = "",
    val onboardingCompleted: Boolean = false,
    val cloudSyncEnabled: Boolean = false
)
