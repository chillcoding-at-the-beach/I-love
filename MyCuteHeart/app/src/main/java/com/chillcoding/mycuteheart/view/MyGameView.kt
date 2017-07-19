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


/**
 * Created by macha on 17/07/2017.
 */
class MyGameView : View, View.OnTouchListener {

    lateinit var mHeart: MyCuteHeart
    var mIsPlaying = false

    private var mSoundPlayer = MediaPlayer.create(context, R.raw.latina)
    private var mSoundHeartPlayer = MediaPlayer.create(context, R.raw.heart)
    private var mVibrator = context.getSystemService(Activity.VIBRATOR_SERVICE) as Vibrator

    private val M_POINTS = 3
    private val M_TAPS_PER_LEVEL = 25
    private var mScore = 0
    private var mLevel = 0

    private var mTextPaint = Paint()

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }


    private fun init() {
        super.setOnTouchListener(this)
        mSoundPlayer.isLooping = true
        mSoundPlayer.setVolume(0.3F, 0.3F)
        mTextPaint.textSize = 50F
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        var size = 0
        if (height < width)
            size = height / 7
        else
            size = width / 7
        mHeart = MyCuteHeart(size)
        mHeart.mXZone = width
        mHeart.mYZone = height
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //draw the main heart
        canvas?.drawPath(mHeart.mHeartPath, mHeart.mPaint)
        //draw the score
        canvas?.drawText(" ${context.getString(R.string.score)} $mScore",
                (8 * width / 10).toFloat(), 50f, mTextPaint)
        //draw the level
        canvas?.drawText(" ${context.getString(R.string.level)} $mLevel",
                (8 * width / 10).toFloat(), 100f, mTextPaint)

        if (mIsPlaying) {
            invalidate()
            mHeart.onUpdate()
            if (!mSoundPlayer.isPlaying)
                mSoundPlayer.start()
        }
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
        // stop the sound and prepare for future
        mSoundPlayer.stop()
        mSoundPlayer.reset()
        mSoundPlayer = MediaPlayer.create(context, R.raw.latina)
        mSoundPlayer.isLooping = true
        // stop the animation
        mIsPlaying = false
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event != null) {
            if (mHeart.isIn(event.x.toInt(), event.y.toInt())) {
                mVibrator.vibrate(50)
                if (mSoundHeartPlayer.isPlaying) {
                    mSoundHeartPlayer.stop()
                    mSoundHeartPlayer.reset()
                    mSoundHeartPlayer = MediaPlayer.create(context, R.raw.heart)
                }
                mSoundHeartPlayer.start()

                if (mIsPlaying) {
                    mScore += M_POINTS
                    mHeart.moveRandomly()


                    if (mScore == tapsForNextLevel() * M_POINTS)
                        levelUp()
                }
            }
        }
        return false
    }

    private fun tapsForNextLevel(): Int {
        return (mLevel + 1) * M_TAPS_PER_LEVEL
    }

    private fun levelUp() {
        mLevel += 1
        mHeart.onSpeedUp()
        Toast.makeText(context, "+ 1 ${context.getString(R.string.levelup)}", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private val TAG = "My GAME VIEW"
    }

}