package com.yustar.core.di

import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.yustar.core.session.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SessionModule {

    @Provides
    @Singleton
    fun provideSessionManager(
        dataStore: DataStore<Preferences>,
        sharedPreferences: SharedPreferences
    ): SessionManager {
        return SessionManager(dataStore, sharedPreferences)
    }
}
