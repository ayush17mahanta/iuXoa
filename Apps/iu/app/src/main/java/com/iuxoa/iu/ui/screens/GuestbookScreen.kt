package com.iuxoa.iu.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.iuxoa.iu.data.GuestbookEntry
import com.iuxoa.iu.ui.components.*
import com.iuxoa.iu.ui.theme.IuColors
import com.iuxoa.iu.ui.theme.IuType
import com.iuxoa.iu.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GuestbookScreen(navController: NavController, vm: MainViewModel) {
    val entries     by vm.guestbook.collectAsStateWithLifecycle()
    val unreadCount by vm.unreadCount.collectAsStateWithLifecycle()
    val haptic      = LocalHapticFeedback.current

    val heroAlpha = remember { Animatable(0f) }
    val heroSlide = remember { Animatable(32f) }
    LaunchedEffect(Unit) {
        heroAlpha.animateTo(1f, tween(600, easing = FastOutSlowInEasing))
        heroSlide.animateTo(0f, tween(600, easing = FastOutSlowInEasing))
    }

    IuScaffold(navController = navController, unreadCount = unreadCount) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ── HEADER ─────────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .graphicsLayer { alpha = heroAlpha.value; translationY = heroSlide.value }
                    .background(
                        Brush.verticalGradient(
                            listOf(IuColors.auroraBlue.copy(0.25f), IuColors.auroraIndigo.copy(0.15f), Color.Transparent)
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                SectionLabel("Community")
                Spacer(Modifier.height(4.dp))
                Text("GUEST", fontSize = 52.sp, fontWeight = FontWeight.Black, color = IuColors.text, letterSpacing = (-1.5).sp, lineHeight = 48.sp)
                Text("BOOK.", fontSize = 52.sp, fontWeight = FontWeight.Black, color = IuColors.orange, letterSpacing = (-1.5).sp, lineHeight = 48.sp)
                Spacer(Modifier.height(10.dp))
                Text("${entries.size} total entries", style = IuType.body.copy(color = IuColors.textMuted))
                Spacer(Modifier.height(14.dp))
                AnimatedOrangeDivider(delayMs = 200)
            }

            IuDivider()

            LazyColumn(
                contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (entries.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillParentMaxWidth().padding(vertical = 60.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("✦", fontSize = 36.sp, color = IuColors.border)
                                Spacer(Modifier.height(16.dp))
                                Text("No entries yet", style = IuType.sectionTitle.copy(fontSize = 20.sp), color = IuColors.textFaint)
                                Spacer(Modifier.height(8.dp))
                                Text("Guestbook messages will appear here.", style = IuType.body, color = IuColors.textDim)
                            }
                        }
                    }
                }
                itemsIndexed(entries, key = { _, e -> e.docId }) { idx, entry ->
                    StaggeredGuestCard(entry = entry, staggerIdx = idx, onDelete = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        vm.deleteGuestbook(entry.docId)
                    })
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun StaggeredGuestCard(entry: GuestbookEntry, staggerIdx: Int, onDelete: () -> Unit) {
    val visible = remember { Animatable(0f) }
    val slideY  = remember { Animatable(40f) }
    LaunchedEffect(entry.docId) {
        delay(minOf(staggerIdx * 55L, 450L))
        visible.animateTo(1f, tween(420, easing = FastOutSlowInEasing))
        slideY.animateTo(0f, tween(420, easing = FastOutSlowInEasing))
    }
    Box(modifier = Modifier.graphicsLayer { alpha = visible.value; translationY = slideY.value }) {
        GuestbookCard(entry = entry, onDelete = onDelete)
    }
}

@Composable
fun GuestbookCard(entry: GuestbookEntry, onDelete: () -> Unit) {
    val accentColor       = if (entry.approved) IuColors.teal else IuColors.orange
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val displayName  = entry.name.ifBlank { "Anonymous" }
    val avatarLetter = displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "?"

    NeuCard(accentColor = accentColor) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                    // Avatar
                    Box(
                        modifier = Modifier.size(48.dp).clip(CircleShape)
                            .shadow(3.dp, CircleShape, ambientColor = accentColor.copy(0.2f))
                            .background(Brush.linearGradient(listOf(accentColor.copy(0.12f), accentColor.copy(0.05f))))
                            .border(1.5.dp, accentColor.copy(0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(avatarLetter, style = IuType.cardTitle.copy(fontSize = 20.sp, color = accentColor), fontWeight = FontWeight.Black)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(displayName, style = IuType.cardTitle.copy(fontSize = 16.sp), color = IuColors.text, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Spacer(Modifier.height(3.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier.clip(RoundedCornerShape(50)).background(accentColor.copy(0.10f))
                                    .border(1.dp, accentColor.copy(0.3f), RoundedCornerShape(50)).padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(if (entry.approved) "APPROVED" else "PENDING", style = IuType.caption.copy(color = accentColor, letterSpacing = 1.sp), fontWeight = FontWeight.Bold)
                            }
                            if (entry.emoji.isNotBlank()) { Text(entry.emoji, fontSize = 14.sp) }
                        }
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    entry.createdAt?.let { ts ->
                        Text(SimpleDateFormat("MMM d", Locale.getDefault()).format(ts.toDate()), style = IuType.caption.copy(color = IuColors.textFaint, fontSize = 9.sp))
                    }
                    if (!showDeleteConfirm) {
                        IconButton(onClick = { showDeleteConfirm = true }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Delete, null, tint = IuColors.pink, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Message bubble
            Box(
                modifier = Modifier.fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(12.dp), ambientColor = IuColors.neuShadow.copy(0.15f))
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.linearGradient(listOf(IuColors.surface2, IuColors.surface3), start = Offset(0f,0f), end = Offset(400f,100f)))
                    .border(1.dp, Brush.linearGradient(listOf(IuColors.neuHigh, IuColors.neuShadow.copy(0.2f))), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Text("\"${entry.message}\"", style = IuType.body.copy(lineHeight = 22.sp, color = IuColors.textMuted))
            }

            AnimatedVisibility(showDeleteConfirm, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
                Column {
                    Spacer(Modifier.height(12.dp)); IuDivider(); Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Delete this entry?", style = IuType.body.copy(color = IuColors.pink, fontSize = 12.sp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel", style = IuType.tagStyle.copy(color = IuColors.textFaint)) }
                            TextButton(onClick = { onDelete(); showDeleteConfirm = false }) { Text("Delete", style = IuType.tagStyle.copy(color = IuColors.pink)) }
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(Brush.horizontalGradient(listOf(accentColor.copy(0.5f), Color.Transparent))))
        }
    }
}
