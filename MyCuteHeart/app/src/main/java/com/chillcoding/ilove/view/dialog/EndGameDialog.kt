package com.chillcoding.ilove.view.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.chillcoding.ilove.App
import com.chillcoding.ilove.MainActivity
import com.chillcoding.ilove.R
import com.chillcoding.ilove.SecondActivity
import com.chillcoding.ilove.extension.DelegatesExt
import com.chillcoding.ilove.extension.playGame
import com.chillcoding.ilove.extension.setUpNewGame
import com.chillcoding.ilove.model.FragmentId
import com.chillcoding.ilove.model.GameData
import com.chillcoding.ilove.view.GameView
import kotlinx.android.synthetic.main.dialog_end_game.view.*
import org.jetbrains.anko.share
import org.jetbrains.anko.startActivity
import java.util.*

/**
 * Created by macha on 01/08/2017.
 */
class EndGameDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the Builder class for convenient dialog construction
        var bestScore: Int by DelegatesExt.preference(activity, App.PREF_BEST_SCORE, 0)
        var awardLevel: Int by DelegatesExt.preference(activity, App.PREF_AWARD_LEVEL, 0)
        var isPremium: Boolean by DelegatesExt.preference(activity, App.PREF_PREMIUM, false)


        val builder = AlertDialog.Builder(activity)
        val successString = resources.getStringArray(R.array.word_success)
        var endGameView = (LayoutInflater.from(activity)).inflate(R.layout.dialog_end_game, null)
        var data = arguments.getParcelable<GameData>(App.BUNDLE_GAME_DATA)
        endGameView.dialogTitleTextView.text = "${successString[Random().nextInt(successString.size)].toUpperCase()}!"
        endGameView.dialogScoreTextView.text = "${data.score}"
        endGameView.dialogLevelTextView.text = "${resources.getString(R.string.word_level)} ${data.level}"

        when {
            data.score < GameView.TAPS_PER_LEVEL -> {
                endGameView.dialogTitleTextView.text = getString(R.string.failure).toUpperCase()
                endGameView.dialogLevelTextView.text = getString(R.string.you_can_better_text)
                endGameView.starImageView.setColorFilter(App.sColors[1])
                endGameView.middleStarImageView.setColorFilter(App.sColors[1])
                endGameView.endStarImageView.setColorFilter(App.sColors[1])
            }
            data.score > bestScore -> {
                bestScore = data.score
                endGameView.dialogBestTextView.visibility = View.VISIBLE
                endGameView.starImageView.setColorFilter(App.sColors[7])
                endGameView.middleStarImageView.setColorFilter(App.sColors[7])
                endGameView.endStarImageView.setColorFilter(App.sColors[7])
                builder.setNegativeButton(R.string.action_see_top, { _, _ -> startActivity<SecondActivity>(SecondActivity.FRAGMENT_ID to FragmentId.TOP.ordinal) })
            }
        }
        var activity = (activity as MainActivity)
        activity.setUpNewGame()
        builder.setView(endGameView)
                .setPositiveButton(R.string.action_play, { _, _ -> activity.playGame(true) })
                .setNeutralButton(R.string.action_share, { _, _ -> share("${getString(R.string.text_share_score)} ${data.score} <3 ${getString(R.string.word_with)} ${getString(R.string.app_name)}!", "I Love") })

        return builder.create()
    }
}
