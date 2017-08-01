package com.chillcoding.mycuteheart.view

import android.graphics.Paint
import android.graphics.Path
import com.chillcoding.mycuteheart.MyApp
import java.util.*

/**
 * Created by macha on 17/07/2017.
 */
class MyCuteHeart {

    var mSize = 0.5f
    var mSpeed = 1
    var mPaint = Paint()
    var mHeartPath = Path()

    var mWakaX = 0f
    var mWakaY = 0f

    var mContrast = 0

    private var mX: FloatArray = floatArrayOf(75f, 60f, 40f, 5f, 40f, 110f, 145f, 110f, 90f)
    private var mY: FloatArray = floatArrayOf(30f, 25f, 5f, 40f, 80f, 102f, 130f)

    private var mDirectionToRight = true
    private var mDirectionToDown = true

    private var mXZone = intArrayOf(0, 0)
    private var mYZone = intArrayOf(0, 0, 0)

    constructor() {
        init()
    }

    constructor(width: Int, height: Int, marginTop: Int) {
        if (height > width)
            mSize = ((height * 3) / 1000).toFloat()
        else
            mSize = ((width * 3) / 1000).toFloat()

        init()
        mXZone = intArrayOf(width - mX[6].toInt(), width)
        mYZone = intArrayOf(height - marginTop - mY[6].toInt(), height, marginTop)
        updatePositionRandomly()
    }

    private fun init() {
        mPaint = Paint()
        mPaint.style = Paint.Style.FILL
        mPaint.isAntiAlias = true
        mPaint.color = MyApp.mColors.last()
        initializeHeartCoordinates()
        createHeart()
    }

    private fun initializeHeartCoordinates() {
        mX = floatArrayOf(75f, 60f, 40f, 5f, 40f, 110f, 145f, 110f, 90f)
        mY = floatArrayOf(30f, 25f, 5f, 40f, 80f, 102f, 130f)

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

    private fun updateDirection() {
        if (mWakaX > mXZone.first())
            mDirectionToRight = false
        if (mWakaX < 0)
            mDirectionToRight = true
        if (mWakaY < mYZone.last())
            mDirectionToDown = true
        if (mWakaY > mYZone.first())
            mDirectionToDown = false
    }

    private fun updatePositionRandomly() {
        val random = Random()
        mWakaX = random.nextInt(mXZone.first()).toFloat()
        mWakaY = (random.nextInt(mYZone.first()) + mYZone.last()).toFloat()
    }

    private fun changeHeartColorRandomly() {
        if (mContrast < MyApp.mColors.size) {
            val random = Random()
            mPaint.color = MyApp.mColors[random.nextInt(MyApp.mColors.size - mContrast)]
        } else
            mPaint.color = MyApp.mColors.first()
    }

    private fun changeDirection() {
        mDirectionToDown = !mDirectionToDown
        mDirectionToRight = !mDirectionToRight
    }

    private fun updateSize() {
        initializeHeartCoordinates()
        createHeart()
    }

    private fun updateZone() {
        mXZone[0] = mXZone.last() - mX[6].toInt()
        mYZone[0] = mYZone[1] - mYZone.last() - mY[6].toInt()
    }

    fun update() {
        updatePosition()
        updateDirection()
    }

    fun update(level: Int) {
        mContrast = level - 1
        if (level < 5)
            mSpeed = Math.pow(2.0, (level - 1).toDouble()).toInt()
        else
            mSpeed = 8
        if (level == 1) {
            if (mXZone[1] > mYZone[1])
                mSize = ((mXZone.last() * 3) / 1000).toFloat()
            else
                mSize = ((mYZone[1] * 3) / 1000).toFloat()
            updateSize()
            updateZone()
        } else if (level in 2..4) {
            mSize = mSize * 2 / 3
            updateSize()
            updateZone()
        }
        updateRandomly()
    }

    fun updateRandomly() {
        updatePositionRandomly()
        changeDirection()
        changeHeartColorRandomly()
    }

    fun isIn(xOf: Int, yOf: Int): Boolean {
        return (xOf in mWakaX..mWakaX + mX[6] && yOf in mWakaY..mWakaY + mY[6])
    }
}