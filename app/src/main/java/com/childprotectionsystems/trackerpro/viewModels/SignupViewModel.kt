package com.childprotectionsystems.trackerpro.viewModels

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.childprotectionsystems.trackerpro.model.SignupCredentials
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class SignupViewModel : ViewModel() {

    var credentials by mutableStateOf(SignupCredentials())
        private set

    val username: String
        get() = credentials.username

    val email: String
        get() = credentials.email

    val password: String
        get() = credentials.password

    var confirmPassword by mutableStateOf("")
        private set

    var passwordVisible by mutableStateOf(false)
    var confirmPasswordVisible by mutableStateOf(false)

    var usernameError by mutableStateOf<String?>(null)
    var emailError by mutableStateOf<String?>(null)
    var passwordError by mutableStateOf<String?>(null)
    var confirmPasswordError by mutableStateOf<String?>(null)

    var isLoading by mutableStateOf(false)
    var showSuccessDialog by mutableStateOf(false)

    private val auth = FirebaseAuth.getInstance()
    private val firestore = Firebase.firestore

    fun onUsernameChange(newUsername: String) {
        credentials = credentials.copy(username = newUsername)
        usernameError = null
    }

    fun onEmailChange(newEmail: String) {
        credentials = credentials.copy(email = newEmail)
        emailError = null
    }

    fun onPasswordChange(newPassword: String) {
        credentials = credentials.copy(password = newPassword)
        passwordError = null
    }

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        confirmPassword = newConfirmPassword
        confirmPasswordError = null
    }

    fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
    }

    fun toggleConfirmPasswordVisibility() {
        confirmPasswordVisible = !confirmPasswordVisible
    }

    private fun validate(): Boolean {
        usernameError = if (credentials.username.isBlank()) "Username cannot be empty" else null
        emailError = if (!Patterns.EMAIL_ADDRESS.matcher(credentials.email).matches()) "Invalid email address" else null
        passwordError = if (credentials.password.length < 6) "Password must be at least 6 characters" else null
        confirmPasswordError = if (credentials.password != confirmPassword) "Passwords do not match" else null

        return usernameError == null && emailError == null && passwordError == null && confirmPasswordError == null
    }

    fun signup(context: Context) {
        if (!validate()) {
            return
        }

        viewModelScope.launch {
            isLoading = true
            val trimmedEmail = credentials.email.trim()
            val trimmedPassword = credentials.password.trim()

            auth.createUserWithEmailAndPassword(trimmedEmail, trimmedPassword)
                .addOnSuccessListener { result ->
                    val uid = result.user?.uid
                    if (uid == null) {
                        showToast(context, "Registration failed.")
                        isLoading = false
                        return@addOnSuccessListener
                    }

                    val userMap = mapOf(
                        "uid" to uid,
                        "username" to credentials.username.trim(),
                        "email" to trimmedEmail,
                        "role" to "child", // Default role for new sign-ups
                        "createdAt" to System.currentTimeMillis()
                    )

                    firestore.collection("users").document(uid)
                        .set(userMap)
                        .addOnSuccessListener {
                            isLoading = false
                            auth.signOut()
                            showSuccessDialog = true
                        }
                        .addOnFailureListener { e ->
                            isLoading = false
                            showToast(context, "Registration failed: ${e.message}")
                        }
                }
                .addOnFailureListener { exception ->
                    isLoading = false
                    val message = when (exception) {
                        is FirebaseAuthWeakPasswordException -> "Password is too weak"
                        is FirebaseAuthUserCollisionException -> "This email is already registered"
                        else -> exception.message ?: "Registration failed"
                    }
                    showToast(context, message)
                }
        }
    }

    fun resetState() {
        credentials = SignupCredentials()
        confirmPassword = ""
        usernameError = null
        emailError = null
        passwordError = null
        confirmPasswordError = null
        showSuccessDialog = false
    }

    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}