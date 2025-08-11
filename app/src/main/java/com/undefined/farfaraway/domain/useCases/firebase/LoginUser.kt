package com.undefined.farfaraway.domain.useCases.firebase

import com.undefined.farfaraway.domain.entities.Response
import com.undefined.farfaraway.domain.entities.User
import com.undefined.farfaraway.domain.interfaces.IAuthRepository
import javax.inject.Inject

class LoginUser @Inject constructor(
    private val _fireAuthRepository: IAuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Response<User> = _fireAuthRepository.loginUser(email, password)
}