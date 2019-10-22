package com.chillcoding.ilove.view.activity

import android.accounts.AccountManager
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.emoji.text.EmojiCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.chillcoding.ilove.App
import com.chillcoding.ilove.R
import com.chillcoding.ilove.extension.DelegatesExt
import com.chillcoding.ilove.util.IabBroadcastReceiver
import com.chillcoding.ilove.util.IabHelper
import com.chillcoding.ilove.util.Purchase
import com.google.android.gms.auth.GoogleAuthUtil
import kotlinx.android.synthetic.main.activity_purchase.*
import org.jetbrains.anko.*

class PurchaseActivity : AppCompatActivity(), IabBroadcastReceiver.IabBroadcastListener {

    private val EXCLAMATION_EMOJI = "\u2763"

    var userPayload: String  by DelegatesExt.preference(this, App.PREF_PAYLOAD, "newinstall")

    lateinit var askedSku: String

    // The helper object
    private var mHelper: IabHelper? = null

    // Provides purchase notification while this app is running
    private var mBroadcastReceiver: IabBroadcastReceiver? = null

    var isPremium: Boolean by DelegatesExt.preference(this, App.PREF_PREMIUM, false)
    var isUnlimitedQuotes: Boolean by DelegatesExt.preference(this, App.PREF_UNLIMITED_QUOTES, false)
    var isUnlimitedAwards: Boolean by DelegatesExt.preference(this, App.PREF_UNLIMITED_AWARDS, false)

    val progressDialog by lazy { indeterminateProgressDialog(R.string.text_waiting_explanation) }

    companion object {
        const val SKU_PREMIUM = "premium"
        const val SKU_UNLIMITED_QUOTES = "unlimited_quotes"
        const val SKU_UNLIMITED_AWARDS = "unlimited_awards"
        // (arbitrary) request code for the purchase flow
        val RC_REQUEST = 10001
    }

    private var isPremiumExpand = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase)
        setSupportActionBar(purchaseToolbar)

        supportActionBar!!.title = resources.getString(R.string.menu_more_features)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        purchasePremiumExpand.setOnClickListener {
            if (!isPremiumExpand) {
                purchasePremiumLine.visibility = View.VISIBLE
                purchasePremiumText.visibility = View.VISIBLE
                purchasePremiumButton.visibility = View.VISIBLE
                purchasePremiumExpand.setImageResource(R.drawable.ic_expand_less)
                isPremiumExpand = true
            } else {
                purchasePremiumLine.visibility = View.INVISIBLE
                purchasePremiumText.visibility = View.GONE
                purchasePremiumButton.visibility = View.GONE
                purchasePremiumExpand.setImageResource(R.drawable.ic_expand_more)
                isPremiumExpand = false
            }
        }

        setUpEmoji()

        purchasePremiumTitle.setOnClickListener { startPurchaseFlow(SKU_PREMIUM) }
        purchasePremiumButton.setOnClickListener { startPurchaseFlow(SKU_PREMIUM) }

        purchaseQuotes.setOnClickListener { startPurchaseFlow(SKU_UNLIMITED_QUOTES) }

        purchaseAwards.setOnClickListener { startPurchaseFlow(SKU_UNLIMITED_AWARDS) }
    }

    private fun setUpEmoji() {
        val premiumTitleText = EmojiCompat.get().process("${App.STAR_EMOJI} ${getString(R.string.title_purchase_premium)} ${App.STAR_EMOJI}")
        purchasePremiumTitle.text = premiumTitleText
        val quotesTitleText = EmojiCompat.get().process("${EXCLAMATION_EMOJI} ${getString(R.string.title_purchase_quotes)} ${EXCLAMATION_EMOJI}")
        purchaseQuotesTitle.text = quotesTitleText
        val awardsTitleText = EmojiCompat.get().process("${App.TROPHY_EMOJI} ${getString(R.string.title_purchase_awards)} ${App.TROPHY_EMOJI}")
        purchaseAwardsTitle.text = awardsTitleText
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setUpInAppPurchase() {

        mHelper = IabHelper(this, getString(R.string.base64_encoded_public_key))

        // /!\ for a production application, you should set this to false).
        mHelper!!.enableDebugLogging(false)

        mHelper!!.startSetup(IabHelper.OnIabSetupFinishedListener { result ->

            if (!result.isSuccess) {
                //"Problem setting up in-app billing: $result")
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
            mBroadcastReceiver = IabBroadcastReceiver(this@PurchaseActivity)
            val broadcastFilter = IntentFilter(IabBroadcastReceiver.ACTION)
            registerReceiver(mBroadcastReceiver, broadcastFilter)

            // IAB is fully set up. Now, let's get an inventory of stuff we own.
            try {
                mHelper!!.queryInventoryAsync(mGotInventoryListener)
            } catch (e: IabHelper.IabAsyncInProgressException) {
                //"Error querying inventory. Another async operation in progress.")
            }
        })
    }

    internal var mGotInventoryListener: IabHelper.QueryInventoryFinishedListener = IabHelper.QueryInventoryFinishedListener { result, inventory ->
        // info("Query inventory finished.")

        // Have we been disposed of in the meantime? If so, quit.
        if (mHelper == null) return@QueryInventoryFinishedListener

        // Is it a failure?
        if (result.isFailure) {
            //"Failed to query inventory: " + result)
            return@QueryInventoryFinishedListener
        }

        // Do we have the premium upgrade?
        val premiumPurchase = inventory.getPurchase(SKU_PREMIUM)
        isPremium = premiumPurchase != null && verifyDeveloperPayload(premiumPurchase)

        // Do we have unlimited quotes
        val unliQuotesPurchase = inventory.getPurchase(SKU_UNLIMITED_QUOTES)
        isUnlimitedQuotes = unliQuotesPurchase != null && verifyDeveloperPayload(unliQuotesPurchase)
        // Do we have unlimited awards
        val unliAwardsPurchase = inventory.getPurchase(SKU_UNLIMITED_AWARDS)
        isUnlimitedAwards = unliAwardsPurchase != null && verifyDeveloperPayload(unliAwardsPurchase)

        if (!isPremium && askedSku == SKU_PREMIUM || !isUnlimitedQuotes && askedSku == SKU_UNLIMITED_QUOTES || !isUnlimitedAwards && askedSku == SKU_UNLIMITED_AWARDS)
            launchPurchase(askedSku)
    }

    internal var mPurchaseFinishedListener: IabHelper.OnIabPurchaseFinishedListener = IabHelper.OnIabPurchaseFinishedListener { result, purchase ->
        // if we were disposed of in the meantime, quit.
        if (mHelper == null) return@OnIabPurchaseFinishedListener

        if (result.isFailure) {
            //"Error purchasing: " + result)
            return@OnIabPurchaseFinishedListener
        }
        if (!verifyDeveloperPayload(purchase)) {
            //"Error purchasing. Authenticity verification failed.")
            return@OnIabPurchaseFinishedListener
        }

        when (purchase.sku) {
            SKU_PREMIUM -> {
                // bought the premium upgrade!
                longToast(R.string.text_thank_you_premium)
                isPremium = true
            }
            SKU_UNLIMITED_QUOTES -> {
                longToast(R.string.text_thank_you_unlimited_quotes)
                isUnlimitedQuotes = true
            }

            SKU_UNLIMITED_AWARDS -> {
                longToast(R.string.text_thank_you_unlimited_awards)
                isUnlimitedAwards = true
            }
        }
    }

    override fun receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
        try {
            mHelper!!.queryInventoryAsync(mGotInventoryListener)
        } catch (e: IabHelper.IabAsyncInProgressException) {
            //"Error querying inventory. Another async operation in progress.")
        }
    }

    fun startPurchaseFlow(sku: String) {
        askedSku = sku
        //In aim of getting userPayload we ask for permissions
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {
            alert(R.string.text_permissions_explanation) {
                yesButton {
                    ActivityCompat.requestPermissions(this@PurchaseActivity,
                            Array<String>(1) { android.Manifest.permission.GET_ACCOUNTS },
                            App.PERMISSIONS_REQUEST_GET_ACCOUNTS);
                }
                noButton { }
            }.show()
        } else
            setUpInAppPurchase()
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

    private fun launchPurchase(sku: String) {
        try {
            mHelper!!.launchPurchaseFlow(this, sku, RC_REQUEST,
                    mPurchaseFinishedListener, userPayload)
        } catch (e: IabHelper.IabAsyncInProgressException) {
            //"Error launching purchase flow. Another async operation in progress."
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
        setUpInAppPurchase()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (mHelper == null) return
        // Pass on the activity result to the helper for handling
        if (!mHelper!!.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (mBroadcastReceiver != null)
            unregisterReceiver(mBroadcastReceiver)
        if (mHelper != null) {
            mHelper!!.disposeWhenFinished()
            mHelper = null
        }
    }

    internal fun verifyDeveloperPayload(p: Purchase): Boolean {
        val payload = p.developerPayload
        return payload == userPayload
    }
}