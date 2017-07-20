package com.chillcoding.mycuteheart

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.chillcoding.mycuteheart.util.*
import com.chillcoding.mycuteheart.view.MyGameView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.firebase.crash.FirebaseCrash
import kotlinx.android.synthetic.main.activity_my_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MyMainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, IabBroadcastReceiver.IabBroadcastListener {

    lateinit var mGameView: MyGameView

    private lateinit var mAdView: AdView

    internal var mIsPremium = false
    var mIsRemoveAds = false

    // SKUs for our products: the premium upgrade and the remove ads
    internal val SKU_PREMIUM = "premium"
    internal val SKU_REMOVE_ADS = "remove_ads"

    // (arbitrary) request code for the purchase flow
    internal val RC_REQUEST = 10001

    // The helper object
    internal var mHelper: IabHelper? = null

    // Provides purchase notification while this app is running
    internal var mBroadcastReceiver: IabBroadcastReceiver? = null

    var mWaitScreen: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_main)
        setSupportActionBar(toolbar)

        val base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0DFHzBK+vaUC8mUBMF7+1AF4KdugEYo5g8dI3pf146Pz5C7Nc6OLnBqcIBLUcJUSRjgR68IXHWopEboR2+xzBxUbaE/2Mg0s1IRiw2xyFNqorU2YfEM8tUsOGKtFTs3D9QxebVMVpi5Uc0Q1n3rtYa4Tirvusynej6GGp0jQTG+cPvj8JLGhADRtQfx7NsGbSgJQd3eDrS/TKOQ1nevZKDTe/czej9jgM+YRQlN7RQIOVznE+UHqQZdno3POJ5PFdQKVlkbbsxPsFJ+yqKMoAMrevVELJ07fVVBy6iSKurWUcJ+XGoz+zktMKt6zhW+kv7Sasi6iiGu37RsEPcKRJQIDAQAB"

        // Create the helper, passing it our context and the public key to verify signatures with
        FirebaseCrash.log("Creating IAB helper.")
        mHelper = IabHelper(this, base64EncodedPublicKey)

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

        //usual stuff about view
        mGameView = findViewById(R.id.gameView) as MyGameView

        fab.setOnClickListener {
            if (!mGameView.mIsPlaying) {
                mGameView.play()
                fab.setImageResource(R.drawable.ic_dialog_pause)
            } else {
                mGameView.pause()
                fab.setImageResource(R.drawable.ic_dialog_play)
            }
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        mAdView = findViewById(R.id.adView) as AdView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

    internal var mGotInventoryListener: IabHelper.QueryInventoryFinishedListener = IabHelper.QueryInventoryFinishedListener { result, inventory ->
        FirebaseCrash.log("Query inventory finished.")

        // Have we been disposed of in the meantime? If so, quit.
        if (mHelper == null) return@QueryInventoryFinishedListener

        // Is it a failure?
        if (result.isFailure) {
            dismissWaitScreen()
            complain("Failed to query inventory: " + result)
            return@QueryInventoryFinishedListener
        }

        FirebaseCrash.log("Query inventory was successful.")

        // Do we have the premium upgrade?
        val premiumPurchase = inventory.getPurchase(SKU_PREMIUM)
        mIsPremium = premiumPurchase != null && verifyDeveloperPayload(premiumPurchase)
        FirebaseCrash.log("User is " + if (mIsPremium) "PREMIUM" else "NOT PREMIUM")

        updateUi()
        dismissWaitScreen()
        FirebaseCrash.log("Initial inventory query finished; enabling main UI.")
    }

    internal var mPurchaseFinishedListener: IabHelper.OnIabPurchaseFinishedListener = IabHelper.OnIabPurchaseFinishedListener { result, purchase ->
        FirebaseCrash.log("Purchase finished: $result, purchase: $purchase")

        // if we were disposed of in the meantime, quit.
        if (mHelper == null) return@OnIabPurchaseFinishedListener

        if (result.isFailure) {
            complain("Error purchasing: " + result)
            dismissWaitScreen()
            return@OnIabPurchaseFinishedListener
        }
        if (!verifyDeveloperPayload(purchase)) {
            complain("Error purchasing. Authenticity verification failed.")
            dismissWaitScreen()
            return@OnIabPurchaseFinishedListener
        }

        FirebaseCrash.log("Purchase successful.")

        if (purchase.sku == SKU_PREMIUM) {
            // bought the premium upgrade!
            FirebaseCrash.log("Purchase is premium upgrade. Congratulating user.")
            alert("${getString(R.string.thank_you_premium)}")
            mIsPremium = true
            updateUi()
            dismissWaitScreen()

        } else if (purchase.sku == SKU_REMOVE_ADS) {

        }
    }

    override fun receivedBroadcast() {
        dismissWaitScreen()
        // Received a broadcast notification that the inventory of items has changed
        FirebaseCrash.log("Received broadcast notification. Querying inventory.")
        try {
            mHelper!!.queryInventoryAsync(mGotInventoryListener)
        } catch (e: IabHelper.IabAsyncInProgressException) {
            complain("Error querying inventory. Another async operation in progress.")
        }
    }

    private fun updateUi() {
        if (mIsPremium || mIsRemoveAds) {
            mAdView.visibility = View.GONE
            mAdView.destroy()
        }
        if (mIsPremium) {
            nav_view.removeAllViews()
            nav_view.inflateMenu(R.menu.activity_main_drawer_premium)
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
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_love -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {

            R.id.nav_premium -> {
                FirebaseCrash.log("Upgrade button clicked; launching purchase flow for upgrade.")
                putWaitScreen()
                //TODO: unique payload per user
                val payload = getString(R.string.payload)
                try {
                    mHelper!!.launchPurchaseFlow(this, SKU_PREMIUM, RC_REQUEST,
                            mPurchaseFinishedListener, payload)
                } catch (e: IabHelper.IabAsyncInProgressException) {
                    complain("Error launching purchase flow. Another async operation in progress.")
                    dismissWaitScreen()
                }
            }
            R.id.nav_remove_ads -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun putWaitScreen() {
        mWaitScreen = ProgressDialog.show(this@MyMainActivity, getString(R.string.wait), getString(R.string.launch_in_app), true)
    }

    private fun dismissWaitScreen() {
        mWaitScreen?.dismiss()
    }

    private fun complain(msg: String) {
        FirebaseCrash.log("Cute Heart Rate Error : $msg")
        dismissWaitScreen()
        alert("${getString(R.string.error)}: +$msg")
    }

    private fun alert(s: String) {
        dismissWaitScreen()
        val bld = AlertDialog.Builder(this)
        bld.setMessage(s)
        bld.setNeutralButton("OK", null)
        FirebaseCrash.log("Showing alert dialog: " + s)
        bld.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        FirebaseCrash.log("onActivityResult($requestCode,$resultCode,$data")
        if (mHelper == null) return

        // Pass on the activity result to the helper for handling
        if (!mHelper!!.handleActivityResult(requestCode, resultCode, data)) {
            dismissWaitScreen()
            super.onActivityResult(requestCode, resultCode, data)
        } else {
            FirebaseCrash.log("onActivityResult handled by IABUtil.")
        }
    }

    public override fun onDestroy() {
        super.onDestroy()

        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver)
        }

        FirebaseCrash.log("Destroying helper.")
        if (mHelper != null) {
            mHelper!!.disposeWhenFinished()
            mHelper = null
        }
    }

    /** Verifies the developer payload of a purchase.  */
    internal fun verifyDeveloperPayload(p: Purchase): Boolean {
        val payload = p.developerPayload

        //TODO: generate id for each user, place it in the end of payload string
        if (payload == getString(R.string.payload))
            return true
        else
            return false
    }

}
