package com.childprotectionsystems.trackerpro.views.Home.Child

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ChildMessage(
    val id: Int,
    val title: String,
    val preview: String,
    val time: String,
    val unread: Boolean
)

@Composable
fun InboxChild(
    onMessagesRead: () -> Unit = {}
) {
    var messages by remember {
        mutableStateOf(
            listOf(
                ChildMessage(1, "Parent Alert", "Come home by 6 PM.", "2:20 PM", true),
                ChildMessage(2, "System", "Your device is online.", "1:05 PM", false),
                ChildMessage(3, "Parent", "Where are you?", "Yesterday", true)
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            "Inbox",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(messages) { msg ->
                InboxItem(
                    message = msg,
                    onClick = {
                        // Mark message as read
                        if (msg.unread) {
                            messages = messages.map {
                                if (it.id == msg.id) it.copy(unread = false) else it
                            }
                        }

                        // Notify parent to remove badge
                        onMessagesRead()
                    }
                )
            }
        }
    }
}

@Composable
fun InboxItem(
    message: ChildMessage,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (message.unread) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color.Red, shape = MaterialTheme.shapes.small)
            )
        } else {
            Spacer(modifier = Modifier.size(10.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                message.title,
                fontWeight = if (message.unread) FontWeight.Bold else FontWeight.Normal,
                fontSize = 16.sp
            )

            Text(
                message.preview,
                fontSize = 13.sp,
                color = Color.Gray
            )
        }

        Text(
            message.time,
            color = Color.Gray,
            fontSize = 12.sp
        )
    }

    Divider(color = Color.LightGray.copy(alpha = .3f))
}