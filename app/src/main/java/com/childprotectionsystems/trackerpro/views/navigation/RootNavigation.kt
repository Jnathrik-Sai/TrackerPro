package com.childprotectionsystems.trackerpro.views.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.childprotectionsystems.trackerpro.views.Authentication.LoginScreen
import com.childprotectionsystems.trackerpro.views.Authentication.SignupScreen

@Composable
fun RootNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignupScreen(navController) }
        composable("parent_dashboard") { ParentAppNavigation() }
        composable("child_dashboard") { ChildAppNavigation() }
    }
}