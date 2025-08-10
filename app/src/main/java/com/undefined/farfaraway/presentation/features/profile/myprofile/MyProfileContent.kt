package com.undefined.farfaraway.presentation.features.profile.myprofile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.undefined.farfaraway.domain.entities.Review
import com.undefined.farfaraway.domain.entities.ReviewType
import com.undefined.farfaraway.domain.entities.UserType

@Composable
fun MyProfileContent(
    paddingValues: PaddingValues,
    navController: NavController,
    viewModel: MyProfileViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val userReviews by viewModel.userReviews.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // Header del perfil con foto grande
            MyProfileHeader(user = currentUser)

            Spacer(modifier = Modifier.height(24.dp))

            // Información básica
            ProfileInfoSection(user = currentUser)

            Spacer(modifier = Modifier.height(24.dp))

            // Estadísticas y ratings
            ProfileStatsSection(user = currentUser)

            Spacer(modifier = Modifier.height(24.dp))

            // Reseñas y opiniones
            ReviewsSection(
                reviews = userReviews,
                totalReviews = currentUser?.totalReviews ?: 0
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun MyProfileHeader(
    user: com.undefined.farfaraway.domain.entities.User?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Foto de perfil más grande
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            if (!user?.profileImageUrl.isNullOrEmpty()) {
                // TODO: Usar AsyncImage cuando implementes carga de imágenes
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Foto de perfil por defecto",
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Nombre completo
        Text(
            text = "${user?.firstName ?: ""} ${user?.lastName ?: ""}".trim().ifEmpty { "Usuario" },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Edad si está disponible
        if ((user?.age ?: 0) > 0) {
            Text(
                text = "${user?.age} años",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Tipo de usuario
        AssistChip(
            onClick = { },
            label = {
                Text(
                    text = when (user?.userType) {
                        UserType.STUDENT.name -> "Estudiante"
                        UserType.LANDLORD.name -> "Arrendador"
                        UserType.ADMIN.name -> "Administrador"
                        else -> "Usuario"
                    }
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = when (user?.userType) {
                        UserType.STUDENT.name -> Icons.Default.School
                        UserType.LANDLORD.name -> Icons.Default.Home
                        UserType.ADMIN.name -> Icons.Default.AdminPanelSettings
                        else -> Icons.Default.Person
                    },
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        )
    }
}

@Composable
fun ProfileInfoSection(
    user: com.undefined.farfaraway.domain.entities.User?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Información de contacto",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(16.dp)
        ) {
            // Email
            InfoRow(
                icon = Icons.Outlined.Email,
                label = "Correo electrónico",
                value = user?.email ?: "No disponible",
                isVerified = user?.isEmailVerified == true
            )

            if (!user?.phoneNumber.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                InfoRow(
                    icon = Icons.Outlined.Phone,
                    label = "Teléfono",
                    value = user?.phoneNumber ?: "",
                    isVerified = false
                )
            }
        }
    }
}

@Composable
fun ProfileStatsSection(
    user: com.undefined.farfaraway.domain.entities.User?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Mi reputación",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if ((user?.totalReviews ?: 0) > 0) {
                // Rating promedio
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "${user?.averageRating?.let { "%.1f".format(it) } ?: "0.0"}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "de 5.0",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Número de reseñas
                Text(
                    text = "${user?.totalReviews} ${if (user?.totalReviews == 1) "reseña" else "reseñas"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Barra de estrellas visual
                StarRatingBar(
                    rating = user?.averageRating?.toFloat() ?: 0f,
                    maxRating = 5
                )
            } else {
                // Sin reseñas aún
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.StarBorder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Aún no tienes reseñas",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Completa tu perfil y empieza a interactuar para recibir reseñas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewsSection(
    reviews: List<Review>,
    totalReviews: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Reseñas recibidas",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(16.dp)
        ) {
            if (reviews.isNotEmpty()) {
                reviews.forEachIndexed { index, review ->
                    ReviewItem(review = review)
                    if (index < reviews.size - 1) {
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                if (totalReviews > reviews.size) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Ver todas las reseñas (${totalReviews})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.RateReview,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No hay reseñas disponibles",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Las reseñas aparecerán aquí cuando otros usuarios las escriban",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    isVerified: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.size(20.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (isVerified) {
            Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = "Verificado",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun StarRatingBar(
    rating: Float,
    maxRating: Int = 5
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(maxRating) { index ->
            val filled = index < rating.toInt()
            val halfFilled = index == rating.toInt() && rating % 1 != 0f

            Icon(
                imageVector = when {
                    filled -> Icons.Default.Star
                    halfFilled -> Icons.Default.StarHalf
                    else -> Icons.Default.StarBorder
                },
                contentDescription = null,
                tint = if (filled || halfFilled) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ReviewItem(review: Review) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar del reviewer
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                if (review.reviewerImage.isNotEmpty()) {
                    // TODO: Usar AsyncImage cuando implementes carga de imágenes
                    Text(
                        text = review.reviewerName.take(1).uppercase(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = review.reviewerName.take(1).uppercase(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = review.reviewerName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Indicador de verificado
                    if (review.isVerified) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Reseña verificada",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < review.rating) Icons.Default.Star
                            else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (index < review.rating) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Tipo de reseña
            AssistChip(
                onClick = { },
                label = {
                    Text(
                        text = when (ReviewType.valueOf(review.reviewType)) {
                            ReviewType.ROOMMATE -> "Roomie"
                            ReviewType.LANDLORD -> "Arrendador"
                            ReviewType.PROPERTY -> "Propiedad"
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                modifier = Modifier.height(24.dp)
            )
        }

        // Título de la reseña
        if (review.title.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = review.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 52.dp)
            )
        }

        // Contenido de la reseña
        if (review.content.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = review.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                modifier = Modifier.padding(start = 52.dp)
            )
        }
    }
}