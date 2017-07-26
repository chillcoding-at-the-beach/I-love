package com.chillcoding.mycuteheart

import android.app.Application

/**
 * Created by macha on 21/07/2017.
 */
class MyApp : Application() {

    companion object {
        lateinit var instance: MyApp
            private set
        val M_GAME_DATA = "game"
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
