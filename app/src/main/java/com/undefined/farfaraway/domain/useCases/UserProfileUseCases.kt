package com.undefined.farfaraway.domain.useCases

import com.undefined.farfaraway.domain.entities.Response
import com.undefined.farfaraway.domain.entities.User
import com.undefined.farfaraway.domain.entities.Review
import com.undefined.farfaraway.domain.interfaces.IUserRepository
import javax.inject.Inject

data class UserProfileUseCases @Inject constructor(
    val getUserProfile: GetUserProfileUseCase,
    val getUserReviews: GetUserReviewsUseCase,
    val updateUserProfile: UpdateUserProfileUseCase
)

class GetUserProfileUseCase @Inject constructor(
    private val userRepository: IUserRepository
) {
    suspend operator fun invoke(userId: String): Response<User> {
        return try {
            userRepository.getUserById(userId)
        } catch (e: Exception) {
            Response.Error(e)
        }
    }
}

class GetUserReviewsUseCase @Inject constructor(
    private val userRepository: IUserRepository
) {
    suspend operator fun invoke(userId: String): Response<List<Review>> {
        return try {
            userRepository.getUserReviews(userId)
        } catch (e: Exception) {
            Response.Error(e)
        }
    }
}

class UpdateUserProfileUseCase @Inject constructor(
    private val userRepository: IUserRepository
) {
    suspend operator fun invoke(user: User): Response<Boolean> {
        return try {
            userRepository.updateUser(user)
        } catch (e: Exception) {
            Response.Error(e)
        }
    }
}
