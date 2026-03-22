package com.yustar.sosmed

import android.app.Application
import com.yustar.auth.di.authModule
import com.yustar.dashboard.di.dashboardModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

/**
 * Created by Yustar Pramudana on 19/03/26.
 */

class SosmedApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@SosmedApplication)
            modules(
                authModule,
                dashboardModule
            )
        }
    }
}
