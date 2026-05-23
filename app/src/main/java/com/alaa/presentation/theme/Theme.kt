package com.alaa.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary         = Gold,
    onPrimary       = TextOnGold,
    primaryContainer = GoldDim,
    secondary       = GreenLight,
    onSecondary     = TextOnGold,
    background      = BackgroundDark,
    surface         = SurfaceDark,
    onBackground    = TextPrimary,
    onSurface       = TextPrimary,
)

private val LightColors = lightColorScheme(
    primary         = Green,
    onPrimary       = TextPrimary,
    secondary       = Gold,
    background      = BackgroundLight,
    surface         = SurfaceLight,
    onBackground    = Green,
    onSurface       = Green,
)

@Composable
fun NoorTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography  = NoorTypography,
        content     = content
    )
}
