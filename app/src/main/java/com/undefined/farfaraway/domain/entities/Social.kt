package com.undefined.farfaraway.domain.entities

import com.google.firebase.database.PropertyName
import java.util.Date

data class PropertyLike(
    @get:PropertyName("id") val id: String = "",
    @get:PropertyName("user_id") val userId: String = "",
    @get:PropertyName("property_id") val propertyId: String = "",
) {
    constructor() : this("", "", "")
}

data class Comment(
    @get:PropertyName("id") val id: String = "",
    @get:PropertyName("user_id") val userId: String = "",
    @get:PropertyName("property_id") val propertyId: String = "",
    @get:PropertyName("content") val content: String = "",
    @get:PropertyName("parent_comment_id") val parentCommentId: String = "",
    @get:PropertyName("likes") val likes: Int = 0,
    @get:PropertyName("is_edited") val isEdited: Boolean = false,
    @get:PropertyName("user_name") val userName: String = "", // Denormalizado para performance
    @get:PropertyName("user_image") val userImage: String = "", // Denormalizado para performance
) {
    constructor() : this("", "", "", "", "", 0, false, "", "")
}

data class Review(
    @get:PropertyName("id") val id: String = "",
    @get:PropertyName("reviewer_id") val reviewerId: String = "",
    @get:PropertyName("reviewed_user_id") val reviewedUserId: String = "",
    @get:PropertyName("property_id") val propertyId: String = "",
    @get:PropertyName("rating") val rating: Int = 0,
    @get:PropertyName("title") val title: String = "",
    @get:PropertyName("content") val content: String = "",
    @get:PropertyName("review_type") val reviewType: String = ReviewType.PROPERTY.name,
    @get:PropertyName("is_verified") val isVerified: Boolean = false,
    @get:PropertyName("reviewer_name") val reviewerName: String = "", // Denormalizado
    @get:PropertyName("reviewer_image") val reviewerImage: String = "", // Denormalizado
) {
    constructor() : this("", "", "", "", 0, "", "", ReviewType.PROPERTY.name, false, "", "")
}

enum class ReviewType {
    PROPERTY,       // Review de propiedad
    ROOMMATE,       // Review de roomie
    LANDLORD        // Review de arrendador
}