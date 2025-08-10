package com.undefined.farfaraway.presentation.features.auth.login

sealed class LoginFormEvent {
    data class EmailChanged(val email: String): LoginFormEvent()
    data class PasswordChanged(val password: String): LoginFormEvent()
    data object TogglePasswordVisibility: LoginFormEvent()
    data object Submit: LoginFormEvent()
}