package com.chillcoding.ilove.view.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import com.chillcoding.ilove.App
import com.chillcoding.ilove.App.Companion.AWARD_LIST_SIZE
import com.chillcoding.ilove.MainActivity
import com.chillcoding.ilove.R
import com.chillcoding.ilove.SecondActivity
import com.chillcoding.ilove.extension.DelegatesExt
import com.chillcoding.ilove.extension.awardUnlocked
import com.chillcoding.ilove.model.FragmentId
import org.jetbrains.anko.share
import org.jetbrains.anko.startActivity

class AwardDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        var activity = (activity as MainActivity)
        val isPremium: Boolean by DelegatesExt.preference(activity, App.PREF_PREMIUM, false)
        var awardLevel: Int by DelegatesExt.preference(activity, App.PREF_AWARD_LEVEL, -1)
        var bestScore: Int by DelegatesExt.preference(activity, App.PREF_BEST_SCORE, 0)

        var award = arguments.getInt(App.BUNDLE_AWARD_DATA)

        if (award - 1 > awardLevel) {
            activity.awardUnlocked()
            awardLevel = award - 1
            if (awardLevel > AWARD_LIST_SIZE)
                awardLevel = AWARD_LIST_SIZE
        }

        val dialogAwardView = (LayoutInflater.from(activity)).inflate(R.layout.dialog_award, null)

        builder.setView(dialogAwardView)
                .setPositiveButton(R.string.action_continue, null)
        if (isPremium)
            builder.setNeutralButton(R.string.action_share, { _, _ -> share("{${getString(R.string.text_share_score)}}", "I Love") })
        builder.setNegativeButton(R.string.action_see_awards, { _, _ -> startActivity<SecondActivity>(SecondActivity.FRAGMENT_ID to FragmentId.AWARDS.ordinal) })
        return builder.create()
    }
}