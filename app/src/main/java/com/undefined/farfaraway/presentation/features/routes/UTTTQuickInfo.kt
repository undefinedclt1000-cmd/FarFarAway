package com.undefined.farfaraway.presentation.features.routes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.undefined.farfaraway.domain.entities.TransportRoute




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UTTTQuickInfoCompact(
    routes: List<TransportRoute>,
    modifier: Modifier = Modifier
) {
    val tulaTepejiRoute = routes.find { it.routeNumber == "Tula-Tepeji" }
    val nextTulaTepeji = tulaTepejiRoute?.let { getNextSchedule(it.schedule) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp) // menos padding
        ) {
            // Título
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(20.dp) // más pequeño
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "UTTT - Info Rápida",
                    style = MaterialTheme.typography.bodyMedium, // tipografía más compacta
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(6.dp)) // menos separación

            // Siguiente camión
            if (tulaTepejiRoute != null && nextTulaTepeji != null) {
                UTTTRouteQuickCardCompact(
                    title = "🚌 Próximo a UTTT",
                    routeName = "Tula-Tepeji",
                    time = nextTulaTepeji.departureTime,
                    frequency = "Cada 15 min",
                    fare = "$${tulaTepejiRoute.fare}",
                    isHighlighted = true
                )

                Spacer(modifier = Modifier.height(6.dp))
            }

            // Chips más pequeños
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickInfoChipCompact(
                    icon = Icons.Default.AccessTime,
                    label = "Primer",
                    value = "6:30 AM",
                    color = MaterialTheme.colorScheme.secondary
                )
                QuickInfoChipCompact(
                    icon = Icons.Default.Schedule,
                    label = "Último",
                    value = "9:30 PM",
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Composable
fun UTTTRouteQuickCardCompact(
    title: String,
    routeName: String,
    time: String,
    frequency: String,
    fare: String,
    isHighlighted: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlighted)
                MaterialTheme.colorScheme.secondary
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp), // menos padding
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isHighlighted)
                        MaterialTheme.colorScheme.onSecondary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = routeName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isHighlighted)
                        MaterialTheme.colorScheme.onSecondary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = frequency,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isHighlighted)
                        MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.8f)
                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = time,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isHighlighted)
                        MaterialTheme.colorScheme.onSecondary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = fare,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isHighlighted)
                        MaterialTheme.colorScheme.onSecondary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun QuickInfoChipCompact(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                color.copy(alpha = 0.08f),
                RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UTTTQuickInfo(
    routes: List<TransportRoute>,
    modifier: Modifier = Modifier
) {
    val tulaTepejiRoute = routes.find { it.routeNumber == "Tula-Tepeji" }
    val nextTulaTepeji = tulaTepejiRoute?.let { getNextSchedule(it.schedule) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "UTTT - Info Rápida",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (tulaTepejiRoute != null && nextTulaTepeji != null) {
                UTTTRouteQuickCard(
                    title = "🚌 Próximo a UTTT",
                    routeName = "Tula-Tepeji",
                    time = nextTulaTepeji.departureTime,
                    frequency = "Cada 15 min",
                    fare = "$${tulaTepejiRoute.fare}",
                    isHighlighted = true
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickInfoChip(
                    icon = Icons.Default.AccessTime,
                    label = "Primer camión",
                    value = "6:30 AM",
                    color = MaterialTheme.colorScheme.secondary
                )
                QuickInfoChip(
                    icon = Icons.Default.Schedule,
                    label = "Último camión",
                    value = "9:30 PM",
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Composable
fun UTTTRouteQuickCard(
    title: String,
    routeName: String,
    time: String,
    frequency: String,
    fare: String,
    isHighlighted: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlighted)
                MaterialTheme.colorScheme.secondary
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isHighlighted)
                        MaterialTheme.colorScheme.onSecondary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = routeName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isHighlighted)
                        MaterialTheme.colorScheme.onSecondary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = frequency,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isHighlighted)
                        MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.8f)
                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = time,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isHighlighted)
                        MaterialTheme.colorScheme.onSecondary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = fare,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isHighlighted)
                        MaterialTheme.colorScheme.onSecondary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun QuickInfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(
                color.copy(alpha = 0.1f),
                RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
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
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun UTTTScheduleAlert(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "⚠️ Horario Importante",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Text(
                    text = "Último camión a UTTT: 9:30 PM",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

// Función para mostrar el tiempo estimado hasta el próximo camión
@Composable
fun NextBusCountdown(
    nextSchedule: String,
    modifier: Modifier = Modifier
) {
    val currentTime = remember { System.currentTimeMillis() }
    var timeLeft by remember { mutableStateOf("") }

    LaunchedEffect(nextSchedule) {
        // Aquí calcularías el tiempo restante
        // Por simplicidad, mostramos un ejemplo
        timeLeft = "En 12 minutos"
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsBus,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Próximo camión",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = timeLeft,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}