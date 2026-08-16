package com.makeforge.forgefit.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ForgeFit color palette — dark, aggressive, energetic
val ForgeOrange = Color(0xFFFF6B1A)
val ForgeOrangeDim = Color(0xFFCC5500)
val ForgeBackground = Color(0xFF0A0A0B)
val ForgeSurface = Color(0xFF141416)
val ForgeSurfaceVariant = Color(0xFF1E1E22)
val ForgeOnSurface = Color(0xFFF0EDE8)
val ForgeOnSurfaceDim = Color(0xFF8A8A95)
val ForgeError = Color(0xFFFF4444)
val ForgeSuccess = Color(0xFF22C55E)

private val ForgeDarkColorScheme = darkColorScheme(
    primary = ForgeOrange,
    onPrimary = Color(0xFF0A0A0B),
    primaryContainer = ForgeOrangeDim,
    onPrimaryContainer = ForgeOnSurface,
    background = ForgeBackground,
    onBackground = ForgeOnSurface,
    surface = ForgeSurface,
    onSurface = ForgeOnSurface,
    surfaceVariant = ForgeSurfaceVariant,
    onSurfaceVariant = ForgeOnSurfaceDim,
    error = ForgeError,
    outline = Color(0xFF2E2E36),
)

@Composable
fun ForgeFitTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ForgeDarkColorScheme,
        typography = ForgeFitTypography,
        content = content
    )
}
