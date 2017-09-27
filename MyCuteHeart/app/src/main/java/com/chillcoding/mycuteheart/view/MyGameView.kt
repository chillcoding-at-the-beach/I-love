package com.chillcoding.mycuteheart.view

import android.content.Context
import android.graphics.Canvas
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.chillcoding.mycuteheart.R
import android.app.Activity
import android.graphics.Paint
import android.os.Vibrator
import android.widget.Toast
import com.chillcoding.mycuteheart.MyApp
import com.chillcoding.mycuteheart.MyMainActivity
import com.chillcoding.mycuteheart.extension.DelegatesExt
import com.chillcoding.mycuteheart.model.MyGameData

/**
 * Created by macha on 17/07/2017.
 */
class MyGameView : View, View.OnTouchListener {

    var isPlaying = false
    var myGameData = MyGameData()

    private lateinit var mHeart: MyCuteHeart

    private var mSoundHeartPlayer = MediaPlayer.create(context, R.raw.heart)
    private var mVibrator = context.getSystemService(Activity.VIBRATOR_SERVICE) as Vibrator

    private var mTextPaint = Paint()

    private var mTopMargin = floatArrayOf(100f, 10f)

    private var mActivity: MyMainActivity = context as MyMainActivity
    var awardLevel: Int by DelegatesExt.preference(context, MyApp.PREF_AWARD_LEVEL, 1)


    companion object {
        private val POINTS = 1
        private val TAPS_PER_LEVEL = 10
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        super.setOnTouchListener(this)
        myGameData.awardLevel = awardLevel
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            var coef: Int
            if (width > height)
                coef = height
            else
                coef = width
            mHeart = MyCuteHeart(width, height, mTopMargin.last().toInt(), myGameData.level)
            mTextPaint.textSize = (coef / 20).toFloat()
            mTopMargin[0] = (coef / 70).toFloat()
            mTopMargin[1] = (coef / 17).toFloat()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (isPlaying) {
            invalidate()
            mHeart.updateTrajectory()
        }
        //draw the main heart
        canvas?.save()
        canvas?.translate(mHeart.wakaX, mHeart.wakaY)
        canvas?.drawPath(mHeart.path, mHeart.paint)
        canvas?.restore()
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event != null) {
            if (mHeart.isIn(event.x.toInt(), event.y.toInt())) {
                if (isPlaying) {
                    win()
                    if (myGameData.score > scoreForNextLevel())
                        levelUp()
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

    private fun playHeartSound() {
        if (mSoundHeartPlayer.isPlaying) {
            mSoundHeartPlayer.stop()
            mSoundHeartPlayer.reset()
            mSoundHeartPlayer = MediaPlayer.create(context, R.raw.heart)
        }
        mSoundHeartPlayer.start()
    }

    private fun win() {
        myGameData.score += POINTS * myGameData.level * myGameData.awardLevel
        mHeart.updateRandomly()
        mActivity.updateScore()
    }

    private fun lost() {
        mVibrator.vibrate(100)
        myGameData.nbLife--
        mActivity.updateNbLife()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stop()
    }

    private fun scoreForNextLevel(): Int {
        var score = 0
        for (k in 1..myGameData.level)
            score += TAPS_PER_LEVEL * k * k
        return score
    }

    private fun levelUp() {
        myGameData.level += 1
        Toast.makeText(context, "+ 1 ${context.getString(R.string.word_level)}!", Toast.LENGTH_SHORT).show()
        mHeart.updateToLevel(myGameData.level)
        mActivity.updateLevel()
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

    fun setUpNewGame() {
        stop()
        myGameData = MyGameData()
        mHeart.updateToLevel(1)
        invalidate()
        myGameData.awardLevel = awardLevel
    }
}
