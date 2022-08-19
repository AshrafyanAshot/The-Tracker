package com.ash.thetracker.di

import android.content.Context
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

object DI {
    private lateinit var diApp: KoinApplication

    @OptIn(ExperimentalSerializationApi::class)
    fun init(appContext: Context) {
        diApp = startKoin {
            androidContext(appContext)
            modules(dataModule, presentationModule)
        }
    }
}