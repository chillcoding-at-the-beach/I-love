package com.chillcoding.mycuteheart

import android.app.AlertDialog
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.chillcoding.mycuteheart.model.MyFragmentId
import com.chillcoding.mycuteheart.util.*
import com.chillcoding.mycuteheart.view.dialog.MyEndGameDialog
import com.google.android.gms.ads.AdRequest
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
    private val mArrayLoveQuote: Array<String> by lazy { resources.getStringArray(R.array.love_quote) }
    private val mRandom = Random()


    companion object {
        val SKU_PREMIUM = "premium"
        // (arbitrary) request code for the purchase flow
        val RC_REQUEST = 10001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_main)
        setSupportActionBar(toolbar)


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

        fab.setOnClickListener {
            if (!gameView.isPlaying)
                playGame()
            else
                pauseGame()
        }

        mToggle = object : ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            override fun onDrawerClosed(view: View?) {}
            override fun onDrawerOpened(drawerView: View?) {
                pauseGame()
            }
        }

        navView.setNavigationItemSelectedListener(this)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
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
            alert(getString(R.string.thank_you_premium))
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
        when (item.itemId) {

            R.id.nav_premium -> {
                FirebaseCrash.log("Upgrade button clicked; launching purchase flow for upgrade.")
                pauseGame()
                //TODO: unique payload per user
                val payload = getString(R.string.payload)
                try {
                    mHelper!!.launchPurchaseFlow(this, SKU_PREMIUM, RC_REQUEST,
                            mPurchaseFinishedListener, payload)
                } catch (e: IabHelper.IabAsyncInProgressException) {
                    complain("Error launching purchase flow. Another async operation in progress.")
                }
            }
            R.id.nav_about -> {
                startActivity<MySecondaryActivity>(MySecondaryActivity.FRAGMENT_ID to MyFragmentId.ABOUT.ordinal)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
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
        FirebaseCrash.log("Cute Heart Rate Error : $msg")
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
        return payload == getString(R.string.payload)
    }

    private fun updateUi() {
        if (isPremium) {
            adView.visibility = View.GONE
            adView.destroy()
            navView.menu.findItem(R.id.nav_premium).isVisible = false
            navView.menu.findItem(R.id.nav_more_features).isVisible = false
            navView.menu.findItem(R.id.nav_awards).isVisible = true
            navView.menu.findItem(R.id.nav_settings).isVisible = true
        }
    }

    private fun showAlertOnLove() {
        alert(mArrayLoveQuote[mRandom.nextInt(mArrayLoveQuote.size)]) {
            positiveButton(getString(R.string.like)) { showAlertOnLove() }
            noButton { }
        }.show()
    }

    private fun pauseGame() {
        gameView.pause()
        fab.setImageResource(R.mipmap.ic_dialog_play)
    }

    fun playGame() {
        gameView.play()
        fab.setImageResource(R.mipmap.ic_dialog_pause)
    }

    fun endGame() {
        pauseGame()
        var bundle = Bundle()
        bundle.putParcelable(MyApp.BUNDLE_GAME_DATA, gameView.myGameData)
        var popup = MyEndGameDialog()
        popup.arguments = bundle
        popup.show(fragmentManager, MyMainActivity::class.java.simpleName)
    }
}
