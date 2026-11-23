package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = CoralPrimary,
    onPrimary = Color.White,
    primaryContainer = CoralContainer,
    onPrimaryContainer = OnCoralContainer,
    secondary = CobaltSecondary,
    onSecondary = Color.White,
    secondaryContainer = CobaltContainer,
    onSecondaryContainer = OnCobaltContainer,
    tertiary = MarigoldTertiary,
    onTertiary = Color.White,
    tertiaryContainer = MarigoldContainer,
    onTertiaryContainer = OnMarigoldContainer,
    background = PaperCream,
    onBackground = InkDeep,
    surface = PaperSurface,
    onSurface = InkDeep,
    surfaceVariant = PaperVariant,
    onSurfaceVariant = InkMuted,
    outline = OutlineWarm,
    outlineVariant = OutlineSubtle,
    error = Color(0xFFD92B3A),
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = CoralDark,
    onPrimary = Color(0xFF470800),
    primaryContainer = CoralDarkContainer,
    onPrimaryContainer = CoralContainer,
    secondary = CobaltDark,
    onSecondary = Color(0xFF001966),
    secondaryContainer = CobaltDarkContainer,
    onSecondaryContainer = CobaltContainer,
    tertiary = MarigoldDark,
    onTertiary = Color(0xFF3E2900),
    tertiaryContainer = MarigoldDarkContainer,
    onTertiaryContainer = MarigoldContainer,
    background = MidnightBg,
    onBackground = CreamInk,
    surface = MidnightSurface,
    onSurface = CreamInk,
    surfaceVariant = MidnightVariant,
    onSurfaceVariant = CreamInkMuted,
    outline = OutlineMidnight,
    outlineVariant = Color(0xFF342D4B),
    error = Color(0xFFFF8A93),
    onError = Color(0xFF5C000E)
)

val WordWackyShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun WordWackyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = WordWackyTypography,
        shapes = WordWackyShapes,
        content = content
    )
}
