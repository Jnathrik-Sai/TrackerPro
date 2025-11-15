package com.childprotectionsystems.trackerpro.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.childprotectionsystems.trackerpro.views.Authentication.LoginScreen
import com.childprotectionsystems.trackerpro.views.navigation.ParentAppNavigation
import com.childprotectionsystems.trackerpro.utils.getSavedRole

@Composable
fun NavGraph(shouldOpenNotifications: Boolean = false) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    // Defensively get the saved role
    val savedRole = remember { getSavedRole(context) ?: "" }

    // Default start destination
    val startDestination = when (savedRole.lowercase()) {
        "parent" -> "parent_dashboard"
        "child" -> "child_dashboard"
        else -> "login"
    }

    // Handle navigation to notifications screen when triggered by notification click
    LaunchedEffect(shouldOpenNotifications) {
        if (shouldOpenNotifications && savedRole.lowercase() == "parent") {
            // Small delay to ensure navigation is ready
            kotlinx.coroutines.delay(300)
            navController.navigate("notifications") {
                // Don't add to back stack if already on notifications
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") {
            LoginScreen(navController = navController)
        }

        composable("parent_dashboard") {
            ParentAppNavigation()
        }

        composable("child_dashboard") {
//            ChildDashboard(navController = navController)
        }

        composable("safe_zone") {
        }

        composable("add_child") {
        }

        composable("notifications") {
        }

        composable("emergency_contacts") {
        }
    }

    // Debug log to verify correct navigation start
    LaunchedEffect(Unit) {
        println("Starting NavGraph with destination: $startDestination")
        if (shouldOpenNotifications) {
            println("Notification click detected - will navigate to notifications")
        }
    }
}