package com.iuxoa.iu

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.iuxoa.iu.ui.theme.IuColors
import com.iuxoa.iu.ui.theme.IuTheme
import com.iuxoa.iu.ui.navigation.IuNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Sign in anonymously to bypass database write security rules (which require request.auth != null)
        FirebaseAuth.getInstance().signInAnonymously()
            .addOnSuccessListener {
                Log.d("Auth", "Successfully signed in anonymously to Firebase")
            }
            .addOnFailureListener { e ->
                Log.e("Auth", "Failed to sign in anonymously: ${e.message}")
            }

        // Make status bar and nav bar transparent (match website dark bg)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        setContent {
            IuTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color    = IuColors.background
                ) {
                    val navController = rememberNavController()
                    IuNavGraph(navController = navController)
                }
            }
        }
    }
}

