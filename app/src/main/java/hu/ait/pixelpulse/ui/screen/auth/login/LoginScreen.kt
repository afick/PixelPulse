package hu.ait.pixelpulse.ui.screen.auth.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.ait.pixelpulse.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    loginScreenViewModel: LoginScreenViewModel = viewModel(),
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    var login by rememberSaveable { mutableStateOf(false) }
    var register by rememberSaveable { mutableStateOf(false) }

    var errorText by rememberSaveable {
        mutableStateOf("")
    }

    if (loginScreenViewModel.loginUiState is LoginUiState.LoginSuccess ||
        loginScreenViewModel.loginUiState is LoginUiState.RegisterSuccess
    ) {
        onLoginSuccess()
    }

    Box {
        Text(
            text = "Pixel Pulse",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 50.dp),
            fontSize = 30.sp, fontWeight = FontWeight.SemiBold
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
                    Text(text = stringResource(id = R.string.login_txt))
                }
                OutlinedButton(
                    onClick = { register = true },
                    modifier = Modifier.fillMaxWidth(0.5f)
                ) {
                    Text(text = stringResource(R.string.sign_up_txt))
                }
            } else {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(vertical = 8.dp),
                    label = {
                        Text(text = stringResource(R.string.e_mail_label))
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
                        Text(text = stringResource(R.string.password_label))
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

                    FieldValidationMessage(loginScreenViewModel = loginScreenViewModel)
                    OutlinedButton(onClick = {
                        if (email.isEmpty()) {
                            loginScreenViewModel.loginUiState =
                                LoginUiState.Error(context.getString(R.string.please_enter_an_email))
                            return@OutlinedButton
                        }
                        if (password.isEmpty()) {
                            loginScreenViewModel.loginUiState =
                                LoginUiState.Error(context.getString(R.string.please_enter_a_password))
                            return@OutlinedButton
                        }

                        coroutineScope.launch {
                            val result = loginScreenViewModel.loginUser(email, password)
                            if (result?.user != null) {
                                onLoginSuccess()
                            }
                        }

                    }, modifier = Modifier.padding(top = 32.dp)) {
                        Text(text = stringResource(R.string.login_txt))
                    }
                    TextButton(onClick = {
                        register = true
                        login = false
                        loginScreenViewModel.loginUiState = LoginUiState.Init
                    }) {
                        Text(text = stringResource(R.string.need_an_account_register_here))
                    }
                } else if (register) {
                    var confirmPassword by rememberSaveable { mutableStateOf("") }
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(vertical = 8.dp),
                        label = {
                            Text(text = stringResource(R.string.confirm_password))
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
                        Text(text = errorText, modifier = Modifier.padding(12.dp))
                    }

                    OutlinedButton(onClick = {
                        if (email.isEmpty()) {
                            errorText = context.getString(R.string.please_enter_an_email)
                            return@OutlinedButton
                        }
                        if (password.isEmpty()) {
                            errorText = context.getString(R.string.please_enter_a_password)
                            return@OutlinedButton
                        }
                        if (confirmPassword.isEmpty()) {
                            errorText = context.getString(R.string.please_confirm_your_password)
                            return@OutlinedButton
                        }
                        if (password != confirmPassword) {
                            errorText = context.getString(R.string.passwords_do_not_match)
                            return@OutlinedButton
                        }
                        loginScreenViewModel.registerUser(email, password)
                    }, modifier = Modifier.padding(top = 32.dp)) {
                        Text(text = stringResource(R.string.register_txt))
                    }
                    TextButton(onClick = {
                        register = false;
                        login = true
                        loginScreenViewModel.loginUiState = LoginUiState.Init

                    }) {
                        Text(text = stringResource(R.string.already_have_an_account_login_here))
                    }
                    FieldValidationMessage(loginScreenViewModel = loginScreenViewModel)
                }
            }


        }
    }
}

@Composable
fun FieldValidationMessage(
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
            is LoginUiState.RegisterSuccess -> Text(text = stringResource(R.string.registration_success))
            is LoginUiState.LoginSuccess -> Text(text = stringResource(R.string.login_success))
            is LoginUiState.Error ->
                Text(
                    text = stringResource(
                        R.string.error_login,
                        (loginScreenViewModel.loginUiState as LoginUiState.Error).error!!
                    ),
                    textAlign = TextAlign.Center,
                    color = androidx.compose.ui.graphics.Color.Red,
                    modifier = Modifier.padding(12.dp)
                )
        }
    }
}