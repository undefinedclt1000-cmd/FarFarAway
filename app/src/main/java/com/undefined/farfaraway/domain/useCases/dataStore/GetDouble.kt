package com.undefined.farfaraway.domain.useCases.dataStore

import com.undefined.farfaraway.domain.repository.DataStoreRepositoryImpl
import javax.inject.Inject

class GetDouble @Inject constructor(
    private val _repository: DataStoreRepositoryImpl
){
    suspend operator fun invoke(key: String): Double = _repository.getDouble(key)
}