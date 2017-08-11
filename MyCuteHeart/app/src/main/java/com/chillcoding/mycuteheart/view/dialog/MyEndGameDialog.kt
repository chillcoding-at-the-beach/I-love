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
import kotlinx.android.synthetic.main.dialog_end_game.view.*
import java.util.*

/**
 * Created by macha on 01/08/2017.
 */
class MyEndGameDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the Builder class for convenient dialog construction
        var bestScore: Int by DelegatesExt.preference(activity, MyApp.PREF_BEST_SCORE, 0)
        val builder = AlertDialog.Builder(activity)
        val successString = resources.getStringArray(R.array.sucess_game)
        var endGameView = (LayoutInflater.from(activity)).inflate(R.layout.dialog_end_game, null)
        var data = arguments.getParcelable<MyGameData>(MyApp.BUNDLE_GAME_DATA)
        endGameView.dialogTitleTextView.text = "${successString[Random().nextInt(successString.size)].toUpperCase()}!"
        endGameView.dialogScoreTextView.text = "${data.score}"
        endGameView.dialogLevelTextView.text = "${resources.getString(R.string.level)} ${data.level}"
        if (data.score > bestScore) {
            bestScore = data.score
            endGameView.dialogBestTextView.visibility = View.VISIBLE
        }
        builder.setView(endGameView)
                .setPositiveButton(R.string.play, { _, _ -> (activity as MyMainActivity).playGame() })
                .setNeutralButton(R.string.share, null)
                .setNegativeButton(android.R.string.cancel, null)
        return builder.create()
    }
}
