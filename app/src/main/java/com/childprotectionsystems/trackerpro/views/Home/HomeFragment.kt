package com.childprotectionsystems.trackerpro.views.Home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.animation.animateContentSize

@Composable
fun HomeFragment(openDrawer: () -> Unit = {}) {
    val backgroundColor = Color.White

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .statusBarsPadding()
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            SearchBar(onMenuClick = openDrawer)
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .animateContentSize(animationSpec = tween(durationMillis = 300))
        ) {
            FavoritesSection()
            TrackersSection()
        }
    }
}

@Composable
fun FavoritesSection() {
    var expanded by remember { mutableStateOf(true) }

    Column {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Favorites",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (expanded)
                        Icons.Filled.KeyboardArrowUp
                    else
                        Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Toggle Profiles",
                    tint = Color.Gray
                )
            }
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)),
            exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(start = 8.dp, end = 16.dp, top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                repeat(10) { index ->
                    Box(
                        modifier = Modifier.size(80.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.LightGray,
                            modifier = Modifier.matchParentSize()
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "U$index",
                                    color = Color.DarkGray,
                                    fontSize = 18.sp
                                )
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            tonalElevation = 2.dp,
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.BottomEnd)
                                .offset(x = (-4).dp, y = (-4).dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Phone,
                                contentDescription = "Phone",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier
                                    .padding(2.dp)
                                    .fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrackersSection(trackers: List<String> = List(5) { "Tracker ${it + 1}" }) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        Text(
            text = "Trackers",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            trackers.forEachIndexed { index, tracker ->
                val shape = when (index) {
                    0 -> RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 8.dp, bottomEnd = 8.dp)
                    trackers.lastIndex -> RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
                    else -> RoundedCornerShape(8.dp)
                }

                CardFragment(
                    lineColor = Color.Red,
                    profileInitials = tracker.take(2).uppercase(),
                    userName = tracker,
                    currentLocation = "Current Location",
                    batteryPercent = 75, // placeholder value
                    stepsCount = 1234,   // placeholder value
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .clip(shape),
                    shape = shape
                )

                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun SearchBar(onMenuClick: () -> Unit = {}) {
    var text by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(25.dp),
        color = Color(0xFFF2F2F2)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu",
                    tint = Color.Gray
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (text.isEmpty()) {
                    Text(
                        text = "Search",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
                BasicTextField(
                    value = text,
                    onValueChange = { text = it },
                    textStyle = TextStyle(fontSize = 16.sp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            IconButton(onClick = { /* TODO: mic action */ }) {
                Icon(
                    imageVector = Icons.Filled.Mic,
                    contentDescription = "Mic",
                    tint = Color.Gray
                )
            }
        }
    }
}

@Preview
@Composable
fun HomeFragmentPreview() {
    HomeFragment()
}
