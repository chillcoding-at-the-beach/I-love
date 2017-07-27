package com.chillcoding.mycuteheart.view

import android.graphics.Paint
import android.graphics.Path
import java.util.*

/**
 * Created by macha on 17/07/2017.
 */
class MyCuteHeart {

    var mSize = 300
    var mSpeed = 1
    var mPaint = Paint()
    var mHeartPath = Path()

    var mWakaX = 0f
    var mWakaY = 0f


    private var mX: FloatArray = floatArrayOf(75f, 60f, 40f, 5f, 40f, 110f, 145f, 110f, 90f)
    private var mY: FloatArray = floatArrayOf(30f, 25f, 5f, 40f, 80f, 102f, 130f)

    private var mDirectionToRight = true
    private var mDirectionToDown = true

    private var mXZone = 0
    private var mYZone = intArrayOf(0, 0)

    constructor(width: Int, height: Int, marginTop: Int, size: Int = 3) {
        if (height > width)
            mSize = (height * size) / 1000
        else
            mSize = (width * size) / 1000
        initializeHeartCoordinates()
        mXZone = width - mX[6].toInt()
        mYZone = intArrayOf(height - marginTop - mY[6].toInt(), marginTop)
        init()
    }

    private fun init() {
        mPaint = Paint()
        mPaint.style = Paint.Style.FILL
        updatePositionRandomly()
        createHeart()
    }

    private fun initializeHeartCoordinates() {
        for (i in mX.indices)
            mX[i] = mX[i] * mSize
        for (i in mY.indices)
            mY[i] = mY[i] * mSize
    }

    private fun createHeart() {
        mHeartPath = Path()
        mHeartPath.moveTo(mX[0], mY[0])
        mHeartPath.cubicTo(mX[0], mY[1], mX[1], mY[2], mX[2], mY[2])
        mHeartPath.cubicTo(mX[3], mY[2], mX[3], mY[3], mX[3], mY[3])
        mHeartPath.cubicTo(mX[3], mY[4], mX[4], mY[5], mX[0], mY[6])
        mHeartPath.cubicTo(mX[5], mY[5], mX[6], mY[4], mX[6], mY[3])
        mHeartPath.cubicTo(mX[6], mY[3], mX[6], mY[2], mX[7], mY[2])
        mHeartPath.cubicTo(mX[8], mY[2], mX[0], mY[1], mX[0], mY[0])

    }

    private fun updatePosition() {
        if (mDirectionToRight)
            mWakaX += mSpeed
        else
            mWakaX -= mSpeed
        if (mDirectionToDown)
            mWakaY += mSpeed
        else
            mWakaY -= mSpeed
    }

   private fun updatePositionRandomly() {
        val random = Random()
        mWakaX = random.nextInt(mXZone).toFloat()
        mWakaY = (random.nextInt(mYZone.first()) + mYZone[1]).toFloat()
    }

    private fun updateDirection() {
        if (mWakaX > mXZone)
            mDirectionToRight = false
        if (mWakaY > mYZone.first())
            mDirectionToDown = false
        if (mWakaX < 0)
            mDirectionToRight = true
        if (mWakaY < mYZone[1])
            mDirectionToDown = true
    }

    private fun changeDirection() {
        mDirectionToDown = !mDirectionToDown
        mDirectionToRight = !mDirectionToRight
    }

    fun update() {
        updatePosition()
        updateDirection()
    }

    fun updateRandomly() {
        updatePositionRandomly()
        changeDirection()
    }

    fun isIn(xOf: Int, yOf: Int): Boolean {
        return (xOf in mWakaX..mWakaX + mX[6] && yOf in mWakaY..mWakaY + mY[6])
    }

}