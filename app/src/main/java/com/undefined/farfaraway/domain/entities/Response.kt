package com.undefined.farfaraway.domain.entities

sealed class Response<out T> {

    data class Success<out T>(val data: T) : Response<T>()

    data class Error<out T>(
        val exception: Exception? = null,
        val message: String? = null
    ) : Response<T>() {
        // Función para obtener el mensaje de error
        fun getErrorMessage(): String {
            return message ?: exception?.message ?: "Error desconocido"
        }
    }

    data object Loading : Response<Nothing>()

    data object Idle : Response<Nothing>()
}