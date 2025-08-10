package com.undefined.farfaraway.core.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.undefined.farfaraway.BuildConfig
import com.undefined.farfaraway.domain.interfaces.IAuthRepository
import com.undefined.farfaraway.domain.interfaces.IUserRepository
import com.undefined.farfaraway.domain.repository.AuthRepositoryImpl
import com.undefined.farfaraway.domain.repository.DataStoreRepositoryImpl
import com.undefined.farfaraway.domain.repository.UserRepositoryImpl
import com.undefined.farfaraway.domain.useCases.GetUserProfileUseCase
import com.undefined.farfaraway.domain.useCases.GetUserReviewsUseCase
import com.undefined.farfaraway.domain.useCases.UpdateUserProfileUseCase
import com.undefined.farfaraway.domain.useCases.UserProfileUseCases
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
            getDataInt = GetDataInt(dataStoreRepository),
            getDouble = com.undefined.farfaraway.domain.useCases.dataStore.GetDouble(dataStoreRepository),
            setDouble = com.undefined.farfaraway.domain.useCases.dataStore.SetDouble(dataStoreRepository)
        )

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        val firebaseAuth = FirebaseAuth.getInstance()

        // Deshabilitar verificaciones para desarrollo
        if (BuildConfig.DEBUG) {
            firebaseAuth.firebaseAuthSettings.setAppVerificationDisabledForTesting(true)
        }

        return firebaseAuth
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Singleton
    @Provides
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): IAuthRepository = AuthRepositoryImpl(firebaseAuth, firestore)

    @Singleton
    @Provides
    fun provideUserRepository(): IUserRepository = UserRepositoryImpl()

    @Provides
    fun provideUserProfileUseCases(repository: IUserRepository): UserProfileUseCases = (
            UserProfileUseCases(
                getUserProfile = GetUserProfileUseCase(repository),
                getUserReviews = GetUserReviewsUseCase(repository),
                updateUserProfile = UpdateUserProfileUseCase(repository)
            )
            )

    // Auth Use Cases
    @Provides
    fun provideAuthUseCases(repository: IAuthRepository): FireAuthUseCases =
        FireAuthUseCases(
            registerUser = RegisterUser(repository),
            loginUser = LoginUser(repository),
            getUser = com.undefined.farfaraway.domain.useCases.firebase.GetUser(repository)
        )
}