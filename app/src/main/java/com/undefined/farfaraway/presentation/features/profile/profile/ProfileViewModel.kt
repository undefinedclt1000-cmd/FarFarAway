package com.undefined.farfaraway.presentation.features.profile.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undefined.farfaraway.core.Constants
import com.undefined.farfaraway.domain.entities.Response
import com.undefined.farfaraway.domain.entities.User
import com.undefined.farfaraway.domain.useCases.dataStore.DataStoreUseCases
import com.undefined.farfaraway.domain.useCases.firebase.FireAuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val dataStoreUseCases: DataStoreUseCases,
    private val fireAuthUseCases: FireAuthUseCases
): ViewModel() {

    // Estado del usuario desde DataStore
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Estado de logout
    private val _logoutState = MutableStateFlow<Response<Boolean>?>(null)
    val logoutState: StateFlow<Response<Boolean>?> = _logoutState.asStateFlow()

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // StateFlow para compatibilidad con código existente
    private val _totalItems = MutableStateFlow(0)
    val totalItems: StateFlow<Int> = _totalItems

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val id = dataStoreUseCases.getDataString(Constants.USER_UID)
                val firstName = dataStoreUseCases.getDataString(Constants.USER_FIRST_NAME)
                val lastName = dataStoreUseCases.getDataString(Constants.USER_LAST_NAME)
                val email = dataStoreUseCases.getDataString(Constants.USER_EMAIL)
                val age = dataStoreUseCases.getDataInt(Constants.USER_AGE)
                val profileImageUrl = dataStoreUseCases.getDataString(Constants.USER_PROFILE_IMAGE_URL)
                val phoneNumber = dataStoreUseCases.getDataString(Constants.USER_PHONE_NUMBER)
                val isEmailVerified = dataStoreUseCases.getDataBoolean(Constants.USER_IS_EMAIL_VERIFIED)
                val userType = dataStoreUseCases.getDataString(Constants.USER_TYPE)
                val universityId = dataStoreUseCases.getDataString(Constants.USER_UNIVERSITY_ID)
                val averageRating = dataStoreUseCases.getDouble(Constants.USER_AVERAGE_RATING)
                val totalReviews = dataStoreUseCases.getDataInt(Constants.USER_TOTAL_REVIEWS)
                val isActive = dataStoreUseCases.getDataBoolean(Constants.USER_IS_ACTIVE)

                if (id.isNotEmpty()) {
                    val user = User(
                        id = id,
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        age = age,
                        profileImageUrl = profileImageUrl,
                        phoneNumber = phoneNumber,
                        isEmailVerified = isEmailVerified,
                        userType = userType,
                        universityId = universityId,
                        averageRating = averageRating,
                        totalReviews = totalReviews,
                        isActive = isActive
                    )
                    _currentUser.value = user
                } else {
                    _currentUser.value = null
                }

                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                // Manejar error si quieres
            }
        }
    }




    fun logout() {
        viewModelScope.launch {
            _logoutState.value = Response.Loading
            try {
                // Cerrar sesión en Firebase
                //fireAuthUseCases.signOut()

                // Limpiar datos del DataStore
                //dataStoreUseCases.clearUserData()

                _logoutState.value = Response.Success(true)
            } catch (e: Exception) {
                _logoutState.value = Response.Error(e)
            }
        }
    }

    fun resetLogoutState() {
        _logoutState.value = null
    }

    fun refreshUserData() {
        loadUserData()
    }
}