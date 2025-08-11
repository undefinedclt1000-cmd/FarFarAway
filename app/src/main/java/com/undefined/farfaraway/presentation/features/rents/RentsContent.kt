package com.undefined.farfaraway.presentation.features.rents

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.undefined.farfaraway.domain.entities.Property
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentsContent(
    paddingValues: PaddingValues,
    navController: NavController,
    viewModel: RentsViewModel
) {
    var selectedProperty by remember { mutableStateOf<Property?>(null) }
    var showSearchAndFilters by remember { mutableStateOf(false) }

    val properties by viewModel.properties.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val error by viewModel.error.collectAsState()
    val isSubmittingComment by viewModel.isSubmittingComment.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isOwner by viewModel.isOwner.collectAsState()

    // Pull to refresh state
    val pullToRefreshState = rememberPullToRefreshState()
    val lazyListState = rememberLazyListState()

    // Bottom sheet state
    val bottomSheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    // Limpiar error cuando se monte el componente
    LaunchedEffect(Unit) {
        viewModel.clearError()
    }

    // Manejar el estado del bottom sheet
    LaunchedEffect(selectedProperty) {
        if (selectedProperty != null) {
            showBottomSheet = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        RentsHeader(
            isOwner = isOwner,
            searchQuery = searchQuery,
            onSearchQueryChange = viewModel::updateSearchQuery,
            onFilterClick = { showSearchAndFilters = true }
        )

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refreshProperties() },
            state = pullToRefreshState,
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                error != null -> {
                    ErrorState(
                        error = error!!,
                        onRetry = {
                            viewModel.clearError()
                            viewModel.refreshProperties()
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                isLoading && properties.isEmpty() -> {
                    LoadingState(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                properties.isEmpty() -> {
                    EmptyState(
                        isOwner = isOwner,
                        onRetry = viewModel::refreshProperties,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    PropertiesList(
                        properties = properties,
                        isSubmittingComment = isSubmittingComment,
                        lazyListState = lazyListState,
                        viewModel = viewModel,
                        onPropertyClick = { property ->
                            selectedProperty = property
                            viewModel.incrementPropertyViews(property.id)
                        }
                    )
                }
            }
        }
    }

    // BottomSheet para mostrar detalles completos
    if (showBottomSheet && selectedProperty != null) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                selectedProperty = null
            },
            sheetState = bottomSheetState
        ) {
            PropertyDetailsContent(
                property = selectedProperty!!,
                viewModel = viewModel
            )
        }
    }

    // Dialog para filtros avanzados
    if (showSearchAndFilters) {
        SearchFiltersDialog(
            onDismiss = { showSearchAndFilters = false },
            viewModel = viewModel
        )
    }
}

@Composable
private fun RentsHeader(
    isOwner: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Título
        Text(
            text = "Propiedades disponibles",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Barra de búsqueda y filtros
        SearchAndFilterBar(
            searchQuery = searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            onFilterClick = onFilterClick
        )

        // Indicador de usuario arrendador
        AnimatedVisibility(
            visible = isOwner,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Modo Arrendador: Puedes agregar y gestionar propiedades",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchAndFilterBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Campo de búsqueda
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Buscar propiedades...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Buscar"
                )
            },
            modifier = Modifier.weight(1f),
            singleLine = true
        )

        // Botón de filtros
        OutlinedButton(
            onClick = onFilterClick,
            modifier = Modifier.wrapContentWidth()
        ) {
            Icon(
                imageVector = Icons.Outlined.FilterList,
                contentDescription = "Filtros",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Filtros")
        }
    }
}

@Composable
private fun PropertiesList(
    properties: List<Property>,
    isSubmittingComment: Boolean,
    lazyListState: androidx.compose.foundation.lazy.LazyListState,
    viewModel: RentsViewModel,
    onPropertyClick: (Property) -> Unit
) {
    LazyColumn(
        state = lazyListState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = properties,
            key = { it.id }
        ) { property ->
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
                    onPropertyClick(property)
                },
                isSubmittingComment = isSubmittingComment
            )
        }

        // Espacio adicional para que el último elemento no quede pegado al bottom
        item {
            Spacer(modifier = Modifier.height(80.dp)) // Espacio extra para el FAB
        }
    }
}

@Composable
private fun LoadingState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Cargando propiedades...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun ErrorState(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.ErrorOutline,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Error al cargar propiedades",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry
        ) {
            Text("Reintentar")
        }
    }
}

@Composable
private fun EmptyState(
    isOwner: Boolean,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (isOwner) Icons.Outlined.AddHome else Icons.Outlined.HourglassEmpty,
            contentDescription = "Sin propiedades",
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (isOwner) "¡Comienza agregando tu primera propiedad!" else "No hay propiedades disponibles",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (isOwner)
                "Como arrendador, puedes usar el botón + para agregar propiedades"
            else
                "Intenta ajustar tus filtros de búsqueda o verifica tu conexión a internet",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (!isOwner) {
            TextButton(
                onClick = onRetry
            ) {
                Text("Actualizar")
            }
        }
    }
}

// Dialog para filtros avanzados
@Composable
fun SearchFiltersDialog(
    onDismiss: () -> Unit,
    viewModel: RentsViewModel
) {
    val priceRange by viewModel.priceRange.collectAsState()
    val selectedPropertyType by viewModel.selectedPropertyType.collectAsState()
    val maxDistance by viewModel.maxDistance.collectAsState()

    var tempPriceRange by remember { mutableStateOf(priceRange) }
    var tempMaxDistance by remember { mutableStateOf(maxDistance ?: 10.0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filtros de búsqueda") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Rango de precio")
                RangeSlider(
                    value = tempPriceRange.first.toFloat()..tempPriceRange.second.toFloat(),
                    onValueChange = { range ->
                        tempPriceRange = Pair(range.start.toDouble(), range.endInclusive.toDouble())
                    },
                    valueRange = 0f..30000f,
                    steps = 30
                )
                Text("${tempPriceRange.first.toInt()} - ${tempPriceRange.second.toInt()}")

                Text("Distancia máxima (km)")
                Slider(
                    value = tempMaxDistance.toFloat(),
                    onValueChange = { tempMaxDistance = it.toDouble() },
                    valueRange = 0f..20f,
                    steps = 20
                )
                Text("${tempMaxDistance.toInt()} km")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                viewModel.updatePriceRange(tempPriceRange.first, tempPriceRange.second)
                viewModel.updateMaxDistance(tempMaxDistance)
                onDismiss()
            }) {
                Text("Aplicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}