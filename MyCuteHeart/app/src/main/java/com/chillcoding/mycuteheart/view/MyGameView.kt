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
import java.util.*


/**
 * Created by macha on 17/07/2017.
 */
class MyGameView : View, View.OnTouchListener {

    lateinit var mHeart: MyCuteHeart
    var isPlaying = false

    private var mSoundPlayer = MediaPlayer.create(context, R.raw.latina)
    private var mSoundHeartPlayer = MediaPlayer.create(context, R.raw.heart)
    private lateinit var mVibrator: Vibrator

    private val mPoint = 3
    private var mScore = 0
    private var mLevel = 0

    private var mStatePaint = Paint()

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
        mVibrator = context.getSystemService(Activity.VIBRATOR_SERVICE) as Vibrator
        mStatePaint.textSize = 50F
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
                (8 * width / 10).toFloat(), 50f, mStatePaint)
        //draw the level
        canvas?.drawText(" ${context.getString(R.string.level)} $mLevel",
                (8 * width / 10).toFloat(), 100f, mStatePaint)

        if (isPlaying) {
            invalidate()
            mHeart.onUpdate()
            if (!mSoundPlayer.isPlaying)
                mSoundPlayer.start()
        }
    }

    fun onPlay() {
        mSoundPlayer.start()
        isPlaying = true
        invalidate()
    }

    fun onPause() {
        mSoundPlayer.pause()
        isPlaying = false
    }

    fun onStop() {
        // stop the sound and prepare for future
        mSoundPlayer.stop()
        mSoundPlayer.reset()
        mSoundPlayer = MediaPlayer.create(context, R.raw.latina)
        mSoundPlayer.isLooping = true
        // stop the animation
        isPlaying = false
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

                if (isPlaying) {
                    mScore += mPoint
                    mHeart.onReplace()

                    when (mScore) {
                        33 * mPoint -> onLevelUp()
                        66 * mPoint -> onLevelUp()
                        100 * mPoint -> onLevelUp()
                        133 * mPoint -> onLevelUp()
                        166 * mPoint -> onLevelUp()
                    }
                }
            }
        }
        return false
    }

    private fun onLevelUp() {
        mLevel += 1
        mHeart.onSpeedUp()
        Toast.makeText(context, "+ 1 ${context.getString(R.string.levelup)}", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private val TAG = "My GAME VIEW"
    }

}