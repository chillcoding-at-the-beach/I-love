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
import com.chillcoding.mycuteheart.MyMainActivity
import com.chillcoding.mycuteheart.model.MyGameData

/**
 * Created by macha on 17/07/2017.
 */
class MyGameView : View, View.OnTouchListener {

    var isPlaying = false
    var myGameData = MyGameData()

    private lateinit var mHeart: MyCuteHeart
    private lateinit var mLife: MyCuteHeart

    private var mSoundPlayer = MediaPlayer.create(context, R.raw.latina)
    private var mSoundHeartPlayer = MediaPlayer.create(context, R.raw.heart)
    private var mVibrator = context.getSystemService(Activity.VIBRATOR_SERVICE) as Vibrator

    private var mTextPaint = Paint()

    private var mTopMargin = floatArrayOf(100f, 10f)
    private var mRighMargin = floatArrayOf(0.06f, 0.4f, 0.10f, 0.8f)

    private var mActivity: MyMainActivity = context as MyMainActivity

    companion object {
        private val POINTS = 3
        private val TAPS_PER_LEVEL = 20
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        super.setOnTouchListener(this)
        mSoundPlayer.isLooping = true
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            var coef: Int
            if (width > height)
                coef = height
            else
                coef = width
            mLife = MyCuteHeart()
            mHeart = MyCuteHeart(width, height, mTopMargin.last().toInt())
            mTextPaint.textSize = (coef / 20).toFloat()
            mLife.paint.strokeWidth = (coef / 150).toFloat()
            mTopMargin[0] = (coef / 70).toFloat()
            mTopMargin[1] = (coef / 17).toFloat()
            for (i in mRighMargin.indices)
                mRighMargin[i] = coef * mRighMargin[i]
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //draw the score and level
        with(myGameData) {
            canvas?.drawText(" ${context.getString(R.string.score)} $score",
                    mRighMargin[0], mTopMargin.last(), mTextPaint)
            canvas?.save()
            mLife.paint.style = Paint.Style.FILL
            if (nbLife < 1) {
                mLife.paint.style = Paint.Style.STROKE
                end()
            }
            canvas?.translate(mRighMargin[1], mTopMargin.first())
            canvas?.drawPath(mLife.path, mLife.paint)
            for (i in 2..3) {
                if (nbLife < i)
                    mLife.paint.style = Paint.Style.STROKE
                canvas?.translate(mRighMargin[2], 0f)
                canvas?.drawPath(mLife.path, mLife.paint)
            }
            canvas?.restore()
            canvas?.drawText("$level ${context.getString(R.string.level)} ",
                    mRighMargin.last(), mTopMargin.last(), mTextPaint)
        }

        if (isPlaying) {
            invalidate()
            mHeart.update()
            if (!mSoundPlayer.isPlaying)
                mSoundPlayer.start()
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
                    if (myGameData.score == tapsForNextLevel() * POINTS)
                        levelUp()
                } else
                    mActivity.playGame(true)
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
        myGameData.score += POINTS
        mHeart.updateRandomly()
    }

    private fun lost() {
        mVibrator.vibrate(100)
        myGameData.nbLife--
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stop()
    }

    private fun tapsForNextLevel(): Int {
        return (myGameData.level) * TAPS_PER_LEVEL
    }

    private fun levelUp() {
        myGameData.level += 1
        Toast.makeText(context, "+ 1 ${context.getString(R.string.level)}!", Toast.LENGTH_SHORT).show()
        mHeart.update(myGameData.level)
    }

    fun play() {
        mSoundPlayer.start()
        isPlaying = true
        invalidate()
    }

    fun pause() {
        mSoundPlayer.pause()
        isPlaying = false
    }

    fun stop() {
        // stop and prepare the sound
        mSoundPlayer.stop()
        mSoundPlayer.reset()
        mSoundPlayer = MediaPlayer.create(context, R.raw.latina)
        mSoundPlayer.isLooping = true
        // stop the animation
        isPlaying = false
    }

    private fun end() {
        mActivity.endGame()
        myGameData = MyGameData()
        mHeart.update(1)
        invalidate()
    }
}
