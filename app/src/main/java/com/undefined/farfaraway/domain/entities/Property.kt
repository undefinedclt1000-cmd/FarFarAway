// Property.kt - Actualizado con todas las propiedades necesarias
package com.undefined.farfaraway.domain.entities

import com.google.firebase.database.PropertyName


data class Property(
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: String = "",

    @get:PropertyName("title")
    @set:PropertyName("title")
    var title: String = "",

    @get:PropertyName("description")
    @set:PropertyName("description")
    var description: String = "",

    @get:PropertyName("address")
    @set:PropertyName("address")
    var address: String = "",

    @get:PropertyName("monthly_rent")
    @set:PropertyName("monthly_rent")
    var monthlyRent: Double = 0.0,

    @get:PropertyName("deposit")
    @set:PropertyName("deposit")
    var deposit: Double = 0.0,

    @get:PropertyName("property_type")
    @set:PropertyName("property_type")
    var propertyType: String = PropertyType.PRIVATE_ROOM.name,

    @get:PropertyName("room_type")
    @set:PropertyName("room_type")
    var roomType: String = RoomType.PRIVATE.name,

    @get:PropertyName("max_occupants")
    @set:PropertyName("max_occupants")
    var maxOccupants: Int = 1,

    @get:PropertyName("current_occupants")
    @set:PropertyName("current_occupants")
    var currentOccupants: Int = 0,

    @get:PropertyName("amenities")
    @set:PropertyName("amenities")
    var amenities: List<String> = emptyList(),

    @get:PropertyName("images")
    @set:PropertyName("images")
    var images: List<String> = emptyList(),

    @get:PropertyName("rules")
    @set:PropertyName("rules")
    var rules: List<String> = emptyList(),

    @get:PropertyName("is_available")
    @set:PropertyName("is_available")
    var isAvailable: Boolean = true,

    @get:PropertyName("is_active")
    @set:PropertyName("is_active")
    var isActive: Boolean = true,

    @get:PropertyName("distance_to_university")
    @set:PropertyName("distance_to_university")
    var distanceToUniversity: Double = 0.0,

    @get:PropertyName("utilities")
    @set:PropertyName("utilities")
    var utilities: UtilitiesInfo = UtilitiesInfo(),

    @get:PropertyName("contact_info")
    @set:PropertyName("contact_info")
    var contactInfo: ContactInfo = ContactInfo(),

    @get:PropertyName("owner_id")
    @set:PropertyName("owner_id")
    var ownerId: String = "",

    @get:PropertyName("created_at")
    @set:PropertyName("created_at")
    var createdAt: Long = System.currentTimeMillis(),

    @get:PropertyName("updated_at")
    @set:PropertyName("updated_at")
    var updatedAt: Long = System.currentTimeMillis(),

    @get:PropertyName("view_count")
    @set:PropertyName("view_count")
    var viewCount: Int = 0,

    @get:PropertyName("likes_count")
    @set:PropertyName("likes_count")
    var likesCount: Int = 0,

    @get:PropertyName("comments_count")
    @set:PropertyName("comments_count")
    var commentsCount: Int = 0,

    @get:PropertyName("latitude")
    @set:PropertyName("latitude")
    var latitude: Double? = null,

    @get:PropertyName("longitude")
    @set:PropertyName("longitude")
    var longitude: Double? = null,

    @get:PropertyName("average_rating")
    @set:PropertyName("average_rating")
    var averageRating: Double = 0.0,

    @get:PropertyName("total_reviews")
    @set:PropertyName("total_reviews")
    var totalReviews: Int = 0
) {
    constructor() : this(
        id = "",
        title = "",
        description = "",
        address = "",
        monthlyRent = 0.0,
        deposit = 0.0,
        propertyType = PropertyType.PRIVATE_ROOM.name,
        roomType = RoomType.PRIVATE.name,
        maxOccupants = 1,
        currentOccupants = 0,
        amenities = emptyList(),
        images = emptyList(),
        rules = emptyList(),
        isAvailable = true,
        isActive = true,
        distanceToUniversity = 0.0,
        utilities = UtilitiesInfo(),
        contactInfo = ContactInfo(),
        ownerId = "",
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis(),
        viewCount = 0,
        likesCount = 0,
        commentsCount = 0,
        latitude = null,
        longitude = null,
        averageRating = 0.0,
        totalReviews = 0
    )
}


// PropertyType.kt
enum class PropertyType(val displayName: String) {
    PRIVATE_ROOM("Habitación privada"),
    SHARED_ROOM("Habitación compartida"),
    ENTIRE_APARTMENT("Apartamento completo"),
    STUDIO("Estudio"),
    HOUSE("Casa"),
    DORMITORY("Dormitorio"),
    APARTMENT("Departamento")
}

// RoomType.kt
enum class RoomType(val displayName: String) {
    PRIVATE("Privada"),
    SHARED("Compartida"),
    MIXED("Mixta"),
    SINGLE("Individual"),
    DOUBLE("Doble")
}

// ContactMethod.kt
enum class ContactMethod(val displayName: String) {
    PHONE("Teléfono"),
    WHATSAPP("WhatsApp"),
    EMAIL("Correo electrónico"),
    ANY("Cualquiera"),
    IN_APP("En la aplicación")
}

// UtilitiesInfo.kt
data class UtilitiesInfo(
    @get:PropertyName("electricity_included") val electricityIncluded: Boolean = false,
    @get:PropertyName("water_included") val waterIncluded: Boolean = false,
    @get:PropertyName("internet_included") val internetIncluded: Boolean = false,
    @get:PropertyName("gas_included") val gasIncluded: Boolean = false,
    @get:PropertyName("cleaning_included") val cleaningIncluded: Boolean = false,
    @get:PropertyName("additional_costs") val additionalCosts: Double = 0.0,
    @get:PropertyName("notes") val notes: String = ""
) {
    constructor() : this(false, false, false, false, false, 0.0, "")
}

// ContactInfo.kt
data class ContactInfo(
    @get:PropertyName("phone_number") val phoneNumber: String = "",
    @get:PropertyName("whatsapp_number") val whatsappNumber: String = "",
    @get:PropertyName("email") val email: String = "",
    @get:PropertyName("preferred_contact_method") val preferredContactMethod: String = ContactMethod.WHATSAPP.name,
    @get:PropertyName("contact_hours") val contactHours: String = "",
    @get:PropertyName("response_time") val responseTime: String = ""
) {
    constructor() : this("", "", "", ContactMethod.WHATSAPP.name, "", "")
}

// PropertyImage.kt
data class PropertyImage(
    @get:PropertyName("id") val id: String = "",
    @get:PropertyName("property_id") val propertyId: String = "",
    @get:PropertyName("image_url") val imageUrl: String = "",
    @get:PropertyName("storage_path") val storagePath: String = "",
    @get:PropertyName("caption") val caption: String = "",
    @get:PropertyName("is_main_image") val isMainImage: Boolean = false,
) {
    constructor() : this("", "", "", "", "", false)
}

// PropertyStats.kt
data class PropertyStats(
    val totalProperties: Int = 0,
    val availableProperties: Int = 0,
    val averageRent: Double = 0.0,
    val propertyTypeDistribution: Map<String, Int> = emptyMap(),
    val mostPopularAmenities: List<String> = emptyList(),
    val averageDistanceToUniversity: Double = 0.0
)

// UserPreferences.kt
data class UserPreferences(
    val maxRent: Double = 10000.0,
    val preferredPropertyTypes: List<PropertyType> = emptyList(),
    val maxDistanceToUniversity: Double = 5.0,
    val requiredAmenities: List<String> = emptyList(),
    val preferredContactMethod: ContactMethod = ContactMethod.WHATSAPP,
    val allowSharedRooms: Boolean = true,
    val allowPets: Boolean = false,
    val smokingAllowed: Boolean = false
)

// Like.kt
data class Like(
    val id: String = "",
    val userId: String = "",
    val propertyId: String = "",
    val commentId: String? = null,
    val type: String = LikeType.PROPERTY.name,
    val createdAt: Long = System.currentTimeMillis()
)

// LikeType.kt
enum class LikeType {
    PROPERTY,
    COMMENT
}

// Amenity.kt
enum class Amenity(val displayName: String) {
    WIFI("WiFi"),
    PARKING("Estacionamiento"),
    LAUNDRY("Lavandería"),
    KITCHEN("Cocina"),
    AIR_CONDITIONING("Aire acondicionado"),
    HEATING("Calefacción"),
    HOT_WATER("Agua caliente"),
    SECURITY("Seguridad"),
    FURNISHED("Amueblado"),
    PETS_ALLOWED("Mascotas permitidas"),
    SMOKING_ALLOWED("Fumar permitido"),
    GARDEN("Jardín"),
    ROOFTOP("Azotea"),
    GYM("Gimnasio"),
    STUDY_AREA("Área de estudio")
}
// Comment.kt - Nueva entidad para comentarios
data class Comment(
    @get:PropertyName("id") val id: String = "",
    @get:PropertyName("property_id") val propertyId: String = "",
    @get:PropertyName("user_id") val userId: String = "",
    @get:PropertyName("user_name") val userName: String = "",
    @get:PropertyName("user_image") val userImage: String = "",
    @get:PropertyName("is_edited") val isEdited: Boolean = false,
    @get:PropertyName("content") val content: String = "",
    @get:PropertyName("likes") val likes: Int = 0,
    @get:PropertyName("parent_comment_id") val parentCommentId: String = "",
    @get:PropertyName("created_at") val createdAt: Long = System.currentTimeMillis(),
    @get:PropertyName("updated_at") val updatedAt: Long = System.currentTimeMillis()
) {
    // Constructor vacío requerido por Firebase
    constructor() : this(
        id = "",
        propertyId = "",
        userId = "",
        userName = "",
        userImage = "",
        isEdited = false,
        content = "",
        likes = 0,
        parentCommentId = "",
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )
}