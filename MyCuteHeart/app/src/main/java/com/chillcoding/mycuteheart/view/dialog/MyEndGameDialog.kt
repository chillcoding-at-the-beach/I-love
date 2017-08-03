package com.chillcoding.mycuteheart.view.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import com.chillcoding.mycuteheart.MyApp
import com.chillcoding.mycuteheart.R
import com.chillcoding.mycuteheart.model.MyGameData
import kotlinx.android.synthetic.main.dialog_my_end_game.view.*
import java.util.*

/**
 * Created by macha on 01/08/2017.
 */
class MyEndGameDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the Builder class for convenient dialog construction
        val builder = AlertDialog.Builder(activity)
        val successString = resources.getStringArray(R.array.sucess_game)
        var endGameView = (LayoutInflater.from(activity)).inflate(R.layout.dialog_my_end_game, null)
        var data = arguments.getParcelable<MyGameData>(MyApp.GAME_DATA)
        endGameView.dialogScoreTextView.text = "${data.score}"
        endGameView.dialogLevelTextView.text = "${resources.getString(R.string.level)} ${data.level}"
        endGameView.dialogTitleTextView.text = "${successString[Random().nextInt(successString.size)].toUpperCase()}!"
        builder.setView(endGameView)
                .setPositiveButton(R.string.play, null)
                .setNeutralButton(R.string.share, null)
                .setNegativeButton(android.R.string.cancel, null)
        return builder.create()
    }
}
