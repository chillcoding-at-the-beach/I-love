package com.chillcoding.mycuteheart.view.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import com.chillcoding.mycuteheart.MainActivity
import com.chillcoding.mycuteheart.R
import kotlinx.android.synthetic.main.dialog_quote.view.*
import java.util.*

class QuoteDialog : DialogFragment() {
    private val mLoveQuoteArray: Array<String> by lazy { resources.getStringArray(R.array.text_love) }
    private val mRandom = Random()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        var activity = (activity as MainActivity)

        val dialogQuoteView = (LayoutInflater.from(activity)).inflate(R.layout.dialog_quote, null)
        dialogQuoteView.dialogQuote.text = mLoveQuoteArray[mRandom.nextInt(mLoveQuoteArray.size)]
        builder.setView(dialogQuoteView)
                .setPositiveButton(R.string.action_like, { _, _ -> activity.showAlertOnLove() })
                .setNegativeButton(android.R.string.cancel, null)
        return builder.create()
    }
}