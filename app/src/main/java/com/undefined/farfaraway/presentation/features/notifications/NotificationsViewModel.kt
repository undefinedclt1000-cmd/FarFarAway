package com.undefined.farfaraway.presentation.features.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.undefined.farfaraway.domain.entities.Notification
import com.undefined.farfaraway.domain.entities.NotificationType

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    // Inyecta aquí tu repositorio de notificaciones cuando lo tengas
    // private val notificationsRepository: NotificationsRepository
): ViewModel() {

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _totalItems = MutableStateFlow(0)
    val totalItems: StateFlow<Int> = _totalItems.asStateFlow()

    // Contador de notificaciones no leídas
    val unreadCount: StateFlow<Int> = notifications.map { notifications ->
        notifications.count { !it.isRead }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    init {
        // Cargar datos de prueba para demostración
        loadMockNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Aquí llamarías a tu repositorio real
                // val notifications = notificationsRepository.getNotifications()
                // _notifications.value = notifications

                // Por ahora usamos datos mock
                loadMockNotifications()
            } catch (e: Exception) {
                // Manejar error
                _notifications.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            val updatedList = _notifications.value.map { notification ->
                if (notification.id == notificationId) {
                    notification.copy(isRead = true)
                } else {
                    notification
                }
            }
            _notifications.value = updatedList

            // Aquí también actualizarías en el repositorio
            // notificationsRepository.markAsRead(notificationId)
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            val updatedList = _notifications.value.map { notification ->
                notification.copy(isRead = true)
            }
            _notifications.value = updatedList

            // Actualizar en el repositorio
            // notificationsRepository.markAllAsRead()
        }
    }

    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            val updatedList = _notifications.value.filter { it.id != notificationId }
            _notifications.value = updatedList
            _totalItems.value = updatedList.size

            // Eliminar del repositorio
            // notificationsRepository.deleteNotification(notificationId)
        }
    }

    private fun loadMockNotifications() {
        val mockNotifications = listOf(
            Notification(
                id = "1",
                userId = "user123",
                title = "Nueva propiedad disponible",
                message = "Se ha agregado una nueva propiedad en tu zona de búsqueda que podría interesarte.",
                type = NotificationType.NEW_PROPERTY.name,
                relatedEntityId = "property123",
                imageUrl = "",
                isRead = false,
                actionUrl = "property_detail/property123",
                senderName = "Sistema",
                senderImage = "",
                createdAt = System.currentTimeMillis() - 300000 // 5 minutos atrás
            ),
            Notification(
                id = "2",
                userId = "user123",
                title = "Laura respondió a tu comentario",
                message = "\"¡Excelente ubicación! Me encantó la vista que tiene esta propiedad.\"",
                type = NotificationType.COMMENT_REPLY.name,
                relatedEntityId = "comment456",
                imageUrl = "",
                isRead = false,
                actionUrl = "comments/comment456",
                senderName = "Laura García",
                senderImage = "https://picsum.photos/100/100?random=1",
                createdAt = System.currentTimeMillis() - 3600000 // 1 hora atrás
            ),
            Notification(
                id = "3",
                userId = "user123",
                title = "Tu propiedad recibió un like",
                message = "A Carlos le gustó tu propiedad \"Casa moderna en el centro\"",
                type = NotificationType.PROPERTY_LIKED.name,
                relatedEntityId = "property789",
                imageUrl = "",
                isRead = false,
                actionUrl = "property_detail/property789",
                senderName = "Carlos Mendez",
                senderImage = "https://picsum.photos/100/100?random=2",
                createdAt = System.currentTimeMillis() - 7200000 // 2 horas atrás
            ),
            Notification(
                id = "4",
                userId = "user123",
                title = "Rutas actualizadas",
                message = "Conoce las nuevas rutas disponibles para llegar a tu destino favorito.",
                type = NotificationType.ROUTE_UPDATE.name,
                relatedEntityId = "",
                imageUrl = "",
                isRead = true,
                actionUrl = "routes",
                senderName = "Sistema",
                senderImage = "",
                createdAt = System.currentTimeMillis() - 86400000 // 1 día atrás
            ),
            Notification(
                id = "5",
                userId = "user123",
                title = "Resumen semanal de gastos",
                message = "Ya puedes revisar tu resumen de gastos de esta semana. Total: $2,450 MXN",
                type = NotificationType.WEEKLY_EXPENSES.name,
                relatedEntityId = "",
                imageUrl = "",
                isRead = true,
                actionUrl = "expenses/weekly",
                senderName = "Sistema",
                senderImage = "",
                createdAt = System.currentTimeMillis() - 172800000 // 2 días atrás
            ),
            Notification(
                id = "6",
                userId = "user123",
                title = "Actualiza tu perfil",
                message = "Completa tu información de perfil para obtener mejores recomendaciones.",
                type = NotificationType.PROFILE_UPDATE.name,
                relatedEntityId = "",
                imageUrl = "",
                isRead = true,
                actionUrl = "profile/edit",
                senderName = "Sistema",
                senderImage = "",
                createdAt = System.currentTimeMillis() - 259200000 // 3 días atrás
            ),
            Notification(
                id = "7",
                userId = "user123",
                title = "¡Bienvenido a FarFarAway!",
                message = "Gracias por unirte a nuestra comunidad. Explora las mejores propiedades y encuentra tu hogar ideal.",
                type = NotificationType.WELCOME.name,
                relatedEntityId = "",
                imageUrl = "",
                isRead = true,
                actionUrl = "onboarding",
                senderName = "Equipo FarFarAway",
                senderImage = "",
                createdAt = System.currentTimeMillis() - 604800000 // 1 semana atrás
            )
        )

        _notifications.value = mockNotifications.sortedByDescending { it.createdAt }
        _totalItems.value = mockNotifications.size
    }
}

// Extensión para agregar createdAt a la clase Notification
val Notification.createdAt: Long
    get() = System.currentTimeMillis() // Por defecto, usar tiempo actual
// En la implementación real, esto vendría de Firebase