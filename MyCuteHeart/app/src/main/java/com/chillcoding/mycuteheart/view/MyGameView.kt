package com.chillcoding.mycuteheart.view

import android.content.Context
import android.graphics.Canvas
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.View
import com.chillcoding.mycuteheart.R

/**
 * Created by macha on 17/07/2017.
 */
class MyGameView : View {

    lateinit var mHeart: MyCuteHeart
    var isPlaying = false
    var mSoundPlayer = MediaPlayer.create(context, R.raw.latina)

    constructor(context: Context?) : super(context) {
        init()
    }

    private fun init() {
        mSoundPlayer.isLooping = true
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
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
        canvas?.drawPath(mHeart.mHeartPath, mHeart.mPaint)
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
}