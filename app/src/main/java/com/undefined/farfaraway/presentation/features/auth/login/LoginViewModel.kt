package com.undefined.farfaraway.presentation.features.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undefined.farfaraway.domain.entities.Response
import com.undefined.farfaraway.presentation.shared.validation.Validations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.undefined.farfaraway.domain.useCases.firebase.FireAuthUseCases
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val _fireAuthUseCases: FireAuthUseCases,
    private val _validations: Validations,
): ViewModel() {

    // Flow para el estado de carga
    private val _isLoading = MutableStateFlow<Response<Boolean>?>(value = null)
    val isLoading: MutableStateFlow<Response<Boolean>?> = _isLoading

    // Variables del formulario
    private val _email = MutableLiveData("")
    val email: LiveData<String> = _email

    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password

    // Variables para errores
    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError

    // Variable para controlar la visibilidad de la contraseña
    private val _isPasswordVisible = MutableLiveData(false)
    val isPasswordVisible: LiveData<Boolean> = _isPasswordVisible

    // StateFlow para compatibilidad con el código existente
    private val _totalItems = MutableStateFlow(0)
    val totalItems: StateFlow<Int> = _totalItems

    fun resetInitState(){
        _isLoading.value = null
    }

    fun onEvent(event: LoginFormEvent){
        when(event){
            is LoginFormEvent.EmailChanged -> {
                _email.value = event.email
                _emailError.value = null
            }
            is LoginFormEvent.PasswordChanged -> {
                _password.value = event.password
                _passwordError.value = null
            }
            is LoginFormEvent.TogglePasswordVisibility -> {
                _isPasswordVisible.value = !(_isPasswordVisible.value ?: false)
            }
            is LoginFormEvent.Submit -> {
                submitData()
            }
        }
    }

    private fun submitData() {
        val emailResult = _validations.validateEmail(_email.value!!)
        val passwordResult = _validations.validateStrongPassword(_password.value!!)

        val hasError = listOf(
            emailResult,
            passwordResult
        ).any{ !it.successful }

        if (hasError){
            _emailError.value = emailResult.errorMessage
            _passwordError.value = passwordResult.errorMessage
            return
        }

        viewModelScope.launch {
            loginUser()
        }
    }

    private suspend fun loginUser() {
        _isLoading.value = Response.Loading

        try {
            // Asumiendo que tienes un método de login en FireAuthUseCases
            val loginResult = _fireAuthUseCases.loginUser(_email.value!!, _password.value!!)
            _isLoading.value = loginResult
        } catch (e: Exception) {
            _isLoading.value = Response.Error(e)
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            try {
                // Asumiendo que tienes un método para reset de contraseña
                // _fireAuthUseCases.resetPassword(email)
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }
}

// Validaciones básicas si no las tienes en tu clase Validations
data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null
)