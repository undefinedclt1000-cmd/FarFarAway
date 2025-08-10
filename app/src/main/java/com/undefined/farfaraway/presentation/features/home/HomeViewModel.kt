package com.undefined.farfaraway.presentation.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undefined.farfaraway.domain.entities.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    // Aquí irían tus repositorios cuando los implementes
    // private val propertyRepository: PropertyRepository,
    // private val routeRepository: RouteRepository,
    // private val notificationRepository: NotificationRepository,
    // private val financialRepository: FinancialRepository
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

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                // Simular carga de datos - reemplazar con llamadas reales a repositorios
                loadFeaturedProperties()
                loadPopularRoutes()
                loadRecentNotifications()
                loadFinancialSummary()
                loadUserProfile()

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
        val properties = listOf(
            Property(
                id = "1",
                title = "Cuarto privado cerca de UTTT",
                description = "Cuarto amplio con baño privado, muy cerca de la universidad",
                address = "Calle Universidad #123",
                latitude = 20.1234,
                longitude = -98.1234,
                monthlyRent = 2500.0,
                deposit = 2500.0,
                propertyType = PropertyType.PRIVATE_ROOM.name,
                roomType = RoomType.PRIVATE.name,
                maxOccupants = 1,
                currentOccupants = 0,
                amenities = listOf("WIFI", "PARKING", "LAUNDRY"),
                isAvailable = true,
                distanceToUniversity = 0.5,
                averageRating = 4.5,
                totalReviews = 12,
                likesCount = 25,
                commentsCount = 8
            ),
            Property(
                id = "2",
                title = "Departamento compartido",
                description = "Departamento de 3 recámaras para compartir con estudiantes",
                address = "Av. Revolución #456",
                latitude = 20.1345,
                longitude = -98.1345,
                monthlyRent = 1800.0,
                deposit = 1800.0,
                propertyType = PropertyType.SHARED_ROOM.name,
                roomType = RoomType.SHARED.name,
                maxOccupants = 3,
                currentOccupants = 2,
                amenities = listOf("WIFI", "KITCHEN", "LAUNDRY"),
                isAvailable = true,
                distanceToUniversity = 1.2,
                averageRating = 4.2,
                totalReviews = 18,
                likesCount = 15,
                commentsCount = 12
            ),
            Property(
                id = "3",
                title = "Casa completa",
                description = "Casa completa ideal para grupo de amigos",
                address = "Col. Centro #789",
                latitude = 20.1456,
                longitude = -98.1456,
                monthlyRent = 4000.0,
                deposit = 4000.0,
                propertyType = PropertyType.HOUSE.name,
                roomType = RoomType.PRIVATE.name,
                maxOccupants = 4,
                currentOccupants = 0,
                amenities = listOf("WIFI", "PARKING", "GARDEN", "FURNISHED"),
                isAvailable = true,
                distanceToUniversity = 0.8,
                averageRating = 4.8,
                totalReviews = 6,
                likesCount = 32,
                commentsCount = 5
            )
        )
        _featuredProperties.value = properties
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

    private suspend fun loadUserProfile() {
        val user = User(
            id = "user123",
            firstName = "Juan",
            lastName = "Pérez",
            email = "juan.perez@uttt.edu.mx",
            age = 20,
            profileImageUrl = "",
            phoneNumber = "7771234567",
            isEmailVerified = true,
            userType = UserType.STUDENT.name,
            universityId = "uttt",
            averageRating = 4.3,
            totalReviews = 8,
            isActive = true
        )
        _user.value = user
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
    ROOMS("Cuartos", "rooms"),
    ROUTES("Rutas", "routes"),
    SHOPPING("Compras", "shopping"),
    FINANCES("Finanzas", "finances"),
    PROFILES("Perfiles", "profiles")
}