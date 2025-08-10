package com.undefined.farfaraway.domain.entities

import com.google.firebase.database.PropertyName

data class Notification(
    @get:PropertyName("id") val id: String = "",
    @get:PropertyName("user_id") val userId: String = "",
    @get:PropertyName("title") val title: String = "",
    @get:PropertyName("message") val message: String = "",
    @get:PropertyName("type") val type: String = NotificationType.GENERAL.name,
    @get:PropertyName("related_entity_id") val relatedEntityId: String = "",
    @get:PropertyName("image_url") val imageUrl: String = "",
    @get:PropertyName("is_read") val isRead: Boolean = false,
    @get:PropertyName("action_url") val actionUrl: String = "",
    @get:PropertyName("sender_name") val senderName: String = "",
    @get:PropertyName("sender_image") val senderImage: String = "",
    @get:PropertyName("created_at") val createdAt: Long = System.currentTimeMillis(),
    @get:PropertyName("priority") val priority: Int = 0, // 0=normal, 1=alta, 2=urgente
    @get:PropertyName("category") val category: String = "general" // Para agrupar notificaciones
) {
    constructor() : this("", "", "", "", NotificationType.GENERAL.name, "", "", false, "", "", "", System.currentTimeMillis(), 0, "general")
}

enum class NotificationType {
    COMMENT_REPLY,        // Laura respondió a tu comentario
    PROPERTY_LIKED,       // Alguien le dio like a tu propiedad
    NEW_PROPERTY,         // Nueva propiedad disponible
    ROUTE_UPDATE,         // Conoce las rutas para llegar a tu destino
    PROFILE_UPDATE,       // Actualiza tu información
    WEEKLY_EXPENSES,      // Ya puedes ver tus gastos de la semana
    MONTHLY_EXPENSES,     // Resumen mensual de gastos
    BUDGET_WARNING,       // Advertencia de presupuesto
    NEW_REVIEW,          // Nueva reseña recibida
    SYSTEM_UPDATE,       // Actualización del sistema
    WELCOME,             // Mensaje de bienvenida
    GENERAL              // Notificación general
}

enum class NotificationPriority(val value: Int) {
    NORMAL(0),
    HIGH(1),
    URGENT(2)
}

enum class NotificationCategory(val displayName: String) {
    GENERAL("General"),
    SOCIAL("Social"),
    PROPERTIES("Propiedades"),
    EXPENSES("Gastos"),
    SYSTEM("Sistema"),
    ROUTES("Rutas")
}