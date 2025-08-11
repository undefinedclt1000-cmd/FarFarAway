package com.undefined.farfaraway.domain.repositories

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.undefined.farfaraway.domain.entities.*
import com.undefined.farfaraway.domain.repositories.FinanceFirestoreRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FinanceFirestoreRepositoryImpl(
    private val firestore: FirebaseFirestore
) : FinanceFirestoreRepository {

    override suspend fun saveFinancialProfile(profile: FinancialProfile) {
        firestore.collection("financialProfiles")
            .document(profile.userId) // Aquí usamos userId como id de doc
            .set(profile)
            .addOnSuccessListener {
                Log.d("FinanceFirestoreUseCases", "Perfil guardado con éxito")
            }
            .addOnFailureListener { e ->
                Log.e("FinanceFirestoreUseCases", "Error al guardar perfil", e)
            }
    }


    override fun getFinancialProfile(userId: String): Flow<FinancialProfile?> = callbackFlow {
        val docRef = firestore.collection("financialProfiles").document(userId)

        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val profile = snapshot.toObject(FinancialProfile::class.java)
                trySend(profile)
            } else {
                trySend(null)
            }
        }

        awaitClose { listener.remove() }
    }


    override suspend fun saveExpense(expense: Expense) {
        firestore.collection("expenses")
            .document(expense.userId)
            .collection("userExpenses")
            .add(expense)
            .await()
    }

    override fun getExpenses(userId: String): Flow<List<Expense>> = callbackFlow {
        val listenerRegistration = firestore.collection("expenses")
            .document(userId)
            .collection("userExpenses")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val expenses = snapshot?.documents?.mapNotNull { it.toObject(Expense::class.java) } ?: emptyList()
                trySend(expenses).isSuccess
            }
        awaitClose { listenerRegistration.remove() }
    }
}
