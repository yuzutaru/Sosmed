package com.yustar.dashboard.di

import android.content.Context
import androidx.room.Room
import com.yustar.core.BuildConfig
import com.yustar.core.data.remote.UsersApi
import com.yustar.core.session.SessionManager
import com.yustar.dashboard.data.local.FeedsDatabase
import com.yustar.dashboard.data.remote.FeedsApi
import com.yustar.dashboard.data.remote.SupabaseClientWrapper
import com.yustar.dashboard.data.remote.SupabaseClientWrapperImpl
import com.yustar.dashboard.data.repository.FeedsRepositoryImpl
import com.yustar.dashboard.data.repository.FeedsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
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
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = BuildConfig.BASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY
        ) {
            install(Postgrest)
            install(Storage)
            install(Functions)
            install(Auth)
        }
    }

    @Provides
    @Singleton
    fun provideSupabaseClientWrapper(supabase: SupabaseClient): SupabaseClientWrapper {
        return SupabaseClientWrapperImpl(supabase)
    }

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
        @ApplicationContext context: Context,
        api: FeedsApi,
        usersApi: UsersApi,
        database: FeedsDatabase,
        sessionManager: SessionManager,
        supabaseWrapper: SupabaseClientWrapper
    ): FeedsRepository {
        return FeedsRepositoryImpl(
            context,
            api,
            usersApi,
            database,
            sessionManager,
            supabaseWrapper
        )
    }
}