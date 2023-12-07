package hu.ait.pixelpulse.ui.screen.auth.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    loginScreenViewModel: LoginScreenViewModel = viewModel(),
    onLoginSuccess: () -> Unit
) {
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    var login by rememberSaveable { mutableStateOf(false) }
    var register by rememberSaveable { mutableStateOf(false) }

    var errorText by rememberSaveable {
        mutableStateOf("")
    }

    var errorState by remember {
        mutableStateOf(false)
    }


    Box() {
        Text(
            text = "Pixel Pulse",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 50.dp),
            fontSize = 30.sp
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!login && !register) {
                OutlinedButton(
                    onClick = { login = true }, modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .padding(vertical = 8.dp)
                ) {
                    Text(text = "Login")
                }
                OutlinedButton(
                    onClick = { register = true },
                    modifier = Modifier.fillMaxWidth(0.5f)
                ) {
                    Text(text = "Sign Up")
                }
            } else {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(vertical = 8.dp),
                    label = {
                        Text(text = "E-mail")
                    },
                    value = email,
                    onValueChange = {
                        email = it
                        errorText = ""
                    },
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Email, null)
                    }
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(vertical = 8.dp),
                    label = {
                        Text(text = "Password")
                    },
                    value = password,
                    onValueChange = {
                        password = it
                        errorText = ""
                    },
                    singleLine = true,
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    leadingIcon = {
                        Icon(Icons.Default.Password, null)
                    },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            if (showPassword) {
                                Icon(Icons.Default.Visibility, null)
                            } else {
                                Icon(Icons.Default.VisibilityOff, null)
                            }
                        }
                    }
                )
                if (login) {

                    fieldValidationMessage(loginScreenViewModel = loginScreenViewModel)
                    OutlinedButton(onClick = {
                        if (email.isEmpty()) {
                            loginScreenViewModel.loginUiState =
                                LoginUiState.Error("Please enter an email")
                            return@OutlinedButton
                        }
                        if (password.isEmpty()) {
                            loginScreenViewModel.loginUiState =
                                LoginUiState.Error("Please enter a password")
                            return@OutlinedButton
                        }

                        coroutineScope.launch {
                            val result = loginScreenViewModel.loginUser(email, password)
                            if (result?.user != null) {
                                onLoginSuccess()
                            }
                        }

                    }, modifier = Modifier.padding(top = 32.dp)) {
                        Text(text = "Login")
                    }
                    TextButton(onClick = { register = true; login = false }) {
                        Text(text = "Need an account? Register here")
                    }
                } else if (register) {
                    var confirmPassword by rememberSaveable { mutableStateOf("") }
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(vertical = 8.dp),
                        label = {
                            Text(text = "Confirm Password")
                        },
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; errorText = "" },
                        singleLine = true,
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        leadingIcon = {
                            Icon(Icons.Default.Password, null)
                        },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                if (showPassword) {
                                    Icon(Icons.Default.Visibility, null)
                                } else {
                                    Icon(Icons.Default.VisibilityOff, null)
                                }
                            }
                        })



                    AnimatedVisibility(
                        visible = errorText.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(text = errorText)
                    }

                    OutlinedButton(onClick = {
                        if (email.isEmpty()) {
                            errorText = "Please enter an email"
                            return@OutlinedButton
                        }
                        if (password.isEmpty()) {
                            errorText = "Please enter a password"
                            return@OutlinedButton
                        }
                        if (confirmPassword.isEmpty()) {
                            errorText = "Please confirm your password"
                            return@OutlinedButton
                        }
                        if (password != confirmPassword) {
                            errorText = "Passwords do not match"
                            return@OutlinedButton
                        }
                        loginScreenViewModel.registerUser(email, password)
                    }, modifier = Modifier.padding(top = 32.dp)) {
                        Text(text = "Register")
                    }
                    TextButton(onClick = { register = false; login = true }) {
                        Text(text = "Already have an account? Login here")
                    }
                    fieldValidationMessage(loginScreenViewModel = loginScreenViewModel)
                }
            }


        }
    }
}

@Composable
fun fieldValidationMessage(
    loginScreenViewModel: LoginScreenViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (loginScreenViewModel.loginUiState) {
            is LoginUiState.Init -> {}
            is LoginUiState.Loading -> CircularProgressIndicator()
            is LoginUiState.RegisterSuccess -> Text(text = "Registration Success")
            is LoginUiState.LoginSuccess -> Text(text = "Login Success")
            is LoginUiState.Error ->
                Text(
                    text = "Error: ${(loginScreenViewModel.loginUiState as LoginUiState.Error).error}",
                    textAlign = TextAlign.Center,
                    color = androidx.compose.ui.graphics.Color.Red,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
        }
    }
}