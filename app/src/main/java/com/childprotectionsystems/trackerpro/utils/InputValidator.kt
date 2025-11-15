package com.childprotectionsystems.trackerpro.utils

import android.util.Patterns

object InputValidator {
    fun validateEmail(email: String): ValidationResult {
        val trimmedEmail = email.trim()

        return when {
            trimmedEmail.isEmpty() -> ValidationResult(false, "Email is required")
            trimmedEmail.length < 5 -> ValidationResult(false, "Email is too short")
            !trimmedEmail.contains("@") -> ValidationResult(false, "Email must contain @")
            !trimmedEmail.contains(".") -> ValidationResult(false, "Email must contain a domain")
            trimmedEmail.startsWith("@") || trimmedEmail.endsWith("@") ->
                ValidationResult(false, "Invalid email format")
            trimmedEmail.count { it == '@' } != 1 ->
                ValidationResult(false, "Email must contain exactly one @")
            !Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches() ->
                ValidationResult(false, "Please enter a valid email address")
            trimmedEmail.length > 254 -> ValidationResult(false, "Email is too long")
            else -> ValidationResult(true)
        }
    }

    fun validatePassword(password: String, isRegistration: Boolean = false): ValidationResult {
        return when {
            password.isEmpty() -> ValidationResult(false, "Password is required")
            password.length < 6 -> ValidationResult(false, "Password must be at least 6 characters")
            isRegistration && password.length > 128 ->
                ValidationResult(false, "Password is too long (max 128 characters)")
            isRegistration && !password.any { it.isUpperCase() } ->
                ValidationResult(false, "Password must contain at least one uppercase letter")
            isRegistration && !password.any { it.isLowerCase() } ->
                ValidationResult(false, "Password must contain at least one lowercase letter")
            isRegistration && !password.any { it.isDigit() } ->
                ValidationResult(false, "Password must contain at least one number")
            isRegistration && password.contains(" ") ->
                ValidationResult(false, "Password cannot contain spaces")
            else -> ValidationResult(true)
        }
    }
}
