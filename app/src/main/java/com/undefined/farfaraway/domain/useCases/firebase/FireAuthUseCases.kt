package com.undefined.farfaraway.domain.useCases.firebase

data class FireAuthUseCases(
    val registerUser: RegisterUser,
    val getUser: GetUser,
    val loginUser: LoginUser
)
