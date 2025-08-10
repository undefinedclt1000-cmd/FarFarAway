package com.undefined.farfaraway.presentation.features.auth.sign_up

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undefined.farfaraway.domain.entities.Response
import com.undefined.farfaraway.domain.entities.User
import com.undefined.farfaraway.domain.entities.UserType
import com.undefined.farfaraway.presentation.shared.validation.Validations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import com.undefined.farfaraway.domain.useCases.firebase.FireAuthUseCases
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val _fireAuthUseCases: FireAuthUseCases,
    private val _validations: Validations,
): ViewModel() {
    // Flow
    private val _isLoading = MutableStateFlow<Response<Boolean>?>(value = null)
    val isLoading: MutableStateFlow<Response<Boolean>?> = _isLoading

    // Variables para el formulario
    private val _email = MutableLiveData("")
    val email: LiveData<String> = _email

    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password

    private val _confirmPassword = MutableLiveData("")
    val confirmPassword: LiveData<String> = _confirmPassword

    private val _firstName = MutableLiveData("")
    val firstName: LiveData<String> = _firstName

    private val _lastName = MutableLiveData("")
    val lastName: LiveData<String> = _lastName

    private val _age = MutableLiveData("")
    val age: LiveData<String> = _age

    private val _phoneNumber = MutableLiveData("")
    val phoneNumber: LiveData<String> = _phoneNumber

    private val _userType = MutableLiveData(UserType.STUDENT.name)
    val userType: LiveData<String> = _userType

    // Definiciones para errores
    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError

    private val _repeatedPasswordError = MutableLiveData<String?>()
    val repeatedPasswordError: LiveData<String?> = _repeatedPasswordError

    private val _firstNameError = MutableLiveData<String?>()
    val firstNameError: LiveData<String?> = _firstNameError

    private val _lastNameError = MutableLiveData<String?>()
    val lastNameError: LiveData<String?> = _lastNameError

    private val _ageError = MutableLiveData<String?>()
    val ageError: LiveData<String?> = _ageError

    private val _phoneNumberError = MutableLiveData<String?>()
    val phoneNumberError: LiveData<String?> = _phoneNumberError

    fun resetInitState(){
        _isLoading.value = null
    }

    fun onEvent(event: SignUpFormEvent){
        when(event){
            is SignUpFormEvent.EmailChanged -> {
                _email.value = event.email
                _emailError.value = null
            }
            is SignUpFormEvent.PasswordChanged -> {
                _password.value = event.password
                _passwordError.value = null
            }
            is SignUpFormEvent.RepeatedPasswordChanged -> {
                _confirmPassword.value = event.repeatedPassword
                _repeatedPasswordError.value = null
            }
            is SignUpFormEvent.FirstNameChanged -> {
                _firstName.value = event.firstName
                _firstNameError.value = null
            }
            is SignUpFormEvent.LastNameChanged -> {
                _lastName.value = event.lastName
                _lastNameError.value = null
            }
            is SignUpFormEvent.AgeChanged -> {
                _age.value = event.age
                _ageError.value = null
            }
            is SignUpFormEvent.PhoneNumberChanged -> {
                _phoneNumber.value = event.phoneNumber
                _phoneNumberError.value = null
            }
            is SignUpFormEvent.UserTypeChanged -> {
                _userType.value = event.userType
            }
            is SignUpFormEvent.Submit -> {
                submitData()
            }
        }
    }

    private fun submitData() {
        val emailResult = _validations.validateEmail(_email.value!!)
        val passwordResult = _validations.validateStrongPassword(_password.value!!)
        val repeatedPasswordResult = _validations.validateRepeatedPassword(_password.value!!, _confirmPassword.value!!)
        val firstNameResult = _validations.validateBasicText(_firstName.value!!)
        val lastNameResult = _validations.validateBasicText(_lastName.value!!)
        val ageResult = validateAge(_age.value!!)
        val phoneResult = validatePhoneNumber(_phoneNumber.value!!)

        val hasError = listOf(
            emailResult.successful,
            passwordResult.successful,
            repeatedPasswordResult.successful,
            firstNameResult.successful,
            lastNameResult.successful,
            ageResult.successful,
            phoneResult.successful
        ).any{ !it }

        if (hasError){
            _emailError.value = emailResult.errorMessage
            _passwordError.value = passwordResult.errorMessage
            _repeatedPasswordError.value = repeatedPasswordResult.errorMessage
            _firstNameError.value = firstNameResult.errorMessage
            _lastNameError.value = lastNameResult.errorMessage
            _ageError.value = ageResult.errorMessage
            _phoneNumberError.value = phoneResult.errorMessage
            return
        }

        viewModelScope.launch {
            signUpUser()
        }
    }

    private suspend fun signUpUser() = viewModelScope.async {
        _isLoading.value = Response.Loading

        val user = User(
            firstName = _firstName.value!!,
            lastName = _lastName.value!!,
            email = _email.value!!,
            age = _age.value!!.toInt(),
            phoneNumber = _phoneNumber.value!!,
            userType = _userType.value!!,
            isActive = true,
            isEmailVerified = false
        )

        val registerResponse = _fireAuthUseCases.registerUser(user, _password.value!!)
        _isLoading.value = registerResponse
    }.await()

    private fun validateAge(age: String): ValidationResult {
        if (age.isBlank()) {
            return ValidationResult(successful = false, errorMessage = "La edad es requerida")
        }

        val ageInt = age.toIntOrNull()
        if (ageInt == null || ageInt < 18 || ageInt > 100) {
            return ValidationResult(successful = false, errorMessage = "La edad debe estar entre 18 y 100 años")
        }

        return ValidationResult(successful = true)
    }

    private fun validatePhoneNumber(phone: String): ValidationResult {
        if (phone.isBlank()) {
            return ValidationResult(successful = false, errorMessage = "El número de teléfono es requerido")
        }

        if (phone.length < 10) {
            return ValidationResult(successful = false, errorMessage = "El número de teléfono debe tener al menos 10 dígitos")
        }

        return ValidationResult(successful = true)
    }
}

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null
)