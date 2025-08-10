package com.undefined.farfaraway.domain.entities

sealed class Response<out T> {

    data class Success<out T>(val data: T) : Response<T>()

    data class Error<out T>(val exception: Exception?) : Response<T>()

    data object Loading : Response<Nothing>()

    data object Idle : Response<Nothing>()
}