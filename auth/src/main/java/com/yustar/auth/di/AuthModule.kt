package com.yustar.auth.di

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.yustar.auth.domain.LoginUserUseCase
import com.yustar.auth.domain.LogoutUseCase
import com.yustar.auth.domain.RefreshUserTokenUseCase
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

    // SharedPreferences
    single<SharedPreferences> {
        val masterKey = MasterKey.Builder(androidContext())
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            androidContext(),
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    // Repository
    single { UsersApi::class.java }
    single<UserRepository> {
        UserRepositoryImpl(get(), get())
    }

    // Response Handler
    single { ResponseHandler() }

    // Session
    single { SessionManager(get(), get()) }

    // UseCases
    factory { LoginUserUseCase(get(), get()) }
    factory { RegisterUserUseCase(get()) }
    factory { LogoutUseCase(get(), get()) }
    factory { RefreshUserTokenUseCase(get(), get()) }

    //ViewModel
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
}
