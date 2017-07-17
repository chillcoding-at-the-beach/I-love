package com.chillcoding.mycuteheart.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

/**
 * Created by macha on 17/07/2017.
 */
class MyCuteHeart : View {
    private var mPaint = Paint()
    private var mSize = 0
    private val mHeartPath = Path()
    var x: FloatArray = floatArrayOf(75f, 60f, 40f, 5f, 40f, 110f, 145f, 110f, 90f)
    var y: FloatArray = floatArrayOf(30f, 25f, 5f, 40f, 80f, 102f, 145f)

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        mPaint = Paint()
        mPaint.style = Paint.Style.FILL
        mPaint.color = Color.RED
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (width < height)
            mSize = width
        else
            mSize = height
        calculateHeartCoordinates()
        createGraphicalObject()
    }

    fun calculateHeartCoordinates() {
        for (i in x.indices) {
            x[i] = (mSize * x[i]) / 150
        }
        for (i in y.indices)
            y[i] = (y[i] * mSize) / 150
    }

    private fun createGraphicalObject() {
        mHeartPath.set(createHeart(mSize))
    }

    private fun createHeart(mSize: Int): Path? {
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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(mHeartPath, mPaint)
    }

    companion object {
        private val TAG = "My CUSTOM VIEW"
    }
}