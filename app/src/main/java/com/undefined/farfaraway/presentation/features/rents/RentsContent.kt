package com.undefined.farfaraway.presentation.features.rents

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.undefined.farfaraway.domain.entities.Property

@Composable
fun RentsContent(
    paddingValues: PaddingValues,
    navController: NavController,
    viewModel: RentsViewModel = hiltViewModel()
) {
    var selectedProperty by remember { mutableStateOf<Property?>(null) }
    val properties by viewModel.properties.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSubmittingComment by viewModel.isSubmittingComment.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (properties.isEmpty()) {
            PlaceHolder(
                paddingValues = PaddingValues(0.dp),
                message = "No hay propiedades disponibles"
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(properties) { property ->
                    val isLiked by remember { derivedStateOf { viewModel.isPropertyLiked(property.id) } }
                    val comments by remember { derivedStateOf { viewModel.getCommentsForProperty(property.id) } }

                    EnhancedPropertyCard(
                        property = property,
                        isLiked = isLiked,
                        comments = comments,
                        onLikeClick = {
                            viewModel.togglePropertyLike(property.id)
                        },
                        onCommentSubmit = { commentText ->
                            viewModel.addComment(property.id, commentText)
                        },
                        onPropertyDetailsClick = {
                            selectedProperty = property
                        },
                        isSubmittingComment = isSubmittingComment
                    )
                }

                // Espacio adicional para que el último elemento no quede pegado al bottom
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // BottomSheet para mostrar detalles completos (sin comentarios)
    selectedProperty?.let { property ->
        PropertyDetailsBottomSheet(
            property = property,
            onDismiss = { selectedProperty = null },
            viewModel = viewModel
        )
    }
}

@Composable
fun PlaceHolder(
    paddingValues: PaddingValues,
    message: String = "Contenido no disponible"
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.HourglassEmpty,
                contentDescription = "Placeholder",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}