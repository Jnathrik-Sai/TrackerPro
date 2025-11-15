package com.childprotectionsystems.trackerpro

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.childprotectionsystems.trackerpro.views.navigation.ParentAppNavigation
import com.google.firebase.FirebaseApp
import com.childprotectionsystems.trackerpro.navigation.NavGraph
@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val shouldOpenNotifications: MutableState<Boolean> = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        Log.d(TAG, "onCreate called")

//        // 🆕 Initialize notification channels
//        NotificationChannelSetup.createNotificationChannels(this)
//
//        // 🆕 START APP BLOCKING SERVICE
//        startAppBlockingService()
//
//        // Handle notification click
//        handleNotificationIntent(intent)
//
//        // Handle SMS deep link
//        handleSmsDeepLink(intent)

        setContent {
            NavGraph(shouldOpenNotifications = shouldOpenNotifications.value)
        }
    }
}
