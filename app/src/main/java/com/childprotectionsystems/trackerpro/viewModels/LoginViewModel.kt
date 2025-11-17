package com.childprotectionsystems.trackerpro.viewModels

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.childprotectionsystems.trackerpro.model.LoginCredentials
import com.childprotectionsystems.trackerpro.utils.FcmTokenManager
import com.childprotectionsystems.trackerpro.utils.InputValidator
import com.childprotectionsystems.trackerpro.utils.saveLoginState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    var credentials by mutableStateOf(LoginCredentials())

    val email: String
        get() = credentials.email

    val password: String
        get() = credentials.password

    val selectedRole: String
        get() = credentials.role

    var isLoading by mutableStateOf(false)
    var passwordVisible by mutableStateOf(false)

    var emailError by mutableStateOf<String?>(null)
    var passwordError by mutableStateOf<String?>(null)

    private val auth = FirebaseAuth.getInstance()
    private val firestore = Firebase.firestore

    // --------------------------
    //  Model-based setters
    // --------------------------
    fun onEmailChange(newEmail: String) {
        credentials = credentials.copy(email = newEmail)
        emailError = null
    }

    fun onPasswordChange(newPassword: String) {
        credentials = credentials.copy(password = newPassword)
        passwordError = null
    }

    fun onRoleChange(newRole: String) {
        credentials = credentials.copy(role = newRole)
    }

    fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
    }

    fun handleLogin(context: Context, navController: NavController) {
        if (!validateInputs()) {
            return
        }

        viewModelScope.launch {
            isLoading = true
            loginUser(context, navController)
        }
    }

    // --------------------------
    //  Validation using model
    // --------------------------
    private fun validateInputs(): Boolean {
        val emailValidation = InputValidator.validateEmail(credentials.email)
        val passwordValidation = InputValidator.validatePassword(credentials.password)

        emailError = emailValidation.errorMessage
        passwordError = passwordValidation.errorMessage

        return emailValidation.isValid && passwordValidation.isValid
    }

    // --------------------------
    //  Login
    // --------------------------
    private fun loginUser(context: Context, navController: NavController) {
        val trimmedEmail = credentials.email.trim()
        val trimmedPassword = credentials.password.trim()

        auth.signInWithEmailAndPassword(trimmedEmail, trimmedPassword)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid
                if (uid == null) {
                    showToast(context, "Login failed")
                    isLoading = false
                    return@addOnSuccessListener
                }

                firestore.collection("users").document(uid)
                    .get()
                    .addOnSuccessListener { doc ->
                        val storedRole = doc.getString("role")
                        if (storedRole == credentials.role) {

                            // ---- Success Path ----
                            viewModelScope.launch {
                                FcmTokenManager.saveFcmToken(uid)
                                saveLoginState(context, credentials.role)

                                isLoading = false
                                showToast(context, "Welcome back!")

                                val destination =
                                    if (credentials.role == "parent") "parent_dashboard"
                                    else "child_dashboard"

                                navController.navigate(destination) {
                                    popUpTo("login") { inclusive = true }
                                }
                            }

                        } else {
                            isLoading = false
                            showToast(context, "Role mismatch! You registered as $storedRole")
                            auth.signOut()
                        }
                    }
                    .addOnFailureListener { e ->
                        isLoading = false
                        showToast(context, "Failed to verify user: ${e.message}")
                    }
            }
            .addOnFailureListener { exception ->
                isLoading = false
                val message = when (exception) {
                    is FirebaseAuthInvalidUserException -> "No account found with this email"
                    is FirebaseAuthInvalidCredentialsException -> "Incorrect email or password"
                    else -> exception.message ?: "Login failed"
                }
                showToast(context, message)
            }
    }

    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}