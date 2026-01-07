package com.bitdue.financeapp.ui.theme

import androidx.compose.ui.graphics.Color

// ============ PREMIUM FINANCIAL THEME ============

// Primary Brand Colors - Deep Emerald & Gold
val FinanceGreenPrimary = Color(0xFF00695C)     // Deep Teal/Emerald - Stability
val FinanceGreenLight = Color(0xFF4DB6AC)       // Lighter Teal for Dark Mode
val FinanceGreenDark = Color(0xFF003D33)        // Darker shade for containers

val FinanceGold = Color(0xFFC0A16B)             // Muted Gold - Wealth/Premium
val FinanceGoldLight = Color(0xFFFFE082)        // Light Gold for Dark Mode/Accents

// Secondary & Tertiary
val FinanceBlue = Color(0xFF455A64)             // Blue Grey - Professional
val FinanceBlueLight = Color(0xFF90A4AE)        // Light Blue Grey
val FinanceBlueDark = Color(0xFF263238)         // Dark Blue Grey

// Backgrounds & Surfaces
val LightBackground = Color(0xFFFAFAFA)         // Warm White / Off-White
val LightSurface = Color(0xFFFFFFFF)            // Pure White
val LightSurfaceVariant = Color(0xFFF5F5F5)     // Light Grey Surface

val DarkBackground = Color(0xFF121212)          // Deep Black/Grey
val DarkSurface = Color(0xFF1E1E1E)             // Dark Grey Surface
val DarkSurfaceVariant = Color(0xFF2D2D2D)      // Slightly Lighter Surface

// Text Colors
val LightTextPrimary = Color(0xFF1C1B1F)
val LightTextSecondary = Color(0xFF49454F)
val DarkTextPrimary = Color(0xFFE6E1E5)
val DarkTextSecondary = Color(0xFFCAC4D0)

// Semantic Colors
val SuccessGreen = Color(0xFF2E7D32)
val ErrorRed = Color(0xFFB3261E)
val WarningAmber = Color(0xFFF57C00)
val InfoBlue = Color(0xFF0288D1)

// Backward Compatibility for Status Colors
val PositiveGreen = SuccessGreen
val NegativeRed = ErrorRed
val WarningOrange = WarningAmber

// ============ CATEGORY COLORS ============
// Adjusted for better contrast in both modes

val CategoryGroceries = Color(0xFF66BB6A)    // Green
val CategoryTransport = Color(0xFF42A5F5)    // Blue
val CategoryDining = Color(0xFFEF5350)       // Red
val CategoryShopping = Color(0xFFAB47BC)     // Purple
val CategoryUtilities = Color(0xFFFFA726)    // Orange
val CategoryHealth = Color(0xFF26C6DA)       // Cyan
val CategoryEntertainment = Color(0xFFEC407A) // Pink
val CategoryEducation = Color(0xFFFF7043)    // Deep Orange
val CategoryInvestment = Color(0xFF5C6BC0)   // Indigo
val CategoryIncome = Color(0xFF9CCC65)       // Light Green

// Palette for Charts
val ChartColors = listOf(
    Color(0xFF26A69A), // Teal
    Color(0xFF5C6BC0), // Indigo
    Color(0xFFAB47BC), // Purple
    Color(0xFFEF5350), // Red
    Color(0xFFFFA726), // Orange
    Color(0xFF8D6E63), // Brown
    Color(0xFF78909C), // Blue Grey
    Color(0xFF9CCC65), // Light Green
    Color(0xFF29B6F6), // Light Blue
    Color(0xFFFFCA28)  // Amber
)
