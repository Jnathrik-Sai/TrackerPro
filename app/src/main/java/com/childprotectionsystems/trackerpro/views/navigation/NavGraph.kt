package com.childprotectionsystems.trackerpro.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.childprotectionsystems.trackerpro.views.Authentication.LoginScreen
import com.childprotectionsystems.trackerpro.views.Authentication.SignupScreen
import com.childprotectionsystems.trackerpro.views.navigation.ParentAppNavigation
import com.childprotectionsystems.trackerpro.utils.getSavedRole
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun NavGraph(shouldOpenNotifications: Boolean = false) {

    val navController = rememberNavController()
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    val savedRole = remember { getSavedRole(context) ?: "" }

    val startDestination = when (savedRole.lowercase()) {
        "parent" -> "parent_dashboard"
        "child" -> "child_dashboard"
        else -> "login"
    }

    LaunchedEffect(shouldOpenNotifications) {
        if (shouldOpenNotifications && savedRole.lowercase() == "parent") {
            delay(300)
            navController.navigate("notifications") {
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // LOGIN SCREEN
        composable("login") {
            LoginScreen(
                navController = navController,
            )
        }

        // SIGNUP SCREEN
        composable("signup") {
            SignupScreen(
                navController = navController,
            )
        }

        // PARENT DASHBOARD
        composable("parent_dashboard") {
            ParentAppNavigation()
        }

        // CHILD DASHBOARD
        composable("child_dashboard") {
            // TODO: Add your child dashboard
            // ChildDashboard(navController)
        }

        // SAFE ZONE
        composable("safe_zone") {
            // SafeZoneScreen(navController)
        }

        // ADD CHILD
        composable("add_child") {
            // AddChildScreen(navController)
        }

        // NOTIFICATIONS
        composable("notifications") {
            // NotificationsScreen(navController)
        }

        // EMERGENCY CONTACTS
        composable("emergency_contacts") {
            // EmergencyContactsScreen(navController)
        }
    }

    // Debug Logs
    LaunchedEffect(Unit) {
        println("Starting NavGraph with destination: $startDestination")
        if (shouldOpenNotifications) {
            println("Notification click detected - navigating to notifications")
        }
    }
}