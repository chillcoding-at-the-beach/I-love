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

class RulesDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)

        val dialogRulesView = (LayoutInflater.from(activity)).inflate(R.layout.dialog_rules, null)

        builder.setView(dialogRulesView)
                .setPositiveButton(R.string.action_love, null)

        return builder.create()
    }
}