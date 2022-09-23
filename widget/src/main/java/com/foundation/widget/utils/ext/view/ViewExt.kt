@file:Suppress("unused")//未使用的检查

package com.foundation.widget.utils.ext.view

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import androidx.annotation.Px
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.drawToBitmap
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.foundation.widget.utils.MjKeyboardUtils
import com.foundation.widget.utils.MjUtils
import com.foundation.widget.utils.ext.global.dp
import com.foundation.widget.utils.touch.TouchUtils
import com.google.android.flexbox.FlexboxLayoutManager

/**
 * View基本拓展
 */

typealias ViewGroupLayoutParams = ViewGroup.LayoutParams
typealias RecyclerViewLayoutParams = RecyclerView.LayoutParams
typealias LinearLayoutLayoutParams = LinearLayout.LayoutParams
typealias FrameLayoutLayoutParams = FrameLayout.LayoutParams
typealias GridLayoutManagerLayoutParams = GridLayoutManager.LayoutParams
typealias FlexboxLayoutManagerLayoutParams = FlexboxLayoutManager.LayoutParams
typealias ConstraintLayoutLayoutParams = ConstraintLayout.LayoutParams

const val MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT
const val WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT

fun ViewGroup.getChildOrNull(index: Int) =
    if (index in 0 until childCount) getChildAt(index) else null

val View.layoutInflater: LayoutInflater get() = LayoutInflater.from(context)

private const val CLICK_INTERVAL = 300L

/**
 * 默认没有margin，见[setMarginOtherDefault]
 */
private const val MARGIN_DEFAULT = Int.MIN_VALUE

/**
 * 避免快速点击
 * @param T
 * @param block
 * @receiver
 */
@JvmOverloads
fun <T : View> T.setOnShakeLessClickListener(
    clickInterval: Long = CLICK_INTERVAL,
    block: (T) -> Unit
) {
    var timestamp = System.currentTimeMillis()
    setOnClickListener {
        val interval = System.currentTimeMillis() - timestamp
        if (isClickable && interval >= clickInterval) {
            block(this)
        }
        timestamp = System.currentTimeMillis()
    }

}

/**
 * view的事件区域拓展，支持跨parent调用，解决一级parent仍然不够的问题
 *
 * 有了它就可以愉快的写margin了，不用再因为点击区域过小而烦恼
 *
 * @param parentDeep 附着在第几层父级（注意自己层级）
 * @param sizePx     四周拓展大小
 */
fun View.expandTouchArea(
    @IntRange(from = 1, to = 8) parentDeep: Int = 1,
    sizePx: Int = 10.dp
) {
    TouchUtils.expandTouchArea(this, parentDeep, sizePx)
}

/**
 * @param parentDeep 必须和你之前add的一致
 */
fun View.removeTouchArea(
    @IntRange(from = 1, to = 8) parentDeep: Int = 1
) {
    TouchUtils.removeTouchArea(this, parentDeep)
}

/**
 * 增加了生命周期判断
 */
fun View.postLifecycle(run: Runnable) {
    post {
        if (ViewCompat.isAttachedToWindow(this)) {
            run.run()
        }
    }
}

/**
 * 增加了生命周期判断
 * @return 提前remove用
 */
fun View.postDelayedLifecycle(mills: Long, run: Runnable): ViewPostDelayedInfo {
    val detachListener = doOnDetachCanCancel { removeCallbacks(run) }
    postDelayed(run, mills)
    return ViewPostDelayedInfo(this, detachListener, run)
}

/**
 * 加了返回值，可自行remove
 */
inline fun View.doOnDetachCanCancel(crossinline action: (view: View) -> Unit): View.OnAttachStateChangeListener? {
    if (!ViewCompat.isAttachedToWindow(this)) {
        action(this)
        return null
    }
    val detachListener = object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(view: View) {}

        override fun onViewDetachedFromWindow(view: View) {
            removeOnAttachStateChangeListener(this)
            action(view)
        }
    }
    addOnAttachStateChangeListener(detachListener)
    return detachListener
}

/**
 * postDelayedLifecycle时想提前remove用的[removeCallback]
 */
class ViewPostDelayedInfo(
    private val view: View,
    private val listener: View.OnAttachStateChangeListener?,
    private val run: Runnable
) {
    /**
     * 提前解绑
     */
    fun removeCallback() {
        if (listener != null) {
            view.removeOnAttachStateChangeListener(listener)
        }
        view.removeCallbacks(run)
    }
}

/**
 * 设置padding，不传就是原值
 */
fun View.setPaddingOtherDefault(
    @Px left: Int = paddingLeft,
    @Px top: Int = paddingTop,
    @Px right: Int = paddingRight,
    @Px bottom: Int = paddingBottom
) {
    setPadding(left, top, right, bottom)
}

/**
 * 设置margin，不传就是原值
 * 注意：必须是MarginLayoutParams
 */
fun View.setMarginOtherDefault(
    @Px left: Int = MARGIN_DEFAULT,
    @Px top: Int = MARGIN_DEFAULT,
    @Px right: Int = MARGIN_DEFAULT,
    @Px bottom: Int = MARGIN_DEFAULT
) {
    val params = layoutParams
    if (params !is ViewGroup.MarginLayoutParams) {
        if (MjUtils.isDebug) {
            throw IllegalArgumentException("不正确的MarginLayoutParams：$params")
        }
        return
    }
    if (left != MARGIN_DEFAULT) {
        params.leftMargin = left
        params.marginStart = left
    }
    if (top != MARGIN_DEFAULT) {
        params.topMargin = top
    }
    if (right != MARGIN_DEFAULT) {
        params.rightMargin = right
        params.marginEnd = right
    }
    if (bottom != MARGIN_DEFAULT) {
        params.bottomMargin = bottom
    }
    requestLayout()
}

/**
 * 获取焦点，并清除其他view的焦点
 * @param hideInput true则顺便隐藏键盘
 */
fun View.requestFocusAndClearOther(hideInput: Boolean = true) {
    rootView.findFocus()?.let {
        it.clearFocus()
        if (hideInput) {
            MjKeyboardUtils.hideKeyboard(it)
        }
    }
    isFocusable = true
    requestFocus()
}

/**
 * 跟[drawToBitmap]类似，增加了底色
 */
@JvmOverloads
fun View.getBitmap(@ColorInt color: Int = Color.TRANSPARENT): Bitmap {
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val c = Canvas(bmp)
    c.drawColor(color)
    //如果不设置canvas画布为白色，则生成透明
    layout(0, 0, width, height)
    draw(c)
    requestLayout()
    invalidate()
    return bmp
}