package com.iuxoa.iu.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ─────────────────────────────────────────────────────────────────────────────
// COLORS — Dark Cinematic Glassmorphism palette
// Deep black backgrounds, dark translucent glass cards, premium orange accents
// ─────────────────────────────────────────────────────────────────────────────
object IuColors {
    // ── Core backgrounds — deep dark base
    val background   = Color(0xFF030303)   // deep black base (matching website #000)
    val surface      = Color(0xFF0C0C0C)   // raised card — dark charcoal
    val surface2     = Color(0xFF141414)   // inset / input — slightly deeper
    val surface3     = Color(0xFF1B1B1B)   // deepest inset layout background
    val neuHigh      = Color(0x0AFFFFFF)   // translucent glass highlight (very soft transparent white)
    val neuShadow    = Color(0x44000000)   // soft dark shadow

    // ── Brand (matching website orange)
    val orange       = Color(0xFFE85533)   // premium branding orange
    val orangeSoft   = Color(0xFFFF7E50)

    // ── Text — light on dark
    val white        = Color(0xFFFFFFFF)   // primary text color (pure white)
    val text         = Color(0xFFFFFFFF)
    val textMuted    = Color(0xFFB0B0B0)
    val textFaint    = Color(0xFF666666)
    val textDim      = Color(0xFF444444)

    // ── Borders — premium thin translucent borders for glass effect
    val border       = Color(0x15FFFFFF)   // translucent white border
    val borderMid    = Color(0x28FFFFFF)

    // ── Accent palette — vibrant on dark
    val pink         = Color(0xFFEC4899)
    val blue         = Color(0xFF3B82F6)
    val purple       = Color(0xFFA855F7)
    val gold         = Color(0xFFF59E0B)
    val teal         = Color(0xFF10B981)

    // ── Aurora colors — soft ambient glow colors
    val auroraBlue   = Color(0x333B82F6)   // translucent blue
    val auroraIndigo = Color(0x286366F1)   // translucent indigo
    val auroraViolet = Color(0x288B5CF6)   // translucent violet
    val auroraPink   = Color(0x20EC4899)   // translucent pink
    val auroraOrange = Color(0x22F97316)   // translucent orange

    // ── Gradient helpers
    val gradientOrange  = listOf(Color(0xFFE85533), Color(0xFFFF7E50))
    val gradientPink    = listOf(Color(0xFFEC4899), Color(0xFFF472B6))
    val gradientBlue    = listOf(Color(0xFF3B82F6), Color(0xFF60A5FA))
    val gradientPurple  = listOf(Color(0xFFA855F7), Color(0xFFC084FC))
    val gradientCard    = listOf(Color(0x15FFFFFF), Color(0x05FFFFFF)) // dark glassmorphism card gradient
    val gradientAurora  = listOf(auroraBlue, auroraIndigo, auroraViolet, auroraPink)

    // ── Neumorphic / Glassmorphism surface gradients (soft emboss on dark)
    val neuSurface      = listOf(Color(0x12FFFFFF), Color(0x06FFFFFF))
    val neuSurfaceInset = listOf(Color(0x04FFFFFF), Color(0x0EFFFFFF))
}

// ─────────────────────────────────────────────────────────────────────────────
// TYPOGRAPHY
// ─────────────────────────────────────────────────────────────────────────────
object IuType {
    val heroTitle = TextStyle(
        fontFamily    = FontFamily.Default,
        fontWeight    = FontWeight.Black,
        fontSize      = 64.sp,
        lineHeight    = 60.sp,
        letterSpacing = (-2).sp,
        color         = IuColors.text
    )
    val sectionTitle = TextStyle(
        fontFamily    = FontFamily.Default,
        fontWeight    = FontWeight.Black,
        fontSize      = 36.sp,
        lineHeight    = 34.sp,
        letterSpacing = (-0.5).sp,
        color         = IuColors.text
    )
    val cardTitle = TextStyle(
        fontFamily    = FontFamily.Default,
        fontWeight    = FontWeight.Black,
        fontSize      = 20.sp,
        lineHeight    = 20.sp,
        letterSpacing = 0.sp,
        color         = IuColors.text
    )
    val label = TextStyle(
        fontFamily    = FontFamily.Default,
        fontWeight    = FontWeight.Normal,
        fontSize      = 10.sp,
        letterSpacing = 2.sp,
        color         = IuColors.textMuted
    )
    val body = TextStyle(
        fontFamily    = FontFamily.Default,
        fontWeight    = FontWeight.Normal,
        fontSize      = 13.sp,
        lineHeight    = 20.sp,
        color         = IuColors.textMuted
    )
    val caption = TextStyle(
        fontFamily    = FontFamily.Default,
        fontWeight    = FontWeight.Normal,
        fontSize      = 10.sp,
        lineHeight    = 14.sp,
        color         = IuColors.textFaint
    )
    val tagStyle = TextStyle(
        fontFamily    = FontFamily.Default,
        fontWeight    = FontWeight.Bold,
        fontSize      = 9.sp,
        letterSpacing = 1.5.sp,
        color         = IuColors.orange
    )
    val statNumber = TextStyle(
        fontFamily    = FontFamily.Default,
        fontWeight    = FontWeight.Black,
        fontSize      = 44.sp,
        lineHeight    = 44.sp,
        letterSpacing = (-1).sp,
        color         = IuColors.orange
    )
    val navItem = TextStyle(
        fontFamily    = FontFamily.Default,
        fontWeight    = FontWeight.Bold,
        fontSize      = 11.sp,
        letterSpacing = 1.5.sp,
        color         = IuColors.text
    )
}

val LocalIuColors = staticCompositionLocalOf { IuColors }
val LocalIuType   = staticCompositionLocalOf { IuType }

object IuTheme {
    val colors: IuColors @Composable get() = LocalIuColors.current
    val type:   IuType   @Composable get() = LocalIuType.current
}

@Composable
fun IuTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalIuColors provides IuColors,
        LocalIuType   provides IuType,
        content       = content
    )
}
