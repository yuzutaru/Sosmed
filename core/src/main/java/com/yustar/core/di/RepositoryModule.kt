package com.yustar.core.di

import com.yustar.core.data.remote.UsersApi
import com.yustar.core.data.remote.model.ResponseHandler
import com.yustar.core.data.repository.UserRepository
import com.yustar.core.data.repository.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(
        api: UsersApi,
        responseHandler: ResponseHandler
    ): UserRepository {
        return UserRepositoryImpl(api, responseHandler)
    }
}
