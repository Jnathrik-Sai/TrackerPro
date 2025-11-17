package com.childprotectionsystems.trackerpro.views.Authentication

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.childprotectionsystems.trackerpro.utils.LabeledPasswordField
import com.childprotectionsystems.trackerpro.utils.LabeledTextField
import com.childprotectionsystems.trackerpro.viewModels.SignupViewModel

@Composable
fun SignupScreen(navController: NavController, signupViewModel: SignupViewModel = viewModel()) {

    val context = LocalContext.current

    if (signupViewModel.showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                signupViewModel.resetState()
                navController.navigate("login") {
                    popUpTo("signup") { inclusive = true }
                }
            },
            title = { Text("Registration Successful") },
            text = { Text("Your account has been created. Please log in.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        signupViewModel.resetState()
                        navController.navigate("login") {
                            popUpTo("signup") { inclusive = true }
                        }
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Create Account",
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                modifier = Modifier.padding(top = 40.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            LabeledTextField(
                label = "Username",
                value = signupViewModel.username,
                onValueChange = { signupViewModel.onUsernameChange(it) },
                isError = signupViewModel.usernameError != null,
                errorMessage = signupViewModel.usernameError,
                enabled = !signupViewModel.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            LabeledTextField(
                label = "Email",
                value = signupViewModel.email,
                onValueChange = { signupViewModel.onEmailChange(it) },
                isError = signupViewModel.emailError != null,
                errorMessage = signupViewModel.emailError,
                keyboardType = KeyboardType.Email,
                enabled = !signupViewModel.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            LabeledPasswordField(
                label = "Password",
                value = signupViewModel.password,
                onValueChange = { signupViewModel.onPasswordChange(it) },
                isError = signupViewModel.passwordError != null,
                errorMessage = signupViewModel.passwordError,
                enabled = !signupViewModel.isLoading,
                passwordVisible = signupViewModel.passwordVisible,
                onVisibilityChange = signupViewModel::togglePasswordVisibility
            )

            Spacer(modifier = Modifier.height(16.dp))

            LabeledPasswordField(
                label = "Confirm Password",
                value = signupViewModel.confirmPassword,
                onValueChange = { signupViewModel.onConfirmPasswordChange(it) },
                isError = signupViewModel.confirmPasswordError != null,
                errorMessage = signupViewModel.confirmPasswordError,
                enabled = !signupViewModel.isLoading,
                passwordVisible = signupViewModel.confirmPasswordVisible,
                onVisibilityChange = signupViewModel::toggleConfirmPasswordVisibility
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { signupViewModel.signup(context) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                enabled = !signupViewModel.isLoading
            ) {
                Text("Sign Up")
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Already have an account?")
                TextButton(
                    onClick = {
                        if (!signupViewModel.isLoading) {
                            navController.navigate("login")
                        }
                    }
                ) {
                    Text("Login")
                }
            }
        }
        if (signupViewModel.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}