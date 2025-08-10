package com.undefined.farfaraway.presentation.features.rents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.undefined.farfaraway.domain.entities.*

@HiltViewModel
class RentsViewModel @Inject constructor(
    // Aquí inyectarías tu repository cuando lo tengas
    // private val propertyRepository: PropertyRepository
): ViewModel(){

    private val _properties = MutableStateFlow<List<Property>>(emptyList())
    val properties: StateFlow<List<Property>> = _properties

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _totalItems = MutableStateFlow(0)
    val totalItems: StateFlow<Int> = _totalItems

    // Estados para likes y comentarios
    private val _likedProperties = MutableStateFlow<Set<String>>(emptySet())
    val likedProperties: StateFlow<Set<String>> = _likedProperties

    private val _comments = MutableStateFlow<Map<String, List<Comment>>>(emptyMap())
    val comments: StateFlow<Map<String, List<Comment>>> = _comments

    private val _isSubmittingComment = MutableStateFlow(false)
    val isSubmittingComment: StateFlow<Boolean> = _isSubmittingComment

    init {
        loadProperties()
        loadLikedProperties()
        loadComments()
    }

    private fun loadProperties() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Aquí cargarías las propiedades desde tu repository
                // Por ahora uso datos de ejemplo
                val sampleProperties = getSampleProperties()
                _properties.value = sampleProperties
                _totalItems.value = sampleProperties.size
            } catch (e: Exception) {
                // Manejar error
                _properties.value = emptyList()
                _totalItems.value = 0
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Datos de ejemplo - reemplaza esto con tu lógica real de carga de datos
    private fun getSampleProperties(): List<Property> {
        return listOf(
            Property(
                id = "1",
                title = "Cuarto privado cerca de la Universidad",
                description = "Hermoso cuarto privado en casa compartida, muy cerca de la universidad. Ambiente tranquilo y seguro, perfecto para estudiantes. Incluye acceso a cocina, sala común y jardín.",
                address = "Av. Universidad 123, Tula de Allende",
                latitude = 20.0536,
                longitude = -99.3420,
                monthlyRent = 3500.0,
                deposit = 3500.0,
                propertyType = PropertyType.PRIVATE_ROOM.name,
                roomType = RoomType.PRIVATE.name,
                maxOccupants = 1,
                currentOccupants = 0,
                amenities = listOf("WiFi", "Cocina equipada", "Jardín", "Estacionamiento", "Área de estudio"),
                images = listOf(
                    PropertyImage(
                        id = "img1",
                        propertyId = "1",
                        imageUrl = "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800",
                        isMainImage = true,
                        caption = "Vista general del cuarto"
                    ),
                    PropertyImage(
                        id = "img2",
                        propertyId = "1",
                        imageUrl = "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=800",
                        isMainImage = false,
                        caption = "Área de descanso"
                    ),
                    PropertyImage(
                        id = "img3",
                        propertyId = "1",
                        imageUrl = "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=800",
                        isMainImage = false,
                        caption = "Cocina compartida"
                    )
                ),
                rules = listOf(
                    "No fumar en áreas comunes",
                    "Respeto por horarios de silencio (10 PM - 7 AM)",
                    "Mantener limpias las áreas compartidas",
                    "No mascotas"
                ),
                isAvailable = true,
                distanceToUniversity = 0.8,
                averageRating = 4.5,
                totalReviews = 12,
                utilities = UtilitiesInfo(
                    electricityIncluded = true,
                    waterIncluded = true,
                    internetIncluded = true,
                    gasIncluded = false,
                    cleaningIncluded = false,
                    additionalCosts = 200.0,
                    notes = "Gas y limpieza se cobran por separado"
                ),
                contactInfo = ContactInfo(
                    phoneNumber = "775-123-4567",
                    whatsappNumber = "775-123-4567",
                    email = "propietario@ejemplo.com",
                    preferredContactMethod = ContactMethod.WHATSAPP.name
                ),
                likesCount = 24,
                commentsCount = 8
            ),
            Property(
                id = "2",
                title = "Departamento completo amueblado",
                description = "Departamento de 2 recámaras completamente amueblado, ideal para estudiantes que buscan comodidad y privacidad. Ubicado en zona segura con fácil acceso al transporte público.",
                address = "Calle Hidalgo 456, Tula de Allende",
                latitude = 20.0590,
                longitude = -99.3380,
                monthlyRent = 8500.0,
                deposit = 8500.0,
                propertyType = PropertyType.APARTMENT.name,
                roomType = RoomType.PRIVATE.name,
                maxOccupants = 3,
                currentOccupants = 1,
                amenities = listOf("WiFi", "Cocina completa", "Lavadora", "TV", "Aire acondicionado", "Estacionamiento"),
                images = listOf(
                    PropertyImage(
                        id = "img4",
                        propertyId = "2",
                        imageUrl = "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800",
                        isMainImage = true,
                        caption = "Sala principal"
                    ),
                    PropertyImage(
                        id = "img5",
                        propertyId = "2",
                        imageUrl = "https://images.unsplash.com/photo-1484154218962-a197022b5858?w=800",
                        isMainImage = false,
                        caption = "Cocina moderna"
                    )
                ),
                rules = listOf(
                    "Máximo 3 personas",
                    "No fiestas",
                    "Horario de visitas hasta las 10 PM",
                    "Mascotas permitidas con depósito adicional"
                ),
                isAvailable = true,
                distanceToUniversity = 1.2,
                averageRating = 4.8,
                totalReviews = 23,
                utilities = UtilitiesInfo(
                    electricityIncluded = false,
                    waterIncluded = true,
                    internetIncluded = true,
                    gasIncluded = true,
                    cleaningIncluded = false,
                    additionalCosts = 800.0,
                    notes = "Electricidad por separado, aproximadamente $800 mensuales"
                ),
                contactInfo = ContactInfo(
                    phoneNumber = "775-987-6543",
                    whatsappNumber = "775-987-6543",
                    email = "depto.tula@ejemplo.com",
                    preferredContactMethod = ContactMethod.WHATSAPP.name
                ),
                likesCount = 45,
                commentsCount = 15
            ),
            Property(
                id = "3",
                title = "Casa compartida para estudiantes",
                description = "Casa grande con 4 habitaciones individuales, perfecta para estudiantes. Ambiente familiar y seguro. Incluye todas las amenidades necesarias para una estancia cómoda.",
                address = "Calle Morelos 789, Tula de Allende",
                latitude = 20.0520,
                longitude = -99.3450,
                monthlyRent = 2800.0,
                deposit = 2800.0,
                propertyType = PropertyType.SHARED_ROOM.name,
                roomType = RoomType.SHARED.name,
                maxOccupants = 4,
                currentOccupants = 2,
                amenities = listOf("WiFi", "Cocina grande", "Sala común", "Jardín", "Lavandería", "Área de estudio"),
                images = listOf(
                    PropertyImage(
                        id = "img6",
                        propertyId = "3",
                        imageUrl = "https://images.unsplash.com/photo-1493809842364-78817add7ffb?w=800",
                        isMainImage = true,
                        caption = "Fachada de la casa"
                    ),
                    PropertyImage(
                        id = "img7",
                        propertyId = "3",
                        imageUrl = "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=800",
                        isMainImage = false,
                        caption = "Habitación tipo"
                    )
                ),
                rules = listOf(
                    "Convivencia respetuosa entre compañeros",
                    "Limpieza rotativa de áreas comunes",
                    "No ruido después de las 11 PM",
                    "Visitas con previo aviso"
                ),
                isAvailable = true,
                distanceToUniversity = 0.5,
                averageRating = 4.2,
                totalReviews = 18,
                utilities = UtilitiesInfo(
                    electricityIncluded = true,
                    waterIncluded = true,
                    internetIncluded = true,
                    gasIncluded = true,
                    cleaningIncluded = true,
                    additionalCosts = 0.0,
                    notes = "Todos los servicios incluidos"
                ),
                contactInfo = ContactInfo(
                    phoneNumber = "775-555-0123",
                    whatsappNumber = "775-555-0123",
                    email = "casa.estudiantes@ejemplo.com",
                    preferredContactMethod = ContactMethod.PHONE.name
                ),
                likesCount = 18,
                commentsCount = 9
            ),
            Property(
                id = "4",
                title = "Estudio moderno cerca del centro",
                description = "Estudio completamente equipado y moderno, perfecto para una persona. Ubicación privilegiada cerca del centro de Tula con fácil acceso a servicios y transporte.",
                address = "Av. 5 de Mayo 321, Centro, Tula de Allende",
                latitude = 20.0570,
                longitude = -99.3400,
                monthlyRent = 4200.0,
                deposit = 4200.0,
                propertyType = PropertyType.STUDIO.name,
                roomType = RoomType.SINGLE.name,
                maxOccupants = 1,
                currentOccupants = 0,
                amenities = listOf("WiFi", "Kitchenette", "Aire acondicionado", "TV", "Escritorio", "Clóset empotrado"),
                images = listOf(
                    PropertyImage(
                        id = "img8",
                        propertyId = "4",
                        imageUrl = "https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800",
                        isMainImage = true,
                        caption = "Vista general del estudio"
                    )
                ),
                rules = listOf(
                    "Una sola persona",
                    "No mascotas",
                    "Cuidado del mobiliario",
                    "Visitas ocasionales permitidas"
                ),
                isAvailable = false,
                distanceToUniversity = 1.5,
                averageRating = 4.6,
                totalReviews = 8,
                utilities = UtilitiesInfo(
                    electricityIncluded = false,
                    waterIncluded = true,
                    internetIncluded = true,
                    gasIncluded = true,
                    cleaningIncluded = false,
                    additionalCosts = 400.0,
                    notes = "Electricidad por medidor individual"
                ),
                contactInfo = ContactInfo(
                    phoneNumber = "775-444-5566",
                    whatsappNumber = "775-444-5566",
                    email = "estudio.centro@ejemplo.com",
                    preferredContactMethod = ContactMethod.EMAIL.name
                ),
                likesCount = 31,
                commentsCount = 12
            )
        )
    }

    fun refreshProperties() {
        loadProperties()
    }

    // Funciones para manejar likes
    private fun loadLikedProperties() {
        // Aquí cargarías los likes del usuario actual desde tu repository
        // Por ahora simulo algunos likes
        _likedProperties.value = setOf("1", "3")
    }

    fun togglePropertyLike(propertyId: String) {
        viewModelScope.launch {
            try {
                val currentLikes = _likedProperties.value.toMutableSet()
                val updatedProperties = _properties.value.toMutableList()
                val propertyIndex = updatedProperties.indexOfFirst { it.id == propertyId }

                if (propertyIndex != -1) {
                    val property = updatedProperties[propertyIndex]

                    if (currentLikes.contains(propertyId)) {
                        // Quitar like
                        currentLikes.remove(propertyId)
                        updatedProperties[propertyIndex] = property.copy(
                            likesCount = maxOf(0, property.likesCount - 1)
                        )
                        // Aquí harías la llamada al repository para quitar el like
                    } else {
                        // Agregar like
                        currentLikes.add(propertyId)
                        updatedProperties[propertyIndex] = property.copy(
                            likesCount = property.likesCount + 1
                        )
                        // Aquí harías la llamada al repository para agregar el like
                    }

                    _likedProperties.value = currentLikes
                    _properties.value = updatedProperties
                }
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }

    // Funciones para manejar comentarios
    private fun loadComments() {
        // Aquí cargarías los comentarios desde tu repository
        // Por ahora simulo algunos comentarios
        val sampleComments = mapOf(
            "1" to listOf(
                Comment(
                    id = "c1",
                    userId = "user1",
                    propertyId = "1",
                    content = "Excelente lugar, muy limpio y cómodo. Lo recomiendo mucho!",
                    likes = 5,
                    userName = "María García",
                    userImage = "https://images.unsplash.com/photo-1494790108755-2616b612b47c?w=100"
                ),
                Comment(
                    id = "c2",
                    userId = "user2",
                    propertyId = "1",
                    content = "La ubicación es perfecta para estudiantes.",
                    likes = 2,
                    userName = "Carlos López",
                    userImage = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100"
                )
            ),
            "2" to listOf(
                Comment(
                    id = "c3",
                    userId = "user3",
                    propertyId = "2",
                    content = "Muy espacioso y bien equipado!",
                    likes = 3,
                    userName = "Ana Ruiz",
                    userImage = "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100"
                )
            )
        )
        _comments.value = sampleComments
    }

    fun addComment(propertyId: String, content: String) {
        if (content.isBlank()) return

        viewModelScope.launch {
            _isSubmittingComment.value = true
            try {
                val newComment = Comment(
                    id = "temp_${System.currentTimeMillis()}",
                    userId = "current_user", // ID del usuario actual
                    propertyId = propertyId,
                    content = content.trim(),
                    likes = 0,
                    userName = "Usuario Actual", // Nombre del usuario actual
                    userImage = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100"
                )

                val currentComments = _comments.value.toMutableMap()
                val propertyComments = currentComments[propertyId]?.toMutableList() ?: mutableListOf()
                propertyComments.add(0, newComment) // Agregar al inicio
                currentComments[propertyId] = propertyComments
                _comments.value = currentComments

                // Actualizar contador de comentarios en la propiedad
                val updatedProperties = _properties.value.toMutableList()
                val propertyIndex = updatedProperties.indexOfFirst { it.id == propertyId }
                if (propertyIndex != -1) {
                    val property = updatedProperties[propertyIndex]
                    updatedProperties[propertyIndex] = property.copy(
                        commentsCount = property.commentsCount + 1
                    )
                    _properties.value = updatedProperties
                }

                // Aquí harías la llamada al repository para guardar el comentario

            } catch (e: Exception) {
                // Manejar error
            } finally {
                _isSubmittingComment.value = false
            }
        }
    }

    fun getCommentsForProperty(propertyId: String): List<Comment> {
        return _comments.value[propertyId] ?: emptyList()
    }

    fun isPropertyLiked(propertyId: String): Boolean {
        return _likedProperties.value.contains(propertyId)
    }

    // Estados para likes en comentarios
    private val _likedComments = MutableStateFlow<Set<String>>(emptySet())
    val likedComments: StateFlow<Set<String>> = _likedComments

    // Función para manejar likes en comentarios
    fun toggleCommentLike(commentId: String) {
        viewModelScope.launch {
            try {
                val currentLikes = _likedComments.value.toMutableSet()
                val currentComments = _comments.value.toMutableMap()

                // Buscar el comentario en todas las propiedades
                var foundComment: Comment? = null
                var propertyId: String? = null

                for ((propId, commentsList) in currentComments) {
                    val commentIndex = commentsList.indexOfFirst { it.id == commentId }
                    if (commentIndex != -1) {
                        foundComment = commentsList[commentIndex]
                        propertyId = propId
                        break
                    }
                }

                if (foundComment != null && propertyId != null) {
                    val updatedComments = currentComments[propertyId]!!.toMutableList()
                    val commentIndex = updatedComments.indexOfFirst { it.id == commentId }

                    if (currentLikes.contains(commentId)) {
                        // Quitar like
                        currentLikes.remove(commentId)
                        updatedComments[commentIndex] = foundComment.copy(
                            likes = maxOf(0, foundComment.likes - 1)
                        )
                    } else {
                        // Agregar like
                        currentLikes.add(commentId)
                        updatedComments[commentIndex] = foundComment.copy(
                            likes = foundComment.likes + 1
                        )
                    }

                    currentComments[propertyId] = updatedComments
                    _likedComments.value = currentLikes
                    _comments.value = currentComments
                }
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }

    fun isCommentLiked(commentId: String): Boolean {
        return _likedComments.value.contains(commentId)
    }

    // Función para agregar respuestas a comentarios (futuro)
    fun addReplyToComment(parentCommentId: String, propertyId: String, content: String) {
        if (content.isBlank()) return

        viewModelScope.launch {
            _isSubmittingComment.value = true
            try {
                val newReply = Comment(
                    id = "reply_${System.currentTimeMillis()}",
                    userId = "current_user",
                    propertyId = propertyId,
                    content = content.trim(),
                    parentCommentId = parentCommentId,
                    likes = 0,
                    userName = "Usuario Actual",
                    userImage = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100"
                )

                val currentComments = _comments.value.toMutableMap()
                val propertyComments = currentComments[propertyId]?.toMutableList() ?: mutableListOf()

                // Encontrar la posición después del comentario padre para insertar la respuesta
                val parentIndex = propertyComments.indexOfFirst { it.id == parentCommentId }
                if (parentIndex != -1) {
                    propertyComments.add(parentIndex + 1, newReply)
                } else {
                    propertyComments.add(0, newReply)
                }

                currentComments[propertyId] = propertyComments
                _comments.value = currentComments

                // Actualizar contador de comentarios en la propiedad
                val updatedProperties = _properties.value.toMutableList()
                val propertyIndex = updatedProperties.indexOfFirst { it.id == propertyId }
                if (propertyIndex != -1) {
                    val property = updatedProperties[propertyIndex]
                    updatedProperties[propertyIndex] = property.copy(
                        commentsCount = property.commentsCount + 1
                    )
                    _properties.value = updatedProperties
                }

            } catch (e: Exception) {
                // Manejar error
            } finally {
                _isSubmittingComment.value = false
            }
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
}