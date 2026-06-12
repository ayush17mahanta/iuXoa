package com.iuxoa.iu.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.iuxoa.iu.ui.screens.*
import com.iuxoa.iu.viewmodel.MainViewModel

sealed class Screen(val route: String) {
    object Splash      : Screen("splash")
    object Dashboard   : Screen("dashboard")
    object Projects    : Screen("projects")
    object Research    : Screen("research")
    object Guestbook   : Screen("guestbook")
    object BucketList  : Screen("bucket_list")
    object Messages    : Screen("messages")
    object Settings    : Screen("settings")
}

// Custom easing — matching website's [0.76, 0, 0.24, 1]
private val portfolioEasing = CubicBezierEasing(0.76f, 0f, 0.24f, 1f)
private val softEasing      = CubicBezierEasing(0.22f, 1f, 0.36f, 1f)

@Composable
fun IuNavGraph(navController: NavHostController) {
    val vm: MainViewModel = viewModel()

    NavHost(
        navController    = navController,
        startDestination = Screen.Splash.route,
        enterTransition  = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec  = tween(420, easing = softEasing)
            ) + fadeIn(tween(280))
        },
        exitTransition   = {
            slideOutHorizontally(
                targetOffsetX = { -it / 3 },
                animationSpec = tween(420, easing = softEasing)
            ) + fadeOut(tween(200))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it / 3 },
                animationSpec  = tween(420, easing = softEasing)
            ) + fadeIn(tween(280))
        },
        popExitTransition  = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(420, easing = softEasing)
            ) + fadeOut(tween(200))
        }
    ) {
        // Splash — no transitions
        composable(
            route          = Screen.Splash.route,
            enterTransition = { EnterTransition.None },
            exitTransition  = { ExitTransition.None }
        ) {
            SplashScreen(
                onComplete = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route)  { DashboardScreen(navController, vm) }
        composable(Screen.Projects.route)   { ProjectsScreen(navController, vm) }
        composable(Screen.Research.route)   { ResearchScreen(navController, vm) }
        composable(Screen.Guestbook.route)  { GuestbookScreen(navController, vm) }
        composable(Screen.BucketList.route) { BucketListScreen(navController, vm) }
        composable(Screen.Messages.route)   { MessagesScreen(navController, vm) }
        composable(Screen.Settings.route)   { SettingsScreen(navController, vm) }
    }
}
