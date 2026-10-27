package com.sosclima.app

import android.app.Application
import com.sosclima.app.di.AppContainer

class SosClimaApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
