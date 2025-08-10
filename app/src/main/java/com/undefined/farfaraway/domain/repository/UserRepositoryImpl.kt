package com.undefined.farfaraway.domain.repository

import com.google.firebase.database.FirebaseDatabase
import com.undefined.farfaraway.domain.entities.Response
import com.undefined.farfaraway.domain.entities.Review
import com.undefined.farfaraway.domain.entities.User
import com.undefined.farfaraway.domain.interfaces.IUserRepository
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl : IUserRepository {

    private val database = FirebaseDatabase.getInstance().reference

    override suspend fun getUserById(userId: String): Response<User> {
        return try {
            val snapshot = database.child("users").child(userId).get().await()
            if (snapshot.exists()) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) Response.Success(user)
                else Response.Error(Exception("User data is null"))
            } else {
                Response.Error(Exception("User not found"))
            }
        } catch (e: Exception) {
            Response.Error(e)
        }
    }

    override suspend fun updateUser(user: User): Response<Boolean> {
        return try {
            database.child("users").child(user.id).setValue(user).await()
            Response.Success(true)
        } catch (e: Exception) {
            Response.Error(e)
        }
    }

    override suspend fun getUserReviews(userId: String): Response<List<Review>> {
        return try {
            val snapshot = database.child("reviews")
                .orderByChild("userId")
                .equalTo(userId)
                .get()
                .await()

            val reviews = snapshot.children.mapNotNull { it.getValue(Review::class.java) }
            Response.Success(reviews)
        } catch (e: Exception) {
            Response.Error(e)
        }
    }
}
