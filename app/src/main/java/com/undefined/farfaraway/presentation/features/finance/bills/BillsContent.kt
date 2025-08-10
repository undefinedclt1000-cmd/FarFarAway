package com.undefined.farfaraway.presentation.features.finance.bills

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.undefined.farfaraway.domain.entities.*
import java.text.NumberFormat
import java.util.*

@Composable
fun BillsContent(
    paddingValues: PaddingValues,
    navController: NavController,
    viewModel: BillsViewModel = hiltViewModel()
) {
    val financialProfile by viewModel.financialProfile.collectAsState()
    val financialSummary by viewModel.financialSummary.collectAsState()
    val expensesByCategory by viewModel.expensesByCategory.collectAsState()
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
    } else if (financialProfile == null || financialSummary == null) {
        EmptyFinancialDataCard(paddingValues = paddingValues)
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Header con estado financiero general
            item {
                FinancialStatusCard(
                    financialProfile = financialProfile!!,
                    financialSummary = financialSummary!!
                )
            }

            // Progreso del presupuesto
            item {
                BudgetProgressCard(
                    financialSummary = financialSummary!!
                )
            }

            // Rúbrica por categorías
            item {
                Text(
                    text = "Rúbrica de Gastos por Categoría",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            if (expensesByCategory.isNotEmpty()) {
                items(expensesByCategory.toList()) { (category, amount) ->
                    CategoryRubricCard(
                        category = ExpenseCategory.valueOf(category),
                        amount = amount,
                        totalBudget = financialProfile!!.monthlyIncome,
                        getCategoryName = { viewModel.getCategoryName(it) }
                    )
                }
            }

            // Recomendaciones
            item {
                RecommendationsCard(
                    financialSummary = financialSummary!!
                )
            }
        }
    }
}

@Composable
fun FinancialStatusCard(
    financialProfile: FinancialProfile,
    financialSummary: FinancialSummary
) {
    val statusInfo = getFinancialStatusInfo(financialSummary.budgetStatus)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = statusInfo.backgroundColor
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = statusInfo.icon,
                contentDescription = null,
                tint = statusInfo.iconColor,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = statusInfo.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = statusInfo.textColor,
                textAlign = TextAlign.Center
            )

            Text(
                text = statusInfo.description,
                style = MaterialTheme.typography.bodyMedium,
                color = statusInfo.textColor.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Resumen de números
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FinancialMetricItem(
                    label = "Ingreso",
                    amount = financialSummary.totalIncome,
                    textColor = statusInfo.textColor
                )
                FinancialMetricItem(
                    label = "Gastado",
                    amount = financialSummary.totalExpenses,
                    textColor = statusInfo.textColor
                )
                FinancialMetricItem(
                    label = "Disponible",
                    amount = financialSummary.remainingBudget,
                    textColor = statusInfo.textColor
                )
            }
        }
    }
}

@Composable
fun BudgetProgressCard(
    financialSummary: FinancialSummary
) {
    val progressPercentage = if (financialSummary.totalIncome > 0) {
        (financialSummary.totalExpenses / financialSummary.totalIncome).coerceIn(0.0, 1.0)
    } else 0.0

    val progressColor = when {
        progressPercentage <= 0.7 -> MaterialTheme.colorScheme.primary
        progressPercentage <= 0.9 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.error
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progreso del Presupuesto",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${(progressPercentage * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = progressColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progressPercentage.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = progressColor,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Has gastado ${formatCurrency(financialSummary.totalExpenses)} de ${formatCurrency(financialSummary.totalIncome)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun CategoryRubricCard(
    category: ExpenseCategory,
    amount: Double,
    totalBudget: Double,
    getCategoryName: (ExpenseCategory) -> String
) {
    val percentage = if (totalBudget > 0) (amount / totalBudget) * 100 else 0.0
    val rubric = getCategoryRubric(category, percentage)

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icono de categoría
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(getCategoryColor(category).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(category),
                    contentDescription = null,
                    tint = getCategoryColor(category),
                    modifier = Modifier.size(24.dp)
                )
            }

            // Información de la categoría
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = getCategoryName(category),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = formatCurrency(amount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${percentage.toInt()}% del ingreso total",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            // Calificación
            RubricBadge(
                grade = rubric.grade,
                color = rubric.color
            )
        }
    }
}

@Composable
fun RubricBadge(
    grade: String,
    color: Color
) {
    Surface(
        modifier = Modifier.size(40.dp),
        shape = CircleShape,
        color = color.copy(alpha = 0.1f)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = grade,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun RecommendationsCard(
    financialSummary: FinancialSummary
) {
    val recommendations = generateRecommendations(financialSummary)

    if (recommendations.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Recomendaciones",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                recommendations.forEachIndexed { index, recommendation ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "•",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = recommendation,
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
fun FinancialMetricItem(
    label: String,
    amount: Double,
    textColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = textColor.copy(alpha = 0.8f)
        )
        Text(
            text = formatCurrency(amount),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun EmptyFinancialDataCard(
    paddingValues: PaddingValues
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Outlined.Assessment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Sin datos financieros",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Regresa a la pantalla de finanzas para configurar tu perfil e ingresos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// Funciones auxiliares
data class FinancialStatusInfo(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val backgroundColor: Color,
    val iconColor: Color,
    val textColor: Color
)

@Composable
private fun getFinancialStatusInfo(budgetStatus: String): FinancialStatusInfo {
    return when (BudgetStatus.valueOf(budgetStatus)) {
        BudgetStatus.EXCELLENT -> FinancialStatusInfo(
            title = "¡Excelente!",
            description = "Estás manejando muy bien tus finanzas",
            icon = Icons.Default.EmojiEvents,
            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
            iconColor = MaterialTheme.colorScheme.primary,
            textColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
        BudgetStatus.ON_TRACK -> FinancialStatusInfo(
            title = "Vas Bien",
            description = "Estás dentro de tu presupuesto",
            icon = Icons.Default.CheckCircle,
            backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
            iconColor = MaterialTheme.colorScheme.tertiary,
            textColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
        BudgetStatus.WARNING -> FinancialStatusInfo(
            title = "Advertencia",
            description = "Cuidado, estás cerca del límite",
            icon = Icons.Default.Warning,
            backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
            iconColor = MaterialTheme.colorScheme.secondary,
            textColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
        BudgetStatus.OVER_BUDGET -> FinancialStatusInfo(
            title = "Te Pasaste",
            description = "Has excedido tu presupuesto",
            icon = Icons.Default.Error,
            backgroundColor = MaterialTheme.colorScheme.errorContainer,
            iconColor = MaterialTheme.colorScheme.error,
            textColor = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}

data class CategoryRubric(
    val grade: String,
    val color: Color
)

@Composable
private fun getCategoryRubric(category: ExpenseCategory, percentage: Double): CategoryRubric {
    val thresholds = getCategoryThresholds(category)

    return when {
        percentage <= thresholds.excellent -> CategoryRubric("A", MaterialTheme.colorScheme.primary)
        percentage <= thresholds.good -> CategoryRubric("B", MaterialTheme.colorScheme.tertiary)
        percentage <= thresholds.warning -> CategoryRubric("C", MaterialTheme.colorScheme.secondary)
        else -> CategoryRubric("D", MaterialTheme.colorScheme.error)
    }
}

data class CategoryThresholds(
    val excellent: Double,
    val good: Double,
    val warning: Double
)

private fun getCategoryThresholds(category: ExpenseCategory): CategoryThresholds {
    return when (category) {
        ExpenseCategory.RENT -> CategoryThresholds(30.0, 40.0, 50.0)
        ExpenseCategory.FOOD -> CategoryThresholds(15.0, 25.0, 35.0)
        ExpenseCategory.TRANSPORTATION -> CategoryThresholds(10.0, 15.0, 25.0)
        ExpenseCategory.UTILITIES -> CategoryThresholds(8.0, 12.0, 18.0)
        ExpenseCategory.EDUCATION -> CategoryThresholds(10.0, 20.0, 30.0)
        ExpenseCategory.HEALTH -> CategoryThresholds(5.0, 10.0, 15.0)
        ExpenseCategory.ENTERTAINMENT -> CategoryThresholds(5.0, 10.0, 15.0)
        ExpenseCategory.SHOPPING -> CategoryThresholds(5.0, 10.0, 20.0)
        ExpenseCategory.SAVINGS -> CategoryThresholds(10.0, 20.0, 30.0)
        ExpenseCategory.OTHER -> CategoryThresholds(5.0, 10.0, 15.0)
    }
}

private fun generateRecommendations(financialSummary: FinancialSummary): List<String> {
    val recommendations = mutableListOf<String>()
    val expensePercentages = financialSummary.expensesByCategory.mapValues {
        (it.value / financialSummary.totalIncome) * 100
    }

    // Recomendaciones basadas en porcentajes de gastos
    expensePercentages.forEach { (category, percentage) ->
        val categoryEnum = ExpenseCategory.valueOf(category)
        val thresholds = getCategoryThresholds(categoryEnum)

        if (percentage > thresholds.warning) {
            when (categoryEnum) {
                ExpenseCategory.RENT -> recommendations.add("Considera buscar una vivienda más económica o un roommate")
                ExpenseCategory.FOOD -> recommendations.add("Intenta cocinar más en casa y reducir las comidas fuera")
                ExpenseCategory.TRANSPORTATION -> recommendations.add("Evalúa opciones más económicas de transporte")
                ExpenseCategory.UTILITIES -> recommendations.add("Revisa tus consumos de luz, agua y gas")
                ExpenseCategory.ENTERTAINMENT -> recommendations.add("Reduce los gastos de entretenimiento temporalmente")
                ExpenseCategory.SHOPPING -> recommendations.add("Evita compras innecesarias por un tiempo")
                else -> recommendations.add("Revisa tus gastos en ${getCategoryNameStatic(categoryEnum)}")
            }
        }
    }

    // Recomendaciones generales
    when (BudgetStatus.valueOf(financialSummary.budgetStatus)) {
        BudgetStatus.OVER_BUDGET -> {
            recommendations.add("Urgente: Revisa todos tus gastos y elimina los no esenciales")
            recommendations.add("Considera generar ingresos adicionales")
        }
        BudgetStatus.WARNING -> {
            recommendations.add("Mantén un control estricto de tus gastos este mes")
        }
        BudgetStatus.ON_TRACK -> {
            recommendations.add("¡Buen trabajo! Intenta ahorrar el dinero que te sobra")
        }
        BudgetStatus.EXCELLENT -> {
            recommendations.add("¡Excelente manejo! Considera invertir tus ahorros")
        }
    }

    return recommendations.take(3) // Máximo 3 recomendaciones
}

private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    return format.format(amount)
}

private fun getCategoryIcon(category: ExpenseCategory): ImageVector {
    return when (category) {
        ExpenseCategory.RENT -> Icons.Default.Home
        ExpenseCategory.TRANSPORTATION -> Icons.Default.DirectionsBus
        ExpenseCategory.FOOD -> Icons.Default.Restaurant
        ExpenseCategory.EDUCATION -> Icons.Default.School
        ExpenseCategory.ENTERTAINMENT -> Icons.Default.MovieCreation
        ExpenseCategory.UTILITIES -> Icons.Default.ElectricBolt
        ExpenseCategory.HEALTH -> Icons.Default.LocalHospital
        ExpenseCategory.SHOPPING -> Icons.Default.ShoppingCart
        ExpenseCategory.SAVINGS -> Icons.Default.Savings
        ExpenseCategory.OTHER -> Icons.Default.MoreHoriz
    }
}

@Composable
private fun getCategoryColor(category: ExpenseCategory): Color {
    return when (category) {
        ExpenseCategory.RENT -> MaterialTheme.colorScheme.primary
        ExpenseCategory.TRANSPORTATION -> MaterialTheme.colorScheme.secondary
        ExpenseCategory.FOOD -> MaterialTheme.colorScheme.tertiary
        ExpenseCategory.EDUCATION -> MaterialTheme.colorScheme.error
        ExpenseCategory.ENTERTAINMENT -> MaterialTheme.colorScheme.outline
        ExpenseCategory.UTILITIES -> MaterialTheme.colorScheme.inversePrimary
        ExpenseCategory.HEALTH -> MaterialTheme.colorScheme.errorContainer
        ExpenseCategory.SHOPPING -> MaterialTheme.colorScheme.secondaryContainer
        ExpenseCategory.SAVINGS -> MaterialTheme.colorScheme.tertiaryContainer
        ExpenseCategory.OTHER -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    }
}

private fun getCategoryNameStatic(category: ExpenseCategory): String {
    return when (category) {
        ExpenseCategory.RENT -> "Renta"
        ExpenseCategory.TRANSPORTATION -> "Transporte"
        ExpenseCategory.FOOD -> "Comida"
        ExpenseCategory.EDUCATION -> "Escuela"
        ExpenseCategory.ENTERTAINMENT -> "Entretenimiento"
        ExpenseCategory.UTILITIES -> "Servicios"
        ExpenseCategory.HEALTH -> "Salud"
        ExpenseCategory.SHOPPING -> "Compras"
        ExpenseCategory.SAVINGS -> "Ahorros"
        ExpenseCategory.OTHER -> "Otros"
    }
}