package com.childprotectionsystems.trackerpro.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.childprotectionsystems.trackerpro.model.Geofence
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GeofenceViewModel : ViewModel() {

    private val _geofences = MutableStateFlow<List<Geofence>>(emptyList())
    val geofences = _geofences.asStateFlow()

    private val firestore = FirebaseFirestore.getInstance()

    init {
        fetchGeofencesFromFirebase()
    }

    private fun fetchGeofencesFromFirebase() {
        firestore.collection("geofences")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    val geofenceList = snapshot.documents.mapNotNull { doc ->
                        try {
                            Geofence(
                                name = doc.getString("name") ?: "",
                                lat = doc.getDouble("lat") ?: 0.0,
                                lng = doc.getDouble("lng") ?: 0.0,
                                radius = (doc.getDouble("radius") ?: 0.0).toFloat(),
                                tag = doc.getString("tag") ?: ""
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    viewModelScope.launch {
                        _geofences.emit(geofenceList)
                    }
                }
            }
    }
}
