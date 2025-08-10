package com.undefined.farfaraway.presentation.features.auth.sign_up

sealed class SignUpFormEvent {
    data class EmailChanged(val email: String): SignUpFormEvent()
    data class PasswordChanged(val password: String): SignUpFormEvent()
    data class RepeatedPasswordChanged(val repeatedPassword: String): SignUpFormEvent()
    data class FirstNameChanged(val firstName: String): SignUpFormEvent()
    data class LastNameChanged(val lastName: String): SignUpFormEvent()
    data class AgeChanged(val age: String): SignUpFormEvent()
    data class PhoneNumberChanged(val phoneNumber: String): SignUpFormEvent()
    data class UserTypeChanged(val userType: String): SignUpFormEvent()
    data object Submit: SignUpFormEvent()
}