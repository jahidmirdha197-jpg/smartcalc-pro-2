package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

@Immutable
data class SmartCalcExtendedColors(
    val isDark: Boolean,
    val bgTop: Color,
    val bgMiddle: Color,
    val bgBottom: Color,
    val glowCyan: Color,
    val glowBlue: Color,
    val glowPurple: Color,
    val glassSurface: Color,
    val glassSurfaceStrong: Color,
    val glassBorderStart: Color,
    val glassBorderEnd: Color,
    val keyDigitSurface: Color,
    val keyOperatorSurface: Color,
    val keyFunctionSurface: Color,
    val keyClearSurface: Color,
    val keyClearText: Color,
    val keyOperatorText: Color,
    val keyFunctionText: Color,
    val equalsGradientStart: Color,
    val equalsGradientMid: Color,
    val equalsGradientEnd: Color,
    val accentCyan: Color,
    val accentBlue: Color,
    val accentPurple: Color,
    val successGreen: Color,
    val warningAmber: Color,
    val errorRose: Color,
    val navBarSurface: Color
)

private val DarkExtendedColors = SmartCalcExtendedColors(
    isDark = true,
    bgTop = NavyDeepest,
    bgMiddle = NavyPrimaryBg,
    bgBottom = Color(0xFF0D1430),
    glowCyan = ElectricCyan.copy(alpha = 0.22f),
    glowBlue = ElectricBlue.copy(alpha = 0.24f),
    glowPurple = NeonPurple.copy(alpha = 0.24f),
    glassSurface = Color(0xB8111B3A),
    glassSurfaceStrong = Color(0xDB162247),
    glassBorderStart = Color(0x5538BDF8),
    glassBorderEnd = Color(0x38A855F7),
    keyDigitSurface = Color(0xCC162144),
    keyOperatorSurface = Color(0xCC1E2D5F),
    keyFunctionSurface = Color(0xB319244A),
    keyClearSurface = Color(0x33FB7185),
    keyClearText = RoseCoral,
    keyOperatorText = ElectricCyan,
    keyFunctionText = Color(0xFFC4B5FD),
    equalsGradientStart = ElectricCyan,
    equalsGradientMid = ElectricBlue,
    equalsGradientEnd = NeonViolet,
    accentCyan = ElectricCyan,
    accentBlue = ElectricBlue,
    accentPurple = NeonPurple,
    successGreen = EmeraldNeon,
    warningAmber = AmberNeon,
    errorRose = RoseCoral,
    navBarSurface = Color(0xE60B1228)
)

private val LightExtendedColors = SmartCalcExtendedColors(
    isDark = false,
    bgTop = LightBgPrimary,
    bgMiddle = Color(0xFFEDF3FC),
    bgBottom = LightBgSecondary,
    glowCyan = LightCyanAccent.copy(alpha = 0.14f),
    glowBlue = LightBlueAccent.copy(alpha = 0.14f),
    glowPurple = LightPurpleAccent.copy(alpha = 0.12f),
    glassSurface = Color(0xCCFFFFFF),
    glassSurfaceStrong = Color(0xF2FFFFFF),
    glassBorderStart = Color(0x802563EB),
    glassBorderEnd = Color(0x557C3AED),
    keyDigitSurface = Color(0xF5FFFFFF),
    keyOperatorSurface = Color(0xFFE4EDFF),
    keyFunctionSurface = Color(0xFFEDE9FE),
    keyClearSurface = Color(0x26E11D48),
    keyClearText = RoseCoralDeep,
    keyOperatorText = LightBlueAccent,
    keyFunctionText = LightPurpleAccent,
    equalsGradientStart = LightCyanAccent,
    equalsGradientMid = LightBlueAccent,
    equalsGradientEnd = LightPurpleAccent,
    accentCyan = LightCyanAccent,
    accentBlue = LightBlueAccent,
    accentPurple = LightPurpleAccent,
    successGreen = Color(0xFF059669),
    warningAmber = Color(0xFFD97706),
    errorRose = RoseCoralDeep,
    navBarSurface = Color(0xF0F8FAFF)
)

val LocalSmartCalcColors = staticCompositionLocalOf { DarkExtendedColors }

private val DarkColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = NavyDeepest,
    primaryContainer = Color(0xFF1A2A5E),
    onPrimaryContainer = TextWhite,
    secondary = NeonPurple,
    onSecondary = TextWhite,
    secondaryContainer = Color(0xFF281E52),
    onSecondaryContainer = Color(0xFFE9D5FF),
    tertiary = ElectricBlue,
    onTertiary = TextWhite,
    background = NavyDeepest,
    onBackground = TextWhite,
    surface = NavySurface,
    onSurface = TextWhite,
    surfaceVariant = NavySurfaceElevated,
    onSurfaceVariant = TextSilver,
    outline = Color(0xFF33416B),
    error = RoseCoral,
    onError = NavyDeepest
)

private val LightColorScheme = lightColorScheme(
    primary = LightBlueAccent,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E3A8A),
    secondary = LightPurpleAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE9FE),
    onSecondaryContainer = Color(0xFF4C1D95),
    tertiary = LightCyanAccent,
    onTertiary = Color.White,
    background = LightBgPrimary,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = LightTextSecondary,
    outline = Color(0xFFCBD5E1),
    error = RoseCoralDeep,
    onError = Color.White
)

private val SmartCalcShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(30.dp)
)

enum class AppThemeMode {
    DARK,
    LIGHT,
    SYSTEM
}

@Composable
fun SmartCalcProTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme
    val extendedColors = if (isDark) DarkExtendedColors else LightExtendedColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    CompositionLocalProvider(LocalSmartCalcColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = SmartCalcTypography,
            shapes = SmartCalcShapes,
            content = content
        )
    }
}
