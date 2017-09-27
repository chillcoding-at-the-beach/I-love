package com.chillcoding.mycuteheart.view.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.chillcoding.mycuteheart.MyApp
import com.chillcoding.mycuteheart.MyMainActivity
import com.chillcoding.mycuteheart.R
import com.chillcoding.mycuteheart.extension.DelegatesExt
import com.chillcoding.mycuteheart.model.MyGameData
import kotlinx.android.synthetic.main.dialog_my_end_game.view.*
import org.jetbrains.anko.share
import java.util.*

/**
 * Created by macha on 01/08/2017.
 */
class MyEndGameDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the Builder class for convenient dialog construction
        var bestScore: Int by DelegatesExt.preference(activity, MyApp.PREF_BEST_SCORE, 0)
        var awardLevel: Int by DelegatesExt.preference(activity, MyApp.PREF_AWARD_LEVEL, 1)


        val builder = AlertDialog.Builder(activity)
        val successString = resources.getStringArray(R.array.word_sucess)
        var endGameView = (LayoutInflater.from(activity)).inflate(R.layout.dialog_my_end_game, null)
        var data = arguments.getParcelable<MyGameData>(MyApp.BUNDLE_GAME_DATA)
        endGameView.dialogTitleTextView.text = "${successString[Random().nextInt(successString.size)].toUpperCase()}!"
        endGameView.dialogScoreTextView.text = "${data.score}"
        endGameView.dialogLevelTextView.text = "${resources.getString(R.string.word_level)} ${data.level}"

        when {
            data.score > awardLevel * MyApp.SCORE_PER_AWARD -> {
                awardLevel = awardLevel + 1
                endGameView.dialogAwardTextView.visibility = View.VISIBLE
                bestScore = data.score
            }
            data.score > bestScore -> {
                bestScore = data.score
                endGameView.dialogBestTextView.visibility = View.VISIBLE
            }
        }
        var activity = (activity as MyMainActivity)
        activity.setUpNewGame()
        builder.setView(endGameView)
                .setPositiveButton(R.string.action_play, { _, _ -> activity.playGame(true) })
                .setNeutralButton(R.string.action_share, { _, _ -> share("${getString(R.string.text_share_score)} ${data.score} <3 ${getString(R.string.word_with)} ${getString(R.string.app_name)}!", "I Love") })
                .setNegativeButton(android.R.string.cancel, null)
        return builder.create()
    }
}
