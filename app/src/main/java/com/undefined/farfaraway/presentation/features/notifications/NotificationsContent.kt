package com.undefined.farfaraway.presentation.features.notifications

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.undefined.farfaraway.domain.entities.Notification
import com.undefined.farfaraway.domain.entities.NotificationType

@Composable
fun NotificationsContent(
    paddingValues: PaddingValues,
    navController: NavController,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val notifications by viewModel.notifications.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadNotifications()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                    )
                )
            )
    ) {
        // Header con título y contador
        NotificationHeader(
            unreadCount = unreadCount,
            onMarkAllAsRead = { viewModel.markAllAsRead() }
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp
                    )
                }
            }
            notifications.isEmpty() -> {
                EmptyNotificationsState()
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(notifications) { notification ->
                        NotificationItem(
                            notification = notification,
                            onItemClick = {
                                viewModel.markAsRead(notification.id)
                                handleNotificationClick(notification, navController)
                            },
                            onDismiss = { viewModel.deleteNotification(notification.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationHeader(
    unreadCount: Int,
    onMarkAllAsRead: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Notificaciones",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                AnimatedVisibility(
                    visible = unreadCount > 0,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    Text(
                        text = "$unreadCount sin leer",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            AnimatedVisibility(
                visible = unreadCount > 0,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                TextButton(
                    onClick = onMarkAllAsRead,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Marcar todo como leído")
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: Notification,
    onItemClick: () -> Unit,
    onDismiss: () -> Unit
) {
    NotificationCard(
        notification = notification,
        onClick = onItemClick,
        onDelete = onDismiss
    )
}

@Composable
fun NotificationCard(
    notification: Notification,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    val animatedScale by animateFloatAsState(
        targetValue = if (notification.isRead) 0.98f else 1f,
        animationSpec = tween(300),
        label = "card_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(animatedScale)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation =  1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icono o imagen del remitente
            NotificationAvatar(notification = notification)

            // Contenido
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (!notification.isRead) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primary,
                                        CircleShape
                                    )
                            )
                        }

                        // Botón de eliminar
                        IconButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Eliminar notificación",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = getRelativeTime(notification.createdAt),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    NotificationTypeChip(type = notification.type)
                }
            }
        }
    }

    // Diálogo de confirmación para eliminar
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text("Eliminar notificación")
            },
            text = {
                Text("¿Estás seguro de que deseas eliminar esta notificación?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Red
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun NotificationAvatar(notification: Notification) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                when (NotificationType.valueOf(notification.type)) {
                    NotificationType.COMMENT_REPLY -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    NotificationType.PROPERTY_LIKED -> Color(0xFFE91E63).copy(alpha = 0.1f)
                    NotificationType.NEW_PROPERTY -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                    NotificationType.SYSTEM_UPDATE -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (notification.senderImage.isNotEmpty()) {
            AsyncImage(
                model = notification.senderImage,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = getNotificationIcon(notification.type),
                contentDescription = "Notification Icon",
                modifier = Modifier.size(24.dp),
                tint = when (NotificationType.valueOf(notification.type)) {
                    NotificationType.COMMENT_REPLY -> MaterialTheme.colorScheme.primary
                    NotificationType.PROPERTY_LIKED -> Color(0xFFE91E63)
                    NotificationType.NEW_PROPERTY -> MaterialTheme.colorScheme.secondary
                    NotificationType.SYSTEM_UPDATE -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

@Composable
fun NotificationTypeChip(type: String) {
    val (color, text) = when (NotificationType.valueOf(type)) {
        NotificationType.COMMENT_REPLY -> MaterialTheme.colorScheme.primary to "Comentario"
        NotificationType.PROPERTY_LIKED -> Color(0xFFE91E63) to "Like"
        NotificationType.NEW_PROPERTY -> MaterialTheme.colorScheme.secondary to "Propiedad"
        NotificationType.ROUTE_UPDATE -> Color(0xFF4CAF50) to "Ruta"
        NotificationType.SYSTEM_UPDATE -> MaterialTheme.colorScheme.tertiary to "Sistema"
        else -> MaterialTheme.colorScheme.outline to "General"
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        contentColor = color,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(0.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp
        )
    }
}

@Composable
fun EmptyNotificationsState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "float_animation")
        val floatOffset by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 10f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = EaseInOut),
                repeatMode = RepeatMode.Reverse
            ),
            label = "float_offset"
        )

        Icon(
            imageVector = Icons.Outlined.Notifications,
            contentDescription = "Sin notificaciones",
            modifier = Modifier
                .size(80.dp)
                .offset(y = floatOffset.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¡Todo al día!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "No tienes notificaciones nuevas por el momento",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

private fun getNotificationIcon(type: String): ImageVector {
    return when (NotificationType.valueOf(type)) {
        NotificationType.COMMENT_REPLY -> Icons.Default.Reply
        NotificationType.PROPERTY_LIKED -> Icons.Default.Favorite
        NotificationType.NEW_PROPERTY -> Icons.Default.Home
        NotificationType.ROUTE_UPDATE -> Icons.Default.DirectionsCar
        NotificationType.PROFILE_UPDATE -> Icons.Default.Person
        NotificationType.WEEKLY_EXPENSES -> Icons.Default.Receipt
        NotificationType.MONTHLY_EXPENSES -> Icons.Default.Analytics
        NotificationType.BUDGET_WARNING -> Icons.Default.Warning
        NotificationType.NEW_REVIEW -> Icons.Default.Star
        NotificationType.SYSTEM_UPDATE -> Icons.Default.Update
        NotificationType.WELCOME -> Icons.Default.Celebration
        NotificationType.GENERAL -> Icons.Default.Info
    }
}

private fun getRelativeTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        seconds < 60 -> "Ahora"
        minutes < 60 -> "${minutes}m"
        hours < 24 -> "${hours}h"
        days < 7 -> "${days}d"
        else -> "${days / 7}sem"
    }
}

private fun handleNotificationClick(notification: Notification, navController: NavController) {
    when (NotificationType.valueOf(notification.type)) {
        NotificationType.NEW_PROPERTY -> {
            if (notification.relatedEntityId.isNotEmpty()) {
                navController.navigate("property_detail/${notification.relatedEntityId}")
            }
        }
        NotificationType.COMMENT_REPLY -> {
            if (notification.actionUrl.isNotEmpty()) {
                navController.navigate(notification.actionUrl)
            }
        }
        // Agregar más casos según sea necesario
        else -> {
            // Acción por defecto o mostrar detalle de notificación
        }
    }
}