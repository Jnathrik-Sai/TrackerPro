package com.childprotectionsystems.trackerpro.model

data class Tracker(
    val id: String = "",
    val name: String = "",
    val location: String = "",
    val battery: Int = 0,
    val steps: Int = 0
)