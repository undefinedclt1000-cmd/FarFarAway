package com.undefined.farfaraway.domain.interfaces

import com.undefined.farfaraway.domain.entities.Response
import com.undefined.farfaraway.domain.entities.User

interface IAuthRepository {
    suspend fun registerUser(user: User, password: String): Response<User>
    suspend fun loginUser(email: String, password: String): Response<User>
    suspend fun getUser(userId: String): Response<User>
    suspend fun logout(): Response<Boolean>
    suspend fun getCurrentUser(): Response<String?>
}