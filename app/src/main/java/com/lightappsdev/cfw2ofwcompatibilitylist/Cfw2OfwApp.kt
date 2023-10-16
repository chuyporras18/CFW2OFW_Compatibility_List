package com.lightappsdev.cfw2ofwcompatibilitylist

import android.app.Application
import android.content.SharedPreferences
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Cfw2OfwApp : Application() {

    companion object {
        lateinit var instance: Cfw2OfwApp
            private set
    }

    lateinit var preferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        instance = this

        preferences = getSharedPreferences("preferences", MODE_PRIVATE)
    }
}