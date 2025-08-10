package com.undefined.farfaraway.domain.useCases.dataStore

import com.undefined.farfaraway.domain.repository.DataStoreRepositoryImpl
import javax.inject.Inject

class GetDataString @Inject constructor(
    private val _repository: DataStoreRepositoryImpl
){
    suspend operator fun invoke(key: String): String = _repository.getDataString(key)
}