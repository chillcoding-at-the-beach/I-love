package com.chillcoding.ilove.view

import android.graphics.Paint
import android.graphics.Path
import com.chillcoding.ilove.App
import java.util.*

/**
 * Created by macha on 17/07/2017.
 */
class CuteHeart {

    var size = 0.5f
    var speed = 1
    var paint = Paint()
    var paintShadow = Paint()
    var path = Path()

    var wakaX = 0f
    var wakaY = 0f

    var colorIndex = 0

    private var mX = FloatArray(7)
    private var mY = FloatArray(7)

    private var mDirectionToRight = true
    private var mDirectionToDown = true

    private var mXZone = intArrayOf(0, 0)
    private var mYZone = intArrayOf(0, 0, 0)

    var level = 1

    constructor(width: Int, height: Int, marginTop: Int, level: Int) {
        mXZone = intArrayOf(0, width)
        mYZone = intArrayOf(0, height, marginTop)
        updateToLevel(level)
        init()
    }

    private fun init() {
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        paint.color = App.sColors.first()

        paintShadow.style = Paint.Style.FILL_AND_STROKE
        paintShadow.color = App.shadowColor
    }

    private fun initializeHeartCoordinates() {
        mX = floatArrayOf(75f, 60f, 40f, 5f, 110f, 145f, 90f)
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
        path.cubicTo(mX[3], mY[4], mX[2], mY[5], mX[0], mY[6])
        path.cubicTo(mX[4], mY[5], mX[5], mY[4], mX[5], mY[3])
        path.cubicTo(mX[5], mY[3], mX[5], mY[2], mX[4], mY[2])
        path.cubicTo(mX[6], mY[2], mX[0], mY[1], mX[0], mY[0])
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
        val random = Random()
        paint.color = App.sColors[random.nextInt(colorIndex)]
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
        mXZone[0] = mXZone.last() - mX[5].toInt()
        mYZone[0] = mYZone[1] - mYZone.last() - mY[6].toInt()
    }

    fun updateTrajectory() {
        updatePosition()
        updateDirection()
        if (isMagic)
            updateSizeToMagic()
    }

    fun updateToLevel(level: Int) {
        this.level = level
        updateColorToLevel(level)
        updateSpeedToLevel(level)
        updateSizeToLevel(level)
        updateRandomly()
    }

    private fun updateColorToLevel(level: Int) {
        if (level <= App.sColors.size)
            colorIndex = level
    }

    private fun updateSizeToLevel(level: Int) {
        var ref: Float
        if (mXZone[1] > mYZone[1])
            ref = ((mXZone[1] * 3) / 1000).toFloat()
        else
            ref = ((mYZone[1] * 3) / 1000).toFloat()
        if (level < 3)
            size = ref / level
        else
            size = ref / 3
        updateSize()
        updateZone()
    }

    private fun updateSizeToMagic() {
        size /= 1.2F
        updateSize()
        if (size < 0.1) {
            isMagic = false
            updateSizeToLevel(level)
            updateRandomly()
        }
    }

    private fun updateSpeedToLevel(level: Int) {
        if (level < 5)
            speed = Math.pow(2.0, (level - 1).toDouble()).toInt()
        else
            speed = 16
    }

    fun updateRandomly() {
        updatePositionRandomly()
        changeDirection()
        changeHeartColorRandomly()
    }

    fun isIn(xOf: Int, yOf: Int): Boolean {
        return (xOf in wakaX..wakaX + mX[5] && yOf in wakaY..wakaY + mY[6])
    }

    private var isMagic: Boolean = false

    fun doMagic() {
        isMagic = true
    }
}