package com.undefined.farfaraway.domain.useCases.dataStore

import com.undefined.farfaraway.domain.repository.DataStoreRepositoryImpl
import javax.inject.Inject

class SetDouble @Inject constructor(
    private val _repository: DataStoreRepositoryImpl
) {
    suspend operator fun invoke(key: String, value: Double) = _repository.setDouble(key, value)
}