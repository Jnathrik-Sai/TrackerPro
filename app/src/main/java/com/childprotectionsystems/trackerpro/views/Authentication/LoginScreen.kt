package com.childprotectionsystems.trackerpro.views.Authentication

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.childprotectionsystems.trackerpro.utils.LabeledPasswordField
import com.childprotectionsystems.trackerpro.utils.LabeledTextField
import com.childprotectionsystems.trackerpro.viewModels.LoginViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = viewModel()
) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Login",
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                modifier = Modifier.padding(top = 40.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LabeledTextField(
                label = "Email",
                value = viewModel.email,
                onValueChange = viewModel::onEmailChange,
                isError = viewModel.emailError != null,
                errorMessage = viewModel.emailError,
                enabled = !viewModel.isLoading
            )

            Spacer(modifier = Modifier.height(8.dp)) // reduced from 16

            LabeledPasswordField(
                label = "Password",
                value = viewModel.password,
                onValueChange = viewModel::onPasswordChange,
                isError = viewModel.passwordError != null,
                errorMessage = viewModel.passwordError,
                enabled = !viewModel.isLoading,
                passwordVisible = viewModel.passwordVisible,
                onVisibilityChange = viewModel::togglePasswordVisibility
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFE6E6E6)),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("parent", "child").forEach { role ->
                    val isSelected = viewModel.selectedRole == role

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                if (isSelected) Color(0xFF2196F3) else Color.Transparent
                            )
                            .clickable { viewModel.onRoleChange(role) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = role.replaceFirstChar { it.uppercase() },
                            color = if (isSelected) Color.White else Color.Black,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { Toast.makeText(context, "Feature coming soon!", Toast.LENGTH_SHORT).show() },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Forgot Password?")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { viewModel.handleLogin(context, navController) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                enabled = !viewModel.isLoading
            ) {
                Text("Login")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Divider(modifier = Modifier.weight(1f))
                Text("  OR  ")
                Divider(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = { /* TODO: Google Login */ },
                modifier = Modifier.fillMaxWidth(),
                enabled = !viewModel.isLoading
            ) {
                Text("Sign in with Google")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { /* TODO: Facebook Login */ },
                modifier = Modifier.fillMaxWidth(),
                enabled = !viewModel.isLoading
            ) {
                Text("Sign in with Facebook")
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("New to TrackerPro?")
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(
                    onClick = { navController.navigate("signup") }
                ) {
                    Text("Create Account")
                }
            }
        }

        if (viewModel.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}