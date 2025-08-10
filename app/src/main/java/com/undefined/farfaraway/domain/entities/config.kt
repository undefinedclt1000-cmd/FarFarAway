package com.undefined.farfaraway.domain.entities

import com.google.firebase.database.PropertyName

data class AppSettings(
    @get:PropertyName("user_id") val userId: String = "",
    @get:PropertyName("notifications_enabled") val notificationsEnabled: Boolean = true,
    @get:PropertyName("email_notifications_enabled") val emailNotificationsEnabled: Boolean = true,
    @get:PropertyName("push_notifications_enabled") val pushNotificationsEnabled: Boolean = true,
    @get:PropertyName("location_services_enabled") val locationServicesEnabled: Boolean = true,
    @get:PropertyName("language") val language: String = "es",
    @get:PropertyName("currency") val currency: String = "MXN",
) {
    constructor() : this("", true, true, true, true, "es", "MXN")
}
