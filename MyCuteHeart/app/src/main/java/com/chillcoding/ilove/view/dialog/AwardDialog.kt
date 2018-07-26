package com.chillcoding.ilove.view.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import com.chillcoding.ilove.R
import com.chillcoding.ilove.view.activity.AwardsActivity
import org.jetbrains.anko.startActivity

class AwardDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val dialogAwardView = (LayoutInflater.from(activity)).inflate(R.layout.dialog_award, null)

        builder.setView(dialogAwardView)
                .setPositiveButton(R.string.action_continue, null)
        builder.setNegativeButton(R.string.action_see_awards, { _, _ -> startActivity<AwardsActivity>() })
        return builder.create()
    }
}