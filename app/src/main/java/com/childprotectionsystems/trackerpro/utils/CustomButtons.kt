package com.childprotectionsystems.trackerpro.utils

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun LabeledTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        shape = MaterialTheme.shapes.medium,
        isError = isError,
        supportingText = {
            if (isError) {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        enabled = enabled
    )
}

@Composable
fun LabeledPasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    passwordVisible: Boolean,
    onVisibilityChange: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        shape = MaterialTheme.shapes.medium,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        isError = isError,
        trailingIcon = {
            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            val description = if (passwordVisible) "Hide password" else "Show password"

            IconButton(onClick = onVisibilityChange) {
                Icon(imageVector = image, contentDescription = description)
            }
        },
        supportingText = {
            if (isError) {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        enabled = enabled
    )
}
