package com.undefined.farfaraway.presentation.features.finance.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undefined.farfaraway.core.Constants
import com.undefined.farfaraway.domain.entities.*
import com.undefined.farfaraway.domain.useCases.dataStore.DataStoreUseCases
import com.undefined.farfaraway.domain.useCases.firebase.FinanceFirestoreUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import android.util.Log
import com.google.firebase.auth.FirebaseAuth

@HiltViewModel
class FinanceViewModel @Inject constructor(
    private val dataStoreUseCases: DataStoreUseCases,
    private val firestoreUseCases: FinanceFirestoreUseCases
) : ViewModel() {


    // Estado del perfil financiero del usuario
    private val _financialProfile = MutableStateFlow<FinancialProfile?>(null)
    val financialProfile: StateFlow<FinancialProfile?> = _financialProfile.asStateFlow()

    // Estado de los gastos del usuario
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    // Estado del presupuesto actual
    private val _currentBudget = MutableStateFlow<Budget?>(null)
    val currentBudget: StateFlow<Budget?> = _currentBudget.asStateFlow()

    // Estado del resumen financiero
    private val _financialSummary = MutableStateFlow<FinancialSummary?>(null)
    val financialSummary: StateFlow<FinancialSummary?> = _financialSummary.asStateFlow()

    // Estados de la UI
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Estados para formularios
    private val _showIncomeDialog = MutableStateFlow(false)
    val showIncomeDialog: StateFlow<Boolean> = _showIncomeDialog.asStateFlow()

    private val _showExpenseDialog = MutableStateFlow(false)
    val showExpenseDialog: StateFlow<Boolean> = _showExpenseDialog.asStateFlow()

    // Campos del formulario de ingresos
    private val _incomeAmount = MutableStateFlow("")
    val incomeAmount: StateFlow<String> = _incomeAmount.asStateFlow()

    private val _incomeFrequency = MutableStateFlow(IncomeFrequency.MONTHLY)
    val incomeFrequency: StateFlow<IncomeFrequency> = _incomeFrequency.asStateFlow()

    // Campos del formulario de gastos
    private val _expenseAmount = MutableStateFlow("")
    val expenseAmount: StateFlow<String> = _expenseAmount.asStateFlow()

    private val _expenseCategory = MutableStateFlow(ExpenseCategory.OTHER)
    val expenseCategory: StateFlow<ExpenseCategory> = _expenseCategory.asStateFlow()

    private val _expenseDescription = MutableStateFlow("")
    val expenseDescription: StateFlow<String> = _expenseDescription.asStateFlow()

    private val _totalItems = MutableStateFlow(0)
    val totalItems: StateFlow<Int> = _totalItems

    init {
        loadFinancialData()
    }

    private fun loadFinancialData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                if (userId.isNotEmpty()) {
                    // Escuchar perfil financiero
                    launch {
                        firestoreUseCases.getFinancialProfile(userId).collect { profile ->
                            _financialProfile.value = profile
                            calculateFinancialSummary()
                        }
                    }
                    // Escuchar gastos
                    launch {
                        firestoreUseCases.getExpenses(userId).collect { expensesList ->
                            _expenses.value = expensesList
                            _totalItems.value = expensesList.size
                            calculateFinancialSummary()
                        }
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar datos financieros: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    private fun loadSampleData() {
        // Perfil financiero de ejemplo
        _financialProfile.value = FinancialProfile(
            id = "fp_1",
            userId = "user_1",
            monthlyIncome = 8000.0,
            weeklyIncome = 2000.0,
            incomeFrequency = IncomeFrequency.MONTHLY.name,
            isActive = true
        )

        // Gastos de ejemplo
        _expenses.value = listOf(
            Expense(
                id = "exp_1",
                userId = "user_1",
                amount = 3500.0,
                category = ExpenseCategory.RENT.name,
                description = "Renta del departamento",
                expenseDate = Date(),
                isRecurring = true,
                recurringFrequency = RecurringFrequency.MONTHLY.name
            ),
            Expense(
                id = "exp_2",
                userId = "user_1",
                amount = 800.0,
                category = ExpenseCategory.TRANSPORTATION.name,
                description = "Transporte público y Uber",
                expenseDate = Date(),
                isRecurring = true,
                recurringFrequency = RecurringFrequency.MONTHLY.name
            ),
            Expense(
                id = "exp_3",
                userId = "user_1",
                amount = 1200.0,
                category = ExpenseCategory.FOOD.name,
                description = "Comida y supermercado",
                expenseDate = Date(),
                isRecurring = true,
                recurringFrequency = RecurringFrequency.MONTHLY.name
            ),
            Expense(
                id = "exp_4",
                userId = "user_1",
                amount = 500.0,
                category = ExpenseCategory.EDUCATION.name,
                description = "Materiales escolares",
                expenseDate = Date(),
                isRecurring = false
            )
        )

        // Resumen financiero calculado
        val totalExpenses = _expenses.value.sumOf { it.amount }
        val totalIncome = _financialProfile.value?.monthlyIncome ?: 0.0
        val remaining = totalIncome - totalExpenses

        _financialSummary.value = FinancialSummary(
            id = "fs_1",
            userId = "user_1",
            period = BudgetPeriod.MONTHLY.name,
            totalIncome = totalIncome,
            totalExpenses = totalExpenses,
            remainingBudget = remaining,
            expensesByCategory = _expenses.value.groupBy { it.category }
                .mapValues { it.value.sumOf { expense -> expense.amount } },
            budgetStatus = when {
                remaining > totalIncome * 0.2 -> BudgetStatus.EXCELLENT.name
                remaining > 0 -> BudgetStatus.ON_TRACK.name
                remaining > -totalIncome * 0.1 -> BudgetStatus.WARNING.name
                else -> BudgetStatus.OVER_BUDGET.name
            }
        )

        _totalItems.value = _expenses.value.size
    }

    // Funciones para manejar diálogos
    fun showIncomeDialog() {
        _showIncomeDialog.value = true
    }

    fun hideIncomeDialog() {
        _showIncomeDialog.value = false
        clearIncomeForm()
    }

    fun showExpenseDialog() {
        _showExpenseDialog.value = true
    }

    fun hideExpenseDialog() {
        _showExpenseDialog.value = false
        clearExpenseForm()
    }

    // Funciones para manejar formulario de ingresos
    fun updateIncomeAmount(amount: String) {
        _incomeAmount.value = amount
    }

    fun updateIncomeFrequency(frequency: IncomeFrequency) {
        _incomeFrequency.value = frequency
    }

    private fun clearIncomeForm() {
        _incomeAmount.value = ""
        _incomeFrequency.value = IncomeFrequency.MONTHLY
    }

    fun saveIncome() {
        viewModelScope.launch {
            try {
                val amount = _incomeAmount.value.toDoubleOrNull()
                if (amount == null || amount <= 0) {
                    _errorMessage.value = "Por favor ingresa un monto válido"
                    return@launch
                }

                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                // Log para verificar userId
                android.util.Log.d("FinanceViewModel", "userId obtenido en saveIncome: '$userId'")

                if (userId.isEmpty()) {
                    _errorMessage.value = "No se encontró userId válido."
                    return@launch
                }

                val updatedProfile = _financialProfile.value?.copy(
                    monthlyIncome = if (_incomeFrequency.value == IncomeFrequency.MONTHLY) amount else amount * 4.33,
                    weeklyIncome = if (_incomeFrequency.value == IncomeFrequency.WEEKLY) amount else amount / 4.33,
                    incomeFrequency = _incomeFrequency.value.name
                ) ?: FinancialProfile(
                    id = userId,  // <---- usar userId como id único
                    userId = userId,
                    monthlyIncome = if (_incomeFrequency.value == IncomeFrequency.MONTHLY) amount else amount * 4.33,
                    weeklyIncome = if (_incomeFrequency.value == IncomeFrequency.WEEKLY) amount else amount / 4.33,
                    incomeFrequency = _incomeFrequency.value.name,
                    isActive = true
                )

                _financialProfile.value = updatedProfile
                hideIncomeDialog()
                calculateFinancialSummary()

                firestoreUseCases.saveFinancialProfile(updatedProfile) // Guardar en Firebase

            } catch (e: Exception) {
                _errorMessage.value = "Error al guardar ingresos: ${e.message}"
            }
        }
    }

    // Funciones para manejar formulario de gastos
    fun updateExpenseAmount(amount: String) {
        _expenseAmount.value = amount
    }

    fun updateExpenseCategory(category: ExpenseCategory) {
        _expenseCategory.value = category
    }

    fun updateExpenseDescription(description: String) {
        _expenseDescription.value = description
    }

    private fun clearExpenseForm() {
        _expenseAmount.value = ""
        _expenseCategory.value = ExpenseCategory.OTHER
        _expenseDescription.value = ""
    }

    fun saveExpense() {
        viewModelScope.launch {
            try {
                val amount = _expenseAmount.value.toDoubleOrNull()
                if (amount == null || amount <= 0) {
                    _errorMessage.value = "Por favor ingresa un monto válido"
                    Log.d("FinanceViewModel", "Monto inválido para gasto: ${_expenseAmount.value}")
                    return@launch
                }

                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                Log.d("FinanceViewModel", "Guardando gasto para userId=$userId, amount=$amount")

                val newExpense = Expense(
                    id = "exp_${System.currentTimeMillis()}",
                    userId = userId,
                    amount = amount,
                    category = _expenseCategory.value.name,
                    description = _expenseDescription.value,
                    expenseDate = Date(),
                    isRecurring = false
                )

                _expenses.value = _expenses.value + newExpense
                _totalItems.value = _expenses.value.size

                hideExpenseDialog()
                calculateFinancialSummary()

                firestoreUseCases.saveExpense(newExpense)
                Log.d("FinanceViewModel", "Gasto guardado en Firestore: $newExpense")

            } catch (e: Exception) {
                _errorMessage.value = "Error al guardar gasto: ${e.message}"
                Log.e("FinanceViewModel", "Error guardando gasto", e)
            }
        }
    }

    private fun calculateFinancialSummary() {
        val profile = _financialProfile.value ?: return
        val expenses = _expenses.value
        val totalExpenses = expenses.sumOf { it.amount }
        val totalIncome = profile.monthlyIncome
        val remaining = totalIncome - totalExpenses

        _financialSummary.value = FinancialSummary(
            id = "fs_1",
            userId = profile.userId,
            period = BudgetPeriod.MONTHLY.name,
            totalIncome = totalIncome,
            totalExpenses = totalExpenses,
            remainingBudget = remaining,
            expensesByCategory = expenses.groupBy { it.category }
                .mapValues { it.value.sumOf { expense -> expense.amount } },
            budgetStatus = when {
                remaining > totalIncome * 0.2 -> BudgetStatus.EXCELLENT.name
                remaining > 0 -> BudgetStatus.ON_TRACK.name
                remaining > -totalIncome * 0.1 -> BudgetStatus.WARNING.name
                else -> BudgetStatus.OVER_BUDGET.name
            }
        )
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun refreshData() {
        loadFinancialData()
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
}