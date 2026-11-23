package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Warm Storybook Paper & Ink — Light Palette
val PaperCream = Color(0xFFFFF8EE)
val PaperSurface = Color(0xFFFFFDF8)
val PaperVariant = Color(0xFFF6ECDC)
val InkDeep = Color(0xFF1F192E)
val InkMuted = Color(0xFF5D5470)

val CoralPrimary = Color(0xFFFF5640)
val CoralContainer = Color(0xFFFFDBD5)
val OnCoralContainer = Color(0xFF410800)

val CobaltSecondary = Color(0xFF3A5BFF)
val CobaltContainer = Color(0xFFDCE2FF)
val OnCobaltContainer = Color(0xFF00155A)

val MarigoldTertiary = Color(0xFFE59A00)
val MarigoldContainer = Color(0xFFFFEAB3)
val OnMarigoldContainer = Color(0xFF3A2500)

val MintAccent = Color(0xFF159E64)
val MintContainer = Color(0xFFC8F7DF)
val PlumAccent = Color(0xFF9136D9)
val PlumContainer = Color(0xFFF0DBFF)
val TangerineAccent = Color(0xFFF27318)
val TangerineContainer = Color(0xFFFFE2CC)
val BerryAccent = Color(0xFFD92672)
val BerryContainer = Color(0xFFFFD8E6)

val OutlineWarm = Color(0xFFD8CBB8)
val OutlineSubtle = Color(0xFFEAE0D0)

// Midnight Storybook — Dark Palette
val MidnightBg = Color(0xFF151221)
val MidnightSurface = Color(0xFF1E1A2E)
val MidnightVariant = Color(0xFF2B2540)
val CreamInk = Color(0xFFF9F3E9)
val CreamInkMuted = Color(0xFFC8BED6)

val CoralDark = Color(0xFFFF8A78)
val CoralDarkContainer = Color(0xFF7A1D10)
val CobaltDark = Color(0xFF9CB0FF)
val CobaltDarkContainer = Color(0xFF1B308F)
val MarigoldDark = Color(0xFFFFCC4D)
val MarigoldDarkContainer = Color(0xFF5C3E00)
val OutlineMidnight = Color(0xFF473E5F)

// Accent pairs for Part-of-Speech badges & Party Players
data class BadgePalette(
    val container: Color,
    val content: Color,
    val border: Color
)

val PlayfulBadgePalettes = listOf(
    BadgePalette(container = CoralContainer, content = Color(0xFF9E1F0F), border = CoralPrimary),
    BadgePalette(container = CobaltContainer, content = Color(0xFF1935A8), border = CobaltSecondary),
    BadgePalette(container = MarigoldContainer, content = Color(0xFF7A4E00), border = MarigoldTertiary),
    BadgePalette(container = MintContainer, content = Color(0xFF0A663E), border = MintAccent),
    BadgePalette(container = PlumContainer, content = Color(0xFF5E1799), border = PlumAccent),
    BadgePalette(container = BerryContainer, content = Color(0xFF8C0D44), border = BerryAccent),
    BadgePalette(container = TangerineContainer, content = Color(0xFF8F3B00), border = TangerineAccent)
)
