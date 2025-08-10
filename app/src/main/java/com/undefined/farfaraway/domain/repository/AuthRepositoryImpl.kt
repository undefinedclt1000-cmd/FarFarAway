package com.undefined.farfaraway.domain.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.undefined.farfaraway.domain.entities.Response
import com.undefined.farfaraway.domain.entities.User
import com.undefined.farfaraway.domain.interfaces.IAuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : IAuthRepository {

    override suspend fun registerUser(user: User, password: String): Response<Boolean> {
        return try {
            // CRUCIAL: Usar createUserWithEmailAndPassword para registro
            val result = firebaseAuth.createUserWithEmailAndPassword(user.email, password).await()

            result.user?.let { firebaseUser ->
                // Crear documento del usuario en Firestore
                val userDoc = hashMapOf(
                    "uid" to firebaseUser.uid,
                    "firstName" to user.firstName,
                    "lastName" to user.lastName,
                    "email" to user.email,
                    "age" to user.age,
                    "phoneNumber" to user.phoneNumber,
                    "userType" to user.userType,
                    "isActive" to user.isActive,
                    "isEmailVerified" to firebaseUser.isEmailVerified,
                    "createdAt" to System.currentTimeMillis()
                )

                // Guardar en Firestore
                firestore.collection("users")
                    .document(firebaseUser.uid)
                    .set(userDoc)
                    .await()

                // Opcional: Enviar email de verificación
                try {
                    firebaseUser.sendEmailVerification().await()
                } catch (e: Exception) {
                    // Log pero no fallar el registro por esto
                    println("Warning: No se pudo enviar email de verificación: ${e.message}")
                }

                Response.Success(true)
            } ?: Response.Error(message = "Error al crear usuario en Firebase Auth")

        } catch (e: Exception) {
            Response.Error(exception = e, message = "Error en el registro: ${e.message}")
        }
    }

    override suspend fun loginUser(email: String, password: String): Response<Boolean> {
        return try {
            // Para login SÍ usamos signInWithEmailAndPassword
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()

            result.user?.let {
                Response.Success(true)
            } ?: Response.Error(message = "Error al iniciar sesión")

        } catch (e: Exception) {
            Response.Error(exception = e, message = "Error en el login: ${e.message}")
        }
    }

    override suspend fun getUser(userId: String): Response<User> {
        return try {
            val document = firestore.collection("users").document(userId).get().await()

            if (document.exists()) {
                val user = document.toObject(User::class.java)
                user?.let {
                    Response.Success(it)
                } ?: Response.Error(message = "Error al convertir documento a User")
            } else {
                Response.Error(message = "Usuario no encontrado")
            }

        } catch (e: Exception) {
            Response.Error(exception = e, message = "Error al obtener usuario: ${e.message}")
        }
    }

    override suspend fun logout(): Response<Boolean> {
        return try {
            firebaseAuth.signOut()
            Response.Success(true)
        } catch (e: Exception) {
            Response.Error(exception = e, message = "Error al cerrar sesión: ${e.message}")
        }
    }

    override suspend fun getCurrentUser(): Response<String?> {
        return try {
            val currentUser = firebaseAuth.currentUser
            Response.Success(currentUser?.uid)
        } catch (e: Exception) {
            Response.Error(exception = e, message = "Error al obtener usuario actual: ${e.message}")
        }
    }
}