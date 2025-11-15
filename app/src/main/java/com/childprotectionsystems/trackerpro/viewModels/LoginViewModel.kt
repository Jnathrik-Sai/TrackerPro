package com.childprotectionsystems.trackerpro.viewModels

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.childprotectionsystems.trackerpro.utils.FcmTokenManager
import com.childprotectionsystems.trackerpro.utils.InputValidator
import com.childprotectionsystems.trackerpro.utils.saveLoginState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var selectedRole by mutableStateOf("parent")
    var isLoading by mutableStateOf(false)
    var isLoginMode by mutableStateOf(true)
    var passwordVisible by mutableStateOf(false)
    var emailError by mutableStateOf<String?>(null)
    var passwordError by mutableStateOf<String?>(null)
    var showSuccessDialog by mutableStateOf(false)

    private val auth = FirebaseAuth.getInstance()
    private val firestore = Firebase.firestore

    fun onEmailChange(newEmail: String) {
        email = newEmail
        emailError = null
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
        passwordError = null
    }

    fun onRoleChange(newRole: String) {
        selectedRole = newRole
    }

    fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
    }

    fun toggleLoginMode() {
        isLoginMode = !isLoginMode
        email = ""
        password = ""
        emailError = null
        passwordError = null
    }

    fun handleAuthentication(context: Context, navController: NavController) {
        viewModelScope.launch {
            isLoading = true
            if (isLoginMode) {
                loginUser(context, navController)
            } else {
                registerUser(context)
            }
        }
    }

    private fun validateInputs(isRegistration: Boolean = false): Boolean {
        val emailValidation = InputValidator.validateEmail(email)
        val passwordValidation = InputValidator.validatePassword(password, isRegistration)

        emailError = emailValidation.errorMessage
        passwordError = passwordValidation.errorMessage

        return emailValidation.isValid && passwordValidation.isValid
    }

    private fun registerUser(context: Context) {
        if (!validateInputs(isRegistration = true)) {
            isLoading = false
            return
        }

        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        auth.createUserWithEmailAndPassword(trimmedEmail, trimmedPassword)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid
                if (uid == null) {
                    showToast(context, "Registration failed")
                    isLoading = false
                    return@addOnSuccessListener
                }

                val userMap = mapOf(
                    "uid" to uid,
                    "email" to trimmedEmail,
                    "role" to selectedRole,
                    "createdAt" to System.currentTimeMillis()
                )

                firestore.collection("users").document(uid)
                    .set(userMap)
                    .addOnSuccessListener {
                        isLoading = false
                        auth.signOut()
                        email = ""
                        password = ""
                        emailError = null
                        passwordError = null
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
                    is FirebaseAuthInvalidCredentialsException -> "Invalid email format"
                    else -> exception.message ?: "Registration failed"
                }
                showToast(context, message)
            }
    }

    private fun loginUser(context: Context, navController: NavController) {
        if (!validateInputs()) {
            isLoading = false
            return
        }

        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

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
                        if (storedRole == selectedRole) {
                            viewModelScope.launch {
                                FcmTokenManager.saveFcmToken(uid)
                                saveLoginState(context, selectedRole)
                                isLoading = false
                                showToast(context, "Welcome back!")

                                val destination = if (selectedRole == "parent") "parent_dashboard" else "child_dashboard"
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
