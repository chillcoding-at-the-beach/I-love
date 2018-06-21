package com.chillcoding.ilove.view

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.media.MediaPlayer
import android.os.Vibrator
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.chillcoding.ilove.App
import com.chillcoding.ilove.MainActivity
import com.chillcoding.ilove.R
import com.chillcoding.ilove.extension.*
import com.chillcoding.ilove.model.GameData

/**
 * Created by macha on 17/07/2017.
 */
class GameView : View, View.OnTouchListener {

    var isPlaying = false
    var gameData = GameData()

    private var iLove = ILoveHeart()

    private var mSoundHeartPlayer = MediaPlayer.create(context, R.raw.heart)
    private var mVibrator = context.getSystemService(Activity.VIBRATOR_SERVICE) as Vibrator

    private var mTextPaint = Paint()

    private var mTopMargin = floatArrayOf(100f, 10f)

    private var mActivity: MainActivity = context as MainActivity

    companion object {
        private val POINTS = 1
        val TAPS_PER_LEVEL = 7
        private val D_SHADOW = 15F
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        super.setOnTouchListener(this)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            var coef: Int
            if (width > height)
                coef = height
            else
                coef = width
            iLove.widthScreen = width
            iLove.heightScreen = height
            iLove.marginTopZone = mTopMargin.last().toInt()
            iLove.level = gameData.level

            mTextPaint.textSize = (coef / 20).toFloat()
            mTopMargin[0] = (coef / 70).toFloat()
            mTopMargin[1] = (coef / 17).toFloat()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (isPlaying) {
            invalidate()
            iLove.updateTrajectory()
        }
        //draw the main heart
        canvas?.save()
        canvas?.translate(iLove.iLoveX, iLove.iLoveY)
        //draw the shadow
        canvas?.translate(D_SHADOW, D_SHADOW)
        canvas?.drawPath(iLove.path, iLove.paintShadow)
        canvas?.translate(-D_SHADOW, -D_SHADOW)
        //draw the heart
        canvas?.drawPath(iLove.path, iLove.paint)
        canvas?.restore()
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event != null) {
            if (iLove.isIn(event.x.toInt(), event.y.toInt())) {
                if (isPlaying) {
                    win()
                    checkScore()
                } else {
                    mActivity.playGame(true)
                }
            } else {
                if (isPlaying)
                    lost()
            }
        }
        return false
    }

    private fun win() {
        gameData.score += POINTS * gameData.level
        iLove.doMagic()
        mActivity.updateScore()
        mActivity.updateGauge()
    }

    private fun checkScore() {
        if (gameData.score > scoreForNextLevel())
            levelUp()
        if (gameData.score > scoreForNextAward())
            awardUp()
    }

    private fun scoreForNextLevel() = scoreForLevel(gameData.level)

    fun scoreForNextAward() = scoreForAward(gameData.awardLevel + 1)

    private fun scoreForAward(awardLevel: Int): Int {
        var score = 0
        if (awardLevel == 0)
            score = TAPS_PER_LEVEL + TAPS_PER_LEVEL / 2
        else
            score = 2 * (awardLevel - 1) * App.SCORE_PER_AWARD + App.SCORE_PER_AWARD
        return score
    }

    fun scoreForLevel(level: Int): Int {
        var score = 0
        for (k in 1..level)
            score += TAPS_PER_LEVEL * (level - 1) + TAPS_PER_LEVEL
        return score
    }

    private fun levelUp() {
        gameData.level += 1
        mActivity.showLevelDialog()
        iLove.level = gameData.level
        mActivity.updateLevel()
        mSoundHeartPlayer.start()
        mActivity.updateGauge()
    }

    private fun awardUp() {
        if (gameData.awardLevel < App.AWARD_LIST_SIZE) {
            gameData.awardLevel += 1
            gameData.awardUnlocked = true
            mActivity.showAwardDialog()
            mActivity.updateGauge()
        }
    }

    fun play() {
        isPlaying = true
        invalidate()
    }

    fun pause() {
        isPlaying = false
    }

    fun stop() {
        // stop the animation
        isPlaying = false
    }

    private fun lost() {
        mVibrator.vibrate(100)
        gameData.nbLife--
        mActivity.updateNbLife()
    }

    fun setUpNewGame() {
        stop()
        gameData.reInit()
        iLove.level = 1
        invalidate()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stop()
    }
}

