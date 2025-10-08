package com.childprotectionsystems.trackerpro.views.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BatteryFull
import androidx.compose.material.icons.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CardFragment(
    lineColor: Color,
    profileInitials: String,
    userName: String,
    currentLocation: String,
    batteryPercent: Int,
    stepsCount: Int,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium
) {
    Surface(
        modifier = modifier
            .height(80.dp),
        shape = shape,
        tonalElevation = 2.dp
    ) {
        Box {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(4.dp)
                    .background(color = lineColor)
                    .clip(
                        if (shape is RoundedCornerShape) {
                            RoundedCornerShape(
                                topStart = shape.topStart,
                                bottomStart = shape.bottomStart,
                                topEnd = CornerSize(0.dp),
                                bottomEnd = CornerSize(0.dp)
                            )
                        } else {
                            shape
                        }
                    )
                    .align(Alignment.TopStart)
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 12.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile initials
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = profileInitials,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                // Main info column
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = currentLocation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.BatteryFull,
                            contentDescription = "Battery Icon",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$batteryPercent%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Icon(
                            imageVector = Icons.Outlined.DirectionsWalk,
                            contentDescription = "Footsteps Icon",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stepsCount.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                // Phone icon on the right
                Box(
                    modifier = Modifier
                        .size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Phone,
                        contentDescription = "Phone Icon",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardFragmentPreview() {
    CardFragment(
        lineColor = Color.Red,
        profileInitials = "AB",
        userName = "Alice Brown",
        currentLocation = "New York, NY",
        batteryPercent = 75,
        stepsCount = 1234
    )
}
