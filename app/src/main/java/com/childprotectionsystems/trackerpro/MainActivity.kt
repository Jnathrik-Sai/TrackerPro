package com.childprotectionsystems.trackerpro
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.childprotectionsystems.trackerpro.model.BottomNavigationItem
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import com.childprotectionsystems.trackerpro.ui.theme.TrackerProTheme
import com.childprotectionsystems.trackerpro.views.Home.HomeFragment
import com.childprotectionsystems.trackerpro.views.Geofences.Geofences
import com.childprotectionsystems.trackerpro.views.settings.SettingsView
import com.childprotectionsystems.trackerpro.views.Alerts.AlertsView
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.layout.*
import androidx.navigation.compose.currentBackStackEntryAsState

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = true
        windowInsetsController.isAppearanceLightNavigationBars = true

        setContent {
            TrackerProTheme {
                val navController = rememberNavController()
                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val drawerItems = listOf(
                    Pair("TrackerChild", Icons.Filled.Info),
                    Pair("Call History", Icons.Filled.Call),
                    Pair("Settings", Icons.Filled.Settings),
                    Pair("Help and Feedback", Icons.Filled.Help)
                )

                val items = listOf(
                    BottomNavigationItem(
                        title = "Home",
                        selectedIcon = Icons.Filled.Home,
                        unselectedIcon = Icons.Outlined.Home,
                        hasNews = false,
                    ),
                    BottomNavigationItem(
                        title = "Geofences",
                        selectedIcon = Icons.Filled.Place,
                        unselectedIcon = Icons.Outlined.Place,
                        hasNews = false,
                        badgeCount = 45
                    ),
                    BottomNavigationItem(
                        title = "Alerts",
                        selectedIcon = Icons.Filled.Notifications,
                        unselectedIcon = Icons.Outlined.Notifications,
                        hasNews = true,
                    ),
                    BottomNavigationItem(
                        title = "Settings",
                        selectedIcon = Icons.Filled.Settings,
                        unselectedIcon = Icons.Outlined.Settings,
                        hasNews = true,
                    ),
                )

                var selectedItemIndex by rememberSaveable { mutableStateOf(0) }

                LaunchedEffect(currentRoute) {
                    items.indexOfFirst { it.title == currentRoute }.takeIf { it != -1 }?.let {
                        selectedItemIndex = it
                    }
                }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    gesturesEnabled = currentRoute == "Home",
                    drawerContent = {
                        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
                        val drawerWidth = screenWidth * 0.7f

                        ModalDrawerSheet(
                            modifier = Modifier.width(drawerWidth)
                        ) {
                            Column(modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars)) {
                                Text(
                                    text = "TrackerChild",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(16.dp)
                                )
                                drawerItems.drop(1).forEach { item ->
                                    NavigationDrawerItem(
                                        label = { Text(item.first) },
                                        selected = false,
                                        onClick = { /* Handle drawer clicks */ },
                                        icon = {
                                            Icon(
                                                imageVector = item.second,
                                                contentDescription = item.first,
                                                tint = Color.Black
                                            )
                                        },
                                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                    )
                                }
                            }
                        }
                    }
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
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
                                                    when {
                                                        item.badgeCount != null -> Badge { Text(item.badgeCount.toString()) }
                                                        item.hasNews -> Badge()
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
                                            indicatorColor = Color(0xFFFFFAF0), // light skin tone for selected tab circle
                                            selectedIconColor = Color(0xFF3E2723), // dark brown shade for selected icon
                                            unselectedIconColor = Color(0xFF5D4037) // medium dark brown for unselected icons
                                        )
                                    )
                                }
                            }
                        }
                    ) { paddingValues ->
                        NavHost(
                            navController = navController,
                            startDestination = "Home",
                            modifier = Modifier
                                .padding(bottom = paddingValues.calculateBottomPadding())
                                .fillMaxSize()
                        ) {
                            composable("Home") { HomeFragment(openDrawer = { scope.launch { drawerState.open() } }) }
                            composable("Geofences") { Geofences() }
                            composable("Alerts") { AlertsView() }
                            composable("Settings") { SettingsView() }
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TrackerProTheme {
        HomeFragment()
    }
}
