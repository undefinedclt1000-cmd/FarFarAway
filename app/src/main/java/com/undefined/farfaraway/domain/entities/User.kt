package com.undefined.farfaraway.domain.entities

import com.google.firebase.database.PropertyName


data class User(
    @get:PropertyName("id") val id: String = "",
    @get:PropertyName("first_name") val firstName: String = "",
    @get:PropertyName("last_name") val lastName: String = "",
    @get:PropertyName("email") val email: String = "",
    @get:PropertyName("age") val age: Int = 0,
    @get:PropertyName("profile_image_url") val profileImageUrl: String = "",
    @get:PropertyName("phone_number") val phoneNumber: String = "",
    @get:PropertyName("is_email_verified") val isEmailVerified: Boolean = false,
    @get:PropertyName("user_type") val userType: String = UserType.STUDENT.name,
    @get:PropertyName("university_id") val universityId: String = "uttt",
    @get:PropertyName("average_rating") val averageRating: Double = 0.0,
    @get:PropertyName("total_reviews") val totalReviews: Int = 0,
    @get:PropertyName("is_active") val isActive: Boolean = true,
) {
    constructor() : this("", "", "", "", 0, "", "", false, UserType.STUDENT.name, "uttt", 0.0, 0, true)
}

enum class UserType {
    STUDENT,        // Estudiante
    LANDLORD,       // Arrendador
    ADMIN
}