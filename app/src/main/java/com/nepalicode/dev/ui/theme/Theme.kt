package com.nepalicode.dev.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = HighDensityPrimary,
    onPrimary = HighDensityOnPrimary,
    primaryContainer = IdeSurfaceVariant,
    onPrimaryContainer = HighDensityPrimary,
    secondary = HighDensityCyan,
    onSecondary = Color(0xFF00363F),
    secondaryContainer = IdeSurfaceContainer,
    onSecondaryContainer = HighDensityCyan,
    tertiary = HighDensityCoral,
    onTertiary = Color(0xFF601410),
    tertiaryContainer = Color(0xFF3B1E22),
    onTertiaryContainer = HighDensityCoral,
    background = IdeBackground,
    onBackground = TextPrimary,
    surface = IdeSurface,
    onSurface = TextPrimary,
    surfaceVariant = IdeSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = IdeBorder,
    outlineVariant = Color(0xFF36343B)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
