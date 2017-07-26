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
import android.support.v4.content.res.ResourcesCompat
import android.widget.Toast
import com.chillcoding.mycuteheart.model.MyGameData


/**
 * Created by macha on 17/07/2017.
 */
class MyGameView : View, View.OnTouchListener {

    lateinit var mHeart: MyCuteHeart

    private var mSoundPlayer = MediaPlayer.create(context, R.raw.latina)
    private var mSoundHeartPlayer = MediaPlayer.create(context, R.raw.heart)
    private var mVibrator = context.getSystemService(Activity.VIBRATOR_SERVICE) as Vibrator

    var mIsPlaying = false

    private var mTextPaint = Paint()

    var mData = MyGameData()

    companion object {
        private val M_POINTS = 3
        private val M_TAPS_PER_LEVEL = 25
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        super.setOnTouchListener(this)
        mSoundPlayer.isLooping = true
        mTextPaint.textSize = 50F
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mHeart = MyCuteHeart(width, height)
        mHeart.mPaint.color = ResourcesCompat.getColor(resources, R.color.colorRed, null);
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //draw the main heart
        canvas?.drawPath(mHeart.mHeartPath, mHeart.mPaint)
        //draw the score and level
        with(mData) {
            canvas?.drawText(" ${context.getString(R.string.score)} ${mScore}",
                    (8 * width / 10).toFloat(), 50f, mTextPaint)
            canvas?.drawText(" ${context.getString(R.string.level)} ${mLevel}",
                    (8 * width / 10).toFloat(), 100f, mTextPaint)
        }

        if (mIsPlaying) {
            invalidate()
            mHeart.updateCoordinates(mData.mSpeed)
            if (!mSoundPlayer.isPlaying)
                mSoundPlayer.start()
        }
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
                    mData.mScore += M_POINTS
                    mHeart.moveRandomly()

                    if (mData.mScore == tapsForNextLevel() * M_POINTS)
                        levelUp()
                }
            }
        }
        return false
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stop()
        prepare()
    }

    private fun tapsForNextLevel(): Int {
        return (mData.mLevel + 1) * M_TAPS_PER_LEVEL
    }

    private fun levelUp() {
        mData.mLevel += 1
        if (mData.mLevel < 5)
            mData.mSpeed = 2 * mData.mSpeed
        Toast.makeText(context, "+ 1 ${context.getString(R.string.levelup)}", Toast.LENGTH_SHORT).show()
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
        // stop the sound
        mSoundPlayer.stop()
        mSoundPlayer.reset()
        // stop the animation
        mIsPlaying = false
    }

    fun prepare() {
        mSoundPlayer = MediaPlayer.create(context, R.raw.latina)
        mSoundPlayer.isLooping = true
    }
}