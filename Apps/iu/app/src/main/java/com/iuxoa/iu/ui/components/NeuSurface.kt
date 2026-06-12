package com.iuxoa.iu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.iuxoa.iu.ui.theme.IuColors

// ─────────────────────────────────────────────────────────────────────────────
// TRUE NEUMORPHISM — dual shadow (white highlight + warm shadow)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SoftCard(
    modifier:  Modifier = Modifier,
    radius:    Dp       = 16.dp,
    elevation: Dp       = 6.dp,
    bgColor:   Color    = IuColors.background,
    content:   @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .softRaisedShadow(radius = radius, elevation = elevation, bgColor = bgColor)
            .clip(RoundedCornerShape(radius))
            .background(bgColor),
        content = content
    )
}

@Composable
fun SoftInset(
    modifier: Modifier = Modifier,
    radius:   Dp       = 12.dp,
    depth:    Dp       = 4.dp,
    bgColor:  Color    = IuColors.surface2,
    content:  @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .softInsetShadow(radius = radius, depth = depth, bgColor = bgColor)
            .clip(RoundedCornerShape(radius))
            .background(bgColor),
        content = content
    )
}

fun Modifier.softRaisedShadow(radius: Dp, elevation: Dp, bgColor: Color): Modifier =
    neuDraw(radius, elevation, bgColor, inset = false)

fun Modifier.softInsetShadow(radius: Dp, depth: Dp, bgColor: Color): Modifier =
    neuDraw(radius, depth, bgColor, inset = true)

private fun Modifier.neuDraw(
    radius:    Dp,
    elevation: Dp,
    bgColor:   Color,
    inset:     Boolean,
): Modifier = this.drawWithCache {
    // toPx() is available directly on Dp inside DrawCacheScope via density
    val r    = radius.toPx()
    val e    = elevation.toPx()
    val blur = e * 2.4f

    val lightPaint = Paint().apply {
        asFrameworkPaint().apply {
            isAntiAlias = true
            color = android.graphics.Color.TRANSPARENT
            setShadowLayer(
                blur,
                if (inset) e  else -e,
                if (inset) e  else -e,
                IuColors.neuHigh.copy(alpha = 0.9f).toArgb()
            )
        }
    }
    val darkPaint = Paint().apply {
        asFrameworkPaint().apply {
            isAntiAlias = true
            color = android.graphics.Color.TRANSPARENT
            setShadowLayer(
                blur,
                if (inset) -e else e,
                if (inset) -e else e,
                IuColors.neuShadow.copy(alpha = 0.92f).toArgb()
            )
        }
    }
    val fillPaint = Paint().apply {
        asFrameworkPaint().apply {
            isAntiAlias = true
            color = bgColor.toArgb()
        }
    }

    onDrawBehind {
        drawIntoCanvas { canvas ->
            canvas.drawRoundRect(0f, 0f, size.width, size.height, r, r, lightPaint)
            canvas.drawRoundRect(0f, 0f, size.width, size.height, r, r, darkPaint)
            canvas.drawRoundRect(0f, 0f, size.width, size.height, r, r, fillPaint)
        }
    }
}
