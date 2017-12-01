package com.chillcoding.mycuteheart

import android.app.Fragment
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.chillcoding.mycuteheart.model.MyFragmentId
import com.chillcoding.mycuteheart.view.fragment.MyAboutFragment
import com.chillcoding.mycuteheart.view.fragment.MyAwardsFragment
import com.chillcoding.mycuteheart.view.fragment.MySettingsFragment
import com.chillcoding.mycuteheart.view.fragment.TopScoresFragment
import kotlinx.android.synthetic.main.app_bar_second.*

/**
 * Created by macha on 02/08/2017.
 */
class MySecondaryActivity : AppCompatActivity() {

    companion object {
        val FRAGMENT_ID = "MySecondaryActivity:fragmentId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out)
        setContentView(R.layout.app_bar_second)
        setSupportActionBar(secondToolbar)

        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        when (intent.getIntExtra(FRAGMENT_ID, 0)) {
            MyFragmentId.ABOUT.ordinal -> {
                setFragment(MyAboutFragment())
                supportActionBar!!.title = getString(R.string.menu_about)
            }
            MyFragmentId.SETTINGS.ordinal -> {
                setFragment(MySettingsFragment())
                supportActionBar!!.title = getString(R.string.menu_settings)
            }
            MyFragmentId.AWARDS.ordinal -> {
                setFragment(MyAwardsFragment())
                supportActionBar!!.title = getString(R.string.menu_awards)
            }
            MyFragmentId.TOP.ordinal -> {
                setFragment(TopScoresFragment())
                supportActionBar!!.title = getString(R.string.menu_top)
            }
            else -> setFragment(MyAboutFragment())
        }
    }

    fun setFragment(frag: Fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.my_content, frag)
                .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
