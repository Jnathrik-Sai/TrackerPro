package com.childprotectionsystems.trackerpro.model

data class Geofence(
    val name: String = "",
    val lat: Double? = null,
    val lng: Double? = null,
    val radius: Float = 0f,
    val tag: String = "",
    val onEnter: Boolean = false,
    val onExit: Boolean = false,
    val timer: Float = 0f,
    val trackerAgent: String = ""
)