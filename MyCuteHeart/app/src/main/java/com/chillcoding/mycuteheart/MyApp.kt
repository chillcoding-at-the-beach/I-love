package com.chillcoding.mycuteheart

import android.app.Application
import android.support.v4.content.res.ResourcesCompat

/**
 * Created by macha on 21/07/2017.
 */
class MyApp : Application() {

    companion object {
        lateinit var instance: MyApp
            private set
        val BUNDLE_GAME_DATA = "BUNDLE_GAME_DATE"
        val PREF_BEST_SCORE = "PREF_BEST_SCORE"
        val PREF_AWARD_LEVEL = "PREF_AWARD_LEVEL"
        val PREF_PAYLOAD = "PREF_PAYLOAD"
        val PREF_SOUND = "PREF_SOUND"
        val SCORE_PER_AWARD = 300
        val sColors: List<Int> by lazy {
            listOf(
                    ResourcesCompat.getColor(instance.resources, R.color.colorPrimaryLight, null),
                    ResourcesCompat.getColor(instance.resources, R.color.colorPrimary, null),
                    ResourcesCompat.getColor(instance.resources, R.color.colorPrimaryDark, null),
                    ResourcesCompat.getColor(instance.resources, R.color.colorAccentLight, null),
                    ResourcesCompat.getColor(instance.resources, R.color.colorAccent, null),
                    ResourcesCompat.getColor(instance.resources, R.color.colorAccentDark, null))
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
