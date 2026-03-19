package com.yustar.auth.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.yustar.core.data.local.UserDB
import com.yustar.core.data.repository.UserRepository
import com.yustar.core.data.repository.UserRepositoryImpl
import com.yustar.auth.domain.LoginUserUseCase
import com.yustar.auth.domain.LogoutUseCase
import com.yustar.auth.domain.RegisterUserUseCase
import com.yustar.auth.presentation.viewmodel.LoginViewModel
import com.yustar.auth.presentation.viewmodel.RegisterViewModel
import com.yustar.core.session.SessionManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Created by Yustar Pramudana on 06/03/26.
 */

private val Context.dataStore by preferencesDataStore(name = "session_prefs")

val authModule = module {
    // DataStore
    single { androidContext().dataStore }

    // Room
    single {
        Room.databaseBuilder(
            androidContext(),
            UserDB::class.java,
            "app_db"
        ).build()
    }

    single { get<UserDB>().userDao() }

    // Repository
    single<UserRepository> {
        UserRepositoryImpl(get())
    }

    // Session
    single { SessionManager(get()) }

    // UseCases
    factory { LoginUserUseCase(get(), get()) }
    factory { RegisterUserUseCase(get()) }
    factory { LogoutUseCase(get()) }

    //ViewModel
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
}