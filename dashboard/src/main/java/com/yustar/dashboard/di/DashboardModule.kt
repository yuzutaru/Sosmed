package com.yustar.dashboard.di

import android.content.Context
import androidx.room.Room
import com.yustar.core.data.remote.UsersApi
import com.yustar.core.session.SessionManager
import com.yustar.dashboard.data.local.FeedsDatabase
import com.yustar.dashboard.data.remote.FeedsApi
import com.yustar.dashboard.domain.repository.FeedsRepository
import com.yustar.dashboard.domain.repository.FeedsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Created by Yustar Pramudana on 22/03/26.
 */

@Module
@InstallIn(SingletonComponent::class)
object DashboardModule {

    @Provides
    @Singleton
    fun provideFeedsDatabase(@ApplicationContext context: Context): FeedsDatabase {
        return Room.databaseBuilder(
            context,
            FeedsDatabase::class.java,
            "feeds.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideFeedsApi(retrofit: Retrofit): FeedsApi {
        return retrofit.create(FeedsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideFeedsRepository(
        api: FeedsApi,
        usersApi: UsersApi,
        database: FeedsDatabase,
        sessionManager: SessionManager
    ): FeedsRepository {
        return FeedsRepositoryImpl(api, usersApi, database, sessionManager)
    }
}
