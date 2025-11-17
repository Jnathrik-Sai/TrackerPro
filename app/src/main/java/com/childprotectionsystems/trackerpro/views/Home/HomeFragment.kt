package com.childprotectionsystems.trackerpro.views.Home

import android.Manifest
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.isGranted
import androidx.compose.ui.platform.LocalContext
import android.provider.ContactsContract
import com.childprotectionsystems.trackerpro.utils.getColorForName
import com.childprotectionsystems.trackerpro.utils.getInitials
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.childprotectionsystems.trackerpro.model.Tracker
import com.childprotectionsystems.trackerpro.model.Contact
import com.childprotectionsystems.trackerpro.viewmodels.HomeViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi

import android.content.Intent
import android.net.Uri


@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class,
    ExperimentalPermissionsApi::class
)
@Composable
fun HomeFragment(
    openDrawer: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val favorites by viewModel.favorites.collectAsState()
    val trackers by viewModel.trackers.collectAsState()

    val contactsPermission = rememberPermissionState(Manifest.permission.READ_CONTACTS)
    val context = LocalContext.current

    LaunchedEffect(contactsPermission.status.isGranted) {
        if (contactsPermission.status.isGranted) {
            loadContactsIntoViewModel(context, viewModel)
        } else {
            contactsPermission.launchPermissionRequest()
        }
    }

    val backgroundColor = Color.White
    var showAddTracker by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
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
                FavoritesSection(favorites = favorites)
                TrackersSection(trackers = trackers, onAddTrackerClick = { showAddTracker = true })
            }
        }

        if (showAddTracker) {
            ModalBottomSheet(
                onDismissRequest = { showAddTracker = false },
                sheetState = sheetState,
                containerColor = Color.White,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                AddtrackerView(onClose = { showAddTracker = false })
            }
        }
    }
}

@Composable
fun FavoritesSection(favorites: List<Contact>) {
    var expanded by remember { mutableStateOf(true) }
    val context = LocalContext.current

    Column {
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
                favorites.forEach { contact ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                            .clickable {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${contact.phone}")
                                }
                                context.startActivity(intent)
                            }
                    ) {
                        Box(
                            modifier = Modifier.size(60.dp),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            if (contact.photoUri != null) {
                                coil.compose.AsyncImage(
                                    model = contact.photoUri,
                                    contentDescription = "Contact Photo",
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(getColorForName(contact.name)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = getInitials(contact.name),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                }
                            }
                            Surface(
                                shape = CircleShape,
                                color = Color.White,
                                tonalElevation = 2.dp,
                                modifier = Modifier
                                    .size(15.dp)
                                    .align(Alignment.BottomEnd)
                                    .offset(x = (-3).dp, y = (-3).dp)
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
                        Text(
                            text = contact.name,
                            fontSize = 12.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TrackersSection(
    trackers: List<Tracker>,
    onAddTrackerClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Trackers",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.LightGray,
                modifier = Modifier
                    .height(32.dp)
                    .padding(end = 4.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onAddTrackerClick() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "+",
                        color = Color.DarkGray,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            trackers.forEachIndexed { index, tracker ->
                val shape = RoundedCornerShape(12.dp)

                CardFragment(
                    lineColor = Color.Red,
                    profileInitials = tracker.name.take(2).uppercase(),
                    userName = tracker.name,
                    currentLocation = tracker.location,
                    batteryPercent = tracker.battery,
                    stepsCount = tracker.steps,
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
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.Transparent,
                shadowElevation = 0.dp,
                modifier = Modifier
                    .height(36.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onMenuClick() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Menu",
                        tint = Color.Gray
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (text.isEmpty()) {
                    Text(text = "Search", color = Color.Gray, fontSize = 16.sp)
                }
                BasicTextField(
                    value = text,
                    onValueChange = { text = it },
                    textStyle = TextStyle(fontSize = 16.sp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            IconButton(onClick = {}) {
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
    HomeFragment(openDrawer = {})
}
private fun String.trimToDisplayName(): String {
    val words = this.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
    return when {
        words.isEmpty() -> ""
        words.size == 1 -> words.first()
        else -> "${words.first()} ${words.last()}"
    }
}

fun loadContactsIntoViewModel(context: android.content.Context, viewModel: HomeViewModel) {
    val resolver = context.contentResolver
    val cursor = resolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        null, null, null, null
    )

    val contacts = mutableListOf<Contact>()

    cursor?.use {
        val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        val phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val photoIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)

        while (cursor.moveToNext()) {
            val name = cursor.getString(nameIndex) ?: ""
            val phone = cursor.getString(phoneIndex) ?: ""
            val photoUri = cursor.getString(photoIndex)

            contacts.add(
                Contact(
                    name = name.trimToDisplayName(),
                    phone = phone,
                    photoUri = photoUri
                )
            )
        }
    }

    viewModel.loadLocalFavorites(contacts)
}