package com.chillcoding.ilove

import android.app.Application
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatDelegate

/**
 * Created by macha on 21/07/2017.
 */
class App : Application() {

    companion object {
        lateinit var instance: App
            private set
        const val PERMISSIONS_REQUEST_GET_ACCOUNTS: Int = 13
        const val STATE_GAME_DATA = "STATE_GAME_DATA"
        const val BUNDLE_GAME_LEVEL = "BUNDLE_GAME_LEVEL"
        const val STATE_AWARD_POSITION = "BUNDLE_AWARD_POSITION"
        const val PREF_BEST_SCORE = "PREF_BEST_SCORE"
        const val PREF_AWARD_LEVEL = "PREF_AWARD_LEVEL"
        const val PREF_PAYLOAD = "PREF_PAYLOAD"
        const val PREF_SOUND = "PREF_SOUND"
        const val PREF_PREMIUM = "PREF_PREMIUM"
        const val PREF_UNLIMITED_QUOTES = "PREF_UNLIMITED_QUOTES"
        const val PREF_UNLIMITED_AWARDS = "PREF_UNLIMITED_AWARDS"
        const val SCORE_PER_AWARD = 150
        const val AWARD_LIST_SIZE = 7
        const val AWARD_MODE = "AWARD_MODE"
        val sColors: List<Int> by lazy {
            listOf(
                    ResourcesCompat.getColor(instance.resources, R.color.colorAccentDark, null),//0
                    ResourcesCompat.getColor(instance.resources, R.color.colorAccent, null),//1
                    ResourcesCompat.getColor(instance.resources, R.color.colorAccentLight, null),//2
                    ResourcesCompat.getColor(instance.resources, R.color.colorPrimarySuperDark, null),//3
                    ResourcesCompat.getColor(instance.resources, R.color.colorPrimaryDark, null),//4
                    ResourcesCompat.getColor(instance.resources, R.color.colorPrimary, null),//5
                    ResourcesCompat.getColor(instance.resources, R.color.colorYellowDark, null),//6
                    ResourcesCompat.getColor(instance.resources, R.color.colorYellowGold, null),//7
                    ResourcesCompat.getColor(instance.resources, R.color.colorYellowLight, null),//8
                    ResourcesCompat.getColor(instance.resources, R.color.colorGreenDark, null),//9
                    ResourcesCompat.getColor(instance.resources, R.color.colorGreen, null),//10
                    ResourcesCompat.getColor(instance.resources, R.color.colorGreenLight, null)
            )
        }
        val shadowColor: Int by lazy { ResourcesCompat.getColor(instance.resources, R.color.colorPrimaryLight, null) }
        val sAwardImg = intArrayOf(R.drawable.ic_menu_awards, R.drawable.ic_award, R.drawable.ic_award, R.drawable.ic_award, R.drawable.ic_award, R.drawable.ic_award, R.drawable.ic_award, R.drawable.ic_award)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }
}
