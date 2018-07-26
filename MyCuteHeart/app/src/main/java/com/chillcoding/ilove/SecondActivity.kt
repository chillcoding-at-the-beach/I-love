package com.chillcoding.ilove

import android.app.Fragment
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.chillcoding.ilove.model.FragmentId
import com.chillcoding.ilove.view.fragment.AboutFragment
import com.chillcoding.ilove.view.fragment.AwardsFragment
import com.chillcoding.ilove.view.fragment.SettingsFragment
import com.chillcoding.ilove.view.fragment.TopScoresFragment

import kotlinx.android.synthetic.main.activity_second.*

/**
 * Created by macha on 02/08/2017.
 */
class SecondActivity : AppCompatActivity() {

    companion object {
        val FRAGMENT_ID = "SecondActivity:fragmentId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out)
        setContentView(R.layout.activity_second)
        setSupportActionBar(secondToolbar)

        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        when (intent.getIntExtra(FRAGMENT_ID, 0)) {
            FragmentId.ABOUT.ordinal -> {
                setFragment(AboutFragment())
                supportActionBar!!.title = getString(R.string.menu_about)
            }
            FragmentId.SETTINGS.ordinal -> {
                setFragment(SettingsFragment())
                supportActionBar!!.title = getString(R.string.menu_settings)
            }
            FragmentId.TOP.ordinal -> {
                setFragment(TopScoresFragment())
                supportActionBar!!.title = getString(R.string.menu_top)
            }
            else -> setFragment(AboutFragment())
        }
    }

    fun setFragment(frag: Fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.secondContent, frag)
                .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
