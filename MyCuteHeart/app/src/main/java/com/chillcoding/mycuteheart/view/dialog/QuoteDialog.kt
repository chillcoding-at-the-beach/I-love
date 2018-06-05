package com.chillcoding.mycuteheart.view.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import com.chillcoding.mycuteheart.App
import com.chillcoding.mycuteheart.MainActivity
import com.chillcoding.mycuteheart.R
import com.chillcoding.mycuteheart.extension.DelegatesExt
import kotlinx.android.synthetic.main.dialog_quote.view.*
import org.jetbrains.anko.share
import java.util.*

class QuoteDialog : DialogFragment() {
    private val mLoveQuoteArray: Array<String> by lazy { resources.getStringArray(R.array.text_love) }
    private val mRandom = Random()


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        var activity = (activity as MainActivity)
        var isPremium: Boolean by DelegatesExt.preference(activity, App.PREF_PREMIUM, false)
        var nbQuotes = mLoveQuoteArray.size
        if (!isPremium)
            nbQuotes = 8
        val dialogQuoteView = (LayoutInflater.from(activity)).inflate(R.layout.dialog_quote, null)
        dialogQuoteView.dialogQuote.text = mLoveQuoteArray[mRandom.nextInt(nbQuotes)]
        builder.setView(dialogQuoteView)
                .setPositiveButton(R.string.action_like, { _, _ -> activity.showAlertOnLove() })
                .setNegativeButton(android.R.string.cancel, null)
        if (isPremium)
            builder.setNeutralButton(R.string.action_share, { _, _ -> share("${dialogQuoteView.dialogQuote.text}", "I Love") })
        return builder.create()
    }
}