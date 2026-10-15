package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkGamingColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color(0xFF001F29),
    primaryContainer = DarkCyanContainer,
    onPrimaryContainer = Color(0xFFA6F5FF),

    secondary = ElectricAmber,
    onSecondary = Color(0xFF281800),
    secondaryContainer = DarkAmberContainer,
    onSecondaryContainer = Color(0xFFFFECC4),

    tertiary = MatrixGreen,
    onTertiary = Color(0xFF00220C),
    tertiaryContainer = DarkGreenContainer,
    onTertiaryContainer = Color(0xFFA6FFC8),

    error = HotCrimson,
    onError = Color.White,
    errorContainer = DarkCrimsonContainer,
    onErrorContainer = Color(0xFFFFD9E2),

    background = DarkObsidian,
    onBackground = TextWhite,

    surface = DarkSurface,
    onSurface = TextWhite,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextMuted,

    outline = DarkSurfaceBorder,
    outlineVariant = Color(0xFF1E2838)
)

@Composable
fun RaunakExploitsTheme(
    darkTheme: Boolean = true, // Always default to professional dark gaming aesthetic
    content: @Composable () -> Unit
) {
    // We enforce the custom dark gaming theme to ensure high-tech esports visual identity
    MaterialTheme(
        colorScheme = DarkGamingColorScheme,
        typography = Typography,
        content = content
    )
}
