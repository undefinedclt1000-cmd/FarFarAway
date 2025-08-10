package com.undefined.farfaraway.domain.interfaces

import com.undefined.farfaraway.domain.entities.Response
import com.undefined.farfaraway.domain.entities.Review
import com.undefined.farfaraway.domain.entities.User

interface IUserRepository {
    suspend fun getUserById(userId: String): Response<User>
    suspend fun updateUser(user: User): Response<Boolean>
    suspend fun getUserReviews(userId: String): Response<List<Review>>


}