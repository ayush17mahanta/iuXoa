package com.iuxoa.iu.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
// Switch and SwitchDefaults used via fully-qualified androidx.compose.material3 reference below
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.iuxoa.iu.data.HeroStat
import com.iuxoa.iu.ui.components.*
import com.iuxoa.iu.ui.navigation.Screen
import com.iuxoa.iu.ui.theme.IuColors
import com.iuxoa.iu.ui.theme.IuType
import com.iuxoa.iu.viewmodel.MainViewModel

@Composable
fun DashboardScreen(navController: NavController, vm: MainViewModel) {
    val projects     by vm.projects.collectAsStateWithLifecycle()
    val patents      by vm.patents.collectAsStateWithLifecycle()
    val papers       by vm.papers.collectAsStateWithLifecycle()
    val bookChapters by vm.bookChapters.collectAsStateWithLifecycle()
    val unreadCount  by vm.unreadCount.collectAsStateWithLifecycle()
    val settings     by vm.settings.collectAsStateWithLifecycle()
    val pendingGuests = 0

    val heroVisible    = remember { Animatable(0f) }
    val contentVisible = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        heroVisible.animateTo(1f, tween(700, easing = FastOutSlowInEasing))
        contentVisible.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
    }

    IuScaffold(navController = navController, unreadCount = unreadCount) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding(), bottom = padding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .graphicsLayer { alpha = heroVisible.value; translationY = (1f - heroVisible.value) * 40f }
                    .background(Brush.verticalGradient(listOf(IuColors.auroraBlue.copy(0.25f), IuColors.auroraViolet.copy(0.15f), Color.Transparent)))
                    .padding(start = 24.dp, end = 24.dp, top = 36.dp, bottom = 28.dp)
            ) {
                Column {
                    SectionLabel("Portfolio Admin")
                    Spacer(Modifier.height(12.dp))
                    Text("AYU", fontSize = 72.sp, fontWeight = FontWeight.Black, color = IuColors.text, letterSpacing = (-2).sp, lineHeight = 68.sp)
                    Spacer(Modifier.height(2.dp))
                    Text("Dashboard", fontSize = 26.sp, fontWeight = FontWeight.Light, color = IuColors.textMuted, letterSpacing = (-0.5).sp)
                    Spacer(Modifier.height(20.dp))
                    AnimatedOrangeDivider(delayMs = 300)
                }
            }

            if (unreadCount > 0) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AlertChip(text = "$unreadCount new message${if (unreadCount > 1) "s" else ""}", onClick = { navController.navigate(Screen.Messages.route) }, modifier = Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(8.dp))

            val heroStats = settings.heroStats.ifEmpty {
                listOf(
                    HeroStat("${projects.size}+", "Works"),
                    HeroStat("${patents.size + papers.size + bookChapters.size}+", "Research"),
                    HeroStat("3+", "Startups"),
                    HeroStat("${patents.size + papers.size + bookChapters.size}+", "Pubs")
                )
            }
            val statColors = listOf(IuColors.orange, IuColors.pink, IuColors.blue, IuColors.purple)

            Column(
                modifier = Modifier.padding(horizontal = 16.dp).graphicsLayer { alpha = contentVisible.value; translationY = (1f - contentVisible.value) * 20f },
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                heroStats.take(4).chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row.forEach { stat ->
                            NeuStatCell(value = stat.value, label = stat.label, color = statColors[heroStats.indexOf(stat) % statColors.size], modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            IuDivider()
            Spacer(Modifier.height(20.dp))
            Text("QUICK ACTIONS", style = IuType.label.copy(color = IuColors.textFaint, letterSpacing = 2.sp), modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(Modifier.height(10.dp))

            val actions = listOf(
                Triple("Works",    Screen.Projects.route,   IuColors.orange),
                Triple("Research", Screen.Research.route,   IuColors.pink),
                Triple("Guests",   Screen.Guestbook.route,  IuColors.blue),
                Triple("Bucket",   Screen.BucketList.route, IuColors.purple),
                Triple("Inbox",    Screen.Messages.route,   IuColors.gold),
                Triple("Settings", Screen.Settings.route,   IuColors.teal),
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 4.dp)) {
                items(actions) { (label, route, color) ->
                    QuickActionChip(label = label, color = color) { navController.navigate(route) }
                }
            }

            Spacer(Modifier.height(32.dp))
            DashboardMaintenanceCard(navController)
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun AlertChip(text: String, modifier: Modifier = Modifier, color: Color = IuColors.orange, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, spring(), label = "chip")
    Box(
        modifier = modifier.scale(scale)
            .shadow(4.dp, RoundedCornerShape(14.dp), ambientColor = color.copy(0.25f), spotColor = color.copy(0.3f))
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.linearGradient(listOf(IuColors.neuHigh, IuColors.surface), start = Offset(0f, 0f), end = Offset(300f, 100f)))
            .border(1.dp, Brush.linearGradient(listOf(color.copy(0.4f), color.copy(0.1f)), start = Offset(0f, 0f), end = Offset(300f, 100f)), RoundedCornerShape(14.dp))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("⚡ $text", style = IuType.body.copy(color = color, fontSize = 12.sp), fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun QuickActionChip(label: String, color: Color, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.93f else 1f, spring(), label = "qa")
    Box(
        modifier = Modifier.scale(scale)
            .shadow(4.dp, RoundedCornerShape(50), ambientColor = color.copy(0.2f), spotColor = color.copy(0.25f))
            .clip(RoundedCornerShape(50))
            .background(Brush.linearGradient(listOf(IuColors.neuHigh, IuColors.surface2), start = Offset(0f, 0f), end = Offset(200f, 80f)))
            .border(1.dp, Brush.linearGradient(listOf(IuColors.neuHigh, color.copy(0.2f), IuColors.neuShadow.copy(0.3f)), start = Offset(0f, 0f), end = Offset(200f, 80f)), RoundedCornerShape(50))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 11.dp)
    ) {
        Text(label.uppercase(), style = IuType.tagStyle.copy(color = color, letterSpacing = 1.sp))
    }
}

@Composable
fun DashboardMaintenanceCard(navController: NavController) {
    val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
    var active by remember { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }
    var loaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        db.collection("settings").document("main").get().addOnSuccessListener { snap ->
            val m = snap.get("maintenance") as? Map<*, *>
            active = m?.get("active") as? Boolean ?: false
            loaded = true
        }
    }

    val modeColor by animateColorAsState(if (active) Color(0xFFF59E0B) else IuColors.teal, tween(350), label = "mc")

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text("WEBSITE STATUS", style = IuType.label.copy(color = IuColors.textFaint, letterSpacing = 2.sp))
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(if (active) Color(0xFFF59E0B).copy(0.07f) else IuColors.teal.copy(0.07f))
                .border(1.dp, modeColor.copy(0.3f), RoundedCornerShape(16.dp))
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(if (active) "⚠️" else "✅", fontSize = 22.sp)
                Column {
                    Text(if (active) "Maintenance ON" else "Site Live", style = IuType.body.copy(color = modeColor), fontWeight = FontWeight.Bold)
                    Text("Tap to toggle · Full control in Settings", style = IuType.caption.copy(color = IuColors.textFaint))
                }
            }
            androidx.compose.material3.Switch(
                checked = active,
                onCheckedChange = { newVal: Boolean ->
                    if (!loaded || saving) return@Switch
                    saving = true; active = newVal
                    val payload = mapOf("active" to newVal, "title" to "Under Maintenance", "message" to "We're making improvements.", "cancelable" to true)
                    db.collection("settings").document("main")
                        .set(mapOf("maintenance" to payload), com.google.firebase.firestore.SetOptions.merge())
                        .addOnSuccessListener { saving = false }
                        .addOnFailureListener { saving = false; active = !newVal }
                },
                colors = androidx.compose.material3.SwitchDefaults.colors(
                    checkedThumbColor = Color.White, checkedTrackColor = Color(0xFFF59E0B),
                    uncheckedThumbColor = Color.White, uncheckedTrackColor = IuColors.teal.copy(0.5f)
                )
            )
        }
        Spacer(Modifier.height(8.dp))
        Text("For custom message → Settings", style = IuType.caption.copy(color = IuColors.textFaint),
            modifier = Modifier.clickable { navController.navigate(Screen.Settings.route) })
    }
}
