package com.iuxoa.iu.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import com.iuxoa.iu.ui.components.*
import com.iuxoa.iu.ui.theme.IuColors
import com.iuxoa.iu.ui.theme.IuType
import com.iuxoa.iu.viewmodel.MainViewModel

// ─────────────────────────────────────────────────────────────────────────────
// RESEARCH SCREEN — neumorphic, full CRUD
// ─────────────────────────────────────────────────────────────────────────────

private fun tabToCollection(tab: String) = when (tab) {
    "Patents"  -> "patents"
    "Papers"   -> "papers"
    "Chapters" -> "bookChapters"
    "Other"    -> "otherPubs"
    else       -> ""
}

private fun tabToType(tab: String) = when (tab) {
    "Patents"  -> "Patent"
    "Papers"   -> "Research Paper"
    "Chapters" -> "Book Chapter"
    "Other"    -> "Journal"
    else       -> "Patent"
}

data class ResearchItemUi(
    val docId: String, val id: String, val title: String,
    val type: String, val year: String, val link: String?, val order: Int,
    val collection: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResearchScreen(navController: NavController, vm: MainViewModel) {
    val patents      by vm.patents.collectAsStateWithLifecycle()
    val papers       by vm.papers.collectAsStateWithLifecycle()
    val bookChapters by vm.bookChapters.collectAsStateWithLifecycle()
    val otherPubs    by vm.otherPubs.collectAsStateWithLifecycle()
    val unreadCount  by vm.unreadCount.collectAsStateWithLifecycle()
    val haptic       = LocalHapticFeedback.current

    val filters = listOf("All", "Patents", "Papers", "Chapters", "Other")
    var active  by remember { mutableStateOf("All") }

    val allItems = remember(patents, papers, bookChapters, otherPubs) {
        buildList {
            addAll(patents.map      { ResearchItemUi(it.docId, it.id, it.title, it.type, it.year, it.link, it.order, "patents") })
            addAll(papers.map       { ResearchItemUi(it.docId, it.id, it.title, it.type, it.year, it.link, it.order, "papers") })
            addAll(bookChapters.map { ResearchItemUi(it.docId, it.id, it.title, it.type, it.year, it.link, it.order, "bookChapters") })
            addAll(otherPubs.map    { ResearchItemUi(it.docId, it.id, it.title, it.type, it.year, it.link, it.order, "otherPubs") })
        }
    }

    val displayed = if (active == "All") allItems else allItems.filter { it.collection == tabToCollection(active) }
    val counts = mapOf("All" to allItems.size, "Patents" to patents.size, "Papers" to papers.size,
        "Chapters" to bookChapters.size, "Other" to otherPubs.size)

    var showSheet   by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<ResearchItemUi?>(null) }
    val addTab = if (active == "All") "Patents" else active

    val heroVisible = remember { Animatable(0f) }
    LaunchedEffect(Unit) { heroVisible.animateTo(1f, tween(600, easing = FastOutSlowInEasing)) }

    if (showSheet) {
        ResearchItemSheet(
            editingItem = editingItem, defaultTab = addTab,
            onDismiss   = { showSheet = false; editingItem = null },
            onSave      = { col, docId, id, title, type, year, link, order ->
                if (docId.isBlank()) vm.addResearchItem(col, id, title, type, year, link.ifBlank { null }, order) { showSheet = false; editingItem = null }
                else vm.updateResearchItem(col, docId, id, title, type, year, link.ifBlank { null }, order) { showSheet = false; editingItem = null }
            }
        )
    }

    IuScaffold(navController = navController, unreadCount = unreadCount) { padding ->
        Box(Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                // ── HEADER ────────────────────────────────────────────────
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .graphicsLayer { alpha = heroVisible.value; translationY = (1f - heroVisible.value) * 30f }
                        .background(Brush.verticalGradient(listOf(IuColors.auroraViolet.copy(0.4f), IuColors.auroraIndigo.copy(0.15f), Color.Transparent)))
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Column {
                        SectionLabel("Publications")
                        Spacer(Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom) {
                            Text("RESEARCH", fontSize = 48.sp, fontWeight = FontWeight.Black,
                                color = IuColors.text, letterSpacing = (-1).sp)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text("Add, edit or delete entries — synced to your website.",
                            style = IuType.body, color = IuColors.textMuted)
                    }
                }

                // ── STATS ROW — neumorphic cells ──────────────────────────
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(patents.size to "Patents", papers.size to "Papers",
                        bookChapters.size to "Chapters", otherPubs.size to "Other")
                        .forEachIndexed { i, (v, l) ->
                            val colors = listOf(IuColors.orange, IuColors.pink, IuColors.blue, IuColors.purple)
                            NeuStatCell(value = "$v", label = l, color = colors[i], modifier = Modifier.weight(1f))
                        }
                }

                IuDivider()

                // ── FILTER PILLS ──────────────────────────────────────────
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 12.dp)) {
                    items(filters) { f ->
                        FilterPillWithCount(label = f, count = counts[f] ?: 0, active = active == f, onClick = { active = f })
                    }
                }

                // ── LIST ──────────────────────────────────────────────────
                AnimatedContent(targetState = displayed, label = "research_list",
                    transitionSpec = {
                        (fadeIn(tween(200)) + slideInVertically { it / 10 }) togetherWith
                        (fadeOut(tween(150)) + slideOutVertically { -it / 10 })
                    }
                ) { list ->
                    if (list.isEmpty()) {
                        Box(Modifier.fillMaxSize().padding(40.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("✦", fontSize = 32.sp, color = IuColors.border)
                                Text("No entries", style = IuType.sectionTitle.copy(fontSize = 18.sp), color = IuColors.textFaint)
                                Text("Tap + to add a new research entry.", style = IuType.body, color = IuColors.textDim)
                            }
                        }
                    } else {
                        LazyColumn(contentPadding = PaddingValues(bottom = 80.dp)) {
                            itemsIndexed(list, key = { _, it -> it.docId }) { idx, item ->
                                ResearchEditRow(
                                    item     = item, index = idx,
                                    onEdit   = { editingItem = item; showSheet = true },
                                    onDelete = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        vm.deleteResearchItem(item.collection, item.docId)
                                    }
                                )
                            }
                        }
                    }
                }
            }
            // ── FAB ──────────────────────────────────────────────────────
            val fabScale = remember { Animatable(0f) }
            LaunchedEffect(Unit) { kotlinx.coroutines.delay(400); fabScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy)) }
            FloatingActionButton(
                onClick        = { editingItem = null; showSheet = true },
                modifier       = Modifier.align(Alignment.BottomEnd).padding(end = 20.dp, bottom = padding.calculateBottomPadding() + 16.dp).scale(fabScale.value),
                containerColor = IuColors.orange, contentColor = Color.White, shape = CircleShape
            ) { Icon(Icons.Default.Add, "Add research", modifier = Modifier.size(22.dp)) }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// RESEARCH EDIT ROW — neumorphic card per entry
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun ResearchEditRow(item: ResearchItemUi, index: Int, onEdit: () -> Unit, onDelete: () -> Unit) {
    val typeColor = when (item.type) {
        "Patent"         -> IuColors.orange
        "Research Paper" -> IuColors.pink
        "Book Chapter"   -> IuColors.blue
        "Journal"        -> IuColors.teal
        else             -> IuColors.purple
    }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val visible = remember { Animatable(0f) }
    LaunchedEffect(item.docId) {
        kotlinx.coroutines.delay(index * 40L)
        visible.animateTo(1f, tween(350, easing = FastOutSlowInEasing))
    }

    Column(
        modifier = Modifier.fillMaxWidth()
            .graphicsLayer { alpha = visible.value; translationY = (1f - visible.value) * 12f }
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        NeuCard(accentColor = typeColor, cornerRadius = 14.dp) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top) {
                    // ID + type dot
                    Column(Modifier.width(36.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(item.id, style = IuType.caption.copy(color = IuColors.textFaint), fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(4.dp))
                        Box(Modifier.size(5.dp).clip(CircleShape).background(typeColor))
                    }
                    // Title + badges
                    Column(Modifier.weight(1f)) {
                        Text(item.title, style = IuType.body.copy(color = IuColors.white, lineHeight = 20.sp),
                            maxLines = 3, overflow = TextOverflow.Ellipsis)
                        Spacer(Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.clip(RoundedCornerShape(50))
                                    .background(Brush.linearGradient(listOf(typeColor.copy(0.12f), typeColor.copy(0.04f))))
                                    .border(1.dp,
                                        Brush.linearGradient(listOf(typeColor.copy(0.4f), typeColor.copy(0.1f))),
                                        RoundedCornerShape(50))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) { Text(item.type.uppercase(), style = IuType.tagStyle.copy(color = typeColor, fontSize = 8.sp)) }
                            if (item.year.isNotBlank())
                                Text(item.year, style = IuType.caption.copy(color = IuColors.textFaint, fontSize = 10.sp))
                            if (!item.link.isNullOrBlank())
                                Text("↗ link", style = IuType.caption.copy(color = IuColors.teal, fontSize = 10.sp))
                        }
                    }
                    // Actions
                    if (!showDeleteConfirm) {
                        Row {
                            IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Edit, null, tint = IuColors.textFaint, modifier = Modifier.size(15.dp))
                            }
                            IconButton(onClick = { showDeleteConfirm = true }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Delete, null, tint = IuColors.pink, modifier = Modifier.size(15.dp))
                            }
                        }
                    }
                }
                // Delete confirm
                AnimatedVisibility(showDeleteConfirm, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text("Delete \"${item.title.take(30)}…\"?",
                            style = IuType.body.copy(color = IuColors.pink, fontSize = 11.sp), modifier = Modifier.weight(1f))
                        Row {
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
// ADD / EDIT SHEET
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResearchItemSheet(
    editingItem: ResearchItemUi?, defaultTab: String, onDismiss: () -> Unit,
    onSave: (col: String, docId: String, id: String, title: String, type: String, year: String, link: String, order: Int) -> Unit
) {
    val tabs = listOf("Patents", "Papers", "Chapters", "Other")
    var selectedTab by remember { mutableStateOf(editingItem?.let {
        when (it.collection) { "patents" -> "Patents"; "papers" -> "Papers"; "bookChapters" -> "Chapters"; else -> "Other" }
    } ?: defaultTab) }

    var id    by remember { mutableStateOf(editingItem?.id ?: "") }
    var title by remember { mutableStateOf(editingItem?.title ?: "") }
    var year  by remember { mutableStateOf(editingItem?.year ?: "2025") }
    var link  by remember { mutableStateOf(editingItem?.link ?: "") }

    val isEdit = editingItem != null
    val col    = tabToCollection(selectedTab)
    val type   = tabToType(selectedTab)

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = IuColors.surface,
        dragHandle = {
            Box(Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                Spacer(Modifier.width(40.dp).height(3.dp).clip(RoundedCornerShape(50)).background(IuColors.borderMid))
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp).verticalScroll(rememberScrollState())) {
            SectionLabel(if (isEdit) "Edit Entry" else "New Entry")
            Spacer(Modifier.height(4.dp))
            Text(if (isEdit) "EDIT RESEARCH" else "ADD RESEARCH",
                fontSize = 28.sp, fontWeight = FontWeight.Black, color = IuColors.white, letterSpacing = (-1).sp)
            Spacer(Modifier.height(16.dp))

            if (!isEdit) {
                Text("TYPE", style = IuType.label.copy(color = IuColors.textFaint))
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(tabs) { t -> FilterPill(t, selectedTab == t) { selectedTab = t } }
                }
                Spacer(Modifier.height(16.dp))
            } else {
                val typeColor = when (type) { "Patent" -> IuColors.orange; "Research Paper" -> IuColors.pink; "Book Chapter" -> IuColors.blue; else -> IuColors.teal }
                Box(Modifier.clip(RoundedCornerShape(50))
                    .background(Brush.linearGradient(listOf(typeColor.copy(0.12f), typeColor.copy(0.04f))))
                    .border(1.dp, typeColor.copy(0.3f), RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 6.dp)) {
                    Text(type.uppercase(), style = IuType.tagStyle.copy(color = typeColor, letterSpacing = 1.sp), fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(16.dp))
            }

            ResearchSheetField(id,   "ID (e.g. P01)")        { id = it }
            ResearchSheetField(title,"Title")                 { title = it }
            ResearchSheetField(year, "Year")                  { year = it }
            ResearchSheetField(link ?: "", "Link URL (optional)") { link = it }

            Spacer(Modifier.height(20.dp)); IuDivider(); Spacer(Modifier.height(16.dp))

            val enabled = title.isNotBlank()
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                    .background(if (enabled) Brush.linearGradient(IuColors.gradientOrange)
                        else Brush.linearGradient(listOf(IuColors.surface2, IuColors.surface3)))
                    .clickable(enabled = enabled) {
                        onSave(col, editingItem?.docId ?: "", id.trim(), title.trim(), type, year.trim(), link?.trim() ?: "", editingItem?.order ?: 0)
                    }.padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(if (isEdit) "SAVE CHANGES →" else "ADD TO RESEARCH →",
                    style = IuType.body.copy(color = if (enabled) Color.White else IuColors.textFaint, letterSpacing = 2.sp, fontSize = 9.sp),
                    fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun ResearchSheetField(value: String, label: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value, onValueChange = onChange,
        label = { Text(label, style = IuType.body.copy(color = IuColors.textFaint, fontSize = 10.sp)) },
        modifier = Modifier.fillMaxWidth(), textStyle = IuType.body.copy(color = IuColors.white),
        singleLine = label != "Title",
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = IuColors.orange, unfocusedBorderColor = IuColors.border,
            cursorColor = IuColors.orange, focusedLabelColor = IuColors.orange,
            unfocusedLabelColor = IuColors.textFaint,
            focusedContainerColor = IuColors.surface2, unfocusedContainerColor = IuColors.surface2
        )
    )
    Spacer(Modifier.height(12.dp))
}
