package com.bitdue.financeapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Premium Financial Dark Scheme
private val DarkColorScheme = darkColorScheme(
    primary = FinanceGreenLight,
    onPrimary = Color(0xFF00382E), // Dark text to contrast with light teal
    primaryContainer = FinanceGreenDark,
    onPrimaryContainer = Color(0xFF86F8DE),
    
    secondary = FinanceGoldLight,
    onSecondary = Color(0xFF422E00),
    secondaryContainer = Color(0xFF5E4400),
    onSecondaryContainer = Color(0xFFFFE082),
    
    tertiary = FinanceBlueLight,
    onTertiary = Color(0xFF0F1E24),
    tertiaryContainer = FinanceBlueDark,
    onTertiaryContainer = Color(0xFFCFD8DC),
    
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    
    outline = Color(0xFF939094),
    
    error = ErrorRed,
    onError = Color.White
)

// Premium Financial Light Scheme
private val LightColorScheme = lightColorScheme(
    primary = FinanceGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB2DFDB), // Lighter container
    onPrimaryContainer = Color(0xFF00201A),
    
    secondary = FinanceGold,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFECB3),
    onSecondaryContainer = Color(0xFF2E2000),
    
    tertiary = FinanceBlue,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFCFD8DC),
    onTertiaryContainer = Color(0xFF102027),
    
    background = LightBackground,
    onBackground = LightTextPrimary,
    
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    
    outline = Color(0xFF79747E),
    
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun FinanceAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Disable dynamic color to ensure consistent premium branding
    dynamicColor: Boolean = false, 
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Use background color for status bar to blend in, or primary for branding
            window.statusBarColor = if (darkTheme) {
                DarkSurface.toArgb() // Blend with app bar
            } else {
                FinanceGreenPrimary.toArgb()
            }
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
