package com.undefined.farfaraway.presentation.features.auth.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.undefined.farfaraway.R
import kotlinx.coroutines.launch
import com.undefined.farfaraway.presentation.shared.components.DialogWithIcon
import com.undefined.farfaraway.presentation.shared.components.GenericOutlinedButton
import com.undefined.farfaraway.presentation.shared.components.GenericTextField
import com.undefined.farfaraway.presentation.shared.components.PasswordTextField
import com.undefined.farfaraway.presentation.shared.navigation.enums.Routes

@Composable
fun LoginContent(
    paddingValues: PaddingValues,
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {

    Box(
        modifier = Modifier.padding(paddingValues = paddingValues)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                WelcomeBackText()
                HorizontalDivider()
                Spacer(modifier = Modifier.height(20.dp))
                LoginSection(viewModel = viewModel)
                Spacer(modifier = Modifier.height(16.dp))
                ForgotPasswordSection(viewModel = viewModel)
                Spacer(modifier = Modifier.height(20.dp))
                SignUpSection(navController = navController)
            }
        }
    }
}

@Composable
fun WelcomeBackText(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.welcome_back_title),
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.welcome_back_body),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun LoginSection(viewModel: LoginViewModel) {
    val scope = rememberCoroutineScope()

    // Variables del formulario
    val email by viewModel.email.observeAsState("")
    val password by viewModel.password.observeAsState("")

    // Variables de error
    val emailError by viewModel.emailError.observeAsState()
    val passwordError by viewModel.passwordError.observeAsState()

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        // Campo de email
        GenericTextField(
            value = email,
            onValueChange = {
                viewModel.onEvent(LoginFormEvent.EmailChanged(it))
            },
            leadingIcon = Icons.Filled.AlternateEmail,
            placeholder = R.string.auth_login_email,
            action = ImeAction.Next,
            errorMessage = emailError,
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo de contraseña
        PasswordTextField(
            value = password,
            onTextFieldChange = {
                viewModel.onEvent(LoginFormEvent.PasswordChanged(it))
            },
            painterResource = R.drawable.ic_pass,
            placeholder = R.string.auth_login_password,
            action = ImeAction.Done,
            errorMessage = passwordError
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón de login
        GenericOutlinedButton(
            text = R.string.auth_login_title,
            backgroundColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            onClick = {
                scope.launch {
                    viewModel.onEvent(LoginFormEvent.Submit)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ForgotPasswordSection(viewModel: LoginViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }
    val email by viewModel.email.observeAsState("")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        TextButton(
            onClick = {
                resetEmail = email
                showDialog = true
            }
        ) {
            Text(
                text = stringResource(id = R.string.auth_forgot_password),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    if (showDialog) {
        ForgotPasswordDialog(
            email = resetEmail,
            onEmailChange = { resetEmail = it },
            onDismiss = { showDialog = false },
            onConfirm = {
                viewModel.resetPassword(resetEmail)
                showDialog = false
            }
        )
    }
}

@Composable
fun ForgotPasswordDialog(
    email: String,
    onEmailChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    // Aquí deberías usar tu DialogWithIcon personalizado
    // Por ahora, uso un dialog básico como ejemplo
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(id = R.string.auth_reset_password_title))
        },
        text = {
            Column {
                Text(
                    text = stringResource(id = R.string.auth_reset_password_body),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                GenericTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    leadingIcon = Icons.Filled.AlternateEmail,
                    placeholder = R.string.auth_login_email,
                    action = ImeAction.Done,
                    keyboardType = KeyboardType.Email
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(id = R.string.send_text))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.cancel_text))
            }
        }
    )
}

@Composable
fun SignUpSection(navController: NavController){
    val annotatedString = buildAnnotatedString {
        append(stringResource(id = R.string.auth_no_account) + " ")

        pushStringAnnotation(tag = "signup", annotation = "")
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline
            )
        ) {
            append(stringResource(id = R.string.auth_register_dialog))
        }
        pop()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp),
        contentAlignment = Alignment.Center
    ) {
        ClickableText(
            text = annotatedString,
            onClick = { offset ->
                annotatedString.getStringAnnotations(tag = "signup", start = offset, end = offset)
                    .firstOrNull()?.let {
                        navController.navigate(Routes.SIGN_UP.name)
                    }
            },
            style = TextStyle(
                color = Color.Gray,
                fontStyle = FontStyle.Italic
            )
        )
    }
}