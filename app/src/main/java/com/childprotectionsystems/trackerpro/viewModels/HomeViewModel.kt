package com.childprotectionsystems.trackerpro.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.DocumentSnapshot
import com.childprotectionsystems.trackerpro.model.Tracker
import com.childprotectionsystems.trackerpro.model.Contact
class HomeViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _favorites = MutableStateFlow<List<Contact>>(emptyList())
    val favorites: StateFlow<List<Contact>> = _favorites

    private val _trackers = MutableStateFlow<List<Tracker>>(emptyList())
    val trackers: StateFlow<List<Tracker>> = _trackers

    init {
        observeTrackersRealtime()
    }

    private fun String.trimToDisplayName(): String {
        val parts = this.trim().split("\\s+".toRegex())
        return when {
            parts.size <= 2 -> this.trim()
            else -> "${parts.first()} ${parts.last()}"
        }
    }

    fun loadLocalFavorites(favoriteList: List<Contact>) {
        _favorites.value = favoriteList.map { contact ->
            contact.copy(name = contact.name.trimToDisplayName())
        }.sortedBy { it.name.lowercase() }
    }

    private fun observeTrackersRealtime() {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(uid)
            .collection("trackers")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.documents.map { doc ->
                        Tracker(
                            id = doc.id,
                            name = doc.getString("name") ?: "Unknown",
                            location = doc.getString("location") ?: "Unknown",
                            battery = (doc.getLong("battery") ?: 0).toInt(),
                            steps = (doc.getLong("steps") ?: 0).toInt()
                        )
                    }
                    _trackers.value = list
                }
            }
    }
}