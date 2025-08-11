// RentsViewModel.kt - Corregido para manejar mejor los estados de carga
package com.undefined.farfaraway.presentation.features.rents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undefined.farfaraway.core.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.undefined.farfaraway.domain.entities.*
import com.undefined.farfaraway.domain.interfaces.IPropertyRepository
import com.undefined.farfaraway.domain.useCases.dataStore.DataStoreUseCases

@HiltViewModel
class RentsViewModel @Inject constructor(
    private val propertyRepository: IPropertyRepository,
    private val dataStoreUseCases: DataStoreUseCases
): ViewModel(){

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isRefreshing = MutableStateFlow(false) // Nuevo estado para pull-to-refresh
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isSubmittingComment = MutableStateFlow(false)
    val isSubmittingComment: StateFlow<Boolean> = _isSubmittingComment

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isOwner = MutableStateFlow(false)
    val isOwner: StateFlow<Boolean> = _isOwner

    private val _currentUserType = MutableStateFlow<UserType?>(null)
    val currentUserType: StateFlow<UserType?> = _currentUserType

    // Filtros de búsqueda
    private val _priceRange = MutableStateFlow(Pair(0.0, 20000.0))
    val priceRange: StateFlow<Pair<Double, Double>> = _priceRange

    private val _selectedPropertyType = MutableStateFlow<PropertyType?>(null)
    val selectedPropertyType: StateFlow<PropertyType?> = _selectedPropertyType

    private val _maxDistance = MutableStateFlow<Double?>(null)
    val maxDistance: StateFlow<Double?> = _maxDistance

    // Estado para agregar nueva propiedad
    private val _isAddingProperty = MutableStateFlow(false)
    val isAddingProperty: StateFlow<Boolean> = _isAddingProperty

    private val _addPropertyResult = MutableSharedFlow<Result<String>>()
    val addPropertyResult: SharedFlow<Result<String>> = _addPropertyResult

    // Estado para controlar la primera carga
    private val _hasLoadedInitialData = MutableStateFlow(false)

    // Propiedades desde Firebase con filtros aplicados
    val properties: StateFlow<List<Property>> = combine(
        propertyRepository.getAvailableProperties(),
        searchQuery,
        priceRange,
        selectedPropertyType,
        maxDistance
    ) { properties, query, priceRange, propertyType, maxDistance ->

        // Solo actualizar isLoading en la primera carga
        if (!_hasLoadedInitialData.value) {
            _isLoading.value = false
            _hasLoadedInitialData.value = true
        }

        // Limpiar refreshing después de obtener datos
        _isRefreshing.value = false
        _error.value = null

        if (query.isBlank() && propertyType == null && maxDistance == null) {
            properties.filter { it.monthlyRent in priceRange.first..priceRange.second }
        } else {
            properties.filter { property ->
                var matches = true

                if (query.isNotBlank()) {
                    matches = matches && (
                            property.title.contains(query, ignoreCase = true) ||
                                    property.description.contains(query, ignoreCase = true) ||
                                    property.address.contains(query, ignoreCase = true)
                            )
                }

                matches = matches && property.monthlyRent in priceRange.first..priceRange.second

                propertyType?.let { type ->
                    matches = matches && property.propertyType == type.name
                }

                maxDistance?.let { distance ->
                    matches = matches && property.distanceToUniversity <= distance
                }

                matches
            }
        }
    }.catch { exception ->
        _isLoading.value = false
        _isRefreshing.value = false
        _error.value = "Error al cargar propiedades: ${exception.message}"
        emit(emptyList())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Likes del usuario desde Firebase
    val likedProperties: StateFlow<Set<String>> = propertyRepository.getUserPropertyLikes()
        .catch {
            emit(emptySet())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    // Comentarios agrupados por propiedad
    private val _commentsMap = MutableStateFlow<Map<String, List<Comment>>>(emptyMap())
    val commentsMap: StateFlow<Map<String, List<Comment>>> = _commentsMap

    init {
        checkUserType()

        // Cargar comentarios para las propiedades visibles
        viewModelScope.launch {
            properties.collect { propertiesList ->
                propertiesList.forEach { property ->
                    loadCommentsForProperty(property.id)
                }
            }
        }
    }

    private fun checkUserType() {
        viewModelScope.launch {
            try {
                val userType = dataStoreUseCases.getDataString(Constants.USER_TYPE)
                _isOwner.value = userType == UserType.LANDLORD.name
            } catch (e: Exception) {
                _error.value = "Error al verificar tipo de usuario: ${e.message}"
            }
        }
    }


    private fun loadCommentsForProperty(propertyId: String) {
        viewModelScope.launch {
            propertyRepository.getPropertyComments(propertyId)
                .catch { /* Manejar error silenciosamente */ }
                .collect { comments ->
                    val currentMap = _commentsMap.value.toMutableMap()
                    currentMap[propertyId] = comments
                    _commentsMap.value = currentMap
                }
        }
    }

    fun refreshProperties() {
        _isRefreshing.value = true
        _error.value = null
        // El StateFlow se actualizará automáticamente cuando Firebase tenga nuevos datos

        // Timeout de seguridad para evitar loading infinito
        viewModelScope.launch {
            delay(5000) // 5 segundos máximo
            if (_isRefreshing.value) {
                _isRefreshing.value = false
            }
        }
    }

    fun addComment(propertyId: String, content: String) {
        if (content.isBlank()) return

        viewModelScope.launch {
            _isSubmittingComment.value = true
            try {
                val result = propertyRepository.addComment(propertyId, content)
                if (result.isFailure) {
                    _error.value = "Error al agregar comentario: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _error.value = "Error al agregar comentario: ${e.message}"
            } finally {
                _isSubmittingComment.value = false
            }
        }
    }

    fun addReplyToComment(parentCommentId: String, propertyId: String, content: String) {
        if (content.isBlank()) return

        viewModelScope.launch {
            _isSubmittingComment.value = true
            try {
                val result = propertyRepository.addComment(propertyId, content, parentCommentId)
                if (result.isFailure) {
                    _error.value = "Error al agregar respuesta: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _error.value = "Error al agregar respuesta: ${e.message}"
            } finally {
                _isSubmittingComment.value = false
            }
        }
    }

    fun deleteComment(commentId: String, propertyId: String) {
        viewModelScope.launch {
            try {
                val result = propertyRepository.deleteComment(commentId, propertyId)
                if (result.isFailure) {
                    _error.value = "Error al eliminar comentario: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _error.value = "Error al eliminar comentario: ${e.message}"
            }
        }
    }

    fun getCommentsForProperty(propertyId: String): List<Comment> {
        return _commentsMap.value[propertyId] ?: emptyList()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updatePriceRange(minPrice: Double, maxPrice: Double) {
        _priceRange.value = Pair(minPrice, maxPrice)
    }

    fun updateSelectedPropertyType(propertyType: PropertyType?) {
        _selectedPropertyType.value = propertyType
    }

    fun updateMaxDistance(distance: Double?) {
        _maxDistance.value = distance
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _priceRange.value = Pair(0.0, 20000.0)
        _selectedPropertyType.value = null
        _maxDistance.value = null
    }

    // Funciones para búsqueda avanzada
    fun searchProperties(
        query: String,
        priceMin: Double? = null,
        priceMax: Double? = null,
        propertyType: PropertyType? = null,
        maxDistance: Double? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val results = propertyRepository.searchProperties(
                    query = query,
                    priceMin = priceMin,
                    priceMax = priceMax,
                    propertyType = propertyType,
                    maxDistance = maxDistance
                )
                // Los resultados se manejan a través de los filtros locales
                updateSearchQuery(query)
                priceMin?.let { min ->
                    priceMax?.let { max -> updatePriceRange(min, max) }
                }
                updateSelectedPropertyType(propertyType)
                updateMaxDistance(maxDistance)
            } catch (e: Exception) {
                _error.value = "Error en búsqueda: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Incrementar contador de vistas
    fun incrementPropertyViews(propertyId: String) {
        viewModelScope.launch {
            propertyRepository.incrementViewCount(propertyId)
        }
    }

    // Función para formatear tiempo (helper)
    fun getTimeAgo(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < 60000 -> "Hace un momento"
            diff < 3600000 -> "${diff / 60000} min"
            diff < 86400000 -> "${diff / 3600000} h"
            diff < 604800000 -> "${diff / 86400000} d"
            else -> "${diff / 604800000} sem"
        }
    }

    // Función para limpiar errores
    fun clearError() {
        _error.value = null
    }

    // Función para obtener estadísticas de la propiedad
    fun getPropertyStats(): StateFlow<PropertyStats> = properties.map { propertiesList ->
        PropertyStats(
            totalProperties = propertiesList.size,
            availableProperties = propertiesList.count { it.isAvailable },
            averageRent = if (propertiesList.isNotEmpty()) {
                propertiesList.sumOf { it.monthlyRent } / propertiesList.size
            } else 0.0,
            propertyTypeDistribution = propertiesList.groupingBy { it.propertyType }.eachCount()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PropertyStats()
    )

    // Funciones para manejar likes
    fun togglePropertyLike(propertyId: String) {
        viewModelScope.launch {
            try {
                val result = propertyRepository.togglePropertyLike(propertyId)
                if (result.isFailure) {
                    _error.value = "Error al actualizar like: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _error.value = "Error al actualizar like: ${e.message}"
            }
        }
    }

    fun isPropertyLiked(propertyId: String): Boolean {
        return likedProperties.value.contains(propertyId)
    }

    // Funciones para agregar nueva propiedad (solo arrendadores) - CORREGIDA
    fun addProperty(property: Property) {
        if (!_isOwner.value) {
            _error.value = "Solo los arrendadores pueden agregar propiedades"
            return
        }

        viewModelScope.launch {
            _isAddingProperty.value = true
            try {
                val result = propertyRepository.addProperty(property)
                _addPropertyResult.emit(result)

                if (result.isSuccess) {
                    // NO llamar refreshProperties() aquí porque causa el loading infinito
                    // Firebase actualizará automáticamente el StateFlow
                    // Solo limpiamos cualquier error previo
                    _error.value = null
                } else {
                    _error.value = "Error al agregar propiedad: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _error.value = "Error al agregar propiedad: ${e.message}"
                _addPropertyResult.emit(Result.failure(e))
            } finally {
                _isAddingProperty.value = false
            }
        }
    }

    fun updateProperty(propertyId: String, updates: Map<String, Any>) {
        if (!_isOwner.value) {
            _error.value = "Solo los arrendadores pueden modificar propiedades"
            return
        }

        viewModelScope.launch {
            try {
                val result = propertyRepository.updateProperty(propertyId, updates)
                if (result.isFailure) {
                    _error.value = "Error al actualizar propiedad: ${result.exceptionOrNull()?.message}"
                }
                // No llamar refreshProperties() - Firebase se actualiza automáticamente
            } catch (e: Exception) {
                _error.value = "Error al actualizar propiedad: ${e.message}"
            }
        }
    }

    fun deleteProperty(propertyId: String) {
        if (!_isOwner.value) {
            _error.value = "Solo los arrendadores pueden eliminar propiedades"
            return
        }

        viewModelScope.launch {
            try {
                val result = propertyRepository.deleteProperty(propertyId)
                if (result.isFailure) {
                    _error.value = "Error al eliminar propiedad: ${result.exceptionOrNull()?.message}"
                }
                // No llamar refreshProperties() - Firebase se actualiza automáticamente
            } catch (e: Exception) {
                _error.value = "Error al eliminar propiedad: ${e.message}"
            }
        }
    }

    fun validateProperty(property: Property): List<String> {
        val errors = mutableListOf<String>()

        if (property.title.isBlank()) {
            errors.add("El título es obligatorio")
        }
        if (property.description.isBlank()) {
            errors.add("La descripción es obligatoria")
        }
        if (property.address.isBlank()) {
            errors.add("La dirección es obligatoria")
        }
        if (property.monthlyRent <= 0) {
            errors.add("El precio debe ser mayor a 0")
        }
        if (property.maxOccupants <= 0) {
            errors.add("El número de ocupantes debe ser mayor a 0")
        }
        if (property.contactInfo.phoneNumber.isBlank() &&
            property.contactInfo.whatsappNumber.isBlank() &&
            property.contactInfo.email.isBlank()) {
            errors.add("Debe proporcionar al menos un método de contacto")
        }

        return errors
    }
}