package com.chillcoding.mycuteheart

import android.accounts.AccountManager
import android.app.AlertDialog
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import be.rijckaert.tim.animatedvector.FloatingMusicActionButton
import com.chillcoding.mycuteheart.extension.DelegatesExt
import com.chillcoding.mycuteheart.model.FragmentId
import com.chillcoding.mycuteheart.util.IabBroadcastReceiver
import com.chillcoding.mycuteheart.util.IabHelper
import com.chillcoding.mycuteheart.util.Purchase
import com.chillcoding.mycuteheart.view.dialog.EndGameDialog
import com.google.android.gms.auth.GoogleAuthUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.*
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, IabBroadcastReceiver.IabBroadcastListener, AnkoLogger {

    var isPremium: Boolean by DelegatesExt.preference(this, App.PREF_PREMIUM, false)

    // The helper object
    private var mHelper: IabHelper? = null

    // Provides purchase notification while this app is running
    private lateinit var mBroadcastReceiver: IabBroadcastReceiver

    private lateinit var mToggle: ActionBarDrawerToggle
    private val mLoveQuoteArray: Array<String> by lazy { resources.getStringArray(R.array.text_love) }
    private val mRandom = Random()

    val isSound: Boolean by DelegatesExt.preference(this, App.PREF_SOUND, true)
    var userPayload: String  by DelegatesExt.preference(this, App.PREF_PAYLOAD, "newinstall")
    private lateinit var mSoundPlayer: MediaPlayer

    val progressDialog by lazy { indeterminateProgressDialog(R.string.text_waiting_explanation) }

    companion object {
        val SKU_PREMIUM = "premium"
        // (arbitrary) request code for the purchase flow
        val RC_REQUEST = 10001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        setUpGame()

        setUpInAppPurchase()

        setUpMenus()
    }

    private fun setUpGame() {
        setUpSound()
        updateScore()
        updateLevel()
    }

    private fun setUpMenus() {
        setUpFab()
        setUpNavigationDrawer()
    }

    private fun setUpInAppPurchase() {
        requestAccountPermission()
        info("Creating IAB helper.")
        mHelper = IabHelper(this, getString(R.string.base64_encoded_public_key))

        // /!\ for a production application, you should set this to false).
        mHelper!!.enableDebugLogging(false)

        mHelper!!.startSetup(IabHelper.OnIabSetupFinishedListener { result ->
            info("Setup finished.")

            if (!result.isSuccess) {
                complain("Problem setting up in-app billing: $result")
                return@OnIabSetupFinishedListener
            }

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return@OnIabSetupFinishedListener

            // Important: Dynamically register for broadcast messages about updated purchases.
            // We register the receiver here instead of as a <receiver> in the Manifest
            // because we always call getPurchases() at startup, so therefore we can ignore
            // any broadcasts sent while the app isn't running.
            // Note: registering this listener in an Activity is a bad idea, but is done here
            // because this is a SAMPLE. Regardless, the receiver must be registered after
            // IabHelper is setup, but before first call to getPurchases().
            mBroadcastReceiver = IabBroadcastReceiver(this@MainActivity)
            val broadcastFilter = IntentFilter(IabBroadcastReceiver.ACTION)
            registerReceiver(mBroadcastReceiver, broadcastFilter)

            // IAB is fully set up. Now, let's get an inventory of stuff we own.
            info("Setup successful. Querying inventory.")
            try {
                mHelper!!.queryInventoryAsync(mGotInventoryListener)
            } catch (e: IabHelper.IabAsyncInProgressException) {
                complain("Error querying inventory. Another async operation in progress.")
            }
        })

    }

    internal var mGotInventoryListener: IabHelper.QueryInventoryFinishedListener = IabHelper.QueryInventoryFinishedListener { result, inventory ->
        info("Query inventory finished.")

        // Have we been disposed of in the meantime? If so, quit.
        if (mHelper == null) return@QueryInventoryFinishedListener

        // Is it a failure?
        if (result.isFailure) {
            complain("Failed to query inventory: " + result)
            return@QueryInventoryFinishedListener
        }

        info("Query inventory was successful.")

        // Do we have the premium upgrade?
        val premiumPurchase = inventory.getPurchase(SKU_PREMIUM)
        isPremium = premiumPurchase != null && verifyDeveloperPayload(premiumPurchase)
        updateUi()
        info("Initial inventory query finished; enabling main UI. PREMIUM : $isPremium")
    }

    internal var mPurchaseFinishedListener: IabHelper.OnIabPurchaseFinishedListener = IabHelper.OnIabPurchaseFinishedListener { result, purchase ->
        info("Purchase finished: $result, purchase: $purchase")

        // if we were disposed of in the meantime, quit.
        if (mHelper == null) return@OnIabPurchaseFinishedListener

        if (result.isFailure) {
            complain("Error purchasing: " + result)
            return@OnIabPurchaseFinishedListener
        }
        if (!verifyDeveloperPayload(purchase)) {
            complain("Error purchasing. Authenticity verification failed.")
            return@OnIabPurchaseFinishedListener
        }

        info("Purchase successful.")

        if (purchase.sku == SKU_PREMIUM) {
            // bought the premium upgrade!
            info("Purchase is premium upgrade. Congratulating user.")
            alert(getString(R.string.text_thank_you_premium))
            isPremium = true
            updateUi()
        }
    }

    override fun receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
        info("Received broadcast notification. Querying inventory.")
        try {
            mHelper!!.queryInventoryAsync(mGotInventoryListener)
        } catch (e: IabHelper.IabAsyncInProgressException) {
            complain("Error querying inventory. Another async operation in progress.")
        }
    }

    private fun requestAccountPermission() {
        //In aim of getting userPayload we ask for permissions
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {

            alert(R.string.text_permissions_explanation) {
                yesButton {
                    ActivityCompat.requestPermissions(this@MainActivity,
                            Array<String>(1) { android.Manifest.permission.GET_ACCOUNTS },
                            App.PERMISSIONS_REQUEST_GET_ACCOUNTS);
                }
                noButton { }
            }.show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            App.PERMISSIONS_REQUEST_GET_ACCOUNTS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    getPayload()
            }
        }
    }

    private fun launchPurchase() {
        try {
            mHelper!!.launchPurchaseFlow(this, SKU_PREMIUM, RC_REQUEST,
                    mPurchaseFinishedListener, userPayload)
        } catch (e: IabHelper.IabAsyncInProgressException) {
            complain("Error launching purchase flow. Another async operation in progress.")
        }
    }

    private fun getPayload(): String {
        if (userPayload == "newinstall") {
            val accountName = getAccountName()
            progressDialog.show()
            doAsync {
                val accountID = GoogleAuthUtil.getAccountId(applicationContext, accountName)
                userPayload = "${getString(R.string.payload)}_$accountID"
                uiThread { myCallBack() }
            }
        }
        return userPayload
    }

    private fun myCallBack() {
        progressDialog.dismiss()
    }

    private fun getAccountName(): String {
        var accountName = "user"
        val manager = getSystemService(ACCOUNT_SERVICE) as AccountManager
        val list = manager.accounts
        for (account in list) {
            if (account.type.equals("com.google", true)) {
                accountName = account.name
                return accountName
            }
        }
        return accountName
    }

    private fun setUpNavigationDrawer() {
        mToggle = object : ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            override fun onDrawerClosed(view: View?) {}
            override fun onDrawerOpened(drawerView: View?) {
                pauseGame(true)
            }
        }

        navView.setNavigationItemSelectedListener(this)
    }

    private fun setUpFab() {
        fab.setOnMusicFabClickListener(object : FloatingMusicActionButton.OnMusicFabClickListener {
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
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawer_layout.closeDrawer(GravityCompat.START)
        when (item.itemId) {
            R.id.nav_premium -> launchPurchase()
            R.id.nav_about -> startActivity<SecondActivity>(SecondActivity.FRAGMENT_ID to FragmentId.ABOUT.ordinal)
            R.id.nav_awards -> startActivity<SecondActivity>(SecondActivity.FRAGMENT_ID to FragmentId.AWARDS.ordinal)
            R.id.nav_send -> email("hello@chillcoding.com", getString(R.string.subject_feedback), "")
            R.id.nav_settings -> startActivity<SecondActivity>(SecondActivity.FRAGMENT_ID to FragmentId.SETTINGS.ordinal)
            R.id.nav_share -> share(getString(R.string.text_share_app), getString(R.string.app_name))
            R.id.nav_top -> startActivity<SecondActivity>(SecondActivity.FRAGMENT_ID to FragmentId.TOP.ordinal)
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        info("onActivityResult($requestCode,$resultCode,$data")
        if (mHelper == null) return
        // Pass on the activity result to the helper for handling
        if (!mHelper!!.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data)
        } else {
            info("onActivityResult handled by IABUtil.")
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mBroadcastReceiver)
        info("Destroying helper.")
        if (mHelper != null) {
            mHelper!!.disposeWhenFinished()
            mHelper = null
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(App.BUNDLE_GAME_DATA, gameView.gameData)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        gameView.gameData = savedInstanceState.getParcelable(App.BUNDLE_GAME_DATA)
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
        pauseGame()
    }

    private fun complain(msg: String) {
        error("${getString(R.string.app_name)} Error : $msg")
    }

    private fun alert(s: String) {
        val bld = AlertDialog.Builder(this)
        bld.setMessage(s)
        bld.setNeutralButton("OK", null)
        error("Showing alert dialog: " + s)
        bld.create().show()
    }

    internal fun verifyDeveloperPayload(p: Purchase): Boolean {
        val payload = p.developerPayload
        return payload == userPayload
    }

    private fun updateUi() {
        if (isPremium) {
            navView.menu.findItem(R.id.nav_premium).isVisible = false
            navView.menu.findItem(R.id.nav_more_features).isVisible = false
        }
    }

    private fun showAlertOnLove() {
        alert(mLoveQuoteArray[mRandom.nextInt(mLoveQuoteArray.size)]) {
            positiveButton(getString(R.string.action_like)) { showAlertOnLove() }
            noButton { }
        }.show()
    }

    private fun pauseGame(animateFab: Boolean = false) {
        if (animateFab)
            fab.playAnimation()
        gameView.pause()
        mSoundPlayer.pause()
    }

    fun playGame(animateFab: Boolean = false) {
        if (animateFab)
            fab.playAnimation()
        if (isSound)
            mSoundPlayer.start()
        gameView.play()
    }

    fun endGame() {
        resetSound()
        fab.playAnimation()
        var bundle = Bundle()
        bundle.putParcelable(App.BUNDLE_GAME_DATA, gameView.gameData)
        var popup = EndGameDialog()
        popup.arguments = bundle
        popup.show(fragmentManager, MainActivity::class.java.simpleName)
    }

    private fun setUpSound() {
        mSoundPlayer = MediaPlayer.create(this, R.raw.latina)
        mSoundPlayer.isLooping = true
        mSoundPlayer.setVolume(0.6F, 0.6F)
    }

    fun updateScore() {
        when (gameView.gameData.score) {
            in 0..9 -> mainScore.text = "${getString(R.string.word_score)}: 00${gameView.gameData.score}"
            in 10..99 -> mainScore.text = "${getString(R.string.word_score)}: 0${gameView.gameData.score}"
            else -> mainScore.text = "${getString(R.string.word_score)}: ${gameView.gameData.score}"
        }
    }

    fun updateLevel() {
        mainLevel.text = "${getString(R.string.word_level)}: ${gameView.gameData.level}"
    }

    fun updateNbLife() {
        when (gameView.gameData.nbLife) {
            0 -> {
                mainFirstLife.setImageResource(R.drawable.ic_life_lost)
                endGame()
            }
            1 -> {
                mainSecondLife.setImageResource(R.drawable.ic_life_lost)
                mainThirdLife.setImageResource(R.drawable.ic_life_lost)
            }
            2 -> mainThirdLife.setImageResource(R.drawable.ic_life_lost)
            3 -> {
                mainFirstLife.setImageResource(R.drawable.ic_life)
                mainSecondLife.setImageResource(R.drawable.ic_life)
                mainThirdLife.setImageResource(R.drawable.ic_life)
            }
        }
    }

    private fun updateGameInfo() {
        updateScore()
        updateNbLife()
        updateLevel()
    }

    private fun resetSound() {
        mSoundPlayer.reset()
        setUpSound()
    }

    fun setUpNewGame() {
        gameView.setUpNewGame()
        updateGameInfo()
    }

    override fun onRestart() {
        super.onRestart()
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        fab.changeMode(FloatingMusicActionButton.Mode.PLAY_TO_PAUSE)
    }
}
