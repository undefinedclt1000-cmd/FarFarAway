package com.undefined.farfaraway.core.di

import com.google.firebase.auth.FirebaseAuth
import com.undefined.farfaraway.domain.interfaces.IAuthRepository
import com.undefined.farfaraway.domain.repository.AuthRepositoryImpl
import com.undefined.farfaraway.domain.repository.DataStoreRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.undefined.farfaraway.domain.useCases.dataStore.DataStoreUseCases
import com.undefined.farfaraway.domain.useCases.dataStore.GetDataBoolean
import com.undefined.farfaraway.domain.useCases.dataStore.GetDataInt
import com.undefined.farfaraway.domain.useCases.dataStore.GetDataString
import com.undefined.farfaraway.domain.useCases.dataStore.SetDataBoolean
import com.undefined.farfaraway.domain.useCases.dataStore.SetDataInt
import com.undefined.farfaraway.domain.useCases.dataStore.SetDataString
import com.undefined.farfaraway.domain.useCases.firebase.FireAuthUseCases
import com.undefined.farfaraway.domain.useCases.firebase.LoginUser
import com.undefined.farfaraway.domain.useCases.firebase.RegisterUser
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideDataStoreUseCases(dataStoreRepository: DataStoreRepositoryImpl): DataStoreUseCases =
        DataStoreUseCases(
            setDataString = SetDataString(dataStoreRepository),
            getDataString = GetDataString(dataStoreRepository),
            setDataBoolean = SetDataBoolean(dataStoreRepository),
            getDataBoolean = GetDataBoolean(dataStoreRepository),
            setDataInt = SetDataInt(dataStoreRepository),
            getDataInt = GetDataInt(dataStoreRepository)
        )

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Singleton
    @Provides
    fun provideAuthRepository(): IAuthRepository = AuthRepositoryImpl()


    // Auth Use Cases
    @Provides
    fun provideAuthUseCases(repository: IAuthRepository): FireAuthUseCases =
        FireAuthUseCases(
            registerUser = RegisterUser(repository),
            loginUser = LoginUser(repository),
            getUser = com.undefined.farfaraway.domain.useCases.firebase.GetUser(repository)
        )

}