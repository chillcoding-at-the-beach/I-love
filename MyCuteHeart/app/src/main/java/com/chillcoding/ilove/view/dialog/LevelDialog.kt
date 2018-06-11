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
import com.chillcoding.ilove.model.GameData
import kotlinx.android.synthetic.main.dialog_level.view.*
import org.jetbrains.anko.share

class LevelDialog : DialogFragment() {
    private val mLoveQuoteArray: Array<String> by lazy { resources.getStringArray(R.array.text_love) }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        var activity = (activity as MainActivity)
        var isPremium: Boolean by DelegatesExt.preference(activity, App.PREF_PREMIUM, false)

        val dialogLevelView = (LayoutInflater.from(activity)).inflate(R.layout.dialog_level, null)

        var data = arguments.getParcelable<GameData>(App.BUNDLE_GAME_DATA)

        //draw the UI
        dialogLevelView.levelNumber.text = "${getString(R.string.word_level)} ${data.level}"
        if (data.level < mLoveQuoteArray.size)
            dialogLevelView.levelQuote.text = mLoveQuoteArray[data.level]
        else
            dialogLevelView.levelQuote.text = mLoveQuoteArray.last()

        builder.setView(dialogLevelView)
                .setPositiveButton(R.string.action_continue, null)
        if (isPremium)
            builder.setNeutralButton(R.string.action_share, { _, _ -> share("${dialogLevelView.levelQuote.text}", "I Love") })
        return builder.create()
    }
}