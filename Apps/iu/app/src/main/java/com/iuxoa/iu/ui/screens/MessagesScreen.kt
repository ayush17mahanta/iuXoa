package com.iuxoa.iu.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.iuxoa.iu.data.Contact
import com.iuxoa.iu.ui.components.*
import com.iuxoa.iu.ui.theme.IuColors
import com.iuxoa.iu.ui.theme.IuType
import com.iuxoa.iu.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.delay

// ─────────────────────────────────────────────────────────────────────────────
// MESSAGES / INBOX SCREEN
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun MessagesScreen(navController: NavController, vm: MainViewModel) {
    val contacts    by vm.contacts.collectAsStateWithLifecycle()
    val unreadCount by vm.unreadCount.collectAsStateWithLifecycle()

    val unread = contacts.filter { !it.read }
    val read   = contacts.filter {  it.read }

    val heroAlpha = remember { Animatable(0f) }
    val heroSlide = remember { Animatable(36f) }
    LaunchedEffect(Unit) {
        heroAlpha.animateTo(1f, tween(600, easing = FastOutSlowInEasing))
        heroSlide.animateTo(0f, tween(600, easing = FastOutSlowInEasing))
    }

    IuScaffold(navController = navController, unreadCount = unreadCount) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top    = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding()
                )
        ) {
            // ── HERO HEADER ───────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { alpha = heroAlpha.value; translationY = heroSlide.value }
                    .background(Brush.verticalGradient(listOf(IuColors.auroraBlue.copy(0.2f), IuColors.auroraPink.copy(0.1f), Color.Transparent)))
                    .padding(horizontal = 20.dp, vertical = 28.dp)
            ) {
                Column {
                    SectionLabel("Inbox")
                    Spacer(Modifier.height(4.dp))
                    Text("MESSAGES", fontSize = 52.sp, fontWeight = FontWeight.Black,
                        color = IuColors.text, letterSpacing = (-1.5).sp, lineHeight = 48.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("${contacts.size} total · $unreadCount unread",
                        style = IuType.body, color = IuColors.textMuted)
                    Spacer(Modifier.height(16.dp))
                    AnimatedOrangeDivider(delayMs = 250)
                }
            }

            // ── MARK ALL READ BANNER ──────────────────────────────────────
            AnimatedVisibility(
                visible = unread.isNotEmpty(),
                enter   = fadeIn(tween(300)) + expandVertically(),
                exit    = fadeOut(tween(200)) + shrinkVertically()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .background(IuColors.orange.copy(0.06f))
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val pulse = rememberInfiniteTransition(label = "unread_pulse")
                        val dotScale by pulse.animateFloat(1f, 1.4f,
                            infiniteRepeatable(tween(700), RepeatMode.Reverse), label = "dot")
                        Box(Modifier.scale(dotScale).size(8.dp).clip(CircleShape).background(IuColors.orange))
                        Text("${unread.size} NEW", style = IuType.label.copy(color = IuColors.orange, letterSpacing = 1.5.sp), fontWeight = FontWeight.Bold)
                    }
                    TextButton(onClick = { unread.forEach { vm.markContactRead(it.docId) } }) {
                        Icon(Icons.Default.DoneAll, null, tint = IuColors.teal, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(5.dp))
                        Text("Mark all read", style = IuType.caption.copy(color = IuColors.teal, fontSize = 11.sp))
                    }
                }
            }

            IuDivider()

            // ── CONTENT ───────────────────────────────────────────────────
            if (contacts.isEmpty()) {
                EmptyInbox()
            } else {
                LazyColumn(
                    contentPadding      = androidx.compose.foundation.layout.PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (unread.isNotEmpty()) {
                        item { MessageSectionHeader("UNREAD (${unread.size})", IuColors.orange) }
                        itemsIndexed(unread, key = { _, c -> c.docId }) { idx, contact ->
                            AnimatedContactCard(contact = contact, index = idx,
                                onRead   = { vm.markContactRead(contact.docId) },
                                onDelete = { vm.deleteContact(contact.docId) })
                        }
                    }
                    if (read.isNotEmpty()) {
                        item {
                            Spacer(Modifier.height(8.dp)); IuDivider(); Spacer(Modifier.height(4.dp))
                            MessageSectionHeader("READ (${read.size})", IuColors.textFaint)
                        }
                        itemsIndexed(read, key = { _, c -> c.docId }) { idx, contact ->
                            AnimatedContactCard(contact = contact, index = unread.size + idx,
                                onRead   = {},
                                onDelete = { vm.deleteContact(contact.docId) })
                        }
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION HEADER
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun MessageSectionHeader(label: String, color: Color) {
    Row(modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(Modifier.size(7.dp).clip(CircleShape).background(color))
        Text(label, style = IuType.label.copy(color = color, letterSpacing = 1.5.sp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ANIMATED CARD WRAPPER
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun AnimatedContactCard(contact: Contact, index: Int, onRead: () -> Unit, onDelete: () -> Unit) {
    val visible = remember { Animatable(0f) }
    val slideY  = remember { Animatable(20f) }
    LaunchedEffect(contact.docId) {
        delay(minOf(index * 60L, 400L))
        visible.animateTo(1f, tween(400, easing = FastOutSlowInEasing))
        slideY.animateTo(0f,  tween(400, easing = FastOutSlowInEasing))
    }
    Box(modifier = Modifier.graphicsLayer { alpha = visible.value; translationY = slideY.value }) {
        ContactCard(contact = contact, onRead = onRead, onDelete = onDelete)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// CONTACT CARD — expandable, with Reply via Email button
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun ContactCard(contact: Contact, onRead: () -> Unit, onDelete: () -> Unit) {
    val context               = LocalContext.current
    var expanded              by remember { mutableStateOf(!contact.read) }
    var showDeleteConfirm     by remember { mutableStateOf(false) }
    val accentColor           = if (!contact.read) IuColors.orange else IuColors.borderMid

    NeuCard(accentColor = accentColor) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── TOP ROW ───────────────────────────────────────────────────
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)) {
                    // Avatar
                    Box(
                        modifier = Modifier.size(44.dp).clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    if (!contact.read)
                                        listOf(IuColors.orange.copy(0.2f), IuColors.orange.copy(0.05f))
                                    else listOf(IuColors.surface2, IuColors.surface3)
                                )
                            )
                            .border(1.dp,
                                if (!contact.read) IuColors.orange.copy(0.5f) else IuColors.borderMid,
                                CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = contact.name.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                            style = IuType.cardTitle.copy(fontSize = 18.sp,
                                color = if (!contact.read) IuColors.orange else IuColors.textFaint),
                            fontWeight = FontWeight.Black
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(contact.name, style = IuType.cardTitle.copy(fontSize = 15.sp), color = IuColors.white,
                                maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
                            if (!contact.read) {
                                val pulse = rememberInfiniteTransition(label = "cp")
                                val ps by pulse.animateFloat(1f, 1.3f, infiniteRepeatable(tween(800), RepeatMode.Reverse), label = "ps")
                                Box(Modifier.scale(ps).size(6.dp).clip(CircleShape).background(IuColors.orange))
                            }
                        }
                        Text(contact.email, style = IuType.caption.copy(color = IuColors.textFaint),
                            maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    contact.createdAt?.let { ts ->
                        Text(formatContactTimestamp(ts.toDate()),
                            style = IuType.caption.copy(color = IuColors.textFaint, fontSize = 9.sp))
                    }
                    Row {
                        TextButton(onClick = { expanded = !expanded }) {
                            Text(if (expanded) "↑" else "↓",
                                style = IuType.tagStyle.copy(color = if (!contact.read) IuColors.orange else IuColors.textFaint))
                        }
                        IconButton(onClick = { showDeleteConfirm = true }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Delete, null, tint = IuColors.pink, modifier = Modifier.size(15.dp))
                        }
                    }
                }
            }

            // ── PREVIEW (collapsed) ───────────────────────────────────────
            if (!expanded) {
                Spacer(Modifier.height(8.dp))
                Text(contact.message, style = IuType.body.copy(fontStyle = FontStyle.Italic),
                    color = IuColors.textMuted, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }

            // ── EXPANDED ──────────────────────────────────────────────────
            AnimatedVisibility(visible = expanded,
                enter = fadeIn(tween(250)) + expandVertically(),
                exit  = fadeOut(tween(200)) + shrinkVertically()) {
                Column {
                    Spacer(Modifier.height(14.dp))
                    // Message box
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.linearGradient(
                                listOf(IuColors.surface2, IuColors.surface3),
                                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                                end   = androidx.compose.ui.geometry.Offset(400f, 100f)
                            ))
                            .border(1.dp,
                                Brush.linearGradient(listOf(IuColors.neuHigh.copy(0.4f), IuColors.neuShadow)),
                                RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Text("\"${contact.message}\"",
                            style = IuType.body.copy(lineHeight = 22.sp, fontStyle = FontStyle.Italic, color = IuColors.white))
                    }

                    Spacer(Modifier.height(12.dp))

                    // ── ACTION BUTTONS ROW ────────────────────────────────
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        // REPLY VIA EMAIL — opens mail app with pre-filled fields
                        Box(
                            modifier = Modifier.weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Brush.linearGradient(IuColors.gradientOrange))
                                .clickable {
                                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:${contact.email}")
                                        putExtra(Intent.EXTRA_SUBJECT, "Re: Your message")
                                        putExtra(Intent.EXTRA_TEXT, "\n\n---\nOn ${
                                            contact.createdAt?.toDate()?.let {
                                                SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(it)
                                            } ?: "a recent date"
                                        }, ${contact.name} wrote:\n\"${contact.message}\"")
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Reply via email"))
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.Send, null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Text("REPLY", style = IuType.tagStyle.copy(color = Color.White, letterSpacing = 2.sp, fontSize = 10.sp), fontWeight = FontWeight.Black)
                            }
                        }

                        // MARK AS READ
                        if (!contact.read) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(IuColors.teal.copy(0.08f))
                                    .border(1.dp, IuColors.teal.copy(0.3f), RoundedCornerShape(12.dp))
                                    .clickable(onClick = onRead)
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.DoneAll, null, tint = IuColors.teal, modifier = Modifier.size(14.dp))
                                    Text("READ", style = IuType.tagStyle.copy(color = IuColors.teal, fontSize = 10.sp, letterSpacing = 1.sp))
                                }
                            }
                        }
                    }
                }
            }

            // ── BOTTOM ACCENT LINE ────────────────────────────────────────
            Spacer(Modifier.height(10.dp))
            Spacer(modifier = Modifier.fillMaxWidth().height(1.dp)
                .background(Brush.horizontalGradient(listOf(accentColor.copy(0.6f), Color.Transparent))))

            // ── DELETE CONFIRM ────────────────────────────────────────────
            AnimatedVisibility(showDeleteConfirm, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
                Column {
                    Spacer(Modifier.height(8.dp))
                    IuDivider()
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically) {
                        Text("Delete this message?", style = IuType.body.copy(color = IuColors.pink, fontSize = 12.sp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(onClick = { showDeleteConfirm = false }) {
                                Text("Cancel", style = IuType.tagStyle.copy(color = IuColors.textFaint))
                            }
                            TextButton(onClick = { onDelete(); showDeleteConfirm = false }) {
                                Text("Delete", style = IuType.tagStyle.copy(color = IuColors.pink))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// EMPTY STATE
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun EmptyInbox() {
    Box(Modifier.fillMaxSize().padding(40.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
            val pulse = rememberInfiniteTransition(label = "empty")
            val iconScale by pulse.animateFloat(1f, 1.1f, infiniteRepeatable(tween(1600), RepeatMode.Reverse), label = "ic")
            Icon(Icons.Default.MailOutline, null, tint = IuColors.border,
                modifier = Modifier.size(56.dp).scale(iconScale))
            Text("No messages yet", style = IuType.sectionTitle.copy(fontSize = 22.sp), color = IuColors.textFaint)
            Text("Contact form submissions will appear here.", style = IuType.body, color = IuColors.textDim)
        }
    }
}

private fun formatContactTimestamp(date: Date): String {
    val now  = Calendar.getInstance()
    val then = Calendar.getInstance().apply { time = date }
    return when {
        now.get(Calendar.DATE) == then.get(Calendar.DATE) ->
            SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
        now.get(Calendar.DATE) - then.get(Calendar.DATE) == 1 -> "Yesterday"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(date)
    }
}
