package com.chillcoding.fablibrary

import android.content.Context
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.delay

class GameFab : FloatingActionButton {

    private val maximumAnimationDuration by lazy { context.resources.getInteger(R.integer.play_button_animation_duration).toLong() }

    private var listener: OnGameFabClickListener? = null
    private var currentMode: Mode = Mode.PLAYPAUSE
        set(value) {
            field = value
            setImageDrawable(field)
        }

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        val typedArray = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.FloatingGameActionButton,
                0, 0)

        try {
            currentMode = getMode(typedArray.getInteger(R.styleable.FloatingGameActionButton_mode, Mode.PLAYPAUSE.styleableInt))
        } finally {
            typedArray.recycle()
        }

        this.setOnClickListener {
            eventActor.offer(it)
            listener?.onClick(it)
        }
    }

    private fun getMode(styleableInt: Int): Mode = listOf(Mode.PLAYPAUSE, Mode.PAUSEPLAY).first { it.styleableInt == styleableInt }

    val eventActor = GlobalScope.actor<View>(Dispatchers.Main) {
        for (event in channel) {
            val oppositeMode = currentMode.getOppositeMode()
            this@GameFab.drawable.startAsAnimatable()
            delay(maximumAnimationDuration)
            currentMode = oppositeMode
        }
    }

    sealed class Mode(val styleableInt: Int, @DrawableRes val drawableRes: Int) {
        object PLAYPAUSE : Mode(0, R.drawable.play_to_pause_animation)
        object PAUSEPLAY : Mode(1, R.drawable.pause_to_play_animation)
    }

    private val opposites = mapOf(
            Mode.PLAYPAUSE to Mode.PAUSEPLAY,
            Mode.PAUSEPLAY to Mode.PLAYPAUSE)

    private fun Mode.getOppositeMode() = opposites[this]!!

    fun setOnGameFabClickListener(listener: OnGameFabClickListener) {
        this.listener = listener
    }

    @RequiresApi(Build.VERSION_CODES.DONUT)
    private fun Drawable.startAsAnimatable() = (this as Animatable).start()

    private fun setImageDrawable(mode: Mode) {
        val animatedVector = AppCompatResources.getDrawable(context, mode.drawableRes)
        this.setImageDrawable(animatedVector)
    }

    fun playAnimation(view: View) {
        eventActor.offer(view)
    }

    interface OnGameFabClickListener {
        fun onClick(view: View)
    }

}