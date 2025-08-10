package com.undefined.farfaraway.presentation.features.finance.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.undefined.farfaraway.domain.entities.*
import com.undefined.farfaraway.presentation.shared.components.ExpenseDialog
import com.undefined.farfaraway.presentation.shared.components.IncomeDialog
import com.undefined.farfaraway.presentation.shared.navigation.enums.Routes
import java.text.NumberFormat
import java.util.*

@Composable
fun FinanceContent(
    paddingValues: PaddingValues,
    navController: NavController,
    viewModel: FinanceViewModel = hiltViewModel()
) {
    val financialProfile by viewModel.financialProfile.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val financialSummary by viewModel.financialSummary.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val showIncomeDialog by viewModel.showIncomeDialog.collectAsState()
    val showExpenseDialog by viewModel.showExpenseDialog.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Mostrar error si existe
    errorMessage?.let { error ->
        LaunchedEffect(error) {
            // Aquí puedes mostrar un snackbar o toast
        }
    }

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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Header con resumen financiero
            item {
                FinancialHeaderCard(
                    financialProfile = financialProfile,
                    financialSummary = financialSummary,
                    onUpdateIncome = { viewModel.showIncomeDialog() }
                )
            }

            // Botones de acción
            item {
                ActionButtonsRow(
                    onAddIncome = { viewModel.showIncomeDialog() },
                    onAddExpense = { viewModel.showExpenseDialog() },
                    onViewReport = {
                        // Navegar a la pantalla de rubrica
                        navController.navigate(Routes.BILLS.name)
                    }
                )
            }

            // Lista de gastos
            item {
                ExpensesSectionHeader(totalExpenses = expenses.size)
            }

            if (expenses.isNotEmpty()) {
                items(expenses) { expense ->
                    ExpenseItem(
                        expense = expense,
                        getCategoryName = { category ->
                            viewModel.getCategoryName(ExpenseCategory.valueOf(category))
                        }
                    )
                }
            } else {
                item {
                    EmptyExpensesCard(
                        onAddExpense = { viewModel.showExpenseDialog() }
                    )
                }
            }
        }
    }

    // Diálogos
    if (showIncomeDialog) {
        IncomeDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.hideIncomeDialog() }
        )
    }

    if (showExpenseDialog) {
        ExpenseDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.hideExpenseDialog() }
        )
    }
}

@Composable
fun FinancialHeaderCard(
    financialProfile: FinancialProfile?,
    financialSummary: FinancialSummary?,
    onUpdateIncome: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ingreso mensual
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Ingreso ${if (financialProfile?.incomeFrequency == IncomeFrequency.WEEKLY.name) "Semanal" else "Mensual"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
                IconButton(
                    onClick = onUpdateIncome,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar ingreso",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Text(
                text = formatCurrency(
                    if (financialProfile?.incomeFrequency == IncomeFrequency.WEEKLY.name)
                        financialProfile?.weeklyIncome ?: 0.0
                    else
                        financialProfile?.monthlyIncome ?: 0.0
                ),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Resumen rápido
            if (financialSummary != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FinancialSummaryItem(
                        title = "Gastado",
                        amount = financialSummary.totalExpenses,
                        icon = Icons.Default.TrendingDown,
                        color = MaterialTheme.colorScheme.error
                    )
                    FinancialSummaryItem(
                        title = "Disponible",
                        amount = financialSummary.remainingBudget,
                        icon = Icons.Default.TrendingUp,
                        color = if (financialSummary.remainingBudget >= 0)
                            MaterialTheme.colorScheme.tertiary
                        else
                            MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun FinancialSummaryItem(
    title: String,
    amount: Double,
    icon: ImageVector,
    color: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
        }
        Text(
            text = formatCurrency(amount),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun ActionButtonsRow(
    onAddIncome: () -> Unit,
    onAddExpense: () -> Unit,
    onViewReport: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onAddIncome,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Ingreso")
        }

        Button(
            onClick = onAddExpense,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Gasto")
        }

        OutlinedButton(
            onClick = onViewReport,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Assessment,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Reporte")
        }
    }
}

@Composable
fun ExpensesSectionHeader(totalExpenses: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Historial de Gastos",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "$totalExpenses ${if (totalExpenses == 1) "gasto" else "gastos"}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun ExpenseItem(
    expense: Expense,
    getCategoryName: (String) -> String
) {
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
                    .background(getCategoryColor(ExpenseCategory.valueOf(expense.category)).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(ExpenseCategory.valueOf(expense.category)),
                    contentDescription = null,
                    tint = getCategoryColor(ExpenseCategory.valueOf(expense.category)),
                    modifier = Modifier.size(24.dp)
                )
            }

            // Información del gasto
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = getCategoryName(expense.category),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (expense.description.isNotEmpty()) {
                    Text(
                        text = expense.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                if (expense.isRecurring) {
                    Text(
                        text = "Gasto recurrente",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Monto
            Text(
                text = formatCurrency(expense.amount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun EmptyExpensesCard(
    onAddExpense: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.Receipt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No hay gastos registrados",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Agrega tu primer gasto para comenzar a administrar tu dinero",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onAddExpense
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Agregar Gasto")
            }
        }
    }
}

// Funciones auxiliares
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
private fun getCategoryColor(category: ExpenseCategory): androidx.compose.ui.graphics.Color {
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