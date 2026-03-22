package com.yustar.dashboard.di

import androidx.room.Room
import com.yustar.dashboard.data.local.FeedsDatabase
import com.yustar.dashboard.data.remote.FeedsApi
import com.yustar.dashboard.domain.repository.FeedsRepository
import com.yustar.dashboard.domain.repository.FeedsRepositoryImpl
import com.yustar.dashboard.domain.usecase.GetFeedsUseCase
import com.yustar.dashboard.presentation.viewmodel.FeedsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

/**
 * Created by Yustar Pramudana on 22/03/26.
 */

val dashboardModule = module {
    // Database
    single {
        Room.databaseBuilder(
            get(),
            FeedsDatabase::class.java,
            "feeds.db"
        ).build()
    }

    // API
    single<FeedsApi> { get<Retrofit>().create(FeedsApi::class.java) }

    // Repository
    single<FeedsRepository> { FeedsRepositoryImpl(get(), get(), get(), get()) }

    // UseCases
    factory { GetFeedsUseCase(get()) }

    // ViewModel
    viewModel { FeedsViewModel(get()) }
}
