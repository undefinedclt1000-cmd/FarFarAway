package com.undefined.farfaraway.presentation.features.finance.bills

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undefined.farfaraway.core.Constants
import com.undefined.farfaraway.domain.entities.*
import com.undefined.farfaraway.domain.useCases.dataStore.DataStoreUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BillsViewModel @Inject constructor(
    private val dataStoreUseCases: DataStoreUseCases
    // Aquí agregarás los use cases de finanzas cuando los implementes
    // private val financeUseCases: FinanceUseCases
): ViewModel() {

    // Estado del perfil financiero del usuario
    private val _financialProfile = MutableStateFlow<FinancialProfile?>(null)
    val financialProfile: StateFlow<FinancialProfile?> = _financialProfile.asStateFlow()

    // Estado del resumen financiero
    private val _financialSummary = MutableStateFlow<FinancialSummary?>(null)
    val financialSummary: StateFlow<FinancialSummary?> = _financialSummary.asStateFlow()

    // Estado de los gastos por categoría
    private val _expensesByCategory = MutableStateFlow<Map<String, Double>>(emptyMap())
    val expensesByCategory: StateFlow<Map<String, Double>> = _expensesByCategory.asStateFlow()

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Estado de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _totalItems = MutableStateFlow(0)
    val totalItems: StateFlow<Int> = _totalItems

    init {
        loadFinancialRubricData()
    }

    private fun loadFinancialRubricData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Por ahora cargamos datos de ejemplo
                loadSampleRubricData()

                // Código real para cuando implementes los use cases:
                /*
                val userId = dataStoreUseCases.getDataString(Constants.USER_UID)
                if (userId.isNotEmpty()) {
                    launch { loadFinancialProfile(userId) }
                    launch { loadFinancialSummary(userId) }
                    launch { loadExpensesByCategory(userId) }
                }
                */
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar la rúbrica financiera: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadSampleRubricData() {
        // Perfil financiero de ejemplo
        _financialProfile.value = FinancialProfile(
            id = "fp_1",
            userId = "user_1",
            monthlyIncome = 8000.0,
            weeklyIncome = 2000.0,
            incomeFrequency = IncomeFrequency.MONTHLY.name,
            isActive = true
        )

        // Gastos por categoría de ejemplo
        val expensesByCategory = mapOf(
            ExpenseCategory.RENT.name to 3500.0,
            ExpenseCategory.TRANSPORTATION.name to 800.0,
            ExpenseCategory.FOOD.name to 1200.0,
            ExpenseCategory.EDUCATION.name to 500.0,
            ExpenseCategory.UTILITIES.name to 400.0,
            ExpenseCategory.ENTERTAINMENT.name to 300.0,
            ExpenseCategory.HEALTH.name to 200.0,
            ExpenseCategory.SHOPPING.name to 350.0,
            ExpenseCategory.SAVINGS.name to 0.0,
            ExpenseCategory.OTHER.name to 150.0
        )

        _expensesByCategory.value = expensesByCategory.filter { it.value > 0 }

        // Calcular resumen financiero
        val totalExpenses = expensesByCategory.values.sum()
        val totalIncome = _financialProfile.value?.monthlyIncome ?: 0.0
        val remainingBudget = totalIncome - totalExpenses

        // Determinar estado del presupuesto
        val budgetStatus = when {
            remainingBudget > totalIncome * 0.2 -> BudgetStatus.EXCELLENT
            remainingBudget > 0 -> BudgetStatus.ON_TRACK
            remainingBudget > -totalIncome * 0.1 -> BudgetStatus.WARNING
            else -> BudgetStatus.OVER_BUDGET
        }

        _financialSummary.value = FinancialSummary(
            id = "fs_1",
            userId = "user_1",
            period = BudgetPeriod.MONTHLY.name,
            totalIncome = totalIncome,
            totalExpenses = totalExpenses,
            remainingBudget = remainingBudget,
            expensesByCategory = expensesByCategory,
            budgetStatus = budgetStatus.name,
            recommendations = generateRecommendations(totalIncome, expensesByCategory, budgetStatus),
            periodStart = Date(),
            periodEnd = Date()
        )

        _totalItems.value = expensesByCategory.size
    }

    private fun generateRecommendations(
        totalIncome: Double,
        expensesByCategory: Map<String, Double>,
        budgetStatus: BudgetStatus
    ): List<String> {
        val recommendations = mutableListOf<String>()

        // Análisis de porcentajes por categoría
        expensesByCategory.forEach { (category, amount) ->
            val percentage = (amount / totalIncome) * 100
            val categoryEnum = ExpenseCategory.valueOf(category)

            when (categoryEnum) {
                ExpenseCategory.RENT -> {
                    if (percentage > 40) {
                        recommendations.add("Tu gasto en renta es muy alto (${percentage.toInt()}%). Considera opciones más económicas.")
                    }
                }
                ExpenseCategory.FOOD -> {
                    if (percentage > 25) {
                        recommendations.add("Estás gastando mucho en comida (${percentage.toInt()}%). Intenta cocinar más en casa.")
                    }
                }
                ExpenseCategory.TRANSPORTATION -> {
                    if (percentage > 15) {
                        recommendations.add("El gasto en transporte es elevado (${percentage.toInt()}%). Evalúa opciones más económicas.")
                    }
                }
                ExpenseCategory.UTILITIES -> {
                    if (percentage > 12) {
                        recommendations.add("Los servicios públicos están altos (${percentage.toInt()}%). Revisa tu consumo.")
                    }
                }
                ExpenseCategory.ENTERTAINMENT -> {
                    if (percentage > 10) {
                        recommendations.add("Considera reducir gastos de entretenimiento (${percentage.toInt()}%).")
                    }
                }
                ExpenseCategory.SHOPPING -> {
                    if (percentage > 10) {
                        recommendations.add("Los gastos de compras son altos (${percentage.toInt()}%). Evita compras innecesarias.")
                    }
                }
                else -> {
                    if (percentage > 15) {
                        recommendations.add("Revisa tus gastos en ${getCategoryName(categoryEnum)} (${percentage.toInt()}%).")
                    }
                }
            }
        }

        // Recomendaciones basadas en el estado general
        when (budgetStatus) {
            BudgetStatus.OVER_BUDGET -> {
                recommendations.add("⚠️ URGENTE: Estás gastando más de lo que ganas. Elimina gastos no esenciales.")
                recommendations.add("Considera buscar ingresos adicionales temporalmente.")
            }
            BudgetStatus.WARNING -> {
                recommendations.add("⚠️ Cuidado: Estás cerca del límite. Controla tus gastos este mes.")
                recommendations.add("Evita gastos no planificados por el resto del período.")
            }
            BudgetStatus.ON_TRACK -> {
                recommendations.add("✅ Buen trabajo manteniendo tu presupuesto.")
                recommendations.add("Intenta ahorrar el dinero restante para emergencias.")
            }
            BudgetStatus.EXCELLENT -> {
                recommendations.add("🎉 ¡Excelente manejo financiero!")
                recommendations.add("Considera invertir tus ahorros para hacerlos crecer.")
                recommendations.add("Podrías aumentar tu fondo de emergencias.")
            }
        }

        // Recomendación de ahorro si no hay
        val savingsAmount = expensesByCategory[ExpenseCategory.SAVINGS.name] ?: 0.0
        val savingsPercentage = (savingsAmount / totalIncome) * 100
        if (savingsPercentage < 10) {
            recommendations.add("💡 Intenta ahorrar al menos el 10% de tus ingresos mensuales.")
        }

        return recommendations.take(4) // Máximo 4 recomendaciones
    }

    fun refreshData() {
        loadFinancialRubricData()
    }

    fun clearError() {
        _errorMessage.value = null
    }

    // Función para obtener el nombre de la categoría en español
    fun getCategoryName(category: ExpenseCategory): String {
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

    // Función para obtener detalles de análisis por categoría
    fun getCategoryAnalysis(category: ExpenseCategory, amount: Double): CategoryAnalysis {
        val totalIncome = _financialProfile.value?.monthlyIncome ?: 1.0
        val percentage = (amount / totalIncome) * 100

        val thresholds = getCategoryThresholds(category)

        val status = when {
            percentage <= thresholds.excellent -> AnalysisStatus.EXCELLENT
            percentage <= thresholds.good -> AnalysisStatus.GOOD
            percentage <= thresholds.warning -> AnalysisStatus.WARNING
            else -> AnalysisStatus.CRITICAL
        }

        val recommendation = getCategoryRecommendation(category, percentage, status)

        return CategoryAnalysis(
            category = category,
            amount = amount,
            percentage = percentage,
            status = status,
            recommendation = recommendation
        )
    }

    private fun getCategoryThresholds(category: ExpenseCategory): CategoryThresholds {
        return when (category) {
            ExpenseCategory.RENT -> CategoryThresholds(30.0, 35.0, 40.0)
            ExpenseCategory.FOOD -> CategoryThresholds(15.0, 20.0, 25.0)
            ExpenseCategory.TRANSPORTATION -> CategoryThresholds(8.0, 12.0, 15.0)
            ExpenseCategory.UTILITIES -> CategoryThresholds(6.0, 9.0, 12.0)
            ExpenseCategory.EDUCATION -> CategoryThresholds(10.0, 15.0, 20.0)
            ExpenseCategory.HEALTH -> CategoryThresholds(5.0, 8.0, 12.0)
            ExpenseCategory.ENTERTAINMENT -> CategoryThresholds(5.0, 8.0, 10.0)
            ExpenseCategory.SHOPPING -> CategoryThresholds(5.0, 8.0, 10.0)
            ExpenseCategory.SAVINGS -> CategoryThresholds(20.0, 15.0, 10.0) // Invertido: más es mejor
            ExpenseCategory.OTHER -> CategoryThresholds(3.0, 5.0, 8.0)
        }
    }

    private fun getCategoryRecommendation(
        category: ExpenseCategory,
        percentage: Double,
        status: AnalysisStatus
    ): String {
        return when (status) {
            AnalysisStatus.EXCELLENT -> when (category) {
                ExpenseCategory.SAVINGS -> "¡Excelente nivel de ahorro!"
                else -> "Gasto óptimo en esta categoría"
            }
            AnalysisStatus.GOOD -> "Gasto controlado, sigue así"
            AnalysisStatus.WARNING -> when (category) {
                ExpenseCategory.RENT -> "Considera buscar opciones más económicas"
                ExpenseCategory.FOOD -> "Intenta cocinar más en casa"
                ExpenseCategory.TRANSPORTATION -> "Evalúa opciones de transporte público"
                ExpenseCategory.ENTERTAINMENT -> "Reduce actividades de entretenimiento"
                ExpenseCategory.SHOPPING -> "Evita compras no esenciales"
                else -> "Controla este gasto el próximo mes"
            }
            AnalysisStatus.CRITICAL -> when (category) {
                ExpenseCategory.RENT -> "URGENTE: Busca vivienda más económica"
                ExpenseCategory.FOOD -> "CRÍTICO: Planifica comidas y cocina en casa"
                ExpenseCategory.TRANSPORTATION -> "CRÍTICO: Usa transporte público"
                else -> "CRÍTICO: Elimina gastos innecesarios"
            }
        }
    }
}


data class CategoryAnalysis(
    val category: ExpenseCategory,
    val amount: Double,
    val percentage: Double,
    val status: AnalysisStatus,
    val recommendation: String
)

enum class AnalysisStatus {
    EXCELLENT,
    GOOD,
    WARNING,
    CRITICAL
}