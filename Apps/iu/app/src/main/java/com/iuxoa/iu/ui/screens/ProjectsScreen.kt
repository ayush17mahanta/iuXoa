package com.iuxoa.iu.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.iuxoa.iu.data.Project
import com.iuxoa.iu.ui.components.*
import com.iuxoa.iu.ui.theme.IuColors
import com.iuxoa.iu.ui.theme.IuType
import com.iuxoa.iu.viewmodel.MainViewModel
import kotlinx.coroutines.delay

// Convert Google Drive share link → direct download URL Coil can load
private fun toDirectImageUrl(url: String): String {
    val fileIdRegex = Regex("(?:/d/|id=)([a-zA-Z0-9_-]{10,})")
    val match = fileIdRegex.find(url)
    return if (match != null) "https://drive.google.com/uc?export=view&id=${match.groupValues[1]}"
    else url
}

@Composable
fun ProjectsScreen(navController: NavController, vm: MainViewModel) {
    val projects    by vm.projects.collectAsStateWithLifecycle()
    val unreadCount by vm.unreadCount.collectAsStateWithLifecycle()
    val cats = listOf("All", "App", "Web", "Data", "Game")
    var activeCat by remember { mutableStateOf("All") }
    val filtered = if (activeCat == "All") projects else projects.filter { it.cat == activeCat }
    var showAddSheet   by remember { mutableStateOf(false) }
    var editingProject by remember { mutableStateOf<Project?>(null) }

    val heroAlpha = remember { Animatable(0f) }
    val heroSlide = remember { Animatable(40f) }
    LaunchedEffect(Unit) {
        heroAlpha.animateTo(1f, tween(700, easing = FastOutSlowInEasing))
        heroSlide.animateTo(0f, tween(700, easing = FastOutSlowInEasing))
    }

    if (showAddSheet || editingProject != null) {
        AddProjectSheet(
            nextOrder      = (projects.maxOfOrNull { it.order } ?: 0) + 1,
            editingProject = editingProject,
            onDismiss      = { showAddSheet = false; editingProject = null },
            onAdd          = { project -> vm.addProject(project) { showAddSheet = false } },
            onUpdate       = { project -> vm.updateProject(project) { editingProject = null } }
        )
    }

    IuScaffold(navController = navController, unreadCount = unreadCount) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // ── HERO HEADER ───────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer { alpha = heroAlpha.value; translationY = heroSlide.value }
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    IuColors.auroraOrange.copy(0.3f),
                                    IuColors.auroraBlue.copy(0.1f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    SectionLabel("Portfolio")
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text          = "MY WORKS.",
                        fontSize      = 52.sp,
                        fontWeight    = FontWeight.Black,
                        color         = IuColors.text,
                        letterSpacing = (-2).sp,
                        lineHeight    = 50.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text  = "Apps, web platforms, data dashboards & games.",
                        style = IuType.body,
                        color = IuColors.textMuted
                    )
                }

                IuDivider()

                // ── LIVE STATS ROW ────────────────────────────────────────
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    cats.forEach { cat ->
                        val count  = if (cat == "All") projects.size else projects.count { it.cat == cat }
                        val active = activeCat == cat
                        val accentAnim by animateColorAsState(
                            if (active) IuColors.orange else IuColors.textFaint, tween(250), label = "sc"
                        )
                        val indicatorAlpha by animateFloatAsState(
                            if (active) 1f else 0f, tween(250), label = "ia"
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { activeCat = cat }
                                .padding(vertical = 14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            var displayCount by remember { mutableIntStateOf(0) }
                            LaunchedEffect(count) {
                                animate(0f, count.toFloat(), animationSpec = tween(1000, easing = FastOutSlowInEasing)) { v, _ -> displayCount = v.toInt() }
                            }
                            Text("$displayCount", fontSize = 22.sp, fontWeight = FontWeight.Black, color = accentAnim, letterSpacing = (-0.5).sp)
                            Text(cat.uppercase(), style = IuType.body.copy(fontSize = 8.sp, letterSpacing = 1.sp), color = accentAnim)
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth(0.6f).height(2.dp)
                                    .graphicsLayer { alpha = indicatorAlpha }
                                    .background(Brush.horizontalGradient(listOf(Color.Transparent, IuColors.orange, Color.Transparent)))
                            )
                        }
                    }
                }

                IuDivider()

                // ── FILTER PILLS ──────────────────────────────────────────
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding        = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    items(cats) { cat ->
                        val count = if (cat == "All") projects.size else projects.count { it.cat == cat }
                        FilterPillWithCount(cat, count, activeCat == cat) { activeCat = cat }
                    }
                }

                // ── ANIMATED PROJECT LIST ─────────────────────────────────
                AnimatedContent(
                    targetState    = activeCat,
                    transitionSpec = {
                        (fadeIn(tween(280)) + slideInVertically { it / 8 }) togetherWith
                        (fadeOut(tween(180)) + slideOutVertically { -it / 8 })
                    },
                    label = "projects_list"
                ) { _ ->
                    if (projects.isEmpty()) {
                        LazyColumn(
                            contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(4) { ProjectCardShimmer() }
                            item { Spacer(Modifier.height(80.dp)) }
                        }
                    } else {
                        LazyColumn(
                            contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            itemsIndexed(filtered, key = { _, p -> p.docId }) { idx, project ->
                                ProjectCard(
                                    project  = project,
                                    index    = idx,
                                    onEdit   = { editingProject = project },
                                    onDelete = { vm.deleteProject(project.docId) }
                                )
                            }
                            item { Spacer(Modifier.height(80.dp)) }
                        }
                    }
                }
            }

            // ── FAB ───────────────────────────────────────────────────────
            val fabScale = remember { Animatable(0f) }
            LaunchedEffect(Unit) {
                delay(500)
                fabScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            }
            FloatingActionButton(
                onClick        = { showAddSheet = true },
                modifier       = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = padding.calculateBottomPadding() + 16.dp)
                    .scale(fabScale.value)
                    .shadow(12.dp, CircleShape, ambientColor = IuColors.orange.copy(0.35f), spotColor = IuColors.orange.copy(0.5f)),
                containerColor = IuColors.orange,
                contentColor   = Color.White,
                shape          = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add project", modifier = Modifier.size(22.dp))
            }
        }
    }
}

@Composable
fun ProjectCard(project: Project, index: Int, onEdit: () -> Unit, onDelete: () -> Unit) {
    val haptic            = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed         by interactionSource.collectIsPressedAsState()

    val cardScale by animateFloatAsState(
        if (isPressed) 0.965f else 1f,
        spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioLowBouncy),
        label = "card_scale"
    )

    val visible = remember { Animatable(0f) }
    val slideY  = remember { Animatable(60f) }
    LaunchedEffect(index) {
        delay(minOf(index * 55L, 450L))
        visible.animateTo(1f, tween(550, easing = FastOutSlowInEasing))
        slideY.animateTo(0f,  tween(550, easing = FastOutSlowInEasing))
    }

    val accentColor = remember(project.accent) {
        try { Color(android.graphics.Color.parseColor(project.accent)) }
        catch (e: Exception) { IuColors.orange }
    }

    var showDeleteConfirm by remember { mutableStateOf(false) }

    val overlayAlpha by animateFloatAsState(if (isPressed) 1f else 0f, tween(if (isPressed) 200 else 300), label = "oa")
    val lineWidth    by animateDpAsState(if (isPressed) 36.dp else 10.dp, tween(300), label = "lw")
    val lineColor    by animateColorAsState(if (isPressed) accentColor else IuColors.border, tween(300), label = "lc")
    val deleteAlpha  by animateFloatAsState(if (showDeleteConfirm) 1f else 0f, tween(220), label = "da")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { alpha = visible.value; translationY = slideY.value }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .scale(cardScale)
                .shadow(
                    elevation    = if (isPressed) 2.dp else 8.dp,
                    shape        = RoundedCornerShape(18.dp),
                    ambientColor = accentColor.copy(0.15f),
                    spotColor    = IuColors.neuShadow.copy(0.25f)
                )
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.linearGradient(
                        listOf(IuColors.neuHigh, IuColors.surface),
                        start = Offset(0f, 0f), end = Offset(0f, 400f)
                    )
                )
                .border(
                    1.dp,
                    Brush.verticalGradient(listOf(accentColor.copy(if (isPressed) 0.4f else 0.15f), IuColors.border)),
                    RoundedCornerShape(18.dp)
                )
                .clickable(interactionSource = interactionSource, indication = null) {}
        ) {
            Column {
                // Top accent line
                Spacer(
                    modifier = Modifier.fillMaxWidth().height(2.5.dp)
                        .background(Brush.horizontalGradient(listOf(accentColor, accentColor.copy(0.3f), Color.Transparent)))
                )
                // Image
                Box(modifier = Modifier.fillMaxWidth().height(190.dp)) {
                    SubcomposeAsyncImage(
                        model              = toDirectImageUrl(project.img),
                        contentDescription = project.name,
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier.fillMaxSize(),
                        loading = { Box(Modifier.fillMaxSize().shimmer()) },
                        error = {
                            Box(
                                modifier         = Modifier.fillMaxSize().background(
                                    Brush.linearGradient(listOf(accentColor.copy(0.08f), IuColors.surface2))
                                ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(project.id, style = IuType.sectionTitle.copy(color = accentColor.copy(0.18f), fontSize = 64.sp), fontWeight = FontWeight.Black)
                            }
                        }
                    )
                    // Soft overlay bottom fade
                    Box(modifier = Modifier.fillMaxSize().background(
                        Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent, Color(0x55030303)))
                    ))
                    // Press overlay
                    Box(modifier = Modifier.fillMaxSize().graphicsLayer { alpha = overlayAlpha }
                        .background(Brush.linearGradient(listOf(accentColor.copy(0.15f), Color.Transparent))))
                    // Category badge
                    Box(
                        modifier = Modifier.align(Alignment.TopStart).padding(12.dp)
                            .shadow(4.dp, RoundedCornerShape(50), ambientColor = accentColor.copy(0.3f))
                            .clip(RoundedCornerShape(50))
                            .background(accentColor)
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Text(project.cat.uppercase(), style = IuType.body.copy(color = Color.White, fontSize = 9.sp, letterSpacing = 1.5.sp), fontWeight = FontWeight.Bold)
                    }
                    // Year badge
                    Box(
                        modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
                            .clip(RoundedCornerShape(50))
                            .background(IuColors.surface.copy(0.85f))
                            .border(1.dp, IuColors.border, RoundedCornerShape(50))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(project.year, style = IuType.body.copy(color = IuColors.textMuted, fontSize = 9.sp))
                    }
                }
                // Info section
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                        Text(project.name, style = IuType.cardTitle.copy(fontSize = 22.sp), color = IuColors.text,
                            fontWeight = FontWeight.Black, maxLines = 2, overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f).padding(end = 8.dp))
                        Spacer(modifier = Modifier.padding(top = 8.dp).width(lineWidth).height(2.dp).background(lineColor, RoundedCornerShape(2.dp)))
                    }
                    if (!showDeleteConfirm) {
                        Spacer(Modifier.height(8.dp))
                        if (project.tags.isNotEmpty()) {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(project.tags.take(4)) { tag -> GhostTag(tag) }
                            }
                            Spacer(Modifier.height(10.dp))
                        }
                        if (project.desc.isNotEmpty()) {
                            Text(project.desc, style = IuType.body, color = IuColors.textMuted, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Edit, "Edit", tint = IuColors.textFaint, modifier = Modifier.size(15.dp))
                            }
                            IconButton(onClick = { haptic.performHapticFeedback(HapticFeedbackType.LongPress); showDeleteConfirm = true }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Delete, "Delete", tint = IuColors.pink, modifier = Modifier.size(15.dp))
                            }
                        }
                    }
                    if (showDeleteConfirm) {
                        Column(modifier = Modifier.graphicsLayer { alpha = deleteAlpha }) {
                            Spacer(Modifier.height(12.dp)); IuDivider(); Spacer(Modifier.height(10.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Remove \"${project.name}\"?", style = IuType.body.copy(color = IuColors.pink, fontSize = 12.sp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TextButton(onClick = { showDeleteConfirm = false }) {
                                        Text("Cancel", style = IuType.body.copy(color = IuColors.textFaint, fontSize = 9.sp, letterSpacing = 1.5.sp), fontWeight = FontWeight.Bold)
                                    }
                                    TextButton(onClick = { haptic.performHapticFeedback(HapticFeedbackType.LongPress); onDelete() }) {
                                        Text("Delete", style = IuType.body.copy(color = IuColors.pink, fontSize = 9.sp, letterSpacing = 1.5.sp), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProjectCardShimmer() {
    Column(modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(18.dp)).clip(RoundedCornerShape(18.dp)).background(IuColors.surface)) {
        Spacer(Modifier.fillMaxWidth().height(2.5.dp).shimmer())
        Box(Modifier.fillMaxWidth().height(190.dp).shimmer())
        Column(modifier = Modifier.padding(16.dp)) {
            Spacer(Modifier.fillMaxWidth(0.55f).height(22.dp).clip(RoundedCornerShape(4.dp)).shimmer())
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(3) { Spacer(Modifier.width(64.dp).height(14.dp).clip(RoundedCornerShape(50)).shimmer()) }
            }
            Spacer(Modifier.height(10.dp))
            Spacer(Modifier.fillMaxWidth().height(13.dp).clip(RoundedCornerShape(4.dp)).shimmer())
            Spacer(Modifier.height(6.dp))
            Spacer(Modifier.fillMaxWidth(0.75f).height(13.dp).clip(RoundedCornerShape(4.dp)).shimmer())
            Spacer(Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProjectSheet(
    nextOrder: Int, editingProject: Project? = null,
    onDismiss: () -> Unit, onAdd: (Project) -> Unit, onUpdate: (Project) -> Unit = {}
) {
    val isEdit = editingProject != null
    var name   by remember { mutableStateOf(editingProject?.name   ?: "") }
    var cat    by remember { mutableStateOf(editingProject?.cat    ?: "App") }
    var year   by remember { mutableStateOf(editingProject?.year   ?: "2025") }
    var accent by remember { mutableStateOf(editingProject?.accent ?: "#e85533") }
    var img    by remember { mutableStateOf(editingProject?.img    ?: "") }
    var desc   by remember { mutableStateOf(editingProject?.desc   ?: "") }
    var tags   by remember { mutableStateOf(editingProject?.tags?.joinToString(", ") ?: "") }
    val cats   = listOf("App", "Web", "Data", "Game")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor   = IuColors.surface,
        dragHandle = {
            Box(Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                Spacer(Modifier.width(40.dp).height(3.dp).clip(RoundedCornerShape(50)).background(IuColors.borderMid))
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp).verticalScroll(rememberScrollState())
        ) {
            SectionLabel(if (isEdit) "Edit Entry" else "New Entry")
            Spacer(Modifier.height(4.dp))
            Text(if (isEdit) "EDIT PROJECT" else "ADD PROJECT", fontSize = 30.sp, fontWeight = FontWeight.Black, color = IuColors.text, letterSpacing = (-1).sp)
            Spacer(Modifier.height(20.dp))
            Text("CATEGORY", style = IuType.label.copy(color = IuColors.textFaint))
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(cats) { c -> FilterPill(c, cat == c) { cat = c } }
            }
            Spacer(Modifier.height(16.dp))
            SheetField(name,   "Project Name")           { name = it }
            SheetField(year,   "Year")                   { year = it }
            SheetField(accent, "Accent Color (#hex)")    { accent = it }
            SheetField(img,    "Image URL (or Google Drive share link)") { img = it }
            if (img.contains("drive.google.com")) {
                Text("✓ Google Drive link detected — will auto-convert", style = IuType.caption.copy(color = IuColors.teal, fontSize = 10.sp), modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
            }
            SheetField(tags,   "Tags (comma-separated)") { tags = it }
            SheetField(desc,   "Description")            { desc = it }
            Spacer(Modifier.height(20.dp)); IuDivider(); Spacer(Modifier.height(16.dp))
            val enabled = name.isNotBlank()
            Box(
                modifier = Modifier.fillMaxWidth()
                    .shadow(if (enabled) 6.dp else 0.dp, RoundedCornerShape(12.dp), ambientColor = IuColors.orange.copy(0.3f))
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (enabled) Brush.linearGradient(IuColors.gradientOrange) else Brush.linearGradient(listOf(IuColors.surface2, IuColors.surface3)))
                    .clickable(enabled = enabled) {
                        if (isEdit) {
                            onUpdate(editingProject!!.copy(name = name.trim(), cat = cat, year = year.trim(), tags = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }, accent = accent.trim(), img = img.trim(), desc = desc.trim()))
                        } else {
                            onAdd(Project(docId = "", order = nextOrder, id = nextOrder.toString().padStart(2, '0'), name = name.trim(), cat = cat, year = year.trim(), tags = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }, accent = accent.trim(), img = img.trim(), desc = desc.trim()))
                        }
                    }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(if (isEdit) "SAVE CHANGES →" else "ADD TO PORTFOLIO →",
                    style = IuType.body.copy(color = if (enabled) Color.White else IuColors.textFaint, letterSpacing = 2.sp, fontSize = 9.sp),
                    fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun SheetField(value: String, label: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value         = value, onValueChange = onValueChange,
        label         = { Text(label, style = IuType.body.copy(color = IuColors.textFaint, fontSize = 10.sp)) },
        modifier      = Modifier.fillMaxWidth(),
        textStyle     = IuType.body.copy(color = IuColors.text),
        singleLine    = label != "Description",
        colors        = OutlinedTextFieldDefaults.colors(
            focusedBorderColor      = IuColors.orange, unfocusedBorderColor = IuColors.border,
            cursorColor             = IuColors.orange, focusedLabelColor    = IuColors.orange,
            unfocusedLabelColor     = IuColors.textFaint,
            focusedContainerColor   = IuColors.surface, unfocusedContainerColor = IuColors.surface
        )
    )
    Spacer(Modifier.height(12.dp))
}
