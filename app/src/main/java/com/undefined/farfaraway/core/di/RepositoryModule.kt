package com.undefined.farfaraway.core.di

import com.google.firebase.database.FirebaseDatabase
import com.undefined.farfaraway.domain.repository.PropertyRepository
import com.undefined.farfaraway.domain.interfaces.IPropertyRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPropertyRepository(
        propertyRepository: PropertyRepository
    ): IPropertyRepository

    companion object {
        @Provides
        @Singleton
        fun provideFirebaseDatabase(): FirebaseDatabase {
            val database = FirebaseDatabase.getInstance()

            // Configurar para desarrollo (opcional)
            database.setPersistenceEnabled(true) // Habilitar caché offline

            return database
        }
    }
}