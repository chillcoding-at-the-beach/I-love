package com.chillcoding.mycuteheart.view

import android.graphics.Paint
import android.graphics.Path
import android.util.Log
import com.chillcoding.mycuteheart.MyApp
import java.util.*

/**
 * Created by macha on 17/07/2017.
 */
class MyCuteHeart {

    var size = 0.5f
    var speed = 1
    var paint = Paint()
    var path = Path()

    var wakaX = 0f
    var wakaY = 0f

    var contrast = 0

    private lateinit  var mX: FloatArray
    private lateinit var mY: FloatArray

    private var mDirectionToRight = true
    private var mDirectionToDown = true

    private var mXZone = intArrayOf(0, 0)
    private var mYZone = intArrayOf(0, 0, 0)

    constructor() {
        init()
    }

    constructor(width: Int, height: Int, marginTop: Int) {
        if (height > width)
            size = ((height * 3) / 1000).toFloat()
        else
            size = ((width * 3) / 1000).toFloat()

        init()
        mXZone = intArrayOf(width - mX[6].toInt(), width)
        mYZone = intArrayOf(height - marginTop - mY[6].toInt(), height, marginTop)
        updatePositionRandomly()
    }

    private fun init() {
        paint = Paint()
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        paint.color = MyApp.sColors.last()
        initializeHeartCoordinates()
        createHeart()
    }

    private fun initializeHeartCoordinates() {
        mX = floatArrayOf(75f, 60f, 40f, 5f, 40f, 110f, 145f, 110f, 90f)
        mY = floatArrayOf(22f, 20f, 5f, 40f, 80f, 102f, 135f)

        for (i in mX.indices)
            mX[i] = mX[i] * size
        for (i in mY.indices)
            mY[i] = mY[i] * size
    }

    private fun createHeart() {
        path = Path()
        path.moveTo(mX[0], mY[0])
        path.cubicTo(mX[0], mY[1], mX[1], mY[2], mX[2], mY[2])
        path.cubicTo(mX[3], mY[2], mX[3], mY[3], mX[3], mY[3])
        path.cubicTo(mX[3], mY[4], mX[4], mY[5], mX[0], mY[6])
        path.cubicTo(mX[5], mY[5], mX[6], mY[4], mX[6], mY[3])
        path.cubicTo(mX[6], mY[3], mX[6], mY[2], mX[7], mY[2])
        path.cubicTo(mX[8], mY[2], mX[0], mY[1], mX[0], mY[0])
    }

    private fun updatePosition() {
        if (mDirectionToRight)
            wakaX += speed
        else
            wakaX -= speed
        if (mDirectionToDown)
            wakaY += speed
        else
            wakaY -= speed
    }

    private fun updateDirection() {
        if (wakaX !in 0..mXZone.first())
            mDirectionToRight = !mDirectionToRight
        if (wakaY !in mYZone.last()..mYZone.first())
            mDirectionToDown = !mDirectionToDown
    }

    private fun updatePositionRandomly() {
        val random = Random()
        wakaX = random.nextInt(mXZone.first()).toFloat()
        wakaY = (random.nextInt(mYZone.first()) + mYZone.last()).toFloat()
    }

    private fun changeHeartColorRandomly() {
        if (contrast < MyApp.sColors.size) {
            val random = Random()
            paint.color = MyApp.sColors[random.nextInt(MyApp.sColors.size - contrast)]
        } else
            paint.color = MyApp.sColors.first()
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

    fun updateTrajectory() {
        updatePosition()
        updateDirection()
    }

    fun updateLevel(level: Int) {
        contrast = level - 1
        if (level < 5)
            speed = Math.pow(2.0, (level - 1).toDouble()).toInt()
        else
            speed = 8
        if (level == 1) {
            if (mXZone[1] > mYZone[1])
                size = ((mXZone.last() * 3) / 1000).toFloat()
            else
                size = ((mYZone[1] * 3) / 1000).toFloat()
            updateSize()
            updateZone()
        } else if (level in 2..4) {
            size = size * 2 / 3
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
        return (xOf in wakaX..wakaX + mX[6] && yOf in wakaY..wakaY + mY[6])
    }
}