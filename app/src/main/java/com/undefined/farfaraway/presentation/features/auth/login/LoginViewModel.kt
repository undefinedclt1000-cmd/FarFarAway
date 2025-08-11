package com.undefined.farfaraway.presentation.features.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undefined.farfaraway.core.Constants
import com.undefined.farfaraway.domain.entities.Response
import com.undefined.farfaraway.domain.entities.User
import com.undefined.farfaraway.domain.useCases.dataStore.DataStoreUseCases
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
    private val dataStoreUseCases: DataStoreUseCases

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
            val loginResult = _fireAuthUseCases.loginUser(_email.value!!, _password.value!!)

            if (loginResult is Response.Success) {
                val user = loginResult.data
                // Guardar datos en DataStore
                saveUserToDataStore(user)
            }

            _isLoading.value = Response.Success(true)
        } catch (e: Exception) {
            _isLoading.value = Response.Error(e)
        }
    }

    private fun saveUserToDataStore(user: User) = viewModelScope.launch {
        dataStoreUseCases.setDataString(Constants.USER_UID, user.id)
        dataStoreUseCases.setDataString(Constants.USER_FIRST_NAME, user.firstName)
        dataStoreUseCases.setDataString(Constants.USER_LAST_NAME, user.lastName)
        dataStoreUseCases.setDataString(Constants.USER_EMAIL, user.email)
        dataStoreUseCases.setDataInt(Constants.USER_AGE, user.age)
        dataStoreUseCases.setDataString(Constants.USER_PROFILE_IMAGE_URL, user.profileImageUrl)
        dataStoreUseCases.setDataString(Constants.USER_PHONE_NUMBER, user.phoneNumber)
        dataStoreUseCases.setDataBoolean(Constants.USER_IS_EMAIL_VERIFIED, user.isEmailVerified)
        dataStoreUseCases.setDataString(Constants.USER_TYPE, user.userType)
        dataStoreUseCases.setDataString(Constants.USER_UNIVERSITY_ID, user.universityId)
        dataStoreUseCases.setDouble(Constants.USER_AVERAGE_RATING, user.averageRating)
        dataStoreUseCases.setDataInt(Constants.USER_TOTAL_REVIEWS, user.totalReviews)
        dataStoreUseCases.setDataBoolean(Constants.USER_IS_ACTIVE, user.isActive)
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