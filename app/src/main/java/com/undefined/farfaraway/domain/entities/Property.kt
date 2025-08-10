package com.undefined.farfaraway.domain.entities

import com.google.firebase.database.PropertyName
import java.util.Date


// ========================================
// MODELOS DE ALOJAMIENTO/RENTAS
// ========================================

data class Property(
    @get:PropertyName("id") val id: String = "",
    @get:PropertyName("landlord_id") val landlordId: String = "",
    @get:PropertyName("title") val title: String = "",
    @get:PropertyName("description") val description: String = "",
    @get:PropertyName("address") val address: String = "",
    @get:PropertyName("latitude") val latitude: Double = 0.0,
    @get:PropertyName("longitude") val longitude: Double = 0.0,
    @get:PropertyName("monthly_rent") val monthlyRent: Double = 0.0,
    @get:PropertyName("deposit") val deposit: Double = 0.0,
    @get:PropertyName("property_type") val propertyType: String = PropertyType.PRIVATE_ROOM.name,
    @get:PropertyName("room_type") val roomType: String = RoomType.SINGLE.name,
    @get:PropertyName("max_occupants") val maxOccupants: Int = 1,
    @get:PropertyName("current_occupants") val currentOccupants: Int = 0,
    @get:PropertyName("amenities") val amenities: List<String> = emptyList(),
    @get:PropertyName("images") val images: List<PropertyImage> = emptyList(),
    @get:PropertyName("rules") val rules: List<String> = emptyList(),
    @get:PropertyName("is_available") val isAvailable: Boolean = true,
    @get:PropertyName("distance_to_university") val distanceToUniversity: Double = 0.0,
    @get:PropertyName("average_rating") val averageRating: Double = 0.0,
    @get:PropertyName("total_reviews") val totalReviews: Int = 0,
    @get:PropertyName("utilities") val utilities: UtilitiesInfo = UtilitiesInfo(),
    @get:PropertyName("contact_info") val contactInfo: ContactInfo = ContactInfo(),
    @get:PropertyName("likes_count") val likesCount: Int = 0,
    @get:PropertyName("comments_count") val commentsCount: Int = 0,
) {
    constructor() : this("", "", "", "", "", 0.0, 0.0, 0.0, 0.0, PropertyType.PRIVATE_ROOM.name,
        RoomType.SINGLE.name, 1, 0, emptyList(), emptyList(), emptyList(), true, 0.0, 0.0, 0,
        UtilitiesInfo(), ContactInfo(), 0, 0)
}

data class PropertyImage(
    @get:PropertyName("id") val id: String = "",
    @get:PropertyName("property_id") val propertyId: String = "",
    @get:PropertyName("image_url") val imageUrl: String = "",
    @get:PropertyName("storage_path") val storagePath: String = "", // Ruta en Firebase Storage
    @get:PropertyName("caption") val caption: String = "",
    @get:PropertyName("is_main_image") val isMainImage: Boolean = false,
) {
    constructor() : this("", "", "", "", "", false)
}

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

data class ContactInfo(
    @get:PropertyName("phone_number") val phoneNumber: String = "",
    @get:PropertyName("whatsapp_number") val whatsappNumber: String = "",
    @get:PropertyName("email") val email: String = "",
    @get:PropertyName("preferred_contact_method") val preferredContactMethod: String = ContactMethod.PHONE.name
) {
    constructor() : this("", "", "", ContactMethod.PHONE.name)
}

enum class PropertyType {
    HOUSE,          // Casa completa
    APARTMENT,      // Departamento
    SHARED_ROOM,    // Cuarto compartido
    PRIVATE_ROOM,   // Cuarto privado
    STUDIO         // Estudio
}

enum class RoomType {
    SINGLE,         // Individual
    DOUBLE,         // Doble
    SHARED,         // Compartido
    PRIVATE         // Privado
}

enum class Amenity {
    WIFI,
    PARKING,
    LAUNDRY,
    KITCHEN,
    AIR_CONDITIONING,
    HEATING,
    HOT_WATER,
    SECURITY,
    FURNISHED,
    PETS_ALLOWED,
    SMOKING_ALLOWED,
    GARDEN,
    ROOFTOP,
    GYM,
    STUDY_AREA
}

enum class ContactMethod {
    PHONE,
    WHATSAPP,
    EMAIL,
    IN_APP
}
