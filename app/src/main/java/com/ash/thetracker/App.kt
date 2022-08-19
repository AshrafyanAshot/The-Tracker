package com.ash.thetracker

import android.app.Application
import android.content.Context
import com.ash.thetracker.di.DI

class App : Application() {

    companion object {
        lateinit var instance: App
            private set

        fun applicationContext(): Context {
            return instance.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        DI.init(this)
        instance = this
    }
}
