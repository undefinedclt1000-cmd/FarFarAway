package com.undefined.farfaraway.presentation.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undefined.farfaraway.core.Constants
import com.undefined.farfaraway.domain.entities.*
import com.undefined.farfaraway.domain.repository.PropertyRepository
import com.undefined.farfaraway.domain.useCases.dataStore.DataStoreUseCases
import com.undefined.farfaraway.presentation.shared.navigation.enums.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataStoreUseCases: DataStoreUseCases,
    // Aquí irían tus repositorios cuando los implementes
    private val propertyRepository: PropertyRepository,
    // private val routeRepository: RouteRepository,
    // private val notificationRepository: NotificationRepository,
    // private val financialRepository: FinancialRepository,
): ViewModel(){

    // Estados para UI
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _featuredProperties = MutableStateFlow<List<Property>>(emptyList())
    val featuredProperties: StateFlow<List<Property>> = _featuredProperties.asStateFlow()

    private val _popularRoutes = MutableStateFlow<List<TransportRoute>>(emptyList())
    val popularRoutes: StateFlow<List<TransportRoute>> = _popularRoutes.asStateFlow()

    private val _recentNotifications = MutableStateFlow<List<Notification>>(emptyList())
    val recentNotifications: StateFlow<List<Notification>> = _recentNotifications.asStateFlow()

    private val _financialSummary = MutableStateFlow<FinancialSummary?>(null)
    val financialSummary: StateFlow<FinancialSummary?> = _financialSummary.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _totalItems = MutableStateFlow(0)
    val totalItems: StateFlow<Int> = _totalItems


     fun loadHomeData() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                loadUserData()
                loadFeaturedProperties()
                loadPopularRoutes()
                loadRecentNotifications()
                loadFinancialSummary()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    hasError = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    hasError = true,
                    errorMessage = e.message ?: "Error desconocido"
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

   private suspend fun loadFeaturedProperties() {
        // Simulación de datos - reemplazar con llamada real
       try {
           propertyRepository.getAvailableProperties()
               .take(1) // Solo tomamos la primera emisión para evitar actualizaciones continuas
               .collect { allProperties ->
                   val filteredProperties = allProperties
                       .filter { it.monthlyRent <= 3000.0 }  // Filtrar por precio <= 3000
                       .take(3)  // Tomar solo las primeras 3

                   _featuredProperties.value = filteredProperties
               }
       } catch (e: Exception) {
           // En caso de error, dejar la lista vacía
           _featuredProperties.value = emptyList()
       }
    }

    private suspend fun loadPopularRoutes() {
        val routes = listOf(
            TransportRoute(
                id = "1",
                routeName = "Ruta 15",
                routeNumber = "15",
                origin = Location(20.1000, -98.1000, "Centro", "Centro"),
                destination = Location(20.1234, -98.1234, "UTTT", "Universidad Tecnológica"),
                fare = 12.0,
                estimatedDuration = 25,
                transportType = TransportType.BUS.name,
                isActive = true
            ),
            TransportRoute(
                id = "2",
                routeName = "Ruta 8",
                routeNumber = "8",
                origin = Location(20.1100, -98.1100, "Col. Juárez", "Colonia Juárez"),
                destination = Location(20.1234, -98.1234, "Universidad", "Campus Universitario"),
                fare = 10.0,
                estimatedDuration = 20,
                transportType = TransportType.BUS.name,
                isActive = true
            ),
            TransportRoute(
                id = "3",
                routeName = "Micro Azul",
                routeNumber = "MA1",
                origin = Location(20.0900, -98.0900, "Terminal", "Terminal de Autobuses"),
                destination = Location(20.1234, -98.1234, "Campus", "Campus UTTT"),
                fare = 8.0,
                estimatedDuration = 15,
                transportType = TransportType.MICRO.name,
                isActive = true
            )
        )
        _popularRoutes.value = routes
    }

    private suspend fun loadRecentNotifications() {
        val notifications = listOf(
            Notification(
                id = "1",
                userId = "user123",
                title = "Nueva propiedad disponible",
                message = "Se agregó un cuarto cerca de tu universidad que podría interesarte",
                type = NotificationType.NEW_PROPERTY.name,
                relatedEntityId = "property123",
                isRead = false,
                createdAt = System.currentTimeMillis() - 3600000, // 1 hora atrás
                priority = NotificationPriority.NORMAL.value,
                category = NotificationCategory.PROPERTIES.name
            ),
            Notification(
                id = "2",
                userId = "user123",
                title = "Resumen de gastos semanal",
                message = "Ya está disponible tu resumen de gastos de esta semana",
                type = NotificationType.WEEKLY_EXPENSES.name,
                isRead = true,
                createdAt = System.currentTimeMillis() - 7200000, // 2 horas atrás
                priority = NotificationPriority.NORMAL.value,
                category = NotificationCategory.EXPENSES.name
            ),
            Notification(
                id = "3",
                userId = "user123",
                title = "Actualización de ruta",
                message = "La ruta 15 tiene nuevos horarios disponibles",
                type = NotificationType.ROUTE_UPDATE.name,
                relatedEntityId = "route15",
                isRead = false,
                createdAt = System.currentTimeMillis() - 10800000, // 3 horas atrás
                priority = NotificationPriority.HIGH.value,
                category = NotificationCategory.ROUTES.name
            )
        )
        _recentNotifications.value = notifications
    }

    private suspend fun loadFinancialSummary() {
        val summary = FinancialSummary(
            id = "summary_current",
            userId = "user123",
            period = BudgetPeriod.MONTHLY.name,
            totalIncome = 3500.0,
            totalExpenses = 2100.0,
            remainingBudget = 1400.0,
            expensesByCategory = mapOf(
                ExpenseCategory.RENT.name to 1200.0,
                ExpenseCategory.FOOD.name to 500.0,
                ExpenseCategory.TRANSPORTATION.name to 250.0,
                ExpenseCategory.ENTERTAINMENT.name to 150.0
            ),
            budgetStatus = BudgetStatus.ON_TRACK.name,
            recommendations = listOf(
                "Vas muy bien con tus gastos este mes",
                "Considera ahorrar un poco más en entretenimiento"
            )
        )
        _financialSummary.value = summary
    }


    private fun loadUserData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val id = dataStoreUseCases.getDataString(Constants.USER_UID)

                if (id.isEmpty()) {
                    println("No user data found in DataStore")
                    _user.value = null
                    return@launch
                }

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
                    println(
                        "User data loaded: " +
                                "id: $id, " +
                                "firstName: $firstName, " +
                                "lastName: $lastName, " +
                                "email: $email, " +
                                "age: $age, " +
                                "profileImageUrl: $profileImageUrl, " +
                                "phoneNumber: $phoneNumber, " +
                                "isEmailVerified: $isEmailVerified, " +
                                "userType: $userType, " +
                                "universityId: $universityId" +
                                "averageRating: $averageRating, " +
                                "totalReviews: $totalReviews, " +
                                "isActive: $isActive"

                    )
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
                    _user.value = user
                } else {
                    _user.value = null
                }

                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                // Manejar error si quieres
            }
        }
    }

    // Funciones para manejar eventos de UI
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onSearchClick() {
        // Implementar lógica de búsqueda
        viewModelScope.launch {
            // Navegar a pantalla de búsqueda o filtrar contenido
        }
    }

    fun onCategoryClick(category: QuickCategory) {
        viewModelScope.launch {
            // Manejar navegación según categoría
        }
    }

    fun onPropertyClick(propertyId: String) {
        viewModelScope.launch {
            // Navegar a detalles de propiedad
        }
    }

    fun onRouteClick(routeId: String) {
        viewModelScope.launch {
            // Navegar a detalles de ruta
        }
    }

    fun onNotificationClick(notificationId: String) {
        viewModelScope.launch {
            // Marcar como leída y navegar si es necesario
            markNotificationAsRead(notificationId)
        }
    }

    fun onRefresh() {
        loadHomeData()
    }

    private fun markNotificationAsRead(notificationId: String) {
        viewModelScope.launch {
            val updatedNotifications = _recentNotifications.value.map { notification ->
                if (notification.id == notificationId) {
                    notification.copy(isRead = true)
                } else {
                    notification
                }
            }
            _recentNotifications.value = updatedNotifications
        }
    }

    fun getGreetingMessage(): String {
        val user = _user.value
        val firstName = user?.firstName ?: "Usuario"

        return when (java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)) {
            in 5..11 -> "¡Buenos días, $firstName!"
            in 12..17 -> "¡Buenas tardes, $firstName!"
            else -> "¡Buenas noches, $firstName!"
        }
    }
}

// Estado de UI para la pantalla Home
data class HomeUiState(
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String = ""
)

// Enum para categorías rápidas
enum class QuickCategory(
    val title: String,
    val route: String // Para navegación
) {
    ROOMS("Cuartos", Routes.RENTS.name),
    ROUTES("Rutas", Routes.ROUTES.name),
    SHOPPING("Compras", Routes.BILLS.name), // ejemplo, depende de a dónde quieras que vaya
    FINANCES("Finanzas", Routes.FINANCE.name),
    PROFILES("Perfiles", Routes.PROFILE.name)
}