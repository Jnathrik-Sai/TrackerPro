package com.childprotectionsystems.trackerpro.views.Home.Child

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.childprotectionsystems.trackerpro.utils.SearchViewModel
import androidx.compose.foundation.clickable
import androidx.compose.runtime.collectAsState

@SuppressLint("MissingPermission")
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun GeofencesChild() {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var childLocation by remember { mutableStateOf<LatLng?>(null) }
    var hasLocationPermission by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val searchViewModel: SearchViewModel = viewModel()
    val suggestions = searchViewModel.suggestions

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasLocationPermission =
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                        permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)
        }
    )

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    DisposableEffect(hasLocationPermission) {
        if (!hasLocationPermission) {
            onDispose {}
        } else {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000L)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(1000L)
                .setMaxUpdateDelayMillis(3000L)
                .build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.lastLocation?.let { location ->
                        childLocation = LatLng(location.latitude, location.longitude)
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                context.mainLooper
            )

            onDispose {
                fusedLocationClient.removeLocationUpdates(locationCallback)
            }
        }
    }

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(2.0)
            center(Point.fromLngLat(-98.0, 39.5))
        }
    }

    LaunchedEffect(childLocation) {
        childLocation?.let {
            mapViewportState.flyTo(
                CameraOptions.Builder()
                    .center(Point.fromLngLat(it.longitude, it.latitude))
                    .zoom(14.0)
                    .build(),
            )
        }
    }

    LaunchedEffect(searchViewModel.selectedPoint.collectAsState().value) {
        searchViewModel.selectedPoint.value?.let { p ->
            mapViewportState.flyTo(
                CameraOptions.Builder()
                    .center(p)
                    .zoom(14.0)
                    .build()
            )
            searchViewModel.clearResults()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapViewportState = mapViewportState,
            compass = { },
            scaleBar = { }
        )

        SearchBar(
            query = searchQuery,
            onQueryChange = {
                searchQuery = it
                searchViewModel.onQueryChanged(it)
            },
            onSearch = {},
            active = false,
            onActiveChange = {},
            placeholder = { Text("Search") },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 12.dp, start = 16.dp, end = 16.dp)
        ) {
            // empty content required by SearchBar API
        }

        val suggestionList by searchViewModel.suggestions.collectAsState()

        LazyColumn(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 70.dp)
                .fillMaxWidth()
        ) {
            items(suggestionList) { suggestion ->
                Text(
                    text = suggestion.name ?: "Unknown",
                    modifier = Modifier
                        .padding(12.dp)
                        .clickable {
                            searchViewModel.selectSuggestion(suggestion)
                        }
                )
            }
        }

        FloatingActionButton(
            onClick = {
                childLocation?.let {
                    mapViewportState.flyTo(
                        CameraOptions.Builder()
                            .center(Point.fromLngLat(it.longitude, it.latitude))
                            .zoom(14.0)
                            .build(),
                    )
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            Icon(Icons.Default.MyLocation, contentDescription = "My Location")
        }

        when {
            !hasLocationPermission -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding()
                        .background(Color.Black.copy(alpha = 0.35f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Location permission is required to acquire GPS location.",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
            childLocation == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding()
                        .background(Color.Black.copy(alpha = 0.35f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Acquiring GPS signal…")
                    }
                }
            }
        }
    }
}