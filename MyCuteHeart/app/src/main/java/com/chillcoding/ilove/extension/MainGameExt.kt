package com.chillcoding.ilove.extension

import android.os.Bundle
import com.chillcoding.ilove.App
import com.chillcoding.ilove.MainActivity
import com.chillcoding.ilove.view.dialog.LevelDialog
import kotlinx.android.synthetic.main.content_main.*

fun MainActivity.showLevelDialog() {
    var bundle = Bundle()
    bundle.putParcelable(App.BUNDLE_GAME_DATA, gameView.gameData)
    var popup = LevelDialog()
    popup.arguments = bundle
    popup.show(fragmentManager, MainActivity::class.java.simpleName)
}