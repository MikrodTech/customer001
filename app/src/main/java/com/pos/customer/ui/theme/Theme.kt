package com.pos.customer.ui.theme

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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreen,
    onPrimary = White,
    primaryContainer = PrimaryGreenDark,
    onPrimaryContainer = White,
    secondary = AccentOrange,
    onSecondary = White,
    secondaryContainer = AccentOrange.copy(alpha = 0.2f),
    onSecondaryContainer = AccentOrange,
    tertiary = AccentPurple,
    onTertiary = White,
    tertiaryContainer = AccentPurple.copy(alpha = 0.2f),
    onTertiaryContainer = AccentPurple,
    error = ErrorRed,
    onError = White,
    errorContainer = ErrorRed.copy(alpha = 0.2f),
    onErrorContainer = ErrorRed,
    background = Black,
    onBackground = White,
    surface = DarkGray,
    onSurface = White,
    surfaceVariant = Gray,
    onSurfaceVariant = LightGray,
    outline = LighterGray
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = White,
    primaryContainer = PrimaryGreenLight,
    onPrimaryContainer = PrimaryGreenDark,
    secondary = AccentOrange,
    onSecondary = White,
    secondaryContainer = AccentOrange.copy(alpha = 0.1f),
    onSecondaryContainer = AccentOrange,
    tertiary = AccentPurple,
    onTertiary = White,
    tertiaryContainer = AccentPurple.copy(alpha = 0.1f),
    onTertiaryContainer = AccentPurple,
    error = ErrorRed,
    onError = White,
    errorContainer = ErrorRed.copy(alpha = 0.1f),
    onErrorContainer = ErrorRed,
    background = BackgroundLight,
    onBackground = Black,
    surface = SurfaceLight,
    onSurface = Black,
    surfaceVariant = OffWhite,
    onSurfaceVariant = Gray,
    outline = LighterGray
)

@Composable
fun POSCustomerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled for consistent branding
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
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
