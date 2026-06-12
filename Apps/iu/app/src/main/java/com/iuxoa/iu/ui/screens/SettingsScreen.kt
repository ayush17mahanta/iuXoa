package com.iuxoa.iu.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.scale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.iuxoa.iu.data.HeroStat
import com.iuxoa.iu.ui.components.*
import com.iuxoa.iu.ui.theme.IuColors
import com.iuxoa.iu.ui.theme.IuType
import com.iuxoa.iu.viewmodel.MainViewModel

@Composable
fun SettingsScreen(navController: NavController, vm: MainViewModel) {
    val settings     by vm.settings.collectAsStateWithLifecycle()
    val unreadCount  by vm.unreadCount.collectAsStateWithLifecycle()
    val projects     by vm.projects.collectAsStateWithLifecycle()
    val patents      by vm.patents.collectAsStateWithLifecycle()
    val papers       by vm.papers.collectAsStateWithLifecycle()
    val bookChapters by vm.bookChapters.collectAsStateWithLifecycle()

    var editableStats by remember(settings.heroStats) {
        mutableStateOf(
            settings.heroStats.ifEmpty {
                listOf(
                    HeroStat("${projects.size}+", "Projects Delivered"),
                    HeroStat("8+",               "Data Analytics"),
                    HeroStat("3+",               "Startups Founded"),
                    HeroStat("${patents.size + papers.size + bookChapters.size}+", "Research & Patents")
                )
            }.toMutableList()
        )
    }

    var saving   by remember { mutableStateOf(false) }
    var saved    by remember { mutableStateOf(false) }
    var hasEdits by remember { mutableStateOf(false) }

    LaunchedEffect(saved) {
        if (saved) { kotlinx.coroutines.delay(3000); saved = false }
    }

    IuScaffold(navController = navController, unreadCount = unreadCount) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(IuColors.background).padding(padding)
        ) {

            item {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(Color(0xFF080808), IuColors.background)))
                        .padding(horizontal = 20.dp, vertical = 28.dp)
                ) {
                    Column {
                        SectionLabel("Configuration")
                        Spacer(Modifier.height(4.dp))
                        Text("SETTINGS", fontSize = 52.sp, fontWeight = FontWeight.Black, color = IuColors.white, letterSpacing = (-1.5).sp, lineHeight = 48.sp)
                        Spacer(Modifier.height(6.dp))
                        Text("Manage hero stats & portfolio display.", style = IuType.body, color = IuColors.textMuted)
                        Spacer(Modifier.height(16.dp))
                        OrangeDivider()
                    }
                }
            }

            item {
                MaintenanceToggleCard()
                IuDivider()
            }

            item {
                Spacer(Modifier.height(8.dp))
                Text("LIVE DATABASE COUNTS", style = IuType.label.copy(color = IuColors.textFaint, letterSpacing = 2.sp), modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
                Row(modifier = Modifier.fillMaxWidth().border(BorderStroke(1.dp, IuColors.border))) {
                    listOf(projects.size.toString() to "Works", patents.size.toString() to "Patents", papers.size.toString() to "Papers", bookChapters.size.toString() to "Chapters").forEachIndexed { i, (value, label) ->
                        val colors = listOf(IuColors.orange, IuColors.pink, IuColors.blue, IuColors.purple)
                        StatCard(value = value, label = label, color = colors[i], modifier = Modifier.weight(1f).then(if (i < 3) Modifier.border(BorderStroke(0.5.dp, IuColors.border)) else Modifier))
                    }
                }
                IuDivider()
            }

            item {
                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("HERO STATS", style = IuType.label.copy(color = IuColors.textFaint, letterSpacing = 2.sp))
                        Text("Shown on your portfolio website homepage", style = IuType.caption.copy(color = IuColors.textDim))
                    }
                    val btnColor by animateColorAsState(targetValue = when { saved -> IuColors.teal; saving -> IuColors.gold; hasEdits -> IuColors.orange; else -> IuColors.textFaint }, animationSpec = tween(350), label = "save_color")
                    val btnScale by animateFloatAsState(targetValue = if (saving) 0.96f else 1f, animationSpec = spring(stiffness = Spring.StiffnessMediumLow), label = "save_scale")
                    Box(
                        modifier = Modifier.scale(btnScale).clip(RoundedCornerShape(50)).background(btnColor.copy(0.12f)).border(1.dp, btnColor.copy(0.4f), RoundedCornerShape(50))
                            .clickable(enabled = !saving && hasEdits) { saving = true; saved = false; hasEdits = false; vm.updateHeroStats(editableStats) { saving = false; saved = true } }
                            .padding(horizontal = 16.dp, vertical = 9.dp)
                    ) {
                        AnimatedContent(targetState = when { saving -> "saving"; saved -> "saved"; else -> "idle" }, transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(150)) }, label = "save_label") { state ->
                            Text(text = when (state) { "saving" -> "Saving…"; "saved" -> "✓ Saved!"; else -> if (hasEdits) "↑ Sync" else "No changes" }, style = IuType.tagStyle.copy(color = btnColor, letterSpacing = 1.sp, fontSize = 10.sp), fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            itemsIndexed(editableStats) { i, stat ->
                StatEditorRow(stat = stat, index = i, onChange = { updated -> editableStats = editableStats.toMutableList().also { it[i] = updated }; hasEdits = true; saved = false })
            }

            item { IuDivider(Modifier.padding(vertical = 8.dp)) }

            item {
                Spacer(Modifier.height(8.dp))
                Text("ABOUT APP", style = IuType.label.copy(color = IuColors.textFaint, letterSpacing = 2.sp), modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
                InfoRow(icon = Icons.Default.Apps,   label = "App Name",  value = "iuXoa Portfolio Admin")
                InfoRow(icon = Icons.Default.Code,   label = "Version",   value = "1.0.0")
                InfoRow(icon = Icons.Default.Cloud,  label = "Firebase",  value = "portfolio-iu")
                InfoRow(icon = Icons.Default.Person, label = "Developer", value = "Ayush Mahanta")
                InfoRow(icon = Icons.Default.Email,  label = "Email",     value = "ayush17mahanta@gmail.com")
                IuDivider()
            }

            item {
                Spacer(Modifier.height(8.dp))
                Text("PORTFOLIO LINKS", style = IuType.label.copy(color = IuColors.textFaint, letterSpacing = 2.sp), modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
                LinkRow(icon = Icons.Default.Language, label = "Website",  value = "ayushmahanta.in")
                LinkRow(icon = Icons.Default.Code,     label = "GitHub",   value = "github.com/ayush17mahanta")
                LinkRow(icon = Icons.Default.Work,     label = "LinkedIn", value = "linkedin.com/in/ayush17mahanta")
                IuDivider()
            }

            item {
                Spacer(Modifier.height(8.dp))
                Text("ADMIN ACCESS", style = IuType.label.copy(color = IuColors.textFaint, letterSpacing = 2.sp), modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(IuColors.teal.copy(0.08f))
                        .border(1.dp, IuColors.teal.copy(0.3f), RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("🛡️", fontSize = 24.sp)
                    Column {
                        Text("Personal Admin App", style = IuType.body.copy(color = IuColors.teal), fontWeight = FontWeight.Bold)
                        Text("No login required — full access to all operations.", style = IuType.caption.copy(color = IuColors.textFaint))
                    }
                }
                IuDivider()
                Spacer(Modifier.height(36.dp))
            }
        }
    }
}

@Composable
fun StatEditorRow(stat: HeroStat, index: Int, onChange: (HeroStat) -> Unit) {
    val colors = listOf(IuColors.orange, IuColors.pink, IuColors.blue, IuColors.purple)
    val accent = colors[index % colors.size]
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
            Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(accent.copy(0.12f)).border(1.dp, accent.copy(0.3f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Text("${index + 1}", style = IuType.caption.copy(color = accent), fontWeight = FontWeight.Black)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = stat.value, onValueChange = { onChange(stat.copy(value = it)) }, label = { Text("Value (e.g. 20+)", style = IuType.caption.copy(color = IuColors.textFaint)) }, singleLine = true, modifier = Modifier.fillMaxWidth(), textStyle = IuType.cardTitle.copy(color = IuColors.white, fontSize = 16.sp), colors = settingsFieldColors(accent))
                OutlinedTextField(value = stat.label, onValueChange = { onChange(stat.copy(label = it)) }, label = { Text("Label", style = IuType.caption.copy(color = IuColors.textFaint)) }, singleLine = true, modifier = Modifier.fillMaxWidth(), textStyle = IuType.body.copy(color = IuColors.textMuted), colors = settingsFieldColors(accent))
            }
        }
        Spacer(Modifier.height(4.dp))
        IuDivider()
    }
}

@Composable
private fun settingsFieldColors(accent: Color) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = accent, unfocusedBorderColor = IuColors.border, cursorColor = accent,
    focusedLabelColor = accent, unfocusedLabelColor = IuColors.textFaint,
    focusedContainerColor = IuColors.surface2, unfocusedContainerColor = IuColors.surface2
)

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(icon, null, tint = IuColors.textFaint, modifier = Modifier.size(18.dp))
        Text(label, style = IuType.body.copy(color = IuColors.textMuted), modifier = Modifier.width(90.dp))
        Text(value, style = IuType.body.copy(color = IuColors.white), fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
    }
    IuDivider(Modifier.padding(horizontal = 20.dp))
}

@Composable
fun LinkRow(icon: ImageVector, label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(icon, null, tint = IuColors.orange, modifier = Modifier.size(18.dp))
        Text(label, style = IuType.body.copy(color = IuColors.textMuted), modifier = Modifier.width(80.dp))
        Text(value, style = IuType.tagStyle.copy(color = IuColors.orange, fontSize = 11.sp), modifier = Modifier.weight(1f))
        Icon(Icons.Default.OpenInNew, null, tint = IuColors.textFaint, modifier = Modifier.size(14.dp))
    }
    IuDivider(Modifier.padding(horizontal = 20.dp))
}

// ── MAINTENANCE TOGGLE CARD ─────────────────────────────────────────────────────
@Composable
fun MaintenanceToggleCard() {
    val db = FirebaseFirestore.getInstance()

    var active      by remember { mutableStateOf(false) }
    var cancelable  by remember { mutableStateOf(true) }
    var title       by remember { mutableStateOf("") }
    var message     by remember { mutableStateOf("") }
    var saving      by remember { mutableStateOf(false) }
    var saved       by remember { mutableStateOf(false) }
    var loaded      by remember { mutableStateOf(false) }

    // Load current state from Firestore once
    LaunchedEffect(Unit) {
        db.collection("settings").document("main")
            .get()
            .addOnSuccessListener { snap ->
                val m = snap.get("maintenance") as? Map<*, *>
                active     = m?.get("active")     as? Boolean ?: false
                cancelable = m?.get("cancelable") as? Boolean ?: true
                title      = m?.get("title")      as? String  ?: ""
                message    = m?.get("message")    as? String  ?: ""
                loaded     = true
            }
    }

    LaunchedEffect(saved) {
        if (saved) { kotlinx.coroutines.delay(2500); saved = false }
    }

    val modeColor by animateColorAsState(
        targetValue   = if (active) Color(0xFFF59E0B) else IuColors.teal,
        animationSpec = tween(350), label = "mc"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "WEBSITE MAINTENANCE",
            style    = IuType.label.copy(color = IuColors.textFaint, letterSpacing = 2.sp),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (active) Color(0xFFF59E0B).copy(0.06f) else IuColors.teal.copy(0.06f))
                .border(1.dp, modeColor.copy(0.3f), RoundedCornerShape(16.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Toggle row
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(if (active) "⚠️" else "✅", fontSize = 22.sp)
                    Column {
                        Text(
                            if (active) "Maintenance Active" else "Site Live",
                            style      = IuType.body.copy(color = modeColor),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            if (active) "Banner is showing on website" else "No banner shown",
                            style = IuType.caption.copy(color = IuColors.textFaint)
                        )
                    }
                }
                Switch(
                    checked         = active,
                    onCheckedChange = { active = it },
                    colors          = SwitchDefaults.colors(
                        checkedThumbColor       = Color.White,
                        checkedTrackColor       = Color(0xFFF59E0B),
                        uncheckedThumbColor     = Color.White,
                        uncheckedTrackColor     = IuColors.teal.copy(0.5f)
                    )
                )
            }

            // ── Title + Message always visible so you can pre-edit ─────
            OutlinedTextField(
                value         = title,
                onValueChange = { title = it },
                label         = { Text("Banner Title", style = IuType.caption.copy(color = IuColors.textFaint)) },
                placeholder   = { Text("e.g. Under Maintenance", style = IuType.caption.copy(color = IuColors.textDim)) },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth(),
                textStyle     = IuType.body.copy(color = IuColors.white),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = modeColor, unfocusedBorderColor = IuColors.border,
                    cursorColor = modeColor, focusedContainerColor = IuColors.surface2,
                    unfocusedContainerColor = IuColors.surface2
                )
            )
            OutlinedTextField(
                value         = message,
                onValueChange = { message = it },
                label         = { Text("Message shown to visitors", style = IuType.caption.copy(color = IuColors.textFaint)) },
                placeholder   = { Text("e.g. We're making improvements. You might experience glitches.", style = IuType.caption.copy(color = IuColors.textDim)) },
                minLines      = 3,
                modifier      = Modifier.fillMaxWidth(),
                textStyle     = IuType.body.copy(color = IuColors.white),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = modeColor, unfocusedBorderColor = IuColors.border,
                    cursorColor = modeColor, focusedContainerColor = IuColors.surface2,
                    unfocusedContainerColor = IuColors.surface2
                )
            )
            // Cancelable toggle — always visible
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("User can dismiss", style = IuType.body.copy(color = IuColors.textMuted), fontWeight = FontWeight.SemiBold)
                    Text("Show \"Got it\" button on banner", style = IuType.caption.copy(color = IuColors.textFaint))
                }
                Switch(
                    checked         = cancelable,
                    onCheckedChange = { cancelable = it },
                    colors          = SwitchDefaults.colors(
                        checkedThumbColor   = Color.White,
                        checkedTrackColor   = Color(0xFFF59E0B),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = IuColors.border
                    )
                )
            }

            // Save button
            val btnColor by animateColorAsState(
                targetValue   = when { saved -> IuColors.teal; saving -> Color(0xFFF59E0B); active -> Color(0xFFF59E0B); else -> IuColors.teal },
                animationSpec = tween(300), label = "mb"
            )
            val btnScale by animateFloatAsState(
                targetValue   = if (saving) 0.97f else 1f,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow), label = "mbs"
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(btnScale)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (saved) IuColors.teal.copy(0.15f) else if (active) Color(0xFFF59E0B).copy(0.15f) else IuColors.teal.copy(0.15f))
                    .border(1.dp, btnColor.copy(0.5f), RoundedCornerShape(12.dp))
                    .clickable(enabled = !saving && loaded) {
                        saving = true
                        val payload = mapOf(
                            "active"     to active,
                            "title"      to title.ifBlank { "Under Maintenance" },
                            "message"    to message.ifBlank { "We're making improvements. You might experience glitches." },
                            "cancelable" to cancelable
                        )
                        db.collection("settings").document("main")
                            .set(mapOf("maintenance" to payload), com.google.firebase.firestore.SetOptions.merge())
                            .addOnSuccessListener { saving = false; saved = true }
                            .addOnFailureListener { saving = false }
                    }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = when { saving -> "saving"; saved -> "saved"; else -> "idle" },
                    transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(150)) },
                    label = "mbl"
                ) { s ->
                    Text(
                        text       = when (s) {
                            "saving" -> "Pushing to website…"
                            "saved"  -> if (active) "⚠️ Banner is now LIVE" else "✅ Banner removed"
                            else     -> if (active) "📡 Push maintenance to website" else "📡 Push live status"
                        },
                        style      = IuType.tagStyle.copy(color = btnColor, letterSpacing = 1.5.sp, fontSize = 10.sp),
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
        Spacer(Modifier.height(4.dp))
    }
}
