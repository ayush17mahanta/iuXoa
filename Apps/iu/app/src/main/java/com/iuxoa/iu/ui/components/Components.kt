package com.iuxoa.iu.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iuxoa.iu.ui.theme.IuColors
import com.iuxoa.iu.ui.theme.IuType

// ─────────────────────────────────────────────────────────────────────────────
// GLASSMORPHIC CARD — aurora shows through frosted glass surface
// white-ish semi-transparent with soft neumorphic shadow + gradient border
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun NeuCard(
    modifier:     Modifier = Modifier,
    cornerRadius: Dp       = 20.dp,
    accentColor:  Color    = IuColors.orange,
    showAccent:   Boolean  = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .shadow(
                elevation    = 8.dp,
                shape        = RoundedCornerShape(cornerRadius),
                ambientColor = IuColors.neuShadow.copy(0.35f),
                spotColor    = IuColors.neuShadow.copy(0.45f)
            )
            .clip(RoundedCornerShape(cornerRadius))
            // GLASS: semi-transparent so aurora peeks through
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0x18FFFFFF),   // translucent white highlight
                        Color(0x06FFFFFF),   // dark glass card surface
                    ),
                    start = Offset(0f, 0f),
                    end   = Offset(500f, 500f)
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0x20FFFFFF),
                        Color(0x08FFFFFF),
                        Color.Transparent
                    ),
                    start = Offset(0f, 0f),
                    end   = Offset(400f, 400f)
                ),
                shape = RoundedCornerShape(cornerRadius)
            )
    ) {
        if (showAccent) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(accentColor, accentColor.copy(0.5f), Color.Transparent)
                        )
                    )
            )
        }
        content()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// GLASS CARD — even more transparent, for overlays / info panels
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun GlassCard(
    modifier:     Modifier = Modifier,
    cornerRadius: Dp       = 18.dp,
    accentColor:  Color    = IuColors.orange,
    alpha:        Float    = 0.55f,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .shadow(
                elevation    = 4.dp,
                shape        = RoundedCornerShape(cornerRadius),
                ambientColor = IuColors.neuShadow.copy(0.2f),
                spotColor    = IuColors.neuShadow.copy(0.25f)
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = alpha + 0.15f),
                        Color.White.copy(alpha = alpha),
                    ),
                    start = Offset(0f, 0f),
                    end   = Offset(400f, 400f)
                )
            )
            .border(
                1.dp,
                Brush.linearGradient(
                    listOf(Color(0xBBFFFFFF), Color(0x44FFFFFF)),
                    start = Offset(0f, 0f),
                    end   = Offset(200f, 200f)
                ),
                RoundedCornerShape(cornerRadius)
            ),
        content = content
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// NEUMORPHIC STAT CELL — frosted glass pill
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun NeuStatCell(
    value:    String,
    label:    String,
    color:    Color    = IuColors.orange,
    modifier: Modifier = Modifier
) {
    val num    = value.filter { it.isDigit() }.toIntOrNull() ?: 0
    val suffix = value.filter { !it.isDigit() }

    Column(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(16.dp), ambientColor = IuColors.neuShadow.copy(0.3f), spotColor = IuColors.neuShadow.copy(0.4f))
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0x18FFFFFF), Color(0x06FFFFFF)),
                    start  = Offset(0f, 0f), end = Offset(200f, 200f)
                )
            )
            .border(
                1.dp,
                Brush.linearGradient(listOf(Color(0x20FFFFFF), Color(0x08FFFFFF)), start = Offset(0f, 0f), end = Offset(200f, 200f)),
                RoundedCornerShape(16.dp)
            )
            .padding(vertical = 20.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedCounter(num, suffix, color)
        Spacer(Modifier.height(4.dp))
        Text(label.uppercase(), style = IuType.caption.copy(letterSpacing = 1.sp), color = IuColors.textFaint)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SHIMMER EFFECT
// ─────────────────────────────────────────────────────────────────────────────
fun Modifier.shimmer(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val offset by transition.animateFloat(
        initialValue  = -1f, targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing)), label = "shimmer_offset"
    )
    background(
        brush = Brush.linearGradient(
            colors = listOf(Color(0xAAF0EDE8), Color(0xEEFFFFFF), Color(0xAAF5F2EE), Color(0xEEFFFFFF), Color(0xAAF0EDE8)),
            start  = Offset(offset * 1000f, 0f),
            end    = Offset((offset + 1f) * 1000f, 0f)
        )
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// PRESS SCALE
// ─────────────────────────────────────────────────────────────────────────────
fun Modifier.pressScale(scaleTo: Float = 0.96f): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue   = if (isPressed) scaleTo else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow), label = "press_scale"
    )
    this.scale(scale).clickable(interactionSource = interactionSource, indication = null) {}
}

// ─────────────────────────────────────────────────────────────────────────────
// ORANGE GRADIENT BUTTON — with aurora-glow shadow
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun GradientButton(
    text:     String,
    modifier: Modifier  = Modifier,
    enabled:  Boolean   = true,
    onClick:  () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, spring(stiffness = Spring.StiffnessMediumLow), label = "gb")

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation    = if (enabled) (if (isPressed) 4.dp else 12.dp) else 0.dp,
                shape        = RoundedCornerShape(14.dp),
                ambientColor = IuColors.orange.copy(if (enabled) 0.4f else 0f),
                spotColor    = IuColors.orange.copy(if (enabled) 0.5f else 0f)
            )
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (enabled)
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFFF7043), IuColors.orange, Color(0xFFE53D00)),
                        start  = Offset(0f, 0f), end = Offset(400f, 100f)
                    )
                else Brush.linearGradient(listOf(Color(0x33000000), Color(0x22000000)))
            )
            .border(
                1.dp,
                if (enabled) Brush.linearGradient(listOf(Color(0x66FFFFFF), Color(0x22FFFFFF)))
                else SolidColor(Color(0x22000000)),
                RoundedCornerShape(14.dp)
            )
            .clickable(enabled = enabled, interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(vertical = 15.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = text,
            style      = IuType.body.copy(
                color         = if (enabled) Color.White else IuColors.textFaint,
                letterSpacing = 2.sp,
                fontSize      = 10.sp
            ),
            fontWeight = FontWeight.ExtraBold
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ORANGE PILL CHIP — glass variant
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun OrangePill(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(50), ambientColor = IuColors.orange.copy(0.18f))
            .clip(RoundedCornerShape(50))
            .background(Brush.linearGradient(listOf(IuColors.orange.copy(0.14f), IuColors.orangeSoft.copy(0.10f))))
            .border(1.dp, IuColors.orange.copy(0.4f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) { Text(text.uppercase(), style = IuType.tagStyle, color = IuColors.orange) }
}

// ─────────────────────────────────────────────────────────────────────────────
// GHOST TAG — frosted glass
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun GhostTag(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xAA141414))
            .border(1.dp, Color(0x15FFFFFF), RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) { Text(text.uppercase(), style = IuType.caption.copy(letterSpacing = 1.sp), color = IuColors.textFaint) }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION LABEL
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Spacer(modifier = Modifier.width(20.dp).height(1.5.dp).background(IuColors.orange))
        Text(text.uppercase(), style = IuType.label.copy(color = IuColors.orange))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ANIMATED COUNTER
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun AnimatedCounter(target: Int, suffix: String = "+", color: Color = IuColors.orange) {
    var value by remember { mutableIntStateOf(0) }
    LaunchedEffect(target) {
        animate(0f, target.toFloat(), animationSpec = tween(1400, easing = FastOutSlowInEasing)) { v, _ -> value = v.toInt() }
    }
    Text("$value$suffix", style = IuType.statNumber.copy(color = color), fontWeight = FontWeight.Black)
}

// ─────────────────────────────────────────────────────────────────────────────
// STAT CARD — glass surface
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun StatCard(value: String, label: String, color: Color = IuColors.orange, modifier: Modifier = Modifier) {
    val num    = value.filter { it.isDigit() }.toIntOrNull() ?: 0
    val suffix = value.filter { !it.isDigit() }
    Column(modifier = modifier.background(Color.Transparent).padding(vertical = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        AnimatedCounter(num, suffix, color)
        Spacer(Modifier.height(4.dp))
        Text(label.uppercase(), style = IuType.caption.copy(letterSpacing = 1.5.sp), color = IuColors.textFaint)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DIVIDERS
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun IuDivider(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.fillMaxWidth().height(1.dp).background(Color(0x1A000000)))
}

@Composable
fun OrangeDivider(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.fillMaxWidth().height(1.5.dp)
        .background(Brush.horizontalGradient(listOf(Color.Transparent, IuColors.orange, Color.Transparent))))
}

@Composable
fun AnimatedOrangeDivider(modifier: Modifier = Modifier, delayMs: Int = 0) {
    val scale = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        if (delayMs > 0) kotlinx.coroutines.delay(delayMs.toLong())
        scale.animateTo(1f, tween(700, easing = FastOutSlowInEasing))
    }
    Spacer(modifier = modifier.fillMaxWidth(scale.value).height(1.5.dp)
        .background(Brush.horizontalGradient(listOf(IuColors.orange, IuColors.orangeSoft))))
}

// ─────────────────────────────────────────────────────────────────────────────
// NOTIFICATION BADGE
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun NotificationBadge(count: Int, modifier: Modifier = Modifier) {
    if (count <= 0) return
    val scale = remember { Animatable(0f) }
    LaunchedEffect(count) { scale.animateTo(1.15f, tween(200)); scale.animateTo(1f, tween(100)) }
    Box(
        modifier = modifier.scale(scale.value).size(18.dp).clip(CircleShape)
            .background(Brush.linearGradient(IuColors.gradientOrange)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            if (count > 9) "9+" else count.toString(),
            style = IuType.caption.copy(color = Color.White, fontSize = 9.sp), fontWeight = FontWeight.Bold
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// STATUS BADGE
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun StatusBadge(approved: Boolean) {
    val (text, color) = if (approved) "Approved" to IuColors.teal else "Pending" to IuColors.gold
    Box(
        modifier = Modifier.clip(RoundedCornerShape(50)).background(color.copy(0.12f))
            .border(1.dp, color.copy(0.4f), RoundedCornerShape(50)).padding(horizontal = 8.dp, vertical = 3.dp)
    ) { Text(text, style = IuType.caption.copy(color = color), fontWeight = FontWeight.Bold) }
}

// ─────────────────────────────────────────────────────────────────────────────
// IuCard — glass alias of NeuCard
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun IuCard(
    modifier:    Modifier = Modifier,
    accentColor: Color    = IuColors.orange,
    showTopLine: Boolean  = true,
    content: @Composable ColumnScope.() -> Unit
) {
    NeuCard(modifier = modifier, accentColor = accentColor, showAccent = showTopLine, content = content)
}

// ─────────────────────────────────────────────────────────────────────────────
// FILTER PILLS — glass neumorphic
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun FilterPillWithCount(label: String, count: Int, active: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f, spring(stiffness = Spring.StiffnessMediumLow), label = "p")
    Row(
        modifier = Modifier.scale(scale)
            .shadow(
                elevation    = if (active) 6.dp else 3.dp,
                shape        = RoundedCornerShape(50),
                ambientColor = if (active) IuColors.orange.copy(0.30f) else IuColors.neuShadow.copy(0.15f),
                spotColor    = if (active) IuColors.orange.copy(0.35f) else IuColors.neuShadow.copy(0.20f)
            )
            .clip(RoundedCornerShape(50))
            .background(
                if (active) Brush.linearGradient(
                    colors = listOf(Color(0xFFFF7043), IuColors.orange),
                    start = Offset(0f, 0f), end = Offset(200f, 50f)
                )
                else Brush.linearGradient(
                    colors = listOf(Color(0xDDFFFFFF), Color(0xAAF5F0EC)),
                    start = Offset(0f, 0f), end = Offset(200f, 80f)
                )
            )
            .border(
                1.dp,
                if (active) Brush.linearGradient(listOf(Color(0x55FFFFFF), Color.Transparent))
                else Brush.linearGradient(listOf(Color(0xCCFFFFFF), Color(0x44000000)), start = Offset(0f, 0f), end = Offset(200f, 80f)),
                RoundedCornerShape(50)
            )
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(start = 14.dp, end = 10.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            label.uppercase(),
            style = IuType.tagStyle.copy(color = if (active) Color.White else IuColors.textMuted, letterSpacing = 1.sp),
            fontWeight = FontWeight.Bold
        )
        Box(
            modifier = Modifier.clip(CircleShape)
                .background(if (active) Color.White.copy(0.25f) else Color(0x22000000))
                .padding(horizontal = 6.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(count.toString(), style = IuType.caption.copy(color = if (active) Color.White else IuColors.textFaint, fontSize = 9.sp), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun FilterPill(label: String, active: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f, spring(stiffness = Spring.StiffnessMediumLow), label = "fp")
    Box(
        modifier = Modifier.scale(scale)
            .shadow(
                elevation    = if (active) 6.dp else 3.dp,
                shape        = RoundedCornerShape(50),
                ambientColor = if (active) IuColors.orange.copy(0.25f) else IuColors.neuShadow.copy(0.15f),
                spotColor    = if (active) IuColors.orange.copy(0.30f) else IuColors.neuShadow.copy(0.20f)
            )
            .clip(RoundedCornerShape(50))
            .background(
                if (active) Brush.linearGradient(colors = listOf(Color(0xFFFF7043), IuColors.orange), start = Offset(0f, 0f), end = Offset(200f, 50f))
                else Brush.linearGradient(colors = listOf(Color(0xDDFFFFFF), Color(0xAAF5F0EC)), start = Offset(0f, 0f), end = Offset(200f, 80f))
            )
            .border(
                1.dp,
                if (active) Brush.linearGradient(listOf(Color(0x55FFFFFF), Color.Transparent))
                else Brush.linearGradient(listOf(Color(0xCCFFFFFF), Color(0x44000000)), start = Offset(0f, 0f), end = Offset(200f, 80f)),
                RoundedCornerShape(50)
            )
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(label.uppercase(), style = IuType.tagStyle.copy(color = if (active) Color.White else IuColors.textMuted, letterSpacing = 1.sp), fontWeight = FontWeight.Bold)
    }
}
