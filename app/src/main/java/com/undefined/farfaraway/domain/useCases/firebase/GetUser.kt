package com.undefined.farfaraway.domain.useCases.firebase

import com.undefined.farfaraway.domain.interfaces.IAuthRepository


class GetUser(private val repository: IAuthRepository) {
    suspend operator fun invoke(userId: String) = repository.getUser(userId)

}