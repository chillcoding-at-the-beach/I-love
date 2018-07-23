package com.chillcoding.ilove.view.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import com.chillcoding.ilove.MainActivity
import com.chillcoding.ilove.R
import com.chillcoding.ilove.extension.showAlertOnLove
import com.chillcoding.ilove.view.activity.PurchaseActivity
import kotlinx.android.synthetic.main.dialog_quote.view.*
import org.jetbrains.anko.share
import org.jetbrains.anko.startActivity
import java.util.*

class QuoteDialog : DialogFragment() {
    private val mLoveQuoteArray: Array<String> by lazy { resources.getStringArray(R.array.text_love) }
    private val mRandom = Random()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val activity = (activity as MainActivity)

        var nbQuotes = mLoveQuoteArray.size
        if (!activity.isPremium)
            nbQuotes = 8
        val dialogQuoteView = (LayoutInflater.from(activity)).inflate(R.layout.dialog_quote, null)
        dialogQuoteView.dialogQuote.text = mLoveQuoteArray[mRandom.nextInt(nbQuotes)]
        builder.setView(dialogQuoteView)
                .setPositiveButton(R.string.action_love, { _, _ -> activity.showAlertOnLove() })
                .setNegativeButton(android.R.string.cancel, null)
        if (activity.isPremium || activity.isUnlimitedQuotes)
            builder.setNeutralButton(R.string.action_share, { _, _ -> share("${dialogQuoteView.dialogQuote.text} \n${getString(R.string.text_from)} ${getString(R.string.url_app)}", "I Love") })
        else
            builder.setNeutralButton(R.string.action_more, { _, _ -> startActivity<PurchaseActivity>() })
        return builder.create()
    }
}