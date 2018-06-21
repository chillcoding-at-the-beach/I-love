package com.chillcoding.ilove.view

import android.graphics.Paint
import android.graphics.Path
import com.chillcoding.ilove.App
import java.util.*

/**
 * Created by macha on 17/07/2017.
 */
class ILoveHeart {

    private var isMagic: Boolean = false
    private var size = 0.5f
    private var speed = 1


    private var colorIndex = 0

    private var mX = FloatArray(7)
    private var mY = FloatArray(7)

    private var mDirectionToRight = true
    private var mDirectionToDown = true

    var paint = Paint()
    var paintShadow = Paint()
    var path = Path()

    var iLoveX = 0f
    var iLoveY = 0f

    var level: Int = 1
        set(value) {
            field = value
            updateToLevel()
        }

    private var xZone: Int = 100

    private var yZone: Int = 100

    var widthScreen = 0

    var heightScreen = 0

    var marginTopZone: Int = 10

    constructor() {
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
            iLoveX += speed
        else
            iLoveX -= speed
        if (mDirectionToDown)
            iLoveY += speed
        else
            iLoveY -= speed
    }

    private fun updateDirection() {
        if (iLoveX !in 0..xZone)
            mDirectionToRight = !mDirectionToRight
        if (iLoveY !in marginTopZone..yZone)
            mDirectionToDown = !mDirectionToDown
    }

    private fun updatePositionRandomly() {
        val random = Random()
        iLoveX = random.nextInt(xZone).toFloat()
        iLoveY = (marginTopZone + random.nextInt(yZone)).toFloat()
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
        xZone = widthScreen - mX[5].toInt()
        yZone = heightScreen - marginTopZone - mY[6].toInt()
    }

    private fun updateToLevel() {
        updateColorToLevel()
        updateSpeedToLevel()
        updateSizeToLevel()
        updateRandomly()
    }

    private fun updateColorToLevel() {
        if (level <= App.sColors.size)
            colorIndex = level
    }

    private fun updateSizeToLevel() {
        var ref: Float
        if (widthScreen > heightScreen)
            ref = ((widthScreen * 3) / 1000).toFloat()
        else
            ref = ((heightScreen * 3) / 1000).toFloat()
        if (level < 7)
            size = ref - ((level - 1) * 0.7F)
        else
            size = ref - 4
        updateSize()
        updateZone()
    }

    private fun updateSizeToMagic() {
        size /= 1.2F
        updateSize()
        if (size < 0.1) {
            isMagic = false
            updateSizeToLevel()
            updateRandomly()
        }
    }

    private fun updateSpeedToLevel() {
        speed = 2 * (level - 1) + 1
    }

    private fun updateRandomly() {
        updatePositionRandomly()
        changeDirection()
        changeHeartColorRandomly()
    }

    fun updateTrajectory() {
        updatePosition()
        updateDirection()
        if (isMagic)
            updateSizeToMagic()
    }

    fun isIn(xOf: Int, yOf: Int): Boolean {
        return (xOf in iLoveX..iLoveX + mX[5] && yOf in iLoveY..iLoveY + mY[6])
    }

    fun doMagic() {
        isMagic = true
    }
}