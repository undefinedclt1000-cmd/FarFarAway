// PropertyRepository.kt - Corregido para usar snake_case como en tus entidades
package com.undefined.farfaraway.domain.repository

import com.google.firebase.database.*
import com.google.firebase.auth.FirebaseAuth
import com.undefined.farfaraway.domain.entities.*
import com.undefined.farfaraway.domain.interfaces.IPropertyRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PropertyRepository @Inject constructor(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
) : IPropertyRepository {

    private val propertiesRef = database.getReference("properties")
    private val likesRef = database.getReference("likes")
    private val commentsRef = database.getReference("comments")
    private val usersRef = database.getReference("users")

    // Obtener todas las propiedades activas
    override fun getActiveProperties(): Flow<List<Property>> = callbackFlow {
        val query = propertiesRef
            .orderByChild("is_active")  // Usar snake_case como en tus entidades
            .equalTo(true)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val properties = mutableListOf<Property>()

                snapshot.children.forEach { childSnapshot ->
                    try {
                        val property = childSnapshot.getValue(Property::class.java)
                        property?.let {
                            // Asignar el ID desde Firebase
                            properties.add(it.copy(id = childSnapshot.key ?: ""))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                trySend(properties.sortedByDescending { it.createdAt })
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }

    // Obtener propiedades disponibles
    override fun getAvailableProperties(): Flow<List<Property>> = callbackFlow {
        val query = propertiesRef
            .orderByChild("is_available")  // Usar snake_case
            .equalTo(true)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val properties = mutableListOf<Property>()

                snapshot.children.forEach { childSnapshot ->
                    try {
                        val property = childSnapshot.getValue(Property::class.java)
                        if (property != null && property.isAvailable) {
                            properties.add(property.copy(id = childSnapshot.key ?: ""))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                trySend(properties.sortedByDescending { it.createdAt })
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }

    // Obtener propiedad por ID
    override suspend fun getPropertyById(propertyId: String): Property? {
        return try {
            val snapshot = propertiesRef.child(propertyId).get().await()
            val property = snapshot.getValue(Property::class.java)
            property?.copy(id = propertyId)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Agregar nueva propiedad
    override suspend fun addProperty(property: Property): Result<String> {
        return try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                ?: return Result.failure(Exception("Usuario no autenticado"))

            val propertyId = propertiesRef.push().key
                ?: return Result.failure(Exception("No se pudo generar ID"))

            val propertyWithId = property.copy(
                id = propertyId,
                ownerId = userId,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                isAvailable = true
            )

            propertiesRef.child(propertyId).setValue(propertyWithId).await()
            Result.success(propertyId)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // Actualizar propiedad
    override suspend fun updateProperty(propertyId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            val updatesWithTimestamp = updates.toMutableMap()
            updatesWithTimestamp["updated_at"] = System.currentTimeMillis()  // snake_case

            propertiesRef.child(propertyId).updateChildren(updatesWithTimestamp).await()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // Eliminar propiedad (soft delete)
    override suspend fun deleteProperty(propertyId: String): Result<Unit> {
        return try {
            val updates = mapOf(
                "is_available" to false,   // snake_case
                "updated_at" to System.currentTimeMillis()
            )
            propertiesRef.child(propertyId).updateChildren(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // Incrementar contador de vistas
    override suspend fun incrementViewCount(propertyId: String) {
        try {
            val propertyRef = propertiesRef.child(propertyId)
            val snapshot = propertyRef.get().await()
            val currentViews = snapshot.child("view_count").getValue(Int::class.java) ?: 0  // snake_case
            propertyRef.child("view_count").setValue(currentViews + 1).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Buscar propiedades
    override suspend fun searchProperties(
        query: String,
        priceMin: Double?,
        priceMax: Double?,
        propertyType: PropertyType?,
        maxDistance: Double?
    ): List<Property> {
        return try {
            val snapshot = propertiesRef
                .orderByChild("is_available")  // snake_case
                .equalTo(true)
                .get()
                .await()

            val allProperties = mutableListOf<Property>()
            snapshot.children.forEach { childSnapshot ->
                try {
                    val property = childSnapshot.getValue(Property::class.java)
                    property?.let {
                        allProperties.add(it.copy(id = childSnapshot.key ?: ""))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Aplicar filtros
            allProperties.filter { property ->
                var matches = true

                if (query.isNotBlank()) {
                    matches = matches && (
                            property.title.contains(query, ignoreCase = true) ||
                                    property.description.contains(query, ignoreCase = true) ||
                                    property.address.contains(query, ignoreCase = true)
                            )
                }

                priceMin?.let { min -> matches = matches && property.monthlyRent >= min }
                priceMax?.let { max -> matches = matches && property.monthlyRent <= max }
                propertyType?.let { type -> matches = matches && property.propertyType == type.name }
                maxDistance?.let { distance -> matches = matches && property.distanceToUniversity <= distance }

                matches
            }.sortedByDescending { it.createdAt }

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // Verificar tipo de usuario actual
    override suspend fun getCurrentUserType(): UserType {
        return try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return UserType.STUDENT
            val snapshot = usersRef.child(userId).get().await()
            val user = snapshot.getValue(User::class.java)

            // Convertir string a enum
            when (user?.userType) {
                "LANDLORD" -> UserType.LANDLORD
                "ADMIN" -> UserType.ADMIN
                else -> UserType.STUDENT
            }
        } catch (e: Exception) {
            e.printStackTrace()
            UserType.STUDENT
        }
    }

    // Verificar si el usuario actual es arrendador
    override suspend fun isCurrentUserOwner(): Boolean {
        return try {
            getCurrentUserType() == UserType.LANDLORD  // Cambio: usar LANDLORD en lugar de OWNER
        } catch (e: Exception) {
            false
        }
    }

    // Alternar like de propiedad
    override suspend fun togglePropertyLike(propertyId: String): Result<Boolean> {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
            ?: return Result.failure(Exception("Usuario no autenticado"))

        return try {
            val likeId = "${userId}_${propertyId}"
            val likeRef = likesRef.child("properties").child(likeId)
            val propertyRef = propertiesRef.child(propertyId)

            val likeSnapshot = likeRef.get().await()
            val isLiked = likeSnapshot.exists()

            if (isLiked) {
                // Quitar like
                likeRef.removeValue().await()

                val propertySnapshot = propertyRef.get().await()
                val currentLikes = propertySnapshot.child("like_count").getValue(Int::class.java) ?: 0  // snake_case
                propertyRef.child("like_count").setValue(maxOf(0, currentLikes - 1)).await()

                Result.success(false)
            } else {
                // Agregar like
                val like = PropertyLike(  // Usar PropertyLike de tus entidades
                    id = likeId,
                    userId = userId,
                    propertyId = propertyId
                )
                likeRef.setValue(like).await()

                val propertySnapshot = propertyRef.get().await()
                val currentLikes = propertySnapshot.child("like_count").getValue(Int::class.java) ?: 0
                propertyRef.child("like_count").setValue(currentLikes + 1).await()

                Result.success(true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // Verificar si la propiedad tiene like del usuario
    override suspend fun isPropertyLiked(propertyId: String): Boolean {
        val userId = auth.currentUser?.uid ?: return false

        return try {
            val likeId = "${userId}_${propertyId}"
            val snapshot = likesRef.child("properties").child(likeId).get().await()
            snapshot.exists()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Obtener likes del usuario actual
    override fun getUserPropertyLikes(): Flow<Set<String>> = callbackFlow {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        if (userId == null) {
            trySend(emptySet())
            close()
            return@callbackFlow
        }

        val query = likesRef.child("properties")
            .orderByChild("user_id")  // snake_case
            .equalTo(userId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val likedPropertyIds = mutableSetOf<String>()

                snapshot.children.forEach { childSnapshot ->
                    try {
                        val like = childSnapshot.getValue(PropertyLike::class.java)
                        like?.propertyId?.let { propertyId ->
                            likedPropertyIds.add(propertyId)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                trySend(likedPropertyIds)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }

    // Obtener comentarios de propiedad
    override fun getPropertyComments(propertyId: String): Flow<List<Comment>> = callbackFlow {
        val query = commentsRef.child("properties").child(propertyId)
            .orderByChild("created_at")  // snake_case

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val comments = mutableListOf<Comment>()

                snapshot.children.forEach { childSnapshot ->
                    try {
                        val comment = childSnapshot.getValue(Comment::class.java)
                        comment?.let {
                            comments.add(it.copy(id = childSnapshot.key ?: ""))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                val sortedComments = comments.sortedWith(
                    compareBy<Comment> { it.parentCommentId.isNotEmpty() }
                        .thenByDescending { 0L } // No tienes createdAt en Comment, usar 0L por defecto
                )

                trySend(sortedComments)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }

    // Agregar comentario
    override suspend fun addComment(propertyId: String, content: String, parentCommentId: String?): Result<String> {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
            ?: return Result.failure(Exception("Usuario no autenticado"))

        return try {
            val userSnapshot = usersRef.child(userId).get().await()
            val user = userSnapshot.getValue(User::class.java)

            val commentId = commentsRef.child("properties").child(propertyId).push().key
                ?: return Result.failure(Exception("No se pudo generar ID"))

            val comment = Comment(
                id = commentId,
                userId = userId,
                propertyId = propertyId,
                content = content.trim(),
                parentCommentId = parentCommentId ?: "",
                likes = 0,
                isEdited = false,
                userName = "${user?.firstName ?: ""} ${user?.lastName ?: ""}".trim(),
                userImage = user?.profileImageUrl ?: ""
            )

            commentsRef.child("properties").child(propertyId).child(commentId).setValue(comment).await()

            Result.success(commentId)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // Eliminar comentario
    override suspend fun deleteComment(commentId: String, propertyId: String): Result<Unit> {
        return try {
            // Como no tienes isActive en Comment, simplemente eliminar
            commentsRef.child("properties").child(propertyId).child(commentId).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
    // Agrega esta función a tu PropertyRepository.kt para crear datos de ejemplo
// Solo para desarrollo - elimínala después

    suspend fun createSampleData() {
        try {
            val sampleProperties = listOf(
                Property(
                    id = "sample1",
                    title = "Cuarto cerca de UTTT",
                    description = "Habitación privada perfecta para estudiantes, a solo 2km de la universidad. Incluye todos los servicios.",
                    address = "Av. Universidad 123, Tula de Allende",
                    monthlyRent = 3500.0,
                    deposit = 1000.0,
                    propertyType = PropertyType.PRIVATE_ROOM.name,
                    roomType = RoomType.PRIVATE.name,
                    maxOccupants = 1,
                    currentOccupants = 0,
                    amenities = listOf("WiFi", "Aire acondicionado", "Closet"),
                    images = listOf(
                        "https://images.unsplash.com/photo-1555854877-bab0e564b8d5?w=800",
                        "https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800"
                    ),
                    rules = listOf("No fumar", "No mascotas", "Silencio después de las 10pm"),
                    isAvailable = true,
                    isActive = true,
                    distanceToUniversity = 2.0,
                    ownerId = "sample_owner",
                    likesCount = 5,
                    viewCount = 23,
                    averageRating = 4.5,
                    totalReviews = 3,
                    createdAt = System.currentTimeMillis() - 86400000, // Hace 1 día
                    updatedAt = System.currentTimeMillis(),
                    utilities = UtilitiesInfo(
                        electricityIncluded = true,
                        waterIncluded = true,
                        internetIncluded = true,
                        gasIncluded = false,
                        cleaningIncluded = false,
                        additionalCosts = 200.0,
                        notes = "Gas y limpieza por separado"
                    ),
                    contactInfo = ContactInfo(
                        phoneNumber = "+52 123 456 7890",
                        whatsappNumber = "+52 123 456 7890",
                        email = "contacto@ejemplo.com",
                        preferredContactMethod = ContactMethod.WHATSAPP.name,
                        contactHours = "9:00 AM - 8:00 PM",
                        responseTime = "Menos de 2 horas"
                    )
                ),
                Property(
                    id = "sample2",
                    title = "Departamento completo",
                    description = "Departamento de 2 recámaras para compartir entre estudiantes. Totalmente amueblado.",
                    address = "Calle Hidalgo 456, Centro",
                    monthlyRent = 2800.0,
                    deposit = 2800.0,
                    propertyType = PropertyType.APARTMENT.name,
                    roomType = RoomType.SHARED.name,
                    maxOccupants = 3,
                    currentOccupants = 1,
                    amenities = listOf("Amueblado", "Cocina equipada", "Lavandería", "Estacionamiento"),
                    images = listOf(
                        "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800",
                        "https://images.unsplash.com/photo-1484154218962-a197022b5858?w=800"
                    ),
                    rules = listOf("No fumar", "Mascotas pequeñas permitidas", "Respetar áreas comunes"),
                    isAvailable = true,
                    isActive = true,
                    distanceToUniversity = 1.5,
                    ownerId = "sample_owner",
                    likesCount = 12,
                    viewCount = 45,
                    averageRating = 4.2,
                    totalReviews = 8,
                    createdAt = System.currentTimeMillis() - 172800000, // Hace 2 días
                    updatedAt = System.currentTimeMillis(),
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
                        phoneNumber = "+52 987 654 3210",
                        whatsappNumber = "+52 987 654 3210",
                        email = "depto@ejemplo.com",
                        preferredContactMethod = ContactMethod.PHONE.name,
                        contactHours = "8:00 AM - 10:00 PM",
                        responseTime = "Inmediata"
                    )
                ),
                Property(
                    id = "sample3",
                    title = "Casa estudiantil",
                    description = "Casa grande con 4 habitaciones para estudiantes. Ambiente familiar y seguro.",
                    address = "Calle Morelos 789, San Marcos",
                    monthlyRent = 2200.0,
                    deposit = 1100.0,
                    propertyType = PropertyType.HOUSE.name,
                    roomType = RoomType.PRIVATE.name,
                    maxOccupants = 4,
                    currentOccupants = 2,
                    amenities = listOf("Jardín", "Cocina grande", "Sala común", "WiFi", "Seguridad"),
                    images = listOf(
                        "https://images.unsplash.com/photo-1568605114967-8130f3a36994?w=800"
                    ),
                    rules = listOf("No fumar", "No fiestas", "Horarios de visita"),
                    isAvailable = true,
                    isActive = true,
                    distanceToUniversity = 3.2,
                    ownerId = "sample_owner",
                    likesCount = 8,
                    viewCount = 31,
                    averageRating = 4.0,
                    totalReviews = 5,
                    createdAt = System.currentTimeMillis() - 259200000, // Hace 3 días
                    updatedAt = System.currentTimeMillis(),
                    utilities = UtilitiesInfo(
                        electricityIncluded = false,
                        waterIncluded = true,
                        internetIncluded = true,
                        gasIncluded = false,
                        cleaningIncluded = false,
                        additionalCosts = 400.0,
                        notes = "Luz y gas dividido entre inquilinos"
                    ),
                    contactInfo = ContactInfo(
                        phoneNumber = "+52 555 123 4567",
                        whatsappNumber = "+52 555 123 4567",
                        email = "casa@ejemplo.com",
                        preferredContactMethod = ContactMethod.WHATSAPP.name,
                        contactHours = "Todo el día",
                        responseTime = "1-2 horas"
                    )
                )
            )

            // Agregar las propiedades a Firebase
            sampleProperties.forEach { property ->
                propertiesRef.child(property.id).setValue(property).await()
            }

            // Crear usuario de ejemplo como LANDLORD
            val sampleUser = mapOf(
                "id" to "sample_owner",
                "first_name" to "María",
                "last_name" to "González",
                "email" to "maria@ejemplo.com",
                "user_type" to UserType.LANDLORD.name,
                "phone_number" to "+52 123 456 7890",
                "is_active" to true
            )
            usersRef.child("sample_owner").setValue(sampleUser).await()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}