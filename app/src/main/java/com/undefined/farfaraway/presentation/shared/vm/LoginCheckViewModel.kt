package com.undefined.farfaraway.presentation.shared.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.undefined.farfaraway.core.Constants
import com.undefined.farfaraway.domain.entities.Response
import com.undefined.farfaraway.domain.entities.User
import com.undefined.farfaraway.domain.useCases.dataStore.DataStoreUseCases
import com.undefined.farfaraway.domain.useCases.firebase.FireAuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class LoginCheckViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val _dataStoreUseCases: DataStoreUseCases,
    private val authUseCases: FireAuthUseCases
): ViewModel() {

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: MutableLiveData<User?> = _currentUser

    private val _userResponse = MutableStateFlow<Response<User>?>(null)
    val userResponse: StateFlow<Response<User>?> = _userResponse

    private val _navigateToLogin = MutableStateFlow(false)
    val navigateToLogin: StateFlow<Boolean> = _navigateToLogin

    private val _isAuthenticationComplete = MutableStateFlow(false)
    val isAuthenticationComplete: StateFlow<Boolean> = _isAuthenticationComplete

    init {
        getCurrentUser()
    }

    private fun getCurrentUser() = viewModelScope.launch {
        _userResponse.value = Response.Loading
        val firebaseUser = firebaseAuth.currentUser

        if (firebaseUser != null) {
            val response = authUseCases.getUser(firebaseUser.uid)
            _userResponse.value = response

            if (response is Response.Success) {
                _currentUser.value = response.data
                setDataStoreInfo(response.data, firebaseUser.email ?: "")
                _navigateToLogin.value = false
            } else {
                _navigateToLogin.value = true
            }
        } else {
            _userResponse.value = Response.Error(Exception("User not found"))
            _navigateToLogin.value = true
        }

        _isAuthenticationComplete.value = true
    }

    private fun setDataStoreInfo(user: User, email: String) = viewModelScope.launch {
        _dataStoreUseCases.setDataString(Constants.USER_UID, user.id)
        _dataStoreUseCases.setDataString(Constants.USER_FIRST_NAME, user.firstName)
        _dataStoreUseCases.setDataString(Constants.USER_LAST_NAME, user.lastName)
        _dataStoreUseCases.setDataString(Constants.USER_EMAIL, user.email.ifEmpty { email })
        _dataStoreUseCases.setDataInt(Constants.USER_AGE, user.age)
        _dataStoreUseCases.setDataString(Constants.USER_PROFILE_IMAGE_URL, user.profileImageUrl)
        _dataStoreUseCases.setDataString(Constants.USER_PHONE_NUMBER, user.phoneNumber)
        _dataStoreUseCases.setDataBoolean(Constants.USER_IS_EMAIL_VERIFIED, user.isEmailVerified)
        _dataStoreUseCases.setDataString(Constants.USER_TYPE, user.userType)
        _dataStoreUseCases.setDataString(Constants.USER_UNIVERSITY_ID, user.universityId)
        _dataStoreUseCases.setDouble(Constants.USER_AVERAGE_RATING, user.averageRating)
        _dataStoreUseCases.setDataInt(Constants.USER_TOTAL_REVIEWS, user.totalReviews)
        _dataStoreUseCases.setDataBoolean(Constants.USER_IS_ACTIVE, user.isActive)
    }



    fun resetInitialState() {
        _userResponse.value = null
        _navigateToLogin.value = false
        _isAuthenticationComplete.value = false
    }

    fun assignCurrentUser(user: User) {
        _currentUser.value = user
    }

    private fun getAge(dateString: String): Int {
        return try {
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val birthDate = LocalDate.parse(dateString, formatter)
            val now = LocalDate.now()

            var age = now.year - birthDate.year
            if (now.dayOfYear < birthDate.dayOfYear) {
                age--
            }
            age
        } catch (_: Exception) {
            0
        }
    }

//    fun handleLogout() {
//        _currentUser.value = null
//        _userResponse.value = null
//        _navigateToLogin.value = true
//        _isAuthenticationComplete.value = true
//    }
}