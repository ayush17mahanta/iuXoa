package com.iuxoa.iu.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.iuxoa.iu.ui.theme.IuColors

@Composable
fun SplashScreen(onComplete: () -> Unit) {
    val letters = listOf("A", "Y", "U")

    val anim0 = remember { Animatable(0f) }
    val anim1 = remember { Animatable(0f) }
    val anim2 = remember { Animatable(0f) }
    val letterAnims = listOf(anim0, anim1, anim2)

    val subtitleAlpha = remember { Animatable(0f) }
    val lineScale     = remember { Animatable(0f) }
    val progressScale = remember { Animatable(0f) }
    val exitSlide     = remember { Animatable(0f) }
    val labelAlpha    = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Staggered letter reveal — launch each in parallel
        launch {
            delay(100L)
            anim0.animateTo(1f, tween(650, easing = FastOutSlowInEasing))
        }
        launch {
            delay(180L)
            anim1.animateTo(1f, tween(650, easing = FastOutSlowInEasing))
        }
        launch {
            delay(260L)
            anim2.animateTo(1f, tween(650, easing = FastOutSlowInEasing))
        }

        delay(450)
        subtitleAlpha.animateTo(1f, tween(600))
        delay(100)
        lineScale.animateTo(1f, tween(700, easing = FastOutSlowInEasing))
        delay(100)
        labelAlpha.animateTo(1f, tween(300))
        progressScale.animateTo(1f, tween(1750, easing = LinearEasing))

        delay(400)
        exitSlide.animateTo(-1f, tween(900, easing = FastOutSlowInEasing))
        onComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { translationY = exitSlide.value * size.height }
            .background(Color(0xFF030303)),
        contentAlignment = Alignment.Center
    ) {
        // ── CENTER CONTENT ──────────────────────────────────────────────────
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // AYU — 3D flip letters
            Row {
                letterAnims.forEachIndexed { i, anim ->
                    Text(
                        text       = letters[i],
                        fontSize   = 100.sp,
                        fontWeight = FontWeight.Black,
                        color      = Color.White,
                        letterSpacing = (-2).sp,
                        modifier   = Modifier
                            .alpha(anim.value)
                            .graphicsLayer {
                                rotationX     = (1f - anim.value) * 90f
                                cameraDistance = 8f * density
                                transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 1f)
                            }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text          = "DATA · AI · RESEARCH · INNOVATION",
                fontSize      = 9.sp,
                fontWeight    = FontWeight.Normal,
                color         = Color(0x77FFFFFF),
                letterSpacing = 4.sp,
                modifier      = Modifier.alpha(subtitleAlpha.value)
            )

            Spacer(Modifier.height(14.dp))

            // Orange accent line
            Box(
                modifier = Modifier
                    .width(60.dp * lineScale.value)
                    .height(2.dp)
                    .background(IuColors.orange)
            )
        }

        // ── BOTTOM PROGRESS LINE ──────────────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(progressScale.value)
                .height(2.dp)
                .background(IuColors.orange)
        )

        // ── BOTTOM LEFT ────────────────────────────────────────────────────────
        Text(
            text          = "PORTFOLIO 2025",
            fontSize      = 8.sp,
            letterSpacing = 2.5.sp,
            color         = Color(0x55FFFFFF),
            modifier      = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 20.dp)
                .alpha(labelAlpha.value)
        )

        // ── BOTTOM RIGHT ───────────────────────────────────────────────────────
        Text(
            text          = "✦ LOADING",
            fontSize      = 8.sp,
            letterSpacing = 2.5.sp,
            color         = IuColors.orange,
            modifier      = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 20.dp)
                .alpha(labelAlpha.value)
        )
    }
}
