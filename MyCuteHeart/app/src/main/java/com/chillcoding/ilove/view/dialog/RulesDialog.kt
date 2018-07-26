package com.chillcoding.ilove.view.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.support.text.emoji.EmojiCompat
import android.view.LayoutInflater
import com.chillcoding.ilove.App
import com.chillcoding.ilove.MainActivity
import com.chillcoding.ilove.R
import com.chillcoding.ilove.view.activity.PurchaseActivity
import kotlinx.android.synthetic.main.dialog_rules.view.*
import org.jetbrains.anko.startActivity


class RulesDialog : DialogFragment() {

    private val RED_HEART_EMOJI = "\u2764"
    private val HEART_EMOJI = "\u2661"
    private val HAND_EMOJI = "\ud83d\udc46"
    private val GREEN_HEART_EMOJI = "\ud83d\udc9a"
    private val YELLOW_HEART_EMOJI = "\ud83d\udc9b"

    private val SPECIAL_HEART_EMOJI = "\ud83d\udc9d"

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val activity = (activity as MainActivity)

        val dialogRulesView = (LayoutInflater.from(activity)).inflate(R.layout.dialog_rules, null)
        val rulesText = EmojiCompat.get().process("$HEART_EMOJI ${getString(R.string.text_rules_aim)}" +
                "-> $RED_HEART_EMOJI + $HAND_EMOJI\n" +
                "\n$RED_HEART_EMOJI ${getString(R.string.text_rules_lifes)}" +
                "-> $RED_HEART_EMOJI $RED_HEART_EMOJI $HEART_EMOJI\n" +
                "\n$GREEN_HEART_EMOJI ${getString(R.string.text_rules_green_gauge)}" +
                "-> ${getString(R.string.word_level)} 1 $RED_HEART_EMOJI + $HAND_EMOJI = 1 ${getString(R.string.word_point)}\n" +
                "-> ${getString(R.string.word_level)} 2 $RED_HEART_EMOJI + $HAND_EMOJI = 2 ${getString(R.string.word_points)}\n" +
                "-> ${getString(R.string.word_level)} k $RED_HEART_EMOJI + $HAND_EMOJI = k ${getString(R.string.word_points)}\n" +
                "\n$YELLOW_HEART_EMOJI ${getString(R.string.text_rules_yellow_gauge)}" +
                "-> n ${getString(R.string.word_points)} = ${App.TROPHY_EMOJI}\n" +
                "\n ${App.TROPHY_EMOJI} ${getString(R.string.text_rules_trophy)}" +
                "\n${App.STAR_EMOJI} ${getString(R.string.text_rules_top)}" +
                "\n$SPECIAL_HEART_EMOJI ${getString(R.string.text_rules_bonus)}")
        dialogRulesView.dialogRule.text = rulesText
        val rulesEndSymbol = EmojiCompat.get().process("~$HEART_EMOJI~$HEART_EMOJI~$HEART_EMOJI~")
        dialogRulesView.dialogEndSymbol.text = rulesEndSymbol
        builder.setView(dialogRulesView)
                .setPositiveButton(R.string.action_love, null)
                .setNegativeButton(android.R.string.cancel, null)
        builder.setNeutralButton(R.string.action_more, { _, _ -> startActivity<PurchaseActivity>() })
        return builder.create()
    }

}