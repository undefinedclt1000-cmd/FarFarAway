package com.undefined.farfaraway.domain.repositories

import com.undefined.farfaraway.domain.entities.*
import kotlinx.coroutines.flow.Flow

interface FinanceFirestoreRepository {

    suspend fun saveFinancialProfile(profile: FinancialProfile)
    fun getFinancialProfile(userId: String): Flow<FinancialProfile?>

    suspend fun saveExpense(expense: Expense)
    fun getExpenses(userId: String): Flow<List<Expense>>
}
