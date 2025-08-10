package com.undefined.farfaraway.presentation.features.auth.sign_up

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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.undefined.farfaraway.domain.entities.UserType
import kotlinx.coroutines.launch
import com.undefined.farfaraway.presentation.shared.components.DialogWithIcon
import com.undefined.farfaraway.presentation.shared.components.GenericCardWithRadio
import com.undefined.farfaraway.presentation.shared.components.GenericOutlinedButton
import com.undefined.farfaraway.presentation.shared.components.GenericTextField
import com.undefined.farfaraway.presentation.shared.components.PasswordTextField

@Composable
fun SignUpContent(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    navController: NavController,
    viewModel: SignUpViewModel = hiltViewModel()
) {

    Box(
        modifier = modifier.padding(paddingValues = paddingValues)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                WelcomeText()
                HorizontalDivider()
                Spacer(modifier = Modifier.height(20.dp))
                RegisterSection(viewModel = viewModel)
                Spacer(modifier = Modifier.height(20.dp))
                LoginSection(navController = navController)
            }
        }
    }
}

@Composable
fun WelcomeText(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.welcome_title),
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.welcome_body),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun RegisterSection(
    viewModel: SignUpViewModel
){

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 3 })
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        userScrollEnabled = false
    ) { page ->
        when (page) {
            0 -> SignUpRoleSelection(
                viewModel = viewModel,
                onNext = {
                    scope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                }
            )

            1 -> SignUpBasicInfo(
                viewModel = viewModel,
                onBack = {
                    scope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                },
                onNext = {
                    scope.launch {
                        pagerState.animateScrollToPage(2)
                    }
                }
            )

            2 -> SignUpCredentials(
                viewModel = viewModel,
                onBack = {
                    scope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                }
            )
        }
    }
}

@Composable
fun SignUpRoleSelection(viewModel: SignUpViewModel, onNext: () -> Unit) {
    val userType by viewModel.userType.observeAsState(UserType.STUDENT.name)
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Soy...", style = MaterialTheme.typography.titleLarge)

        GenericCardWithRadio(
            title = R.string.auth_user_type_student_title,
            description = R.string.auth_user_type_student_body,
            isSelected = userType == UserType.STUDENT.name,
            onSelect = {
                viewModel.onEvent(SignUpFormEvent.UserTypeChanged(UserType.STUDENT.name))
            },
            icon = Icons.Default.School
        )

        GenericCardWithRadio(
            title = R.string.auth_user_type_owner_title,
            description = R.string.auth_user_type_owner_body,
            isSelected = userType == UserType.LANDLORD.name,
            onSelect = {
                viewModel.onEvent(SignUpFormEvent.UserTypeChanged(UserType.LANDLORD.name))
            },
            icon = Icons.Default.Home
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                onNext()
                showDialog = true
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = stringResource(id = R.string.continue_text),
                color = Color.White
            )
        }
    }

    if (showDialog) {
        DialogWithIcon(
            onDismissRequest = { showDialog = false },
            onConfirmation = { showDialog = false },
            dialogTitle = R.string.auth_verify_alert_title,
            dialogText = R.string.auth_verify_alert_body,
            icon = R.drawable.ic_info
        )
    }
}

@Composable
fun SignUpBasicInfo(viewModel: SignUpViewModel, onBack: () -> Unit, onNext: () -> Unit) {
    val firstName by viewModel.firstName.observeAsState("")
    val lastName by viewModel.lastName.observeAsState("")
    val age by viewModel.age.observeAsState("")
    val phoneNumber by viewModel.phoneNumber.observeAsState("")

    val firstNameError by viewModel.firstNameError.observeAsState()
    val lastNameError by viewModel.lastNameError.observeAsState()
    val ageError by viewModel.ageError.observeAsState()
    val phoneNumberError by viewModel.phoneNumberError.observeAsState()

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(24.dp)
    ) {
        Text(
            text = "Información Personal",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Nombre
        GenericTextField(
            value = firstName,
            onValueChange = {
                viewModel.onEvent(SignUpFormEvent.FirstNameChanged(it))
            },
            leadingIcon = Icons.Filled.Person,
            placeholder = R.string.auth_register_first_name,
            action = ImeAction.Next,
            errorMessage = firstNameError
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Apellido
        GenericTextField(
            value = lastName,
            onValueChange = {
                viewModel.onEvent(SignUpFormEvent.LastNameChanged(it))
            },
            leadingIcon = Icons.Filled.AccountCircle,
            placeholder = R.string.auth_register_last_name,
            action = ImeAction.Next,
            errorMessage = lastNameError
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Edad
        GenericTextField(
            value = age,
            onValueChange = { input ->
                if (input.all { it.isDigit() }) {
                    viewModel.onEvent(SignUpFormEvent.AgeChanged(input))
                }
            },
            leadingIcon = Icons.Filled.Numbers,
            placeholder = R.string.auth_register_age,
            action = ImeAction.Next,
            errorMessage = ageError,
            keyboardType = KeyboardType.Number
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Teléfono
        GenericTextField(
            value = phoneNumber,
            onValueChange = { input ->
                if (input.all { it.isDigit() }) {
                    viewModel.onEvent(SignUpFormEvent.PhoneNumberChanged(input))
                }
            },
            leadingIcon = Icons.Filled.Phone,
            placeholder = R.string.auth_register_phone,
            action = ImeAction.Done,
            errorMessage = phoneNumberError,
            keyboardType = KeyboardType.Phone
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botones de navegación
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text(text = stringResource(id = R.string.back_text))
            }

            Button(
                onClick = {
                    // Validar campos básicos antes de continuar
                    if (firstName.isNotBlank() && lastName.isNotBlank() &&
                        age.isNotBlank() && phoneNumber.isNotBlank()) {
                        onNext()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = stringResource(id = R.string.continue_text),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun SignUpCredentials(viewModel: SignUpViewModel, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()

    val email by viewModel.email.observeAsState("")
    val password by viewModel.password.observeAsState("")
    val confirmPassword by viewModel.confirmPassword.observeAsState("")

    val emailError by viewModel.emailError.observeAsState()
    val passwordError by viewModel.passwordError.observeAsState()
    val repeatedPasswordError by viewModel.repeatedPasswordError.observeAsState()

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(24.dp)
    ) {
        Text(
            text = "Credenciales de Acceso",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Email
        GenericTextField(
            value = email,
            onValueChange = {
                viewModel.onEvent(SignUpFormEvent.EmailChanged(it))
            },
            leadingIcon = Icons.Filled.AlternateEmail,
            placeholder = R.string.auth_login_email,
            action = ImeAction.Next,
            errorMessage = emailError,
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Contraseña
        PasswordTextField(
            value = password,
            onTextFieldChange = {
                viewModel.onEvent(SignUpFormEvent.PasswordChanged(it))
            },
            painterResource = R.drawable.ic_pass,
            placeholder = R.string.auth_login_password,
            action = ImeAction.Next,
            errorMessage = passwordError
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Confirmar contraseña
        PasswordTextField(
            value = confirmPassword,
            onTextFieldChange = {
                viewModel.onEvent(SignUpFormEvent.RepeatedPasswordChanged(it))
            },
            painterResource = R.drawable.ic_pass,
            placeholder = R.string.auth_register_confirm_password,
            action = ImeAction.Done,
            errorMessage = repeatedPasswordError
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botones de navegación
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text(text = stringResource(id = R.string.back_text))
            }

            GenericOutlinedButton(
                text = R.string.auth_register_title,
                backgroundColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = {
                    scope.launch {
                        viewModel.onEvent(SignUpFormEvent.Submit)
                    }
                }
            )
        }
    }
}

@Composable
fun LoginSection(navController: NavController){
    val annotatedString = buildAnnotatedString {
        append(stringResource(id = R.string.auth_already_account) + " ")

        pushStringAnnotation(tag = "login", annotation = "")
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline
            )
        ) {
            append(stringResource(id = R.string.auth_login_dialog))
        }
        pop()
    }
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(3.dp), contentAlignment = Alignment.Center){
        ClickableText(text = annotatedString, onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "login", start = offset, end = offset).firstOrNull()?.let {
                navController.popBackStack()
            }
        }, style = TextStyle(color = Color.Gray, fontStyle = FontStyle.Italic))
    }
}