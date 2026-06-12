package com.iuxoa.iu.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.iuxoa.iu.data.BucketItem
import com.iuxoa.iu.ui.components.*
import com.iuxoa.iu.ui.theme.IuColors
import com.iuxoa.iu.ui.theme.IuType
import com.iuxoa.iu.viewmodel.MainViewModel
import kotlinx.coroutines.delay

// ─────────────────────────────────────────────────────────────────────────────
// BUCKET LIST SCREEN
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BucketListScreen(navController: NavController, vm: MainViewModel) {
    val items       by vm.bucketList.collectAsStateWithLifecycle()
    val unreadCount by vm.unreadCount.collectAsStateWithLifecycle()
    val haptic      = LocalHapticFeedback.current

    val done     = items.count { it.done }
    val total    = items.size
    val progress = if (total > 0) done.toFloat() / total else 0f

    val categories = listOf("All") + items.map { it.category }.distinct().filter { it.isNotEmpty() }
    var activeCat  by remember { mutableStateOf("All") }
    val filtered    = if (activeCat == "All") items else items.filter { it.category == activeCat }

    var showSheet   by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<BucketItem?>(null) }

    val heroAlpha = remember { Animatable(0f) }
    val heroSlide = remember { Animatable(32f) }
    LaunchedEffect(Unit) {
        heroAlpha.animateTo(1f, tween(600, easing = FastOutSlowInEasing))
        heroSlide.animateTo(0f, tween(600, easing = FastOutSlowInEasing))
    }

    val animatedProgress by animateFloatAsState(targetValue = progress, animationSpec = tween(900, easing = FastOutSlowInEasing), label = "progress")
    val displayPct by animateIntAsState(targetValue = (progress * 100).toInt(), animationSpec = tween(900, easing = FastOutSlowInEasing), label = "pct")

    if (showSheet) {
        BucketItemSheet(
            editingItem = editingItem,
            onDismiss   = { showSheet = false; editingItem = null },
            onSave      = { item ->
                if (item.docId.isBlank()) vm.addBucketItem(item) { showSheet = false; editingItem = null }
                else vm.updateBucketItem(item) { showSheet = false; editingItem = null }
            }
        )
    }

    IuScaffold(navController = navController, unreadCount = unreadCount) { padding ->
        Box(modifier = Modifier.fillMaxSize().background(IuColors.background).padding(padding)) {
            Column(modifier = Modifier.fillMaxSize()) {

                // ── HEADER ─────────────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .graphicsLayer { alpha = heroAlpha.value; translationY = heroSlide.value }
                        .background(IuColors.background)
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    SectionLabel("Life Goals")
                    Spacer(Modifier.height(4.dp))
                    Text("BUCKET", fontSize = 52.sp, fontWeight = FontWeight.Black, color = IuColors.white, letterSpacing = (-1.5).sp, lineHeight = 48.sp)
                    Text("LIST.", fontSize = 52.sp, fontWeight = FontWeight.Black, color = IuColors.orange, letterSpacing = (-1.5).sp, lineHeight = 48.sp)
                    Spacer(Modifier.height(20.dp))

                    // Neu stats row
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        SoftCard(modifier = Modifier.size(96.dp), radius = 48.dp, elevation = 7.dp, bgColor = IuColors.background) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                BucketProgressRing(progress = animatedProgress, radius = 44.dp, stroke = 5.dp)
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("$displayPct%", fontSize = 17.sp, fontWeight = FontWeight.Black, color = IuColors.white, letterSpacing = (-0.5).sp)
                                    Text("done", style = IuType.caption.copy(color = IuColors.textFaint, letterSpacing = 1.sp))
                                }
                            }
                        }
                        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(done.toString() to "DONE", (total - done).toString() to "LEFT", total.toString() to "TOTAL").forEach { (v, l) ->
                                SoftCard(modifier = Modifier.weight(1f), radius = 14.dp, elevation = 5.dp, bgColor = IuColors.background) {
                                    Column(modifier = Modifier.padding(vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(v, fontSize = 22.sp, fontWeight = FontWeight.Black, color = if (l == "DONE") IuColors.orange else IuColors.white, letterSpacing = (-0.5).sp)
                                        Text(l, style = IuType.caption.copy(color = IuColors.textFaint, letterSpacing = 1.sp))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    // Neu inset progress bar
                    SoftInset(modifier = Modifier.fillMaxWidth().height(8.dp), radius = 4.dp, depth = 3.dp, bgColor = IuColors.surface2) {
                        Box(modifier = Modifier.fillMaxWidth(animatedProgress).fillMaxHeight().clip(RoundedCornerShape(4.dp)).background(Brush.horizontalGradient(listOf(IuColors.orange, IuColors.orangeSoft))))
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("$done / $total completed", style = IuType.caption.copy(color = IuColors.textMuted))
                        Text("$displayPct%", style = IuType.caption.copy(color = IuColors.orange), fontWeight = FontWeight.Bold)
                    }
                }

                IuDivider()

                // ── Category pills ──────────────────────────────────────────
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)) {
                    items(categories) { cat ->
                        val count  = if (cat == "All") items.size else items.count { it.category == cat }
                        val active = activeCat == cat
                        val color  = bucketCatColor(cat)
                        if (active) {
                            SoftInset(modifier = Modifier.clickable { activeCat = cat }, radius = 50.dp, depth = 3.dp, bgColor = IuColors.surface2) { SoftPillContent(cat, count, color, true) }
                        } else {
                            SoftCard(modifier = Modifier.clickable { activeCat = cat }, radius = 50.dp, elevation = 4.dp, bgColor = IuColors.background) { SoftPillContent(cat, count, color, false) }
                        }
                    }
                }

                // ── Item list ───────────────────────────────────────────────
                AnimatedContent(targetState = activeCat, label = "bucket",
                    transitionSpec = { (fadeIn(tween(250)) + slideInVertically { it / 10 }) togetherWith (fadeOut(tween(180)) + slideOutVertically { -it / 10 }) }
                ) { _ ->
                    LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        itemsIndexed(filtered, key = { _, item -> item.docId }) { idx, item ->
                            NeuBucketRow(
                                item     = item, index = idx,
                                onToggle = { haptic.performHapticFeedback(if (!item.done) HapticFeedbackType.LongPress else HapticFeedbackType.TextHandleMove); vm.toggleBucketItem(item.docId, !item.done) },
                                onEdit   = { editingItem = item; showSheet = true },
                                onDelete = { haptic.performHapticFeedback(HapticFeedbackType.LongPress); vm.deleteBucketItem(item.docId) }
                            )
                        }
                        item { Spacer(Modifier.height(88.dp)) }
                    }
                }
            }

            // ── FAB ─────────────────────────────────────────────────────────
            val fabScale = remember { Animatable(0f) }
            LaunchedEffect(Unit) { delay(500); fabScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy)) }
            SoftCard(
                modifier  = Modifier.align(Alignment.BottomEnd).padding(end = 20.dp, bottom = 20.dp).size(56.dp).scale(fabScale.value).clickable { editingItem = null; showSheet = true },
                radius    = 28.dp, elevation = 10.dp, bgColor = IuColors.orange
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SHARED PILL CONTENT
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun SoftPillContent(label: String, count: Int, color: Color, active: Boolean) {
    Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label.uppercase(), style = IuType.tagStyle.copy(color = if (active) color else IuColors.textMuted, letterSpacing = 1.sp), fontWeight = FontWeight.Bold)
        if (count > 0) {
            Box(modifier = Modifier.clip(RoundedCornerShape(50)).background(if (active) color.copy(0.15f) else IuColors.surface2).padding(horizontal = 5.dp, vertical = 1.dp)) {
                Text("$count", style = IuType.caption.copy(color = if (active) color else IuColors.textFaint, fontSize = 9.sp), fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// NEU BUCKET ROW
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun NeuBucketRow(item: BucketItem, index: Int, onToggle: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val visible = remember { Animatable(0f) }
    LaunchedEffect(item.docId) { delay(minOf(index * 40L, 400L)); visible.animateTo(1f, tween(340, easing = FastOutSlowInEasing)) }

    Column(modifier = Modifier.fillMaxWidth().graphicsLayer { alpha = visible.value }) {
        SoftCard(modifier = Modifier.fillMaxWidth(), radius = 16.dp, elevation = if (item.done) 2.dp else 6.dp, bgColor = if (item.done) IuColors.surface2 else IuColors.background) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable(onClick = onToggle).padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                val checkScale by animateFloatAsState(targetValue = if (item.done) 1f else 0f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "chk")
                if (item.done) {
                    SoftInset(modifier = Modifier.size(28.dp), radius = 14.dp, depth = 3.dp, bgColor = IuColors.orange) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(15.dp).graphicsLayer { scaleX = checkScale; scaleY = checkScale })
                        }
                    }
                } else {
                    SoftCard(modifier = Modifier.size(28.dp), radius = 14.dp, elevation = 4.dp, bgColor = IuColors.background) { Box(Modifier.fillMaxSize()) }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(item.title, style = IuType.body.copy(color = if (item.done) IuColors.textFaint else IuColors.white, textDecoration = if (item.done) TextDecoration.LineThrough else TextDecoration.None), fontWeight = FontWeight.SemiBold)
                    if (item.category.isNotEmpty()) { Spacer(Modifier.height(3.dp)); Text(item.category.uppercase(), style = IuType.caption.copy(color = bucketCatColor(item.category), letterSpacing = 1.sp), fontWeight = FontWeight.Bold) }
                }

                if (item.year.isNotEmpty()) Text(item.year, style = IuType.caption.copy(color = IuColors.textFaint))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.Edit, null, tint = IuColors.textFaint, modifier = Modifier.size(14.dp)) }
                    IconButton(onClick = { showDeleteConfirm = true }, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.Delete, null, tint = IuColors.pink, modifier = Modifier.size(14.dp)) }
                }
            }
        }
        AnimatedVisibility(showDeleteConfirm, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
            SoftInset(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), radius = 12.dp, depth = 3.dp, bgColor = IuColors.surface2) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Delete \"${item.title.take(22)}\"?", style = IuType.caption.copy(color = IuColors.pink), modifier = Modifier.weight(1f))
                    Row {
                        TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel", style = IuType.tagStyle.copy(color = IuColors.textFaint)) }
                        TextButton(onClick = { onDelete(); showDeleteConfirm = false }) { Text("Delete", style = IuType.tagStyle.copy(color = IuColors.pink)) }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ADD / EDIT SHEET
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BucketItemSheet(editingItem: BucketItem?, onDismiss: () -> Unit, onSave: (BucketItem) -> Unit) {
    val isEdit   = editingItem != null
    var title    by remember { mutableStateOf(editingItem?.title ?: "") }
    var category by remember { mutableStateOf(editingItem?.category ?: "") }
    var year     by remember { mutableStateOf(editingItem?.year ?: "") }
    val catOptions = listOf("Travel", "Tech & Code", "Adventure", "Life", "Creative", "Career", "Health", "Learning")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor   = IuColors.background,
        dragHandle = {
            Box(modifier = Modifier.padding(vertical = 10.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                SoftInset(modifier = Modifier.width(44.dp).height(4.dp), radius = 2.dp, depth = 2.dp, bgColor = IuColors.surface2) {}
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxWidth().background(IuColors.background).padding(horizontal = 20.dp).padding(bottom = 32.dp).verticalScroll(rememberScrollState())) {
            SectionLabel(if (isEdit) "Edit Goal" else "New Goal")
            Spacer(Modifier.height(4.dp))
            Text(if (isEdit) "EDIT BUCKET ITEM" else "ADD BUCKET ITEM", fontSize = 26.sp, fontWeight = FontWeight.Black, color = IuColors.white, letterSpacing = (-1).sp)
            Spacer(Modifier.height(20.dp))

            Text("CATEGORY", style = IuType.label.copy(color = IuColors.textFaint))
            Spacer(Modifier.height(10.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(catOptions) { c ->
                    val active = category.equals(c, ignoreCase = true)
                    val color  = bucketCatColor(c)
                    if (active) {
                        SoftInset(modifier = Modifier.clickable { category = c }, radius = 50.dp, depth = 3.dp, bgColor = IuColors.surface2) { SoftPillContent(c, 0, color, true) }
                    } else {
                        SoftCard(modifier = Modifier.clickable { category = c }, radius = 50.dp, elevation = 4.dp, bgColor = IuColors.background) { SoftPillContent(c, 0, color, false) }
                    }
                }
            }
            Spacer(Modifier.height(20.dp))

            Text("GOAL TITLE", style = IuType.label.copy(color = IuColors.textFaint))
            Spacer(Modifier.height(8.dp))
            SoftInset(modifier = Modifier.fillMaxWidth(), radius = 12.dp, depth = 4.dp, bgColor = IuColors.surface2) {
                BasicTextField(value = title, onValueChange = { title = it },
                    textStyle = IuType.body.copy(color = IuColors.white, fontSize = 15.sp),
                    modifier  = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                    decorationBox = { inner -> if (title.isEmpty()) Text("e.g. Visit Japan", style = IuType.body.copy(color = IuColors.textFaint)); inner() }
                )
            }
            Spacer(Modifier.height(16.dp))

            Text("TARGET YEAR (optional)", style = IuType.label.copy(color = IuColors.textFaint))
            Spacer(Modifier.height(8.dp))
            SoftInset(modifier = Modifier.fillMaxWidth(), radius = 12.dp, depth = 4.dp, bgColor = IuColors.surface2) {
                BasicTextField(value = year, onValueChange = { year = it },
                    textStyle = IuType.body.copy(color = IuColors.white, fontSize = 15.sp),
                    modifier  = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                    decorationBox = { inner -> if (year.isEmpty()) Text("e.g. 2026", style = IuType.body.copy(color = IuColors.textFaint)); inner() }
                )
            }
            Spacer(Modifier.height(24.dp))

            val enabled = title.isNotBlank()
            SoftCard(
                modifier  = Modifier.fillMaxWidth().clickable(enabled = enabled) {
                    onSave(BucketItem(docId = editingItem?.docId ?: "", order = editingItem?.order ?: 0, title = title.trim(), category = category, done = editingItem?.done ?: false, year = year.trim()))
                },
                radius = 14.dp, elevation = if (enabled) 8.dp else 2.dp, bgColor = if (enabled) IuColors.orange else IuColors.surface2
            ) {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                    Text(if (isEdit) "SAVE CHANGES →" else "ADD TO BUCKET LIST →", style = IuType.tagStyle.copy(color = if (enabled) Color.White else IuColors.textFaint, letterSpacing = 2.sp, fontSize = 10.sp), fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// CIRCULAR PROGRESS RING
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun BucketProgressRing(progress: Float, radius: androidx.compose.ui.unit.Dp = 44.dp, stroke: androidx.compose.ui.unit.Dp = 5.dp) {
    val strokePx = with(LocalDensity.current) { stroke.toPx() }
    val sweepAnim by animateFloatAsState(targetValue = progress * 360f, animationSpec = tween(1400, delayMillis = 300, easing = FastOutSlowInEasing), label = "ring")
    Canvas(modifier = Modifier.size(radius * 2)) {
        drawArc(color = IuColors.neuShadow.copy(0.5f), startAngle = -90f, sweepAngle = 360f, useCenter = false, style = Stroke(width = strokePx, cap = StrokeCap.Round))
        drawArc(color = IuColors.orange,               startAngle = -90f, sweepAngle = sweepAnim, useCenter = false, style = Stroke(width = strokePx, cap = StrokeCap.Round))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Category colour map
// ─────────────────────────────────────────────────────────────────────────────
fun bucketCatColor(category: String): Color = when (category.lowercase()) {
    "travel"       -> IuColors.blue
    "tech & code",
    "tech"         -> IuColors.orange
    "adventure"    -> IuColors.teal
    "life"         -> IuColors.gold
    "creative"     -> IuColors.purple
    "career"       -> IuColors.pink
    "health"       -> IuColors.teal
    "learning"     -> IuColors.orange
    else           -> IuColors.textFaint
}

// ─────────────────────────────────────────────────────────────────────────────
// KEEP OLD NAMES for backwards compat (used in other screens)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun AnimatedBucketRow(item: BucketItem, index: Int, onToggle: () -> Unit) {
    NeuBucketRow(item = item, index = index, onToggle = onToggle, onEdit = {}, onDelete = {})
}

@Composable
fun BucketItemRow(item: BucketItem, onToggle: () -> Unit) {
    NeuBucketRow(item = item, index = 0, onToggle = onToggle, onEdit = {}, onDelete = {})
}

@Composable
fun FilterPillColored(label: String, count: Int, active: Boolean, color: Color, onClick: () -> Unit) {
    if (active) {
        SoftInset(modifier = Modifier.clickable(onClick = onClick), radius = 50.dp, depth = 3.dp, bgColor = IuColors.surface2) { SoftPillContent(label, count, color, true) }
    } else {
        SoftCard(modifier = Modifier.clickable(onClick = onClick), radius = 50.dp, elevation = 4.dp, bgColor = IuColors.background) { SoftPillContent(label, count, color, false) }
    }
}
