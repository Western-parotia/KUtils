package com.foundation.widget.utils

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment

object MjKeyboardUtils {
    /**
     * @param view 任意view
     */
    @JvmStatic
    fun hideKeyboard(view: View) {
        view.clearFocus()
        val inm = view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    /**
     * @param view 任意view
     */
    fun showKeyboard(view: EditText) {
        view.isFocusable = true
        view.isFocusableInTouchMode = true
        view.requestFocus()
        val inm = view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    /**
     * @param view 任意view，每个view可添加一个监听
     * @param onShowListener true：键盘弹起
     */
    fun setOnKeyboardChangedListener(view: View, onShowListener: (isShowing: Boolean) -> Unit) {
        val cacheData = object {
            var mShowInput: Boolean? = null
            var mRect = Rect()
        }
        val screenHeight = view.resources.displayMetrics.heightPixels
        var listener =
            view.getTag(R.id.tag_keyboard_tree_listener) as? OnGlobalLayoutListener

        //删掉以前的
        if (listener != null) {
            view.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }

        //新创建一个
        listener = OnGlobalLayoutListener {
            view.post {
                //获取屏幕底部坐标
                view.rootView.getWindowVisibleDisplayFrame(cacheData.mRect)
                //获取键盘的高度
                val heightDifference = screenHeight - cacheData.mRect.bottom
                val showInput = heightDifference > screenHeight / 5
                if (cacheData.mShowInput == null || cacheData.mShowInput != showInput) { //键盘状态变化
                    cacheData.mShowInput = showInput
                    onShowListener(cacheData.mShowInput!!)
                }
            }
        }
        view.setTag(R.id.tag_keyboard_tree_listener, listener)
        view.viewTreeObserver.addOnGlobalLayoutListener(listener)
        //view被删除后应取消相关监听，但是单纯的remove反而会引起更多问题，鉴于场景几乎没有，暂时忽略
//        view.doOnDetach { view.viewTreeObserver.removeOnGlobalLayoutListener(listener) }
    }
}

fun Activity.hideKeyboard() {
    window?.decorView?.let { MjKeyboardUtils.hideKeyboard(it) }
}

fun Fragment.hideKeyboard() {
    activity?.window?.decorView?.let { MjKeyboardUtils.hideKeyboard(it) }
}

/**
 * 键盘显隐监听
 * 目前一个Activity只能有一个监听
 * @param onChangedListener true:键盘弹出，false键盘收起
 */
fun Activity.setOnKeyboardChangedListener(onChangedListener: (Boolean) -> Unit) {
    window?.decorView?.let { MjKeyboardUtils.setOnKeyboardChangedListener(it, onChangedListener) }
}

fun Fragment.setOnKeyboardChangedListener(onChangedListener: (Boolean) -> Unit) {
    view?.let { MjKeyboardUtils.setOnKeyboardChangedListener(it, onChangedListener) }
}