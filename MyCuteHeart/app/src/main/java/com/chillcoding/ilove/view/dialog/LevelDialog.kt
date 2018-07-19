package com.chillcoding.ilove.view.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import com.chillcoding.ilove.App
import com.chillcoding.ilove.MainActivity
import com.chillcoding.ilove.R
import com.chillcoding.ilove.view.activity.PurchaseActivity
import kotlinx.android.synthetic.main.dialog_level.view.*
import org.jetbrains.anko.share
import org.jetbrains.anko.startActivity

class LevelDialog : DialogFragment() {
    private val mLoveQuoteArray: Array<String> by lazy { resources.getStringArray(R.array.text_love) }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = (activity as MainActivity)
        val builder = AlertDialog.Builder(activity)

        val dialogLevelView = (LayoutInflater.from(activity)).inflate(R.layout.dialog_level, null)

        val level = arguments.getInt(App.BUNDLE_GAME_LEVEL)

        //draw the UI
        dialogLevelView.levelNumber.text = "${getString(R.string.word_level)} ${level}"
        dialogLevelView.levelQuote.text = mLoveQuoteArray[(level - 2) % mLoveQuoteArray.size]

        builder.setView(dialogLevelView)
                .setPositiveButton(R.string.action_continue, null)
        if (activity.isPremium || activity.isUnlimitedQuotes)
            builder.setNeutralButton(R.string.action_share, { _, _ -> share("${dialogLevelView.levelQuote.text}  \n${getString(R.string.text_from)} ${getString(R.string.url_app)}", "I Love") })
        else
            builder.setNeutralButton(R.string.action_more, { _, _ -> startActivity<PurchaseActivity>() })
        return builder.create()
    }
}