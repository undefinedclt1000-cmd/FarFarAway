package com.undefined.farfaraway.presentation.shared.validation

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null,
)
