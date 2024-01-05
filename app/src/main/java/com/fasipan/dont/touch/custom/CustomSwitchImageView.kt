package com.fasipan.dont.touch.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import com.fasipan.dont.touch.R

class CustomSwitchImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var isSwitchOn: Boolean = false
    private var trueDrawable: Drawable? = null
    private var falseDrawable: Drawable? = null
    private var listener: OnSwitchStateChangeListener? = null

    init {
        trueDrawable = AppCompatResources.getDrawable(context, R.drawable.image_sw_true)
        falseDrawable = AppCompatResources.getDrawable(context, R.drawable.image_sw_false)
        setImageDrawable(if (isSwitchOn) trueDrawable else falseDrawable)
        setOnClickListener {
            toggleSwitch()
        }
    }

    fun setSwitchState(isOn: Boolean) {
        isSwitchOn = isOn
        setImageDrawable(if (isSwitchOn) trueDrawable else falseDrawable)
        invalidate()
    }

    fun setDrawables(trueDrawable: Drawable, falseDrawable: Drawable) {
        this.trueDrawable = trueDrawable
        this.falseDrawable = falseDrawable
        setImageDrawable(if (isSwitchOn) trueDrawable else falseDrawable)
    }

    fun setSwitchStateChangeListener(listener: OnSwitchStateChangeListener) {
        this.listener = listener
    }

    private fun toggleSwitch() {
        isSwitchOn = !isSwitchOn
        setImageDrawable(if (isSwitchOn) trueDrawable else falseDrawable)
        listener?.onSwitchStateChange(isSwitchOn)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }
}

interface OnSwitchStateChangeListener {
    fun onSwitchStateChange(isOn: Boolean)
}