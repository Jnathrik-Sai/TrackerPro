package com.childprotectionsystems.trackerpro.views.Geofences

import com.childprotectionsystems.trackerpro.model.Geofence

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlin.math.roundToInt
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.wrapContentSize


@Composable
fun Geofences() {
    val singapore = LatLng(1.35, 103.87)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(singapore, 10f)
    }

    var selectedColor by remember { mutableStateOf(Color.Red) }
    var radius by remember { mutableFloatStateOf(100f) }
    var menuVisible by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    // State variables for geofence creation
    var geofenceName by remember { mutableStateOf("") }
    var geofenceRadius by remember { mutableFloatStateOf(100f) }
    var geofenceTag by remember { mutableStateOf("") }
    var geofenceOnEnter by remember { mutableStateOf(false) }
    var geofenceOnExit by remember { mutableStateOf(false) }
    var geofenceTimer by remember { mutableFloatStateOf(15f) }
    var geofenceTrackerAgent by remember { mutableStateOf("DefaultAgent") }

    var geofenceList by remember {
        mutableStateOf(listOf("School Zone", "Home Area", "Playground","College"))
    }
    var expandedIndex by remember { mutableIntStateOf(-1) }

    Box(modifier = Modifier.fillMaxSize()) {

        GoogleMap(
            modifier = Modifier
                .fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                compassEnabled = false,
                indoorLevelPickerEnabled = false,
                mapToolbarEnabled = false,
                scrollGesturesEnabled = true,
                zoomGesturesEnabled = true,
                tiltGesturesEnabled = true,
                rotationGesturesEnabled = true
            )
        ) {
            geofenceList.forEachIndexed { index, name ->
                val markerState = remember { MarkerState(position = LatLng(1.35 + index * 0.01, 103.87)) }
                Marker(
                    state = markerState,
                    title = name,
                    snippet = "Radius: 100m",
                    onClick = {
                        expandedIndex = if (expandedIndex == index) -1 else index
                        true
                    }
                )
            }
            geofenceList.forEach { name ->
                Circle(
                    center = LatLng(1.35, 103.87),
                    radius = 100.0,
                    fillColor = Color.Blue.copy(alpha = 0.3f),
                    strokeColor = Color.Blue,
                    strokeWidth = 2f
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(50),
                    placeholder = { Text("Search") },
                    leadingIcon = {
                        Icon(
                            imageVector = if (menuVisible) Icons.Default.Close else Icons.Default.Add,
                            contentDescription = if (menuVisible) "Close" else "Add",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(6.dp)
                                .clickable {
                                    menuVisible = !menuVisible
                                    if (menuVisible) radius = 1f
                                }
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Microphone",
                            modifier = Modifier.padding(6.dp)
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
                )
            }
            AnimatedVisibility(visible = menuVisible) {
                // Dropdown menu with reduced width
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    val scrollState = rememberScrollState()
                    var step by remember { mutableIntStateOf(1) }
                    var nameText by remember { mutableStateOf("") }
                    var nameTouched by remember { mutableStateOf(false) }

                    if (step == 1) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .widthIn(min = 220.dp, max = 300.dp)
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                                .verticalScroll(scrollState),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {
                                        if (nameText.isBlank()) {
                                            nameTouched = true
                                        } else {
                                            geofenceName = nameText
                                            geofenceRadius = radius
                                            geofenceTag = selectedColor.toString()
                                            step = 2
                                        }
                                    },
                                    enabled = nameText.isNotBlank(),
                                    shape = RoundedCornerShape(50),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Forward"
                                    )
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                TextField(
                                    value = nameText,
                                    onValueChange = { newValue ->
                                        if (!newValue.startsWith(" ")) nameText = newValue
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    singleLine = true,
                                    shape = RoundedCornerShape(50),
                                    placeholder = { Text("Name") },
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        focusedIndicatorColor = if (nameText.isBlank() && nameTouched) Color.Red else Color.Transparent,
                                        unfocusedIndicatorColor = if (nameText.isBlank() && nameTouched) Color.Red else Color.Transparent,
                                        disabledIndicatorColor = Color.Transparent
                                    )
                                )
                                Text(
                                    text = "Radius: ${radius.toInt()} meters",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Slider(
                                    value = radius,
                                    onValueChange = { radius = it },
                                    valueRange = 1f..2000f,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(16.dp)
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Text(
                                        text = "Tag:",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    val tagColors = listOf(
                                        Color(0xFF8B0000),
                                        Color(0xFFB8860B),
                                        Color(0xFF006400)
                                    )
                                    if (selectedColor !in tagColors) {
                                        selectedColor = Color(0xFF006400)
                                    }
                                    tagColors.forEach { color ->
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clickable { selectedColor = color },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Surface(
                                                modifier = Modifier.matchParentSize(),
                                                shape = CircleShape,
                                                color = if (selectedColor == color) color else Color.Transparent,
                                                border = BorderStroke(2.dp, color)
                                            ) {}
                                            if (selectedColor == color) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (step == 2) {
                        val itemsList = listOf("On Enter", "On Exit", "Alert Message")
                        val itemCompleted = remember { mutableStateListOf(*Array(itemsList.size) { false }) }
                        Column(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .widthIn(min = 220.dp, max = 300.dp)
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { step = 3 },
                                    shape = RoundedCornerShape(50),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Forward"
                                    )
                                }
                            }
                            Text("Choose a Geofence Trigger", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
                            var alertDuration by remember { mutableStateOf(15f) } // default 15 mins
                            LazyColumn {
                                itemsIndexed(itemsList) { index, item ->
                                    when(item) {
                                        "On Enter", "On Exit" -> {
                                            var isEnabled by remember { mutableStateOf(false) }
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(12.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(item)
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    androidx.compose.material3.Switch(
                                                        checked = isEnabled,
                                                        onCheckedChange = {
                                                            isEnabled = it
                                                            itemCompleted[index] = it
                                                            // Update geofence trigger state variables
                                                            if (item == "On Enter") geofenceOnEnter = it
                                                            if (item == "On Exit") geofenceOnExit = it
                                                        }
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    // Completion dot
                                                    Surface(
                                                        modifier = Modifier.size(12.dp),
                                                        shape = CircleShape,
                                                        color = if (itemCompleted[index]) MaterialTheme.colorScheme.primary else Color.Transparent,
                                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                                                    ) {}
                                                }
                                            }
                                        }
                                        "Alert Message" -> {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(12.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(item)
                                                    // Completion dot
                                                    Surface(
                                                        modifier = Modifier.size(12.dp),
                                                        shape = CircleShape,
                                                        color = if (itemCompleted[index]) MaterialTheme.colorScheme.primary else Color.Transparent,
                                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                                                    ) {}
                                                }
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = "Alert for every: ${alertDuration.toInt()} mins",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                                Slider(
                                                    value = alertDuration,
                                                    onValueChange = {
                                                        // Snap to nearest 5
                                                        alertDuration = (it / 5).roundToInt() * 5f
                                                        // Update geofence timer state variable
                                                        geofenceTimer = alertDuration
                                                    },
                                                    valueRange = 5f..60f,
                                                    steps = 10
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (step == 3) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .widthIn(min = 220.dp, max = 300.dp)
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                                .heightIn(min = 300.dp, max = 400.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(100.dp), // Capsule shape
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .wrapContentWidth()
                                        .height(28.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .wrapContentSize()
                                            .padding(horizontal = 6.dp, vertical = 2.dp),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Completed",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            val placeholderCount = 4
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 300.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(placeholderCount) {
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(80.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        tonalElevation = 2.dp
                                    ) {}
                                }
                            }

                            val newGeofence = Geofence(
                                name = geofenceName,
                                radius = geofenceRadius,
                                tag = geofenceTag,
                                onEnter = geofenceOnEnter,
                                onExit = geofenceOnExit,
                                timer = geofenceTimer,
                                trackerAgent = geofenceTrackerAgent
                            )
                            println("Created Geofence: $newGeofence")
                        }
                    }
                }
            }
        }

        var expanded by remember { mutableStateOf(false) }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            contentPadding = PaddingValues(bottom = 0.dp, top = 12.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        FloatingActionButton(
                            onClick = { /* TODO: move to current location */ },
                            shape = CircleShape,
                            containerColor = Color.White,
                            contentColor = Color.Black,
                            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = "Current Location"
                            )
                        }

                        FloatingActionButton(
                            onClick = { /* TODO: open directions */ },
                            shape = RoundedCornerShape(16.dp),
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Directions,
                                contentDescription = "Directions"
                            )
                        }
                    }
                }
            }
            item {
                Button(
                    onClick = { expanded = !expanded },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 12.dp)
                ) {
                    Text(if (expanded) "Geofences ↑" else "Geofences ↓")
                }
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        tonalElevation = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp)
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(geofenceList.size) { index ->
                                val name = geofenceList[index]
                                GeofenceCardItem(
                                    name = name,
                                    expanded = index == expandedIndex,
                                    onCardClick = { expandedIndex = if (expandedIndex == index) -1 else index },
                                    onEditClick = { println("Edit clicked for $name") },
                                    onGeofenceClick = { println("Geofences clicked for $name") }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
