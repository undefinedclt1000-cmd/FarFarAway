package com.undefined.farfaraway.domain.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.undefined.farfaraway.domain.entities.Response
import com.undefined.farfaraway.domain.entities.User
import com.undefined.farfaraway.domain.interfaces.IAuthRepository
import kotlinx.coroutines.tasks.await
import kotlin.jvm.java

class AuthRepositoryImpl : IAuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    override suspend fun registerUser(user: User, password: String): Response<Boolean> {
        return try {
            // Crear usuario en Firebase Auth
            val result = auth.createUserWithEmailAndPassword(user.email, password).await()

            val uid = result.user?.uid ?: return Response.Error(Exception("User ID is null"))

            // Copiar el UID en el modelo User
            val data = user.copy(id = uid)

            try {
                // Guardar en Realtime Database
                database.child("users").child(uid).setValue(data).await()
                Response.Success(true)
            } catch (dbException: Exception) {
                // Si falla la base de datos, eliminamos la cuenta creada
                result.user?.delete()?.await()
                Response.Error(
                    Exception(
                        "Fallo al guardar en la base de datos. Se ha revertido el registro.",
                        dbException
                    )
                )
            }
        } catch (authException: Exception) {
            Response.Error(authException)
        }
    }

    override suspend fun getUser(userId: String): Response<User> {
        return try {
            val snapshot = database.child("users").child(userId).get().await()
            if (snapshot.exists()) {
                val user = snapshot.getValue(User::class.java) // Aquí usamos User
                if (user != null) {
                    Response.Success(user)
                } else {
                    Response.Error(Exception("User data is null"))
                }
            } else {
                Response.Error(Exception("User not found"))
            }
        } catch (e: Exception) {
            Response.Error(e)
        }
    }

    override suspend fun loginUser(email: String, password: String): Response<Boolean> {
        return try {
            // Autenticar en Firebase Auth
            val result = auth.signInWithEmailAndPassword(email, password).await()

            val uid = result.user?.uid ?: return Response.Error(Exception("User ID is null"))

            // Obtener datos del usuario desde la base de datos
            getUser(uid)
            if(result.user != null){
                Response.Success(true)
            }else{
                Response.Error(Exception("User not found"))
            }

        } catch (e: Exception) {
            Response.Error(e)
        }
    }





}