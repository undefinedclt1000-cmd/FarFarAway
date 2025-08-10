package com.undefined.farfaraway.domain.entities

import com.google.firebase.database.PropertyName
import java.util.Date


// ========================================
// MODELOS DE FINANZAS
// ========================================

data class FinancialProfile(
    @get:PropertyName("id") val id: String = "",
    @get:PropertyName("user_id") val userId: String = "",
    @get:PropertyName("monthly_income") val monthlyIncome: Double = 0.0,
    @get:PropertyName("weekly_income") val weeklyIncome: Double = 0.0,
    @get:PropertyName("income_frequency") val incomeFrequency: String = IncomeFrequency.MONTHLY.name,
    @get:PropertyName("is_active") val isActive: Boolean = true
) {
    constructor() : this("", "", 0.0, 0.0, IncomeFrequency.MONTHLY.name, true)
}

data class Expense(
    @get:PropertyName("id") val id: String = "",
    @get:PropertyName("user_id") val userId: String = "",
    @get:PropertyName("amount") val amount: Double = 0.0,
    @get:PropertyName("category") val category: String = ExpenseCategory.OTHER.name,
    @get:PropertyName("description") val description: String = "",
    @get:PropertyName("expense_date") val expenseDate: Date = Date(),
    @get:PropertyName("is_recurring") val isRecurring: Boolean = false,
    @get:PropertyName("recurring_frequency") val recurringFrequency: String = "",
) {
    constructor() : this("", "", 0.0, ExpenseCategory.OTHER.name, "", Date(), false, "")
}

data class Budget(
    @get:PropertyName("id") val id: String = "",
    @get:PropertyName("user_id") val userId: String = "",
    @get:PropertyName("total_budget") val totalBudget: Double = 0.0,
    @get:PropertyName("period") val period: String = BudgetPeriod.MONTHLY.name,
    @get:PropertyName("category_budgets") val categoryBudgets: Map<String, Double> = emptyMap(),
    @get:PropertyName("start_date") val startDate: Date = Date(),
    @get:PropertyName("end_date") val endDate: Date = Date(),
) {
    constructor() : this("", "", 0.0, BudgetPeriod.MONTHLY.name, emptyMap(), Date(), Date())
}

// Modelo para resúmenes financieros (subcollection en user)
data class FinancialSummary(
    @get:PropertyName("id") val id: String = "",
    @get:PropertyName("user_id") val userId: String = "",
    @get:PropertyName("period") val period: String = BudgetPeriod.MONTHLY.name,
    @get:PropertyName("total_income") val totalIncome: Double = 0.0,
    @get:PropertyName("total_expenses") val totalExpenses: Double = 0.0,
    @get:PropertyName("remaining_budget") val remainingBudget: Double = 0.0,
    @get:PropertyName("expenses_by_category") val expensesByCategory: Map<String, Double> = emptyMap(),
    @get:PropertyName("budget_status") val budgetStatus: String = BudgetStatus.ON_TRACK.name,
    @get:PropertyName("recommendations") val recommendations: List<String> = emptyList(),
    @get:PropertyName("period_start") val periodStart: Date = Date(),
    @get:PropertyName("period_end") val periodEnd: Date = Date(),
) {
    constructor() : this("", "", BudgetPeriod.MONTHLY.name, 0.0, 0.0, 0.0, emptyMap(),
        BudgetStatus.ON_TRACK.name, emptyList(), Date(), Date())
}

enum class IncomeFrequency {
    WEEKLY,
    MONTHLY,
    BIWEEKLY
}

enum class ExpenseCategory {
    RENT,           // Renta
    TRANSPORTATION, // Transporte
    FOOD,          // Comida
    EDUCATION,     // Escuela
    ENTERTAINMENT, // Entretenimiento
    UTILITIES,     // Servicios
    HEALTH,        // Salud
    SHOPPING,      // Compras
    SAVINGS,       // Ahorros
    OTHER          // Otros
}

enum class RecurringFrequency {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}

enum class BudgetPeriod {
    WEEKLY,
    MONTHLY,
    YEARLY
}

enum class BudgetStatus {
    ON_TRACK,      // Vas bien
    WARNING,       // Advertencia
    OVER_BUDGET,   // Te pasaste
    EXCELLENT      // Excelente
}