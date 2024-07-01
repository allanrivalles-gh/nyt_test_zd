package com.theathletic.test

import android.app.Application
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.loadKoinModules
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import timber.log.Timber

private val testModule = module {
    single(named("application-context")) {
        androidApplication().applicationContext
    }
}

class TestApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        try {
            startKoin {
                androidContext(applicationContext)
                loadKoinModules(listOf(testModule))
            }
        } catch (error: Throwable) {
            Timber.e(error, "Can't initialize the TestApplication")
        }
    }
}