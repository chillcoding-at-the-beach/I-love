package com.chillcoding.ilove.view.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import com.chillcoding.ilove.App
import com.chillcoding.ilove.R
import com.chillcoding.ilove.extension.DelegatesExt
import kotlinx.android.synthetic.main.dialog_level.view.*
import org.jetbrains.anko.share

class LevelDialog : DialogFragment() {
    private val mLoveQuoteArray: Array<String> by lazy { resources.getStringArray(R.array.text_love) }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val isPremium: Boolean by DelegatesExt.preference(activity, App.PREF_PREMIUM, false)

        val dialogLevelView = (LayoutInflater.from(activity)).inflate(R.layout.dialog_level, null)

        val level = arguments.getInt(App.BUNDLE_GAME_LEVEL)

        //draw the UI
        dialogLevelView.levelNumber.text = "${getString(R.string.word_level)} ${level}"
        dialogLevelView.levelQuote.text = mLoveQuoteArray[(level - 2) % mLoveQuoteArray.size]

        builder.setView(dialogLevelView)
                .setPositiveButton(R.string.action_continue, null)
        if (isPremium)
            builder.setNeutralButton(R.string.action_share, { _, _ -> share("${dialogLevelView.levelQuote.text}", "I Love") })
        return builder.create()
    }
}