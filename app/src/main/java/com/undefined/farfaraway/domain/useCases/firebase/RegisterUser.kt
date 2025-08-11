package com.undefined.farfaraway.domain.useCases.firebase

import com.undefined.farfaraway.domain.entities.Response
import com.undefined.farfaraway.domain.entities.User
import com.undefined.farfaraway.domain.interfaces.IAuthRepository
import javax.inject.Inject

class RegisterUser @Inject constructor(
    private val fireAuthRepository: IAuthRepository
) {
    suspend operator fun invoke(user: User, password: String): Response<User> = fireAuthRepository.registerUser(user, password)
}