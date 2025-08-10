package com.undefined.farfaraway.presentation.shared.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FoodBank
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.undefined.farfaraway.domain.entities.ExpenseCategory
import com.undefined.farfaraway.domain.entities.IncomeFrequency
import com.undefined.farfaraway.presentation.features.finance.finance.FinanceViewModel


@Composable
fun IncomeDialog(
    viewModel: FinanceViewModel,
    onDismiss: () -> Unit
) {
    val incomeAmount by viewModel.incomeAmount.collectAsState()
    val incomeFrequency by viewModel.incomeFrequency.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Configurar Ingresos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Campo de monto
                OutlinedTextField(
                    value = incomeAmount,
                    onValueChange = { viewModel.updateIncomeAmount(it) },
                    label = { Text("Monto") },
                    placeholder = { Text("Ej: 8000") },
                    leadingIcon = {
                        Text(
                            text = "$",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Selector de frecuencia
                Column {
                    Text(
                        text = "Frecuencia de ingreso",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = incomeFrequency == IncomeFrequency.WEEKLY,
                            onClick = { viewModel.updateIncomeFrequency(IncomeFrequency.WEEKLY) },
                            label = { Text("Semanal") },
                            leadingIcon = if (incomeFrequency == IncomeFrequency.WEEKLY) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null
                        )
                        FilterChip(
                            selected = incomeFrequency == IncomeFrequency.MONTHLY,
                            onClick = { viewModel.updateIncomeFrequency(IncomeFrequency.MONTHLY) },
                            label = { Text("Mensual") },
                            leadingIcon = if (incomeFrequency == IncomeFrequency.MONTHLY) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null
                        )
                    }
                }

                // Información adicional
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (incomeFrequency == IncomeFrequency.WEEKLY) {
                                "Se calculará tu ingreso mensual automáticamente"
                            } else {
                                "Se calculará tu ingreso semanal automáticamente"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.saveIncome()
                },
                enabled = incomeAmount.isNotBlank()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun ExpenseDialog(
    viewModel: FinanceViewModel,
    onDismiss: () -> Unit
) {
    val expenseAmount by viewModel.expenseAmount.collectAsState()
    val expenseCategory by viewModel.expenseCategory.collectAsState()
    val expenseDescription by viewModel.expenseDescription.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Agregar Gasto",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.heightIn(max = 400.dp)
            ) {
                item {
                    // Campo de monto
                    OutlinedTextField(
                        value = expenseAmount,
                        onValueChange = { viewModel.updateExpenseAmount(it) },
                        label = { Text("Monto del gasto") },
                        placeholder = { Text("Ej: 150") },
                        leadingIcon = {
                            Text(
                                text = "$",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    // Campo de descripción
                    OutlinedTextField(
                        value = expenseDescription,
                        onValueChange = { viewModel.updateExpenseDescription(it) },
                        label = { Text("Descripción (opcional)") },
                        placeholder = { Text("Ej: Comida del día") },
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    // Selector de categoría
                    Text(
                        text = "Categoría del gasto",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                // Grid de categorías
                items(ExpenseCategory.values().toList().chunked(2)
                ) { categoryPair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categoryPair.forEach { category ->
                            CategoryChip(
                                category = category,
                                isSelected = expenseCategory == category,
                                onClick = { viewModel.updateExpenseCategory(category) },
                                getCategoryName = { cat -> viewModel.getCategoryName(cat) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Si solo hay una categoría en esta fila, agregar un spacer
                        if (categoryPair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.saveExpense()
                },
                enabled = expenseAmount.isNotBlank()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun CategoryChip(
    category: ExpenseCategory,
    isSelected: Boolean,
    onClick: () -> Unit,
    getCategoryName: (ExpenseCategory) -> String,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = getCategoryName(category),
                style = MaterialTheme.typography.bodySmall
            )
        },
        leadingIcon = {
            Icon(
                imageVector = getCategoryIcon(category),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        },
        modifier = modifier
    )
}

fun getCategoryIcon(category: ExpenseCategory): ImageVector {
    return when (category) {
        ExpenseCategory.FOOD -> Icons.Default.FoodBank
        ExpenseCategory.SHOPPING -> Icons.Default.ShoppingCart
        // Agrega más casos para tus categorías
        else -> Icons.Default.ShoppingCart
    }
}