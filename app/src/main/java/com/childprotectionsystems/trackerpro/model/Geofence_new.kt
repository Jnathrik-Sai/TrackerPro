package com.childprotectionsystems.trackerpro.model

data class Geofence_new(
    val name: String,
    val radius: Float,
    val tag: String,
    val onEnter: Boolean,
    val onExit: Boolean,
    val trackerAgent: String
)