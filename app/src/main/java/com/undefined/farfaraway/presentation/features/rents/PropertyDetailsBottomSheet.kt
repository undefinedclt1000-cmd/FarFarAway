package com.undefined.farfaraway.presentation.features.rents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.undefined.farfaraway.domain.entities.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun EnhancedPropertyCard(
    property: Property,
    isLiked: Boolean,
    comments: List<Comment>,
    onLikeClick: () -> Unit,
    onCommentSubmit: (String) -> Unit,
    onPropertyDetailsClick: () -> Unit,
    isSubmittingComment: Boolean = false,
    modifier: Modifier = Modifier
) {
    var showComments by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            // Header con imagen y información básica
            PropertyHeader(
                property = property,
                onDetailsClick = onPropertyDetailsClick
            )

            // Información principal
            PropertyMainInfo(
                property = property,
                modifier = Modifier.padding(16.dp)
            )

            // Barra de interacción
            PropertyInteractionBar(
                isLiked = isLiked,
                likesCount = property.likesCount,
                commentsCount = comments.size,
                showComments = showComments,
                onLikeClick = onLikeClick,
                onCommentsToggle = { showComments = !showComments }
            )

            // Sección de comentarios expandible
            AnimatedVisibility(
                visible = showComments,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                CommentsSection(
                    comments = comments,
                    commentText = commentText,
                    onCommentTextChange = { commentText = it },
                    onCommentSubmit = {
                        if (commentText.trim().isNotEmpty()) {
                            onCommentSubmit(commentText)
                            commentText = ""
                            keyboardController?.hide()
                        }
                    },
                    isSubmittingComment = isSubmittingComment
                )
            }
        }
    }
}

@Composable
private fun PropertyHeader(
    property: Property,
    onDetailsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clickable { onDetailsClick() }
    ) {
        val mainImageUrl = property.images.firstOrNull() ?: ""

        AsyncImage(
            model = mainImageUrl,
            contentDescription = "Imagen de ${property.title}",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
            contentScale = ContentScale.Crop
        )

        // Overlay con gradiente para mejor legibilidad
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.3f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        // Badge de disponibilidad
        if (property.isAvailable) {
            Surface(
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.TopEnd),
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Disponible",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Precio
        Surface(
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.BottomStart),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "$${property.monthlyRent.toInt()}/mes",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Indicador de más imágenes
        if (property.images.size > 1) {
            Surface(
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.BottomEnd),
                color = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Photo,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${property.images.size}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun PropertyMainInfo(
    property: Property,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Título y dirección
        Text(
            text = property.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = property.address,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Información compacta en chips
        PropertyInfoChips(property = property)
    }
}

@Composable
private fun PropertyInfoChips(
    property: Property
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Tipo de propiedad
        InfoChip(
            icon = when {
                property.propertyType == PropertyType.HOUSE.name -> Icons.Default.Home
                property.propertyType == PropertyType.APARTMENT.name -> Icons.Default.Apartment
                property.propertyType == PropertyType.PRIVATE_ROOM.name -> Icons.Default.Bed
                property.propertyType == PropertyType.SHARED_ROOM.name -> Icons.Default.Group
                property.propertyType == PropertyType.STUDIO.name -> Icons.Default.SingleBed
                property.propertyType == PropertyType.ENTIRE_APARTMENT.name -> Icons.Default.Apartment
                property.propertyType == PropertyType.DORMITORY.name -> Icons.Default.School
                else -> Icons.Default.Home
            },
            text = when {
                property.propertyType == PropertyType.HOUSE.name -> "Casa"
                property.propertyType == PropertyType.APARTMENT.name -> "Depto"
                property.propertyType == PropertyType.PRIVATE_ROOM.name -> "Cuarto"
                property.propertyType == PropertyType.SHARED_ROOM.name -> "Compartido"
                property.propertyType == PropertyType.STUDIO.name -> "Estudio"
                property.propertyType == PropertyType.ENTIRE_APARTMENT.name -> "Apartamento"
                property.propertyType == PropertyType.DORMITORY.name -> "Dormitorio"
                else -> "Propiedad"
            }
        )

        // Ocupantes
        InfoChip(
            icon = Icons.Default.People,
            text = "${property.currentOccupants}/${property.maxOccupants}"
        )

        // Distancia si está disponible
        if (property.distanceToUniversity > 0) {
            InfoChip(
                icon = Icons.Default.Directions,
                text = "${property.distanceToUniversity} km"
            )
        }

        // Rating si está disponible
        if (property.averageRating > 0) {
            InfoChip(
                icon = Icons.Default.Star,
                text = "${property.averageRating}",
                iconTint = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}


@Composable
private fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    iconTint: Color = MaterialTheme.colorScheme.primary
) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun PropertyInteractionBar(
    isLiked: Boolean,
    likesCount: Int,
    commentsCount: Int,
    showComments: Boolean,
    onLikeClick: () -> Unit,
    onCommentsToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Lado izquierdo - Like y comentarios
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón de Like
                InteractionButton(
                    icon = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    count = likesCount,
                    isActive = isLiked,
                    activeColor = Color.Red,
                    onClick = onLikeClick,
                    contentDescription = if (isLiked) "Quitar like" else "Dar like"
                )

                // Botón de Comentarios
                InteractionButton(
                    icon = if (showComments) Icons.Filled.ChatBubble else Icons.Outlined.ChatBubbleOutline,
                    count = commentsCount,
                    isActive = showComments,
                    activeColor = MaterialTheme.colorScheme.primary,
                    onClick = onCommentsToggle,
                    contentDescription = if (showComments) "Ocultar comentarios" else "Ver comentarios"
                )
            }

            // Lado derecho - Compartir
            IconButton(
                onClick = { /* TODO: Implementar compartir */ }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Share,
                    contentDescription = "Compartir",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun InteractionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int,
    isActive: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    contentDescription: String
) {
    Row(
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (isActive) activeColor else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        if (count > 0) {
            Text(
                text = when {
                    count >= 1000 -> "${count / 1000}k"
                    else -> count.toString()
                },
                style = MaterialTheme.typography.bodySmall,
                color = if (isActive) activeColor else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun CommentsSection(
    comments: List<Comment>,
    commentText: String,
    onCommentTextChange: (String) -> Unit,
    onCommentSubmit: () -> Unit,
    isSubmittingComment: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        HorizontalDivider(thickness = 1.dp)

        // Campo para nuevo comentario
        CommentInputSection(
            value = commentText,
            onValueChange = onCommentTextChange,
            onSubmit = onCommentSubmit,
            isSubmitting = isSubmittingComment,
            modifier = Modifier.padding(16.dp)
        )

        // Lista de comentarios
        if (comments.isEmpty()) {
            EmptyCommentsState(
                modifier = Modifier.padding(24.dp)
            )
        } else {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                comments.take(3).forEach { comment -> // Mostrar solo los primeros 3
                    CompactCommentItem(
                        comment = comment,
                        onLikeClick = { /* TODO: Implementar likes en comentarios */ }
                    )
                }

                if (comments.size > 3) {
                    TextButton(
                        onClick = { /* TODO: Navegar a pantalla completa de comentarios */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ver todos los ${comments.size} comentarios")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun CommentInputSection(
    value: String,
    onValueChange: (String) -> Unit,
    onSubmit: () -> Unit,
    isSubmitting: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Avatar del usuario actual
        Surface(
            modifier = Modifier.size(32.dp),
            color = MaterialTheme.colorScheme.primary,
            shape = CircleShape
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "U",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Campo de texto
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("Agregar un comentario...") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(24.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { onSubmit() }),
            maxLines = 2,
            enabled = !isSubmitting,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            ),
            trailingIcon = {
                if (value.trim().isNotEmpty()) {
                    IconButton(
                        onClick = onSubmit,
                        enabled = !isSubmitting
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Enviar",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun CompactCommentItem(
    comment: Comment,
    onLikeClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = comment.userImage.ifEmpty {
                        "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100"
                    },
                    contentDescription = "Avatar de ${comment.userName}",
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = comment.userName,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "• hace un momento",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Contenido
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            // Acciones
            if (comment.likes > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ThumbUp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${comment.likes}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyCommentsState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.ChatBubbleOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Sé el primero en comentar",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun PropertyDetailsContent(
    property: Property,
    viewModel: RentsViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            // Carrusel de imágenes
            if (property.images.isNotEmpty()) {
                ImageCarousel(
                    images = property.images,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Sin imagen",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
        }

        item {
            // Contenido principal
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Título, precio y rating
                PropertyHeaderInfo(property = property)

                Spacer(modifier = Modifier.height(20.dp))

                // Información básica en tarjetas
                PropertyInfoCards(property = property)

                Spacer(modifier = Modifier.height(20.dp))

                // Descripción
                if (property.description.isNotEmpty()) {
                    PropertyDescriptionSection(description = property.description)
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Servicios incluidos
                PropertyUtilitiesSection(utilities = property.utilities)

                Spacer(modifier = Modifier.height(20.dp))

                // Amenidades
                if (property.amenities.isNotEmpty()) {
                    PropertyAmenitiesSection(amenities = property.amenities)
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Reglas
                if (property.rules.isNotEmpty()) {
                    PropertyRulesSection(rules = property.rules)
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Información de contacto
                PropertyContactSection(contactInfo = property.contactInfo)

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun PropertyHeaderInfo(property: Property) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = property.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = property.address,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            // Rating y reviews
            if (property.averageRating > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < property.averageRating.toInt())
                                Icons.Default.Star else Icons.Outlined.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${property.averageRating} (${property.totalReviews} reseñas)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Precio destacado
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${property.monthlyRent.toInt()}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "por mes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun PropertyInfoCards(property: Property) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Tipo de propiedad
        InfoCard(
            modifier = Modifier.weight(1f),
            icon = when {
                property.propertyType == PropertyType.HOUSE.name -> Icons.Default.Home
                property.propertyType == PropertyType.APARTMENT.name -> Icons.Default.Apartment
                property.propertyType == PropertyType.PRIVATE_ROOM.name -> Icons.Default.Bed
                property.propertyType == PropertyType.SHARED_ROOM.name -> Icons.Default.Group
                property.propertyType == PropertyType.STUDIO.name -> Icons.Default.SingleBed
                property.propertyType == PropertyType.ENTIRE_APARTMENT.name -> Icons.Default.Apartment
                property.propertyType == PropertyType.DORMITORY.name -> Icons.Default.School
                else -> Icons.Default.Home
            },
            title = "Tipo",
            subtitle = when {
                property.propertyType == PropertyType.HOUSE.name -> "Casa"
                property.propertyType == PropertyType.APARTMENT.name -> "Departamento"
                property.propertyType == PropertyType.PRIVATE_ROOM.name -> "Cuarto privado"
                property.propertyType == PropertyType.SHARED_ROOM.name -> "Cuarto compartido"
                property.propertyType == PropertyType.STUDIO.name -> "Estudio"
                property.propertyType == PropertyType.ENTIRE_APARTMENT.name -> "Apartamento completo"
                property.propertyType == PropertyType.DORMITORY.name -> "Dormitorio"
                else -> "Propiedad"
            }
        )

        // Ocupantes
        InfoCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.People,
            title = "Ocupantes",
            subtitle = "${property.currentOccupants}/${property.maxOccupants}"
        )
    }

    if (property.distanceToUniversity > 0 || property.deposit > 0) {
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Distancia
            if (property.distanceToUniversity > 0) {
                InfoCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Directions,
                    title = "Distancia",
                    subtitle = "${property.distanceToUniversity} km"
                )
            }

            // Depósito
            if (property.deposit > 0) {
                InfoCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.AccountBalance,
                    title = "Depósito",
                    subtitle = "${property.deposit.toInt()}"
                )
            }
        }
    }
}

@Composable
private fun InfoCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PropertyDescriptionSection(description: String) {
    Column {
        Text(
            text = "Descripción",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = description,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
            )
        }
    }
}

@Composable
private fun PropertyUtilitiesSection(utilities: UtilitiesInfo) {
    Column {
        Text(
            text = "Servicios incluidos",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (utilities.electricityIncluded) {
                item { UtilityChip(text = "Electricidad", icon = Icons.Default.ElectricBolt) }
            }
            if (utilities.waterIncluded) {
                item { UtilityChip(text = "Agua", icon = Icons.Default.Water) }
            }
            if (utilities.internetIncluded) {
                item { UtilityChip(text = "Internet", icon = Icons.Default.Wifi) }
            }
            if (utilities.gasIncluded) {
                item { UtilityChip(text = "Gas", icon = Icons.Default.LocalGasStation) }
            }
            if (utilities.cleaningIncluded) {
                item { UtilityChip(text = "Limpieza", icon = Icons.Default.CleaningServices) }
            }
        }

        if (utilities.additionalCosts > 0) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Costos adicionales: ${utilities.additionalCosts.toInt()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        if (utilities.notes.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = utilities.notes,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}

@Composable
private fun PropertyAmenitiesSection(amenities: List<String>) {
    Column {
        Text(
            text = "Amenidades",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(amenities) { amenity ->
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = amenity,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun PropertyRulesSection(rules: List<String>) {
    Column {
        Text(
            text = "Reglas de la casa",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rules.forEach { rule ->
                    Row(
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircleOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = rule,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PropertyContactSection(contactInfo: ContactInfo) {
    Column {
        Text(
            text = "Información de contacto",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (contactInfo.phoneNumber.isNotEmpty()) {
                    ContactItem(
                        icon = Icons.Default.Phone,
                        label = "Teléfono",
                        value = contactInfo.phoneNumber,
                        isPreferred = contactInfo.preferredContactMethod == ContactMethod.PHONE.name
                    )
                }

                if (contactInfo.whatsappNumber.isNotEmpty()) {
                    ContactItem(
                        icon = Icons.Default.Chat,
                        label = "WhatsApp",
                        value = contactInfo.whatsappNumber,
                        isPreferred = contactInfo.preferredContactMethod == ContactMethod.WHATSAPP.name
                    )
                }

                if (contactInfo.email.isNotEmpty()) {
                    ContactItem(
                        icon = Icons.Default.Email,
                        label = "Email",
                        value = contactInfo.email,
                        isPreferred = contactInfo.preferredContactMethod == ContactMethod.EMAIL.name
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    isPreferred: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                if (isPreferred) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "Preferido",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ImageCarousel corregido para usar List<String>
@Composable
fun ImageCarousel(
    images: List<String>,
    modifier: Modifier = Modifier
) {
    var currentImageIndex by remember { mutableIntStateOf(0) }

    Box(modifier = modifier) {
        // Imagen actual
        AsyncImage(
            model = images[currentImageIndex],
            contentDescription = "Imagen ${currentImageIndex + 1}",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Overlay gradient para mejor legibilidad de controles
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.1f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.3f)
                        )
                    )
                )
        )

        // Indicadores de página mejorados
        if (images.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .background(
                        Color.Black.copy(alpha = 0.3f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                images.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .size(if (index == currentImageIndex) 10.dp else 8.dp)
                            .background(
                                color = if (index == currentImageIndex)
                                    Color.White
                                else
                                    Color.White.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                    )
                }
            }
        }

        // Navegación de imágenes mejorada
        if (images.size > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        currentImageIndex = if (currentImageIndex > 0)
                            currentImageIndex - 1
                        else
                            images.size - 1
                    },
                    modifier = Modifier
                        .background(
                            Color.Black.copy(alpha = 0.4f),
                            CircleShape
                        )
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Imagen anterior",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(
                    onClick = {
                        currentImageIndex = if (currentImageIndex < images.size - 1)
                            currentImageIndex + 1
                        else
                            0
                    },
                    modifier = Modifier
                        .background(
                            Color.Black.copy(alpha = 0.4f),
                            CircleShape
                        )
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Siguiente imagen",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Contador de imágenes
        if (images.size > 1) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                color = Color.Black.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "${currentImageIndex + 1} / ${images.size}",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun UtilityChip(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}