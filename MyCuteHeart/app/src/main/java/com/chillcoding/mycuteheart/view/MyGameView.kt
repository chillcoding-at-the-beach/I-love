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

    lateinit var mHeart: MyCuteHeart
    lateinit var mLife: MyCuteHeart

    private var mSoundPlayer = MediaPlayer.create(context, R.raw.latina)
    private var mSoundHeartPlayer = MediaPlayer.create(context, R.raw.heart)
    private var mVibrator = context.getSystemService(Activity.VIBRATOR_SERVICE) as Vibrator


    var mIsPlaying = false

    private var mTextPaint = Paint()

    var mData = MyGameData()

    var mTopMargin = floatArrayOf(100f, 10f)
    var mRighMargin = floatArrayOf(0.06f, 0.4f, 0.10f, 0.8f)


    companion object {
        private val M_POINTS = 3
        private val M_TAPS_PER_LEVEL = 20
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
            mLife.mPaint.strokeWidth = (coef / 150).toFloat()
            mTopMargin[0] = (coef / 70).toFloat()
            mTopMargin[1] = (coef / 17).toFloat()
            for (i in mRighMargin.indices)
                mRighMargin[i] = coef * mRighMargin[i]

        }

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //draw the score and level
        with(mData) {
            canvas?.drawText(" ${context.getString(R.string.score)} $mScore",
                    mRighMargin[0], mTopMargin.last(), mTextPaint)
            canvas?.save()
            mLife.mPaint.style = Paint.Style.FILL
            if (mNbLife < 1) {
                mLife.mPaint.style = Paint.Style.STROKE
                end()
            }
            canvas?.translate(mRighMargin[1], mTopMargin.first())
            canvas?.drawPath(mLife.mHeartPath, mLife.mPaint)
            for (i in 2..3) {
                if (mNbLife < i)
                    mLife.mPaint.style = Paint.Style.STROKE
                canvas?.translate(mRighMargin[2], 0f)
                canvas?.drawPath(mLife.mHeartPath, mLife.mPaint)
            }
            canvas?.restore()
            canvas?.drawText("$mLevel ${context.getString(R.string.level)} ",
                    mRighMargin.last(), mTopMargin.last(), mTextPaint)
        }

        if (mIsPlaying) {
            invalidate()
            mHeart.update()
            if (!mSoundPlayer.isPlaying)
                mSoundPlayer.start()
        }
        //draw the main heart
        canvas?.save()
        canvas?.translate(mHeart.mWakaX, mHeart.mWakaY)
        canvas?.drawPath(mHeart.mHeartPath, mHeart.mPaint)
        canvas?.restore()
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event != null) {
            if (mHeart.isIn(event.x.toInt(), event.y.toInt())) {
                if (mIsPlaying) {
                    win()
                    if (mData.mScore == tapsForNextLevel() * M_POINTS)
                        levelUp()
                } else
                    playHeartSound()
            } else {
                if (mIsPlaying)
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
        mData.mScore += M_POINTS
        mHeart.updateRandomly()
    }

    private fun lost() {
        mVibrator.vibrate(100)
        mData.mNbLife--
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stop()
    }

    private fun tapsForNextLevel(): Int {
        return (mData.mLevel) * M_TAPS_PER_LEVEL
    }

    private fun levelUp() {
        mData.mLevel += 1
        Toast.makeText(context, "+ 1 ${context.getString(R.string.level)}!", Toast.LENGTH_SHORT).show()
        mHeart.update(mData.mLevel)
    }

    fun play() {
        mSoundPlayer.start()
        mIsPlaying = true
        invalidate()
    }

    fun pause() {
        mSoundPlayer.pause()
        mIsPlaying = false
    }

    fun stop() {
        // stop and prepare the sound
        mSoundPlayer.stop()
        mSoundPlayer.reset()
        mSoundPlayer = MediaPlayer.create(context, R.raw.latina)
        mSoundPlayer.isLooping = true
        // stop the animation
        mIsPlaying = false

    }

    private fun end() {
        var act: MyMainActivity = context as MyMainActivity
        act.endGame()
        mData = MyGameData()
        mHeart.update(1)
        invalidate()

    }
}
