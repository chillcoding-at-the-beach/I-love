package com.chillcoding.mycuteheart.view

import android.graphics.Paint
import android.graphics.Path
import java.util.*

/**
 * Created by macha on 17/07/2017.
 */
class MyCuteHeart {

    var mSize = 0
    var mPaint = Paint()
    var mHeartPath = Path()
    var mXZone = 0
    var mYZone = 0

    private var mX: FloatArray = floatArrayOf(75f, 60f, 40f, 5f, 40f, 110f, 145f, 110f, 90f)
    private var mY: FloatArray = floatArrayOf(30f, 25f, 5f, 40f, 80f, 102f, 130f)

    private var mDirectionToRight = true
    private var mDirectionToDown = true

    constructor(width: Int, height: Int) {
        mXZone = width
        mYZone = height
        if (height < width)
            mSize = height / 7
        else
            mSize = width / 7
        init()
    }

    private fun init() {
        mPaint = Paint()
        mPaint.style = Paint.Style.FILL

        calculateHeartCoordinates()
        moveRandomly()
        createGraphicalObject()
    }

    private fun calculateHeartCoordinates() {
        for (i in mX.indices) {
            mX[i] = (mSize * mX[i]) / 150
        }
        for (i in mY.indices)
            mY[i] = (mY[i] * mSize) / 150
    }

    private fun createGraphicalObject() {
        mHeartPath.set(createHeart())
    }

    private fun createHeart(): Path? {
        val path = Path()
        path.moveTo(mX[0], mY[0])
        path.cubicTo(mX[0], mY[1], mX[1], mY[2], mX[2], mY[2])
        path.cubicTo(mX[3], mY[2], mX[3], mY[3], mX[3], mY[3])
        path.cubicTo(mX[3], mY[4], mX[4], mY[5], mX[0], mY[6]);
        path.cubicTo(mX[5], mY[5], mX[6], mY[4], mX[6], mY[3])
        path.cubicTo(mX[6], mY[3], mX[6], mY[2], mX[7], mY[2])
        path.cubicTo(mX[8], mY[2], mX[0], mY[1], mX[0], mY[0])
        return path
    }

    fun updateCoordinates(speed: Int) {
        if (mX[6] > mXZone)
            mDirectionToRight = false
        if (mY[6] > mYZone)
            mDirectionToDown = false
        if (mX[3] < 0)
            mDirectionToRight = true
        if (mY[2] < 0)
            mDirectionToDown = true

        if (mDirectionToRight)
            for (i in mX.indices)
                mX[i] = mX[i] + speed
        else
            for (i in mX.indices)
                mX[i] = mX[i] - speed

        if (mDirectionToDown)
            for (i in mY.indices)
                mY[i] = mY[i] + speed
        else
            for (i in mY.indices)
                mY[i] = mY[i] - speed

        createGraphicalObject()
    }

    fun moveRandomly() {
        var random = Random()
        var wakaX = random.nextInt(mXZone)
        var wakaY = random.nextInt(mYZone)

        if (random.nextBoolean())
            changeDirection()

        if (wakaX > (mXZone - mX[6])) {
            wakaX -= mX[6].toInt()
            mDirectionToRight = false
        }
        if (wakaY > (mYZone - mY[6])) {
            wakaY -= mY[6].toInt()
            mDirectionToDown = false
        }

        for (i in mX.indices)
            mX[i] = mX[i] + wakaX
        for (i in mY.indices)
            mY[i] = mY[i] + wakaY

        createGraphicalObject()
    }

    fun changeDirection() {
        mDirectionToDown = !mDirectionToDown
        mDirectionToRight = !mDirectionToRight
    }

    fun isIn(xOf: Int, yOf: Int): Boolean {
        return (xOf < mX[6] && xOf > mX[3] && yOf < mY[6] && yOf > mY[2])
    }

}