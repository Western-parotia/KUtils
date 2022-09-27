package com.foundation.widget.utils.ext.view

import android.content.Intent
import android.os.Bundle
import com.foundation.widget.utils.MjKeyboardUtils
import com.foundation.widget.utils.MjUtils
import com.foundation.widget.utils.ext.doOnDestroyed
import com.foundation.widget.utils.ext.doOnNextResumed
import com.foundation.widget.utils.ext.doOnResumed
import com.foundation.widget.utils.ui.IUIContext

/**
 * 关掉当前页面（fragment则关掉对应Activity）
 */
fun IUIContext.finish() {
    this.getActivity()?.finish()
}

fun IUIContext.hideKeyboard() {
    rootView?.let { MjKeyboardUtils.hideKeyboard(it) }
}

fun IUIContext.safetyStartActivity(intent: Intent) {
    try {
        startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
        if (MjUtils.isDebug) {
            throw  e
        }
    }
}

fun IUIContext.safetyStartActivityForResult(
    intent: Intent,
    requestCode: Int,
    options: Bundle? = null
) {
    try {
        startActivityForResult(intent, requestCode, options)
    } catch (e: Exception) {
        e.printStackTrace()
        if (MjUtils.isDebug) {
            throw  e
        }
    }
}

/**
 * create之后执行，只执行一次
 */
fun IUIContext.doOnCreated(callback: Runnable) {
    viewLifecycleWithCallback {
        it.doOnDestroyed(callback)
    }
}

/**
 * 执行在resume时（如果已经resume过会立即执行）
 */
fun IUIContext.doOnResumed(callback: Runnable) {
    viewLifecycleWithCallback {
        it.doOnResumed(callback)
    }
}

/**
 * 执行在下一次resume后（忽略之前的resume）
 */
fun IUIContext.doOnNextResumed(callback: Runnable) {
    viewLifecycleWithCallback {
        it.doOnNextResumed(callback)
    }
}

fun IUIContext.doOnDestroyed(callback: Runnable) {
    viewLifecycleWithCallback {
        it.doOnDestroyed(callback)
    }
}

/**
 * 键盘显隐监听
 * 目前一个Fragment只能有一个监听，你可以指定多个view进行多监听[MjKeyboardUtils.setOnKeyboardChangedListener]
 * @param onChangedListener true:键盘弹出，false键盘收起
 */
fun IUIContext.setOnKeyboardChangedListener(onChangedListener: (Boolean) -> Unit) {
    rootView?.let { MjKeyboardUtils.setOnKeyboardChangedListener(it, onChangedListener) }
}

/**
 * @param params 入参
 */
inline fun <reified T> IUIContext.jumpToActivity(params: (Intent.() -> Unit) = {}) {
    val intent = Intent(getActivity(), T::class.java)
    params(intent)
    startActivity(intent)
}

/**
 * Activity的非null情况，当然你必须明确知道Activity存在
 */
val IUIContext.requireActivity
    get() = getActivity() ?: throw IllegalStateException("this $this not attached to an activity.")