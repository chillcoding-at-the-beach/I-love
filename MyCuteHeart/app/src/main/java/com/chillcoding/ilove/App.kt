package com.chillcoding.ilove

import android.app.Application
import android.support.v4.content.res.ResourcesCompat

/**
 * Created by macha on 21/07/2017.
 */
class App : Application() {

    companion object {
        lateinit var instance: App
            private set
        const val PERMISSIONS_REQUEST_GET_ACCOUNTS: Int = 13
        const val BUNDLE_GAME_DATA = "BUNDLE_GAME_DATA"
        const val BUNDLE_AWARD_DATA = "BUNDLE_AWARD_DATA"
        const val PREF_BEST_SCORE = "PREF_BEST_SCORE"
        const val PREF_AWARD_LEVEL = "PREF_AWARD_LEVEL"
        const val PREF_PAYLOAD = "PREF_PAYLOAD"
        const val PREF_SOUND = "PREF_SOUND"
        const val PREF_PREMIUM = "PREF_PREMIUM"
        const val SCORE_PER_AWARD = 150
        const val AWARD_MODE = "AWARD_MODE"
        val sColors: List<Int> by lazy {
            listOf(
                    ResourcesCompat.getColor(instance.resources, R.color.colorAccentDark, null),
                    ResourcesCompat.getColor(instance.resources, R.color.colorAccent, null),
                    ResourcesCompat.getColor(instance.resources, R.color.colorAccentLight, null),
                    ResourcesCompat.getColor(instance.resources, R.color.colorPrimarySuperDark, null),
                    ResourcesCompat.getColor(instance.resources, R.color.colorPrimaryDark, null),
                    ResourcesCompat.getColor(instance.resources, R.color.colorPrimary, null),
                    ResourcesCompat.getColor(instance.resources, R.color.colorYellowDark, null),
                    ResourcesCompat.getColor(instance.resources, R.color.colorYellowGold, null),
                    ResourcesCompat.getColor(instance.resources, R.color.colorYellowLight, null),
                    ResourcesCompat.getColor(instance.resources, R.color.colorGreenDark, null),
                    ResourcesCompat.getColor(instance.resources, R.color.colorGreen, null),
                    ResourcesCompat.getColor(instance.resources, R.color.colorGreenLight, null)
            )
        }
        val shadowColor: Int by lazy { ResourcesCompat.getColor(instance.resources, R.color.colorPrimaryLight, null) }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
