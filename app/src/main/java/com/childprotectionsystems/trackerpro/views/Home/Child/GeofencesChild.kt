package com.childprotectionsystems.trackerpro.views.Home.Child

import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.*
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.location.LocationRequest

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun GeofencesChild() {

    val context = LocalContext.current

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var childLocation by remember { mutableStateOf<LatLng?>(null) }

    val cameraPositionState = rememberCameraPositionState()

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val request = LocationRequest.create().apply {
            interval = 2000L
            fastestInterval = 1000L
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation ?: return
                val newLatLng = LatLng(loc.latitude, loc.longitude)

                childLocation = newLatLng

                scope.launch {
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLngZoom(newLatLng, 17f),
                        durationMs = 600
                    )
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(request, callback, context.mainLooper)
    }

    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search places…") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            singleLine = true
        )

        if (childLocation != null) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                Marker(
                    state = MarkerState(position = childLocation!!),
                    title = "Your Location",
                    snippet = "Live GPS position"
                )
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}