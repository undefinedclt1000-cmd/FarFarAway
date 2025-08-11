// IPropertyRepository.kt - Interfaz del repositorio
package com.undefined.farfaraway.domain.interfaces

import com.undefined.farfaraway.domain.entities.*
import kotlinx.coroutines.flow.Flow

interface IPropertyRepository {

    // Métodos para propiedades
    fun getActiveProperties(): Flow<List<Property>>
    fun getAvailableProperties(): Flow<List<Property>>
    suspend fun getPropertyById(propertyId: String): Property?
    suspend fun addProperty(property: Property): Result<String>
    suspend fun updateProperty(propertyId: String, updates: Map<String, Any>): Result<Unit>
    suspend fun deleteProperty(propertyId: String): Result<Unit>
    suspend fun incrementViewCount(propertyId: String)

    // Métodos de búsqueda
    suspend fun searchProperties(
        query: String,
        priceMin: Double? = null,
        priceMax: Double? = null,
        propertyType: PropertyType? = null,
        maxDistance: Double? = null
    ): List<Property>

    // Métodos de usuario
    suspend fun getCurrentUserType(): UserType
    suspend fun isCurrentUserOwner(): Boolean

    // Métodos de likes
    suspend fun togglePropertyLike(propertyId: String): Result<Boolean>
    suspend fun isPropertyLiked(propertyId: String): Boolean
    fun getUserPropertyLikes(): Flow<Set<String>>

    // Métodos de comentarios
    fun getPropertyComments(propertyId: String): Flow<List<Comment>>
    suspend fun addComment(propertyId: String, content: String, parentCommentId: String? = null): Result<String>
    suspend fun deleteComment(commentId: String, propertyId: String): Result<Unit>
}