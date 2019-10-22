package com.chillcoding.ilove

import android.media.MediaPlayer
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.chillcoding.fablibrary.GameFab
import com.chillcoding.ilove.extension.*
import com.chillcoding.ilove.model.FragmentId
import com.chillcoding.ilove.view.activity.AwardsActivity
import com.chillcoding.ilove.view.activity.PurchaseActivity
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.email
import org.jetbrains.anko.startActivity
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, AnkoLogger {

    var isPremium: Boolean by DelegatesExt.preference(this, App.PREF_PREMIUM, false)
    var isUnlimitedQuotes: Boolean by DelegatesExt.preference(this, App.PREF_UNLIMITED_QUOTES, false)

    private lateinit var mToggle: ActionBarDrawerToggle
    private val mLoveQuoteArray: Array<String> by lazy { resources.getStringArray(R.array.text_love) }
    private val mRandom = Random()

    val isSound: Boolean by DelegatesExt.preference(this, App.PREF_SOUND, true)
    lateinit var mSoundPlayer: MediaPlayer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        setUpGame()

        setUpMenus()
    }

    private fun setUpMenus() {
        setUpFab()
        setUpNavigationDrawer()
    }

    private fun setUpNavigationDrawer() {
        if (isPremium)
            navView.menu.findItem(R.id.nav_purchase).isVisible = false
        mToggle = object : ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            override fun onDrawerClosed(view: View) {}
            override fun onDrawerOpened(drawerView: View) {
                if (gameView.isPlaying)
                    pauseGame(true)
            }
        }
        navView.setNavigationItemSelectedListener(this)
    }

    private fun setUpFab() {
        fab.setOnGameFabClickListener(object : GameFab.OnGameFabClickListener {
            override fun onClick(view: View) {
                if (!gameView.isPlaying)
                    playGame()
                else
                    pauseGame()
            }
        })
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_love -> {
                showAlertOnLove()
                return true
            }
            R.id.action_help -> {
                showHelpDialog()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawer_layout.closeDrawer(GravityCompat.START)
        when (item.itemId) {
            R.id.nav_purchase -> startActivity<PurchaseActivity>()
            R.id.nav_about -> startActivity<SecondActivity>(SecondActivity.FRAGMENT_ID to FragmentId.ABOUT.ordinal)
            R.id.nav_awards -> startActivity<AwardsActivity>()
            R.id.nav_send -> email("hello@chillcoding.com", getString(R.string.subject_feedback), "")
            R.id.nav_settings -> startActivity<SecondActivity>(SecondActivity.FRAGMENT_ID to FragmentId.SETTINGS.ordinal)
            R.id.nav_top -> startActivity<SecondActivity>(SecondActivity.FRAGMENT_ID to FragmentId.TOP.ordinal)
            R.id.nav_help -> showHelpDialog()
        }
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(App.STATE_GAME_DATA, gameView.gameData)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        gameView.gameData = savedInstanceState.getParcelable(App.STATE_GAME_DATA)
        updateGameInfo()
    }

    override fun onStart() {
        super.onStart()
        drawer_layout.addDrawerListener(mToggle)
        mToggle.syncState()
    }

    override fun onStop() {
        super.onStop()
        drawer_layout.removeDrawerListener(mToggle)
        if (gameView.isPlaying)
            pauseGame(true)
    }

    override fun onRestart() {
        super.onRestart()
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
    }
}
