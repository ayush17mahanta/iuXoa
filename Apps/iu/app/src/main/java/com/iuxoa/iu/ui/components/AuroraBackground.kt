package com.iuxoa.iu.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import com.iuxoa.iu.ui.theme.IuColors
import kotlin.math.*

// ─────────────────────────────────────────────────────────────────────────────
// AURORA BACKGROUND — animated aurora borealis effect for light theme
// Soft shifting gradient blobs — blue / indigo / violet / pink / orange
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun Modifier.auroraBackground(): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "aurora")

    // Blob 1 — blue/indigo, slow drift
    val blob1X by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(14000, easing = LinearEasing), RepeatMode.Reverse),
        label = "b1x"
    )
    val blob1Y by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(11000, easing = LinearEasing), RepeatMode.Reverse),
        label = "b1y"
    )

    // Blob 2 — violet/pink, medium drift
    val blob2X by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(12000, easing = LinearEasing), RepeatMode.Reverse),
        label = "b2x"
    )
    val blob2Y by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing), RepeatMode.Reverse),
        label = "b2y"
    )

    // Blob 3 — orange/peach, fast drift
    val blob3X by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 0.8f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Reverse),
        label = "b3x"
    )
    val blob3Y by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 0.1f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing), RepeatMode.Reverse),
        label = "b3y"
    )

    // Sweep animation — slow left-to-right shimmer across full canvas
    val sweep by infiniteTransition.animateFloat(
        initialValue = -0.5f, targetValue = 1.5f,
        animationSpec = infiniteRepeatable(tween(7000, easing = LinearEasing), RepeatMode.Restart),
        label = "sweep"
    )

    // Pulse — gentle opacity breathing
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.55f, targetValue = 0.85f,
        animationSpec = infiniteRepeatable(tween(4000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    val bgColor   = IuColors.background
    val blob1Col  = IuColors.auroraBlue.copy(alpha = 0.35f)
    val blob2Col  = IuColors.auroraViolet.copy(alpha = 0.30f)
    val blob3Col  = IuColors.auroraOrange.copy(alpha = 0.25f)
    val indigoCol = IuColors.auroraIndigo.copy(alpha = 0.20f)
    val pinkCol   = IuColors.auroraPink.copy(alpha = 0.18f)

    return this.drawBehind {
        // Base warm background
        drawRect(bgColor)

        val w = size.width
        val h = size.height

        // ── Aurora sweep band — main horizontal shimmer ────────────────────
        val sweepStartX = w * sweep
        drawRect(
            brush = Brush.linearGradient(
                colorStops = arrayOf(
                    0.0f to Color.Transparent,
                    0.3f to blob1Col,
                    0.5f to indigoCol,
                    0.7f to blob2Col,
                    1.0f to Color.Transparent
                ),
                start = Offset(sweepStartX - w * 0.4f, 0f),
                end   = Offset(sweepStartX + w * 0.4f, h * 0.5f)
            )
        )

        // ── Blob 1 — top-left area, large blue radial ─────────────────────
        val b1cx = w * (0.1f + blob1X * 0.5f)
        val b1cy = h * (0.05f + blob1Y * 0.3f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(blob1Col, Color.Transparent),
                center = Offset(b1cx, b1cy),
                radius = w * 0.65f
            ),
            radius = w * 0.65f,
            center = Offset(b1cx, b1cy),
            alpha  = pulse
        )

        // ── Blob 2 — right side, violet ────────────────────────────────────
        val b2cx = w * (0.5f + blob2X * 0.5f)
        val b2cy = h * blob2Y
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(blob2Col, pinkCol, Color.Transparent),
                center = Offset(b2cx, b2cy),
                radius = w * 0.55f
            ),
            radius = w * 0.55f,
            center = Offset(b2cx, b2cy),
            alpha  = pulse * 0.85f
        )

        // ── Blob 3 — bottom, warm orange/peach ─────────────────────────────
        val b3cx = w * blob3X
        val b3cy = h * (0.6f + blob3Y * 0.35f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(blob3Col, IuColors.auroraOrange.copy(0.10f), Color.Transparent),
                center = Offset(b3cx, b3cy),
                radius = w * 0.45f
            ),
            radius = w * 0.45f,
            center = Offset(b3cx, b3cy),
            alpha  = pulse * 0.70f
        )

        // ── Top-right indigo accent ─────────────────────────────────────────
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(indigoCol, Color.Transparent),
                center = Offset(w * (0.7f + blob1X * 0.3f), h * 0.08f),
                radius = w * 0.40f
            ),
            radius = w * 0.40f,
            center = Offset(w * (0.7f + blob1X * 0.3f), h * 0.08f),
            alpha  = pulse * 0.60f
        )

        // ── Noise/texture overlay — subtle radial mask to soften edges ─────
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(Color.Transparent, IuColors.background.copy(0.25f)),
                center = Offset(w * 0.5f, h * 0.5f),
                radius = w * 0.85f
            )
        )
    }
}
