package com.undefined.farfaraway.presentation.features.routes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.undefined.farfaraway.domain.entities.*
import com.undefined.farfaraway.presentation.shared.navigation.enums.Routes
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutesContent(
    paddingValues: PaddingValues,
    navController: NavController,
    viewModel: RoutesViewModel = hiltViewModel()
) {
    val routes by viewModel.routes.collectAsState()
    val selectedRoute by viewModel.selectedRoute.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showMapView by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Barra de búsqueda y filtros
        SearchAndFilterBar(
            searchQuery = searchQuery,
            onSearchChange = { searchQuery = it },
            onMapToggle = { showMapView = !showMapView },
            showMapView = showMapView
        )

        // Información rápida de UTTT
        if (!showMapView && routes.isNotEmpty()) {
            UTTTQuickInfoCompact(
                routes = routes,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            if (showMapView) {
                // Vista de mapa con rutas
                RouteMapView(
                    routes = routes,
                    selectedRoute = selectedRoute,
                    onRouteSelect = viewModel::selectRoute,
                    onSelectedSchool = { navController.navigate(Routes.SCHOOL.name) },
                    modifier = Modifier.weight(1f)
                )
            } else {
                // Lista de rutas
                RoutesListView(
                    routes = routes.filter {
                        it.routeName.contains(searchQuery, ignoreCase = true) ||
                                it.routeNumber.contains(searchQuery, ignoreCase = true)
                    },
                    onRouteClick = { route ->
                        viewModel.selectRoute(route)
                        showMapView = true
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            // Panel inferior con detalles de la ruta seleccionada
            selectedRoute?.let { route ->
                RouteDetailsBottomSheet(
                    route = route,
                    onDismiss = { viewModel.clearSelection() },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAndFilterBar(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onMapToggle: () -> Unit,
    showMapView: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(50),
        tonalElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Buscar ruta") },
                modifier = Modifier
                    .weight(1f)
                    .background(Color.Transparent),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onMapToggle,
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (showMapView) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant,
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (showMapView) Icons.Default.List else Icons.Default.Map,
                    contentDescription = "Toggle view",
                    tint = if (showMapView) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun RoutesListView(
    routes: List<TransportRoute>,
    onRouteClick: (TransportRoute) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(routes) { route ->
            RouteCard(
                route = route,
                onClick = { onRouteClick(route) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteCard(
    route: TransportRoute,
    onClick: () -> Unit
) {
    val transportIcon = when (TransportType.valueOf(route.transportType)) {
        TransportType.BUS -> Icons.Default.DirectionsBus
        TransportType.MICRO -> Icons.Default.DirectionsBus
        TransportType.SHARED_TAXI -> Icons.Default.LocalTaxi
        TransportType.METRO -> Icons.Default.Train
        TransportType.OTHER -> Icons.Default.DirectionsBus
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = transportIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = route.routeNumber,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = route.routeName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "$${route.fare}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "${route.estimatedDuration} min",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Ruta origen - destino
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    RoundedCornerShape(4.dp)
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = route.origin.name.ifEmpty { route.origin.address },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    MaterialTheme.colorScheme.secondary,
                                    RoundedCornerShape(4.dp)
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = route.destination.name.ifEmpty { route.destination.address },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Próximo horario
            if (route.schedule.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))

                val nextSchedule = getNextSchedule(route.schedule)
                nextSchedule?.let { schedule ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Próximo: ${schedule.departureTime}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RouteMapView(
    routes: List<TransportRoute>,
    selectedRoute: TransportRoute?,
    onRouteSelect: (TransportRoute) -> Unit,
    onSelectedSchool: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            selectedRoute?.origin?.let {
                LatLng(it.latitude, it.longitude)
            } ?: LatLng(20.008986, -99.342964),
            12f
        )
    }



    Box(modifier = modifier) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                mapType = MapType.NORMAL,
                isTrafficEnabled = true
            )
        ) {

            val defaultPosition = LatLng(20.008986, -99.342964)


            val markerState = remember {
                MarkerState(position = defaultPosition)
            }

            Marker(
                state = markerState,
                onClick = {
                    onSelectedSchool()
                    true
                },
                title = "UNIVERSIDAD TECNOLÓGICA DE TULA-TEPEJI",
                snippet = "Origen por defecto"
            )

            // Marcadores para todas las rutas
            routes.forEach { route ->
                // Marcador origen
                Marker(
                    state = MarkerState(
                        position = LatLng(route.origin.latitude, route.origin.longitude)
                    ),
                    title = "${route.routeNumber} - Origen",
                    snippet = route.origin.name.ifEmpty { route.origin.address },
                    onClick = {
                        onRouteSelect(route)
                        true
                    }
                )

                // Marcador destino
                Marker(
                    state = MarkerState(
                        position = LatLng(route.destination.latitude, route.destination.longitude)
                    ),
                    title = "${route.routeNumber} - Destino",
                    snippet = route.destination.name.ifEmpty { route.destination.address },
                    onClick = {
                        onRouteSelect(route)
                        true
                    }
                )

                // Paradas intermedias
                route.stops.forEach { stop ->
                    Marker(
                        state = MarkerState(
                            position = LatLng(stop.location.latitude, stop.location.longitude)
                        ),
                        title = stop.name,
                        snippet = "Parada - ${route.routeNumber}"
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailsBottomSheet(
    route: TransportRoute,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Handle para arrastrar
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                        RoundedCornerShape(2.dp)
                    )
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${route.routeNumber} - ${route.routeName}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Horarios del día actual
            if (route.schedule.isNotEmpty()) {
                Text(
                    text = "Horarios de hoy",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                val todaySchedules = getTodaySchedules(route.schedule)
                LazyColumn(
                    modifier = Modifier.heightIn(max = 200.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(todaySchedules) { schedule ->
                        ScheduleItem(schedule = schedule)
                    }
                }
            }

            // Información adicional
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoChip(
                    icon = Icons.Default.AttachMoney,
                    label = "Tarifa",
                    value = "$${route.fare}"
                )
                InfoChip(
                    icon = Icons.Default.Schedule,
                    label = "Duración",
                    value = "${route.estimatedDuration} min"
                )
                InfoChip(
                    icon = Icons.Default.LocationOn,
                    label = "Paradas",
                    value = "${route.stops.size}"
                )
            }
        }
    }
}

@Composable
fun ScheduleItem(schedule: ScheduleTime) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Schedule,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = schedule.departureTime,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

// Funciones auxiliares
fun getNextSchedule(schedules: List<ScheduleTime>): ScheduleTime? {
    val currentDay = getCurrentDayOfWeek()
    val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

    return schedules
        .filter { it.dayOfWeek == currentDay.name }
        .sortedBy { it.departureTime }
        .firstOrNull { it.departureTime > currentTime }
}

fun getTodaySchedules(schedules: List<ScheduleTime>): List<ScheduleTime> {
    val currentDay = getCurrentDayOfWeek()
    return schedules
        .filter { it.dayOfWeek == currentDay.name }
        .sortedBy { it.departureTime }
}

fun getCurrentDayOfWeek(): DayOfWeek {
    return when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> DayOfWeek.MONDAY
        Calendar.TUESDAY -> DayOfWeek.TUESDAY
        Calendar.WEDNESDAY -> DayOfWeek.WEDNESDAY
        Calendar.THURSDAY -> DayOfWeek.THURSDAY
        Calendar.FRIDAY -> DayOfWeek.FRIDAY
        Calendar.SATURDAY -> DayOfWeek.SATURDAY
        Calendar.SUNDAY -> DayOfWeek.SUNDAY
        else -> DayOfWeek.MONDAY
    }
}