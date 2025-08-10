package com.undefined.farfaraway.presentation.features.profile.myprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undefined.farfaraway.core.Constants
import com.undefined.farfaraway.domain.entities.Review
import com.undefined.farfaraway.domain.entities.ReviewType
import com.undefined.farfaraway.domain.entities.User
import com.undefined.farfaraway.domain.useCases.dataStore.DataStoreUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val dataStoreUseCases: DataStoreUseCases
    // Aquí puedes agregar más use cases cuando implementes la funcionalidad de reseñas
    // private val reviewsUseCases: ReviewsUseCases
): ViewModel() {

    // Estado del usuario
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Estado de las reseñas del usuario - usando el modelo Review directamente
    private val _userReviews = MutableStateFlow<List<Review>>(emptyList())
    val userReviews: StateFlow<List<Review>> = _userReviews.asStateFlow()

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Estado de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadUserData()
        loadUserReviews()
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
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar los datos del usuario: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadUserReviews() {
        viewModelScope.launch {
            try {
                // Por ahora usamos datos de ejemplo con el modelo Review
                _userReviews.value = getSampleReviews()

                // Código para cuando implementes la funcionalidad real:
                /*
                val userId = dataStoreUseCases.getDataString(Constants.USER_UID)
                if (userId.isNotEmpty()) {
                    reviewsUseCases.getUserReviews(userId).collect { response ->
                        when (response) {
                            is Response.Loading -> {
                                // Manejar estado de carga si es necesario
                            }
                            is Response.Success -> {
                                _userReviews.value = response.data ?: emptyList()
                            }
                            is Response.Error -> {
                                _errorMessage.value = "Error al cargar reseñas: ${response.exception?.message}"
                            }
                        }
                    }
                }
                */
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar las reseñas: ${e.message}"
            }
        }
    }

    // Datos de ejemplo para las reseñas usando el modelo Review
    private fun getSampleReviews(): List<Review> {
        return listOf(
            Review(
                id = "review_1",
                reviewerId = "reviewer_1",
                reviewedUserId = _currentUser.value?.id ?: "",
                propertyId = "property_1",
                rating = 5,
                title = "Excelente compañera de cuarto",
                content = "Muy ordenada y respetuosa. Siempre mantiene los espacios comunes limpios y es muy considerada con los horarios.",
                reviewType = ReviewType.ROOMMATE.name,
                isVerified = true,
                reviewerName = "María García",
                reviewerImage = ""
            ),
            Review(
                id = "review_2",
                reviewerId = "reviewer_2",
                reviewedUserId = _currentUser.value?.id ?: "",
                propertyId = "property_2",
                rating = 4,
                title = "Buena experiencia de convivencia",
                content = "Muy buena experiencia viviendo con ella. Es responsable con los pagos y muy comunicativa. Solo le falta un poco más de organización en la cocina.",
                reviewType = ReviewType.ROOMMATE.name,
                isVerified = false,
                reviewerName = "Juan Pérez",
                reviewerImage = ""
            ),
            Review(
                id = "review_3",
                reviewerId = "reviewer_3",
                reviewedUserId = _currentUser.value?.id ?: "",
                propertyId = "property_1",
                rating = 5,
                title = "Totalmente recomendable",
                content = "La recomiendo completamente! Es una persona súper confiable y amigable. Hace que convivir sea muy fácil y agradable.",
                reviewType = ReviewType.ROOMMATE.name,
                isVerified = true,
                reviewerName = "Ana López",
                reviewerImage = ""
            )
        )
    }

    fun refreshData() {
        loadUserData()
        loadUserReviews()
    }

    fun clearError() {
        _errorMessage.value = null
    }
}