package com.iuxoa.iu.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.iuxoa.iu.ui.navigation.Screen
import com.iuxoa.iu.ui.theme.IuColors
import com.iuxoa.iu.ui.theme.IuType

data class NavItem(
    val screen: Screen,
    val icon:   ImageVector,
    val label:  String,
    val badge:  Int = 0
)

// ─────────────────────────────────────────────────────────────────────────────
// BOTTOM NAVIGATION BAR — light neumorphic
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun IuBottomBar(navController: NavController, unreadCount: Int = 0) {
    val items = listOf(
        NavItem(Screen.Dashboard,  Icons.Default.Home,        "Home"),
        NavItem(Screen.Projects,   Icons.Default.GridView,    "Works"),
        NavItem(Screen.Research,   Icons.Default.Science,     "Research"),
        NavItem(Screen.Guestbook,  Icons.Default.Chat,        "Guests"),
        NavItem(Screen.BucketList, Icons.Default.CheckCircle, "Bucket"),
        NavItem(Screen.Messages,   Icons.Default.Mail,        "Inbox", unreadCount),
    )
    val current = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar(
        containerColor = Color(0xCC0C0C0C),  // frosted glass — aurora shows through
        tonalElevation = 0.dp,
        modifier = Modifier
            .shadow(
                elevation    = 16.dp,
                ambientColor = IuColors.neuShadow.copy(0.3f),
                spotColor    = IuColors.neuShadow.copy(0.4f)
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(Color(0x15FFFFFF), Color.Transparent),
                    start = Offset(0f, 0f), end = Offset(0f, 4f)
                ),
                shape = RoundedCornerShape(0.dp)
            )
    ) {
        items.forEach { item ->
            val selected = current == item.screen.route

            val iconScale by animateFloatAsState(
                targetValue   = if (selected) 1.18f else 1f,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label         = "icon_scale"
            )

            NavigationBarItem(
                selected = selected,
                onClick  = {
                    navController.navigate(item.screen.route) {
                        popUpTo(Screen.Dashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState    = true
                    }
                },
                icon = {
                    Box {
                        Icon(
                            imageVector        = item.icon,
                            contentDescription = item.label,
                            modifier           = Modifier.size(22.dp).scale(iconScale)
                        )
                        if (item.badge > 0) {
                            NotificationBadge(
                                count    = item.badge,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 6.dp, y = (-4).dp)
                            )
                        }
                    }
                },
                label = {
                    AnimatedContent(
                        targetState    = selected,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label          = "nav_label"
                    ) { sel ->
                        Text(
                            text       = item.label,
                            style      = IuType.caption.copy(fontSize = 9.sp),
                            fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = IuColors.orange,
                    selectedTextColor   = IuColors.orange,
                    unselectedIconColor = IuColors.textFaint,
                    unselectedTextColor = IuColors.textFaint,
                    indicatorColor      = IuColors.orange.copy(alpha = 0.12f)
                )
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SCREEN SCAFFOLD — aurora background applied here
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun IuScaffold(
    navController: NavController,
    unreadCount:   Int = 0,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        containerColor = Color.Transparent,
        bottomBar      = { IuBottomBar(navController, unreadCount) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .auroraBackground()
        ) {
            content(padding)
        }
    }
}
