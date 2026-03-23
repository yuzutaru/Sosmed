package com.yustar.auth.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Created by Yustar Pramudana on 06/03/26.
 */

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    // Currently, all Auth UseCases and ViewModels use constructor injection.
    // Core dependencies are provided by the :core module's Hilt modules.
}
