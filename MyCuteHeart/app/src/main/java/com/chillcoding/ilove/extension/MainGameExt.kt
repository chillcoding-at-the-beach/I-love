package com.chillcoding.ilove.extension

import android.media.MediaPlayer
import android.os.Bundle
import com.chillcoding.ilove.App
import com.chillcoding.ilove.MainActivity
import com.chillcoding.ilove.R
import com.chillcoding.ilove.view.dialog.AwardDialog
import com.chillcoding.ilove.view.dialog.EndGameDialog
import com.chillcoding.ilove.view.dialog.LevelDialog
import com.chillcoding.ilove.view.dialog.QuoteDialog
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.contentView

fun MainActivity.showLevelDialog() {
    val bundle = Bundle()
    bundle.putInt(App.BUNDLE_GAME_LEVEL, gameView.gameData.level)
    val popup = LevelDialog()
    popup.arguments = bundle
    popup.show(fragmentManager, MainActivity::class.java.simpleName)
}

fun MainActivity.showAlertOnLove() {
    QuoteDialog().show(fragmentManager, MainActivity::class.java.simpleName)
}

fun MainActivity.showAwardDialog() {
    AwardDialog().show(fragmentManager, MainActivity::class.java.simpleName)
}

fun MainActivity.setUpGame() {
    setUpSound()
    updateScore()
    updateLevel()
}

fun MainActivity.pauseGame(animateFab: Boolean = false) {
    if (animateFab)
        fab.playAnimation(contentView!!)
    gameView.pause()
    mSoundPlayer.pause()
}

fun MainActivity.playGame(animateFab: Boolean = false) {
    if (animateFab)
        fab.playAnimation(contentView!!)
    if (isSound)
        mSoundPlayer.start()
    gameView.play()
}

fun MainActivity.endGame() {
    resetSound()
    fab.playAnimation(contentView!!)
    val bundle = Bundle()
    bundle.putParcelable(App.BUNDLE_GAME_DATA, gameView.gameData)
    val popup = EndGameDialog()
    popup.arguments = bundle
    popup.show(fragmentManager, MainActivity::class.java.simpleName)
}

private fun MainActivity.setUpSound() {
    mSoundPlayer = MediaPlayer.create(this, R.raw.latina)
    mSoundPlayer.isLooping = true
    mSoundPlayer.setVolume(0.6F, 0.6F)
}

fun MainActivity.updateScore() {
    when (gameView.gameData.score) {
        in 0..9 -> mainScorePoints.text = "00${gameView.gameData.score}"
        in 10..99 -> mainScorePoints.text = "0${gameView.gameData.score}"
        else -> mainScorePoints.text = "${gameView.gameData.score}"
    }
}

fun MainActivity.updateLevel() {
    mainLevel.text = "${gameView.gameData.level}"
}

fun MainActivity.updateGauge() {
    val temp = gameView.gameData.score * 600 / gameView.scoreForNextAward()
    if (temp < 3)
        mainGaugeAward.value = 200 + 3
    else
        mainGaugeAward.value = 200 + temp
    mainGaugeLevel.value = 200 + (gameView.gameData.score - gameView.scoreForLevel(gameView.gameData.level - 1)) * 600 / (gameView.scoreForLevel(gameView.gameData.level) - gameView.scoreForLevel(gameView.gameData.level - 1))
}

fun MainActivity.updateNbLife() {
    when (gameView.gameData.nbLife) {
        0 -> {
            mainFirstLife.setImageResource(R.drawable.ic_life_lost)
            endGame()
        }
        1 -> {
            mainSecondLife.setImageResource(R.drawable.ic_life_lost)
            mainThirdLife.setImageResource(R.drawable.ic_life_lost)
        }
        2 -> mainThirdLife.setImageResource(R.drawable.ic_life_lost)
        3 -> {
            mainFirstLife.setImageResource(R.drawable.ic_life)
            mainSecondLife.setImageResource(R.drawable.ic_life)
            mainThirdLife.setImageResource(R.drawable.ic_life)
        }
    }
}

fun MainActivity.updateGameInfo() {
    updateScore()
    updateNbLife()
    updateLevel()
    updateGauge()
}

fun MainActivity.resetSound() {
    mSoundPlayer.reset()
    setUpSound()
}

fun MainActivity.setUpNewGame() {
    gameView.setUpNewGame()
    updateGameInfo()
}