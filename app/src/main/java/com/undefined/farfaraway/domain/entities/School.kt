package com.undefined.farfaraway.domain.entities

import com.google.firebase.database.PropertyName

data class University(
    @get:PropertyName("id") val id: String = "",
    @get:PropertyName("name") val name: String = "",
    @get:PropertyName("full_name") val fullName: String = "",
    @get:PropertyName("location") val location: Location = Location(),
    @get:PropertyName("images") val images: List<UniversityImage> = emptyList(),
    @get:PropertyName("contact_info") val contactInfo: ContactInfo = ContactInfo(),
    @get:PropertyName("description") val description: String = "",
    @get:PropertyName("founded_year") val foundedYear: Int = 0,
    @get:PropertyName("student_count") val studentCount: Int = 0,
    @get:PropertyName("program_count") val programCount: Int = 0,
    @get:PropertyName("website") val website: String = "",
    @get:PropertyName("is_active") val isActive: Boolean = true
) {
    constructor() : this("", "", "", Location(), emptyList(), ContactInfo(), "", 0, 0, 0, "", true)
}

data class UniversityImage(
    @get:PropertyName("id") val id: String = "",
    @get:PropertyName("university_id") val universityId: String = "",
    @get:PropertyName("image_url") val imageUrl: String = "",
    @get:PropertyName("storage_path") val storagePath: String = "",
    @get:PropertyName("caption") val caption: String = "",
    @get:PropertyName("is_main_image") val isMainImage: Boolean = false,
) {
    constructor() : this("", "", "", "", "", false)
}
