package com.chillcoding.ilove.view.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import com.chillcoding.ilove.App
import com.chillcoding.ilove.MainActivity
import com.chillcoding.ilove.R
import com.chillcoding.ilove.extension.DelegatesExt
import com.chillcoding.ilove.extension.showAlertOnLove
import kotlinx.android.synthetic.main.dialog_quote.view.*
import org.jetbrains.anko.share
import java.util.*

class QuoteDialog : DialogFragment() {
    private val mLoveQuoteArray: Array<String> by lazy { resources.getStringArray(R.array.text_love) }
    private val mRandom = Random()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val isPremium: Boolean by DelegatesExt.preference(activity, App.PREF_PREMIUM, true)
        val builder = AlertDialog.Builder(activity)
        val activity = (activity as MainActivity)

        var nbQuotes = mLoveQuoteArray.size
        if (!isPremium)
            nbQuotes = 8
        val dialogQuoteView = (LayoutInflater.from(activity)).inflate(R.layout.dialog_quote, null)
        dialogQuoteView.dialogQuote.text = mLoveQuoteArray[mRandom.nextInt(nbQuotes)]
        builder.setView(dialogQuoteView)
                .setPositiveButton(R.string.action_love, { _, _ -> activity.showAlertOnLove() })
                .setNegativeButton(android.R.string.cancel, null)
        if (isPremium)
            builder.setNeutralButton(R.string.action_share, { _, _ -> share("${dialogQuoteView.dialogQuote.text} \n${getString(R.string.text_from)} ${getString(R.string.url_app)}", "I Love") })
        return builder.create()
    }
}