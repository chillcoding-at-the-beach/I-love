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
import com.chillcoding.mycuteheart.model.MyFragmentId
import com.chillcoding.mycuteheart.util.*
import com.chillcoding.mycuteheart.view.dialog.MyEndGameDialog
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.firebase.crash.FirebaseCrash
import kotlinx.android.synthetic.main.activity_my_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.*
import java.util.*

class MyMainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, IabBroadcastReceiver.IabBroadcastListener {

    var isPremium = false

    // The helper object
    private var mHelper: IabHelper? = null

    // Provides purchase notification while this app is running
    private lateinit var mBroadcastReceiver: IabBroadcastReceiver

    private lateinit var mToggle: ActionBarDrawerToggle
    private val mArrayLoveQuote: Array<String> by lazy { resources.getStringArray(R.array.text_love) }
    private val mRandom = Random()

    val isSound: Boolean by DelegatesExt.preference(this, MyApp.PREF_SOUND, true)
    var mPayload: String  by DelegatesExt.preference(this, MyApp.PREF_PAYLOAD, "first")
    private lateinit var mSoundPlayer: MediaPlayer

    companion object {
        val SKU_PREMIUM = "premium"
        // (arbitrary) request code for the purchase flow
        val RC_REQUEST = 10001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        setContentView(R.layout.activity_my_main)
        setSupportActionBar(toolbar)

        mSoundPlayer = MediaPlayer.create(this, R.raw.latina)
        mSoundPlayer.isLooping = true

        setUpGameInfo()

        // Create the helper, passing it our context and the public key to verify signatures with
        FirebaseCrash.log("Creating IAB helper.")
        mHelper = IabHelper(this, getString(R.string.base64_encoded_public_key))

        // enable debug logging (for a production application, you should set this to false).
        mHelper!!.enableDebugLogging(true)

        mHelper!!.startSetup(IabHelper.OnIabSetupFinishedListener { result ->
            FirebaseCrash.log("Setup finished.")

            if (!result.isSuccess) {
                // Oh noes, there was a problem.
                complain("Problem setting up in-app billing: " + result)
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
            mBroadcastReceiver = IabBroadcastReceiver(this@MyMainActivity)
            val broadcastFilter = IntentFilter(IabBroadcastReceiver.ACTION)
            registerReceiver(mBroadcastReceiver, broadcastFilter)

            // IAB is fully set up. Now, let's get an inventory of stuff we own.
            FirebaseCrash.log("Setup successful. Querying inventory.")
            try {
                mHelper!!.queryInventoryAsync(mGotInventoryListener)
            } catch (e: IabHelper.IabAsyncInProgressException) {
                complain("Error querying inventory. Another async operation in progress.")
            }
        })

        fab.setOnMusicFabClickListener(object : FloatingMusicActionButton.OnMusicFabClickListener {
            override fun onClick(view: View) {
                if (!gameView.isPlaying)
                    playGame()
                else
                    pauseGame()
            }
        })

        mToggle = object : ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            override fun onDrawerClosed(view: View?) {}
            override fun onDrawerOpened(drawerView: View?) {
                pauseGame()
            }
        }

        navView.setNavigationItemSelectedListener(this)
    }

    internal var mGotInventoryListener: IabHelper.QueryInventoryFinishedListener = IabHelper.QueryInventoryFinishedListener { result, inventory ->
        FirebaseCrash.log("Query inventory finished.")

        // Have we been disposed of in the meantime? If so, quit.
        if (mHelper == null) return@QueryInventoryFinishedListener

        // Is it a failure?
        if (result.isFailure) {
            complain("Failed to query inventory: " + result)
            return@QueryInventoryFinishedListener
        }

        FirebaseCrash.log("Query inventory was successful.")

        // Do we have the premium upgrade?
        val premiumPurchase = inventory.getPurchase(SKU_PREMIUM)
        isPremium = premiumPurchase != null && verifyDeveloperPayload(premiumPurchase)
        FirebaseCrash.log("User is " + if (isPremium) "PREMIUM" else "NOT PREMIUM")

        updateUi()
        FirebaseCrash.log("Initial inventory query finished; enabling main UI.")
    }

    internal var mPurchaseFinishedListener: IabHelper.OnIabPurchaseFinishedListener = IabHelper.OnIabPurchaseFinishedListener { result, purchase ->
        FirebaseCrash.log("Purchase finished: $result, purchase: $purchase")

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

        FirebaseCrash.log("Purchase successful.")

        if (purchase.sku == SKU_PREMIUM) {
            // bought the premium upgrade!
            FirebaseCrash.log("Purchase is premium upgrade. Congratulating user.")
            alert(getString(R.string.text_thank_you_premium))
            isPremium = true
            updateUi()
        }
    }

    override fun receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
        FirebaseCrash.log("Received broadcast notification. Querying inventory.")
        try {
            mHelper!!.queryInventoryAsync(mGotInventoryListener)
        } catch (e: IabHelper.IabAsyncInProgressException) {
            complain("Error querying inventory. Another async operation in progress.")
        }
    }

    private fun requestAccountPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.GET_ACCOUNTS)) {
                alert(R.string.text_permissions_explanation) {
                    yesButton { requestAccountPermission() }
                }
            } else {
                ActivityCompat.requestPermissions(this,
                        Array<String>(1) { android.Manifest.permission.GET_ACCOUNTS },
                        MyApp.MY_PERMISSIONS_REQUEST_GET_ACCOUNTS);
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MyApp.MY_PERMISSIONS_REQUEST_GET_ACCOUNTS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    launchPurchase()
            }
        }
    }

    private fun launchPurchase() {
        mPayload = getPayload()
        try {
            mHelper!!.launchPurchaseFlow(this, SKU_PREMIUM, RC_REQUEST,
                    mPurchaseFinishedListener, mPayload)
        } catch (e: IabHelper.IabAsyncInProgressException) {
            complain("Error launching purchase flow. Another async operation in progress.")
        }
    }

    private fun getPayload(): String {
        if (mPayload == "first") {
            val accountName = getAccountName()
            doAsync {
                val accountID = GoogleAuthUtil.getAccountId(applicationContext, accountName)
                mPayload = "${getString(R.string.payload)}_$accountID"
            }
        }
        return mPayload
    }

    private fun getAccountName(): String {
        var accountName = "user"
        val manager = getSystemService(ACCOUNT_SERVICE) as AccountManager
        val list = manager.accounts
        for (account in list) {
            if (account.type.equals("com.google", true)) {
                accountName = account.name
            }
        }
        return accountName
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
            R.id.nav_premium -> {
                FirebaseCrash.log("Upgrade button clicked; launching purchase flow for upgrade.")
                requestAccountPermission()
            }
            R.id.nav_about -> startActivity<MySecondaryActivity>(MySecondaryActivity.FRAGMENT_ID to MyFragmentId.ABOUT.ordinal)
            R.id.nav_awards -> startActivity<MySecondaryActivity>(MySecondaryActivity.FRAGMENT_ID to MyFragmentId.AWARDS.ordinal)
            R.id.nav_send -> email("hello@chillcoding.com", getString(R.string.subject_feedback), "")
            R.id.nav_settings -> startActivity<MySecondaryActivity>(MySecondaryActivity.FRAGMENT_ID to MyFragmentId.SETTINGS.ordinal)
            R.id.nav_share -> share(getString(R.string.text_share_app), getString(R.string.app_name))
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        FirebaseCrash.log("onActivityResult($requestCode,$resultCode,$data")
        if (mHelper == null) return
        // Pass on the activity result to the helper for handling
        if (!mHelper!!.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data)
        } else {
            FirebaseCrash.log("onActivityResult handled by IABUtil.")
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mBroadcastReceiver)
        FirebaseCrash.log("Destroying helper.")
        if (mHelper != null) {
            mHelper!!.disposeWhenFinished()
            mHelper = null
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(MyApp.BUNDLE_GAME_DATA, gameView.myGameData)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        gameView.myGameData = savedInstanceState.getParcelable(MyApp.BUNDLE_GAME_DATA)
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
        FirebaseCrash.log("${getString(R.string.app_name)} Error : $msg")
    }

    private fun alert(s: String) {
        val bld = AlertDialog.Builder(this)
        bld.setMessage(s)
        bld.setNeutralButton("OK", null)
        FirebaseCrash.log("Showing alert dialog: " + s)
        bld.create().show()
    }

    internal fun verifyDeveloperPayload(p: Purchase): Boolean {
        val payload = p.developerPayload
        return payload == getPayload()
    }

    private fun updateUi() {
        if (isPremium) {
            navView.menu.findItem(R.id.nav_premium).isVisible = false
            navView.menu.findItem(R.id.nav_more_features).isVisible = false
            navView.menu.findItem(R.id.nav_awards).isVisible = true
            navView.menu.findItem(R.id.nav_settings).isVisible = true
        }
    }

    private fun showAlertOnLove() {
        alert(mArrayLoveQuote[mRandom.nextInt(mArrayLoveQuote.size)]) {
            positiveButton(getString(R.string.action_like)) { showAlertOnLove() }
            noButton { }
        }.show()
    }

    private fun pauseGame() {
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
        bundle.putParcelable(MyApp.BUNDLE_GAME_DATA, gameView.myGameData)
        var popup = MyEndGameDialog()
        popup.arguments = bundle
        popup.show(fragmentManager, MyMainActivity::class.java.simpleName)
    }

    private fun setUpGameInfo() {
        updateScore()
        updateLevel()
    }

    fun updateScore() {
        when (gameView.myGameData.score) {
            in 0..9 -> mainScore.text = "${getString(R.string.word_score)}: 00${gameView.myGameData.score}"
            in 10..99 -> mainScore.text = "${getString(R.string.word_score)}: 0${gameView.myGameData.score}"
            else -> mainScore.text = "${getString(R.string.word_score)}: ${gameView.myGameData.score}"
        }
    }

    fun updateLevel() {
        mainLevel.text = "${getString(R.string.word_level)}: ${gameView.myGameData.level}"
    }

    fun updateNbLife() {
        when (gameView.myGameData.nbLife) {
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
        mSoundPlayer = MediaPlayer.create(this, R.raw.latina)
        mSoundPlayer.isLooping = true
    }

    fun setUpNewGame() {
        gameView.setUpNewGame()
        updateGameInfo()
    }

    override fun onRestart() {
        super.onRestart()
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
    }
}
