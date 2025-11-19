package com.childprotectionsystems.trackerpro.views.navigation

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.childprotectionsystems.trackerpro.model.BottomNavigationItem
import com.childprotectionsystems.trackerpro.ui.theme.*
import com.childprotectionsystems.trackerpro.views.Home.Child.GeofencesChild
import com.childprotectionsystems.trackerpro.views.Home.Child.InboxChild

@Composable
fun ChildAppNavigation() {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.statusBarColor = Color.Transparent.toArgb()
        }
    }
    val navController = rememberNavController()

    var inboxBadgeCount by remember { mutableStateOf(5) }

    val items = listOf(
        BottomNavigationItem(
            title = "Geofences",
            selectedIcon = Icons.Filled.Place,
            unselectedIcon = Icons.Outlined.Place,
            hasNews = false
        ),
        BottomNavigationItem(
            title = "Inbox",
            selectedIcon = Icons.Filled.Mail,
            unselectedIcon = Icons.Outlined.Mail,
            hasNews = inboxBadgeCount > 0,
            badgeCount = inboxBadgeCount
        ),
        BottomNavigationItem(
            title = "Settings",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            hasNews = false
        )
    )

    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(currentRoute) {
        items.indexOfFirst { it.title == currentRoute }.takeIf { it != -1 }?.let {
            selectedItemIndex = it
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0.dp),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = {
                            selectedItemIndex = index
                            navController.navigate(item.title) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        alwaysShowLabel = false,
                        icon = {
                            BadgedBox(
                                badge = {
                                    item.badgeCount?.takeIf { it > 0 }?.let { count ->
                                        Badge { Text(count.toString()) }
                                    } ?: run {
                                        if (item.hasNews) Badge()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (selectedItemIndex == index)
                                        item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = PrimaryColor,
                            selectedIconColor = DarkBrown,
                            unselectedIconColor = MediumBrown
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "Geofences",
            modifier = Modifier
                .padding(bottom = paddingValues.calculateBottomPadding())
                .fillMaxSize()
        ) {
            composable("Geofences") { GeofencesChild() }
            composable("Inbox") { InboxChild(
                onMessagesRead = { inboxBadgeCount = 0 }
            ) }
            composable("Settings") {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Settings Screen")
                }
            }
        }
    }
}