package com.yustar.auth.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.yustar.auth.domain.LoginUserUseCase
import com.yustar.auth.domain.LogoutUseCase
import com.yustar.auth.domain.RegisterUserUseCase
import com.yustar.auth.presentation.viewmodel.LoginViewModel
import com.yustar.auth.presentation.viewmodel.RegisterViewModel
import com.yustar.core.data.remote.UsersApi
import com.yustar.core.data.remote.model.ResponseHandler
import com.yustar.core.data.repository.UserRepository
import com.yustar.core.data.repository.UserRepositoryImpl
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

    // Repository
    single { UsersApi::class.java }
    single<UserRepository> {
        UserRepositoryImpl(get(), get())
    }

    // Response Handler
    single { ResponseHandler() }

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
