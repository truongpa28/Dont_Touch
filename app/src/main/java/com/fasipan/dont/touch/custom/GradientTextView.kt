package com.fasipan.dont.touch.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.widget.TextView

@SuppressLint("AppCompatCustomView")
class GradientTextView : TextView {
    var lstColor: List<Int> = listOf<Int>()

    constructor(context: Context) : super(context)
    constructor(context: Context, attr: AttributeSet) : super(context, attr)
    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int) : super(
        context,
        attr,
        defStyleAttr
    )

    fun setColor(list: List<Int>) {
        lstColor = list
        paint.shader = LinearGradient(
            0f, 0f, 0f, height.toFloat(),
            lstColor.toIntArray(), null,
            Shader.TileMode.CLAMP
        )
    }

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            paint.shader = LinearGradient(
                0f, 0f, 0f, height.toFloat(),
                lstColor.toIntArray(), null,
                Shader.TileMode.CLAMP
            )
        }
    }
}