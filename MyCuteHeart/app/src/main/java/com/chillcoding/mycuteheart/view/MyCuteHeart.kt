package com.chillcoding.mycuteheart.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

/**
 * Created by macha on 17/07/2017.
 */
class MyHeartView : View {
    private var mPaint = Paint()
    private val mHeartPath = Path()

    constructor(context: Context) : super(context) {
        init()
    }

    private fun init() {
        mPaint = Paint()
        mPaint.style = Paint.Style.STROKE
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        createGraphicalObject()
    }

    private fun createGraphicalObject() {
        mHeartPath.set(createHeart())
    }

    private fun createHeart(): Path? {
        val path = Path()
        path.moveTo(75F, 40F)
        path.cubicTo(75F, 37F, 70F, 25F, 50F, 25F)
        path.cubicTo(20F, 25F, 20F, 62.5F, 20F, 62.5F)
        path.cubicTo(20F, 80F, 40F, 102F, 75F, 120F);
        path.cubicTo(110F, 102F, 130F, 80F, 130F, 62.5F)
        path.cubicTo(130F, 62.5F, 130F, 25F, 100F, 25F)
        path.cubicTo(85F, 25F, 75F, 37F, 75F, 40F)
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