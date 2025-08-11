package com.undefined.farfaraway.domain.useCases.firebase

import com.undefined.farfaraway.domain.entities.*
import com.undefined.farfaraway.domain.repositories.FinanceFirestoreRepository
import kotlinx.coroutines.flow.Flow

class FinanceFirestoreUseCases(
    private val repository: FinanceFirestoreRepository
) {
    suspend fun saveFinancialProfile(profile: FinancialProfile) =
        repository.saveFinancialProfile(profile)

    fun getFinancialProfile(userId: String): Flow<FinancialProfile?> =
        repository.getFinancialProfile(userId)

    suspend fun saveExpense(expense: Expense) =
        repository.saveExpense(expense)

    fun getExpenses(userId: String): Flow<List<Expense>> =
        repository.getExpenses(userId)
}
