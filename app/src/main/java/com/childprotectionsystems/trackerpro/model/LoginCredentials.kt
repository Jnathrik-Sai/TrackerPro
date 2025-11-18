package com.childprotectionsystems.trackerpro.model

data class LoginCredentials(
    val email: String = "",
    val password: String = "",
    val role: String = "parent"
)