package com.chillcoding.mycuteheart.view

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path

/**
 * Created by macha on 17/07/2017.
 */
class MyCuteHeart {

    var mSize = 0
    var mColor = Color.RED
    var mPaint = Paint()
    val mHeartPath = Path()
    var mXZone = 0
    var mYZone = 0


    private var x: FloatArray = floatArrayOf(75f, 60f, 40f, 5f, 40f, 110f, 145f, 110f, 90f)
    private var y: FloatArray = floatArrayOf(30f, 25f, 5f, 40f, 80f, 102f, 145f)


    constructor(size: Int) {
        mSize = size
        init()
    }

    private fun init() {
        mPaint = Paint()
        mPaint.style = Paint.Style.FILL
        mPaint.color = mColor

        calculateHeartCoordinates()
        createGraphicalObject()
    }

    private fun calculateHeartCoordinates() {
        for (i in x.indices) {
            x[i] = (mSize * x[i]) / 150
        }
        for (i in y.indices)
            y[i] = (y[i] * mSize) / 150
    }

    private fun createGraphicalObject() {
        mHeartPath.set(createHeart())
    }

    private fun createHeart(): Path? {
        val path = Path()
        path.moveTo(x[0], y[0])
        path.cubicTo(x[0], y[1], x[1], y[2], x[2], y[2])
        path.cubicTo(x[3], y[2], x[3], y[3], x[3], y[3])
        path.cubicTo(x[3], y[4], x[4], y[5], x[0], y[6]);
        path.cubicTo(x[5], y[5], x[6], y[4], x[6], y[3])
        path.cubicTo(x[6], y[3], x[6], y[2], x[7], y[2])
        path.cubicTo(x[8], y[2], x[0], y[1], x[0], y[0])
        return path
    }

    private var mDirectionToRight = true

    private var mDirectionToDown = true

    fun onUpdate() {
        if (x[6] > mXZone)
            mDirectionToRight = false
        if (y[6] > mYZone)
            mDirectionToDown = false
        if (x[3] < 0)
            mDirectionToRight = true
        if (y[2] < 0)
            mDirectionToDown = true

        if (mDirectionToRight)
            for (i in x.indices)
                x[i]++
        else
            for (i in x.indices)
                x[i]--

        if (mDirectionToDown)
            for (i in y.indices)
                y[i]++
        else
            for (i in y.indices)
                y[i]--

        createGraphicalObject()
    }

    fun isIn(xOf: Int, yOf: Int): Boolean {
        if (xOf < x[6] && xOf > x[3] && yOf < y[6] && yOf > y[2])
            return true
        else
            return false
    }

    companion object {
        private val TAG = "My CUTE HEART"
    }

}