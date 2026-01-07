package com.bitdue.financeapp.utils

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * Manager class for handling biometric authentication
 */
class BiometricAuthManager(private val activity: FragmentActivity) {
    
    private val biometricManager = BiometricManager.from(activity)
    
    /**
     * Check if biometric authentication is available on this device
     */
    fun isBiometricAvailable(): BiometricStatus {
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                BiometricStatus.Available
            
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                BiometricStatus.NoHardware
            
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                BiometricStatus.HardwareUnavailable
            
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                BiometricStatus.NoneEnrolled
            
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED ->
                BiometricStatus.SecurityUpdateRequired
            
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED ->
                BiometricStatus.Unsupported
            
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN ->
                BiometricStatus.Unknown
            
            else -> BiometricStatus.Unknown
        }
    }
    
    /**
     * Show biometric authentication prompt
     */
    fun authenticate(
        title: String = "Biometric Authentication",
        subtitle: String = "Authenticate to access your financial data",
        negativeButtonText: String = "Cancel",
        onSuccess: () -> Unit,
        onError: (errorCode: Int, errorMessage: String) -> Unit,
        onFailed: () -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        
        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }
                
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errorCode, errString.toString())
                }
                
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onFailed()
                }
            }
        )
        
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText(negativeButtonText)
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()
        
        biometricPrompt.authenticate(promptInfo)
    }
    
    /**
     * Status of biometric authentication capability
     */
    sealed class BiometricStatus {
        object Available : BiometricStatus()
        object NoHardware : BiometricStatus()
        object HardwareUnavailable : BiometricStatus()
        object NoneEnrolled : BiometricStatus()
        object SecurityUpdateRequired : BiometricStatus()
        object Unsupported : BiometricStatus()
        object Unknown : BiometricStatus()
        
        fun getMessage(): String {
            return when (this) {
                is Available -> "Biometric authentication is available"
                is NoHardware -> "No biometric hardware detected on this device"
                is HardwareUnavailable -> "Biometric hardware is currently unavailable"
                is NoneEnrolled -> "No biometric credentials enrolled. Please set up fingerprint or face recognition in your device settings"
                is SecurityUpdateRequired -> "A security update is required to use biometric authentication"
                is Unsupported -> "Biometric authentication is not supported on this device"
                is Unknown -> "Biometric authentication status is unknown"
            }
        }
        
        fun isAvailable(): Boolean = this is Available
    }
}
