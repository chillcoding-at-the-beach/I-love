package com.chillcoding.mycuteheart

import android.app.Fragment
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.chillcoding.mycuteheart.model.MyFragmentId
import com.chillcoding.mycuteheart.view.fragment.MyAboutFragment
import com.chillcoding.mycuteheart.view.fragment.MySettingsFragment
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
        setContentView(R.layout.app_bar_second)
        setSupportActionBar(secondToolbar)

        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        when (intent.getIntExtra(FRAGMENT_ID, 0)) {
            MyFragmentId.ABOUT.ordinal -> {
                setFragment(MyAboutFragment())
                supportActionBar!!.title = getString(R.string.title_about)
            }
            MyFragmentId.SETTINGS.ordinal -> setFragment(MySettingsFragment())
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
