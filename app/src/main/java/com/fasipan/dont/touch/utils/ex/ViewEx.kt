package com.fasipan.dont.touch.utils.ex

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.BatteryManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.material.tabs.TabLayout

fun View.disableView() {
    this.isClickable = false
    this.postDelayed({ this.isClickable = true }, 1000)
}

class SafeClickListener(val onSafeClickListener: (View) -> Unit) : View.OnClickListener {
    override fun onClick(v: View) {
        v.disableView()
        onSafeClickListener(v)
    }
}

fun View.setOnSafeClick(onSafeClickListener: (View) -> Unit) {
    val safeClick = SafeClickListener {
        onSafeClickListener(it)
    }
    setOnClickListener(safeClick)
}

var isAvailableClick = true
fun handleAvailableClick(time: Long) {
    Handler(Looper.getMainLooper()).postDelayed({
        isAvailableClick = true
    }, time)
}

fun View.clickSafe(time: Long = 250, action: () -> Unit) {
    this.setOnClickListener {
        if (isAvailableClick) {
            isAvailableClick = false
            handleAvailableClick(time)
            action.invoke()
        }
    }
}

fun Context.openActivity(pClass: Class<out Activity>, bundle: Bundle?) {
    val intent = Intent(this, pClass)
    if (bundle != null) {
        intent.putExtras(bundle)
    }
    startActivity(intent)
}

fun Context.openActivity(pClass: Class<out Activity>, isFinish: Boolean = false) {
    openActivity(pClass, null)
    if (isFinish) {
        (this as Activity).finish()
    }
}

fun View.hide() {
    this.visibility = View.INVISIBLE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.showOrGone(isShow: Boolean) {
    if (isShow) {
        show()
    } else {
        gone()
    }
}

fun View.isUnlock(isShow: Boolean) {
    alpha = if (isShow) {
        1f
    } else {
        0.2f
    }
}

fun ImageView.setTint() {
    val matrix = ColorMatrix()
    matrix.setSaturation(0f)

    val filter = ColorMatrixColorFilter(matrix)
    this.colorFilter = filter
}

fun ImageView.clearTint() {
    this.colorFilter = null
}

fun View.setMargins(left: Int, top: Int, right: Int, bottom: Int) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val p = layoutParams as ViewGroup.MarginLayoutParams
        p.setMargins(left, top, right, bottom)
        requestLayout()
    }
}

fun TabLayout.createTab(tabName: String): TabLayout.Tab {
    return newTab().setText(tabName)
}

val Int.toDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

fun Context.getBatteryLevel(): Int {
    val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
}


@SuppressLint("ClickableViewAccessibility")
fun View.setOnTouchScale(action: () -> Unit, scale: Float, disView: Boolean = true) {
    var isClick = true
    this.setOnTouchListener { view, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                isClick = true
                view.scaleX = scale
                view.scaleY = scale
            }

            MotionEvent.ACTION_MOVE -> {
                if (motionEvent.x < 0 || motionEvent.x > this.width || motionEvent.y < 0 || motionEvent.y > this.height) {
                    isClick = false
                    if (disView) {
                        view.scaleX = 1f
                        view.scaleY = 1f
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                if (isClick) {
                    if (isAvailableClick) {
                        isAvailableClick = false
                        handleAvailableClick(300L)
                        action()
                    }
                }
                view.scaleX = 1f
                view.scaleY = 1f
            }

            MotionEvent.ACTION_CANCEL -> {
                view.scaleX = 1f
                view.scaleY = 1f
            }
        }
        true
    }
}