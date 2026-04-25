package com.curso.memorycardapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary            = MemoryPurple,
    onPrimary          = Color.White,
    primaryContainer   = MemoryPurplePale,
    onPrimaryContainer = MemoryPurple,
    secondary          = MemoryPurpleLight,
    onSecondary        = Color.White,
    secondaryContainer = MemoryPurplePale,
    onSecondaryContainer = MemoryPurple,
    background         = MemoryBackground,
    onBackground       = Color(0xFF1C1B2E),
    surface            = MemorySurface,
    onSurface          = Color(0xFF1C1B2E),
    surfaceVariant     = MemoryPurplePale,
    onSurfaceVariant   = Color(0xFF3C3489),
    error              = MemoryError,
    onError            = Color.White,
    errorContainer     = Color(0xFFF7C1C1),
    onErrorContainer   = MemoryError,
    outline            = MemoryPurpleLight
)

private val DarkColorScheme = darkColorScheme(
    primary            = MemoryPurpleDark,
    onPrimary          = Color(0xFF0D0B1F),
    primaryContainer   = MemoryPurpleDark3,
    onPrimaryContainer = MemoryPurplePale,
    secondary          = MemoryPurpleLight,
    onSecondary        = Color(0xFF0D0B1F),
    secondaryContainer = MemoryPurpleDark2,
    onSecondaryContainer = MemoryPurplePale,
    background         = MemoryBackgroundDk,
    onBackground       = Color(0xFFE8E6FF),
    surface            = MemorySurfaceDk,
    onSurface          = Color(0xFFE8E6FF),
    surfaceVariant     = Color(0xFF26215C),
    onSurfaceVariant   = Color(0xFFCECBF6),
    error              = Color(0xFFF09595),
    onError            = Color(0xFF3B0000),
    errorContainer     = Color(0xFF791F1F),
    onErrorContainer   = Color(0xFFF7C1C1),
    outline            = MemoryPurpleDark2
)

@Composable
fun MemoryCardAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}