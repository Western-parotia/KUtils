package com.foundation.widget.utils.ext.view

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleEventObserver
import com.foundation.widget.utils.ext.doOnCreated
import com.foundation.widget.utils.ext.doOnDestroyed
import com.foundation.widget.utils.ext.doOnNextResumed
import com.foundation.widget.utils.ext.doOnResumed

/**
 * create by zhusw on 6/9/21 18:29
 */

/**
 * @param params 入参
 */
inline fun <reified T> Fragment.jumpToActivity(params: (Intent.() -> Unit) = {}) {
    val intent = Intent(activity, T::class.java)
    params(intent)
    startActivity(intent)
}

/**
 * @param params 入参
 */
inline fun <reified T> Context.jumpToActivity(params: (Intent.() -> Unit) = {}) {
    val intent = Intent(this, T::class.java)
    params(intent)
    startActivity(intent)
}

/**
 * create之后执行，只执行一次
 */
fun ComponentActivity.doOnCreated(callback: Runnable): LifecycleEventObserver? {
    return lifecycle.doOnCreated(callback)
}

/**
 * 执行在resume时（如果已经resume过会立即执行）
 */
fun ComponentActivity.doOnResumed(callback: Runnable): LifecycleEventObserver {
    return lifecycle.doOnResumed(callback)
}

/**
 * 执行在下一次resume后（忽略之前的resume）
 */
fun ComponentActivity.doOnNextResumed(callback: Runnable): LifecycleEventObserver {
    return lifecycle.doOnNextResumed(callback)
}

fun ComponentActivity.doOnDestroyed(callback: Runnable): LifecycleEventObserver? {
    return lifecycle.doOnDestroyed(callback)
}