package com.calsnap.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val LightColors = lightColorScheme(
    primary          = Streak,
    onPrimary        = Color.White,
    primaryContainer = Streak.copy(alpha = 0.12f),
    background       = Bg0Light,
    surface          = Bg1Light,
    surfaceVariant   = Bg2Light,
    onBackground     = Text0Light,
    onSurface        = Text0Light,
    onSurfaceVariant = Text1Light,
    outline          = Text3Light,
    error            = ErrLight,
)

private val DarkColors = darkColorScheme(
    primary          = StreakLight,
    onPrimary        = Color.White,
    primaryContainer = StreakLight.copy(alpha = 0.12f),
    background       = Bg0Dark,
    surface          = Bg1Dark,
    surfaceVariant   = Bg2Dark,
    onBackground     = Text0Dark,
    onSurface        = Text0Dark,
    onSurfaceVariant = Text1Dark,
    outline          = Text3Dark,
    error            = ErrDark,
)

@Composable
fun CalSnapTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography  = CalSnapTypography,
        content     = content
    )
}

val CalSnapTypography = Typography(
    displayLarge  = TextStyle(fontWeight = FontWeight.Black, fontSize = 54.sp, letterSpacing = (-3).sp),
    displayMedium = TextStyle(fontWeight = FontWeight.Black, fontSize = 42.sp, letterSpacing = (-2).sp),
    headlineLarge = TextStyle(fontWeight = FontWeight.Bold,  fontSize = 24.sp, letterSpacing = (-0.5).sp),
    headlineMedium= TextStyle(fontWeight = FontWeight.Bold,  fontSize = 20.sp),
    titleLarge    = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 17.sp),
    titleMedium   = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
    bodyLarge     = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodyMedium    = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp),
    labelLarge    = TextStyle(fontWeight = FontWeight.Bold,   fontSize = 13.sp, letterSpacing = 0.3.sp),
    labelMedium   = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
    labelSmall    = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.sp, letterSpacing = 0.4.sp),
)
