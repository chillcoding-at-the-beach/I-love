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
import java.util.*


/**
 * Created by macha on 17/07/2017.
 */
class MyGameView : View, View.OnTouchListener {

    lateinit var mHeart: MyCuteHeart
    lateinit var mLife: MyCuteHeart
    
    private var mSoundPlayer = MediaPlayer.create(context, R.raw.latina)
    private var mSoundHeartPlayer = MediaPlayer.create(context, R.raw.heart)
    private var mVibrator = context.getSystemService(Activity.VIBRATOR_SERVICE) as Vibrator

    val mColors = listOf(
            ResourcesCompat.getColor(resources, R.color.colorPrimaryLight, null),
            ResourcesCompat.getColor(resources, R.color.colorAccentLight, null),
            ResourcesCompat.getColor(resources, R.color.colorAccent, null),
            ResourcesCompat.getColor(resources, R.color.colorPrimary, null),
            ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, null),
            ResourcesCompat.getColor(resources, R.color.colorAccentDark, null))

    var mIsPlaying = false

    private var mTextPaint = Paint()

    var mData = MyGameData()

    var mTopMargin = floatArrayOf(100f, 10f)
    var mRighMargin = floatArrayOf(0.06f, 0.33f, 0.13f, 0.8f)


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
            mTopMargin[0] = (coef / 70).toFloat()
            mTextPaint.textSize = (coef / 20).toFloat()
            mTopMargin[1] = (coef / 14).toFloat()
            mLife.mPaint.strokeWidth = (coef / 150).toFloat()
            for (i in mRighMargin.indices)
                mRighMargin[i] = coef * mRighMargin[i]

            createMyCuteHeart()
            mHeart.mPaint.color = mColors.last();
            mLife.mPaint.color = mColors.last();
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
            canvas?.translate(mRighMargin[1], mTopMargin.first())
            canvas?.drawPath(mLife.mHeartPath, mLife.mPaint)
            if (mNbLife < 2)
                mLife.mPaint.style = Paint.Style.STROKE
            canvas?.translate(mRighMargin[2], 0f)
            canvas?.drawPath(mLife.mHeartPath, mLife.mPaint)
            if (mNbLife < 3)
                mLife.mPaint.style = Paint.Style.STROKE
            canvas?.translate(mRighMargin[2], 0f)
            canvas?.drawPath(mLife.mHeartPath, mLife.mPaint)
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
                mVibrator.vibrate(50)

                if (mIsPlaying) {
                    mData.mScore += M_POINTS
                    if (mData.mScore == tapsForNextLevel() * M_POINTS)
                        levelUp()

                    mHeart.updateRandomly()
                    changeHeartColorRandomly()
                } else {
                    if (mSoundHeartPlayer.isPlaying) {
                        mSoundHeartPlayer.stop()
                        mSoundHeartPlayer.reset()
                        mSoundHeartPlayer = MediaPlayer.create(context, R.raw.heart)
                    }
                    mSoundHeartPlayer.start()
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
        return (mData.mLevel) * M_TAPS_PER_LEVEL
    }

    private fun levelUp() {
        mData.mLevel += 1
        createMyCuteHeart()
        updateSpeed()
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

    private fun changeHeartColorRandomly() {
        val random = Random()
        when (mData.mLevel) {
            1 -> mHeart.mPaint.color = mColors[random.nextInt(mColors.size)]
            2 -> mHeart.mPaint.color = mColors[random.nextInt(mColors.size - 2)]
            in 5..10 -> mHeart.mPaint.color = mColors.first()
            else -> mHeart.mPaint.color = mColors[random.nextInt(mColors.size - 4)]
        }
    }

    fun updateSpeed() {
        if (mData.mLevel < 5)
            mHeart.mSpeed = Math.pow(2.0, (mData.mLevel - 1).toDouble()).toInt()
        else
            mHeart.mSpeed = 8
    }

    fun createMyCuteHeart() {
        when (mData.mLevel) {
            1 -> mHeart = MyCuteHeart(width, height, mTopMargin.last().toInt(), 3)
            2 -> mHeart = MyCuteHeart(width, height, mTopMargin.last().toInt(), 2)
            else -> mHeart = MyCuteHeart(width, height, mTopMargin.last().toInt(), 1)
        }
    }
}