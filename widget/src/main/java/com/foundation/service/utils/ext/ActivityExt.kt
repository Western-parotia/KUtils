package com.foundation.service.utils.ext

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleEventObserver

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
 * 下一次resume时自行，只执行一次
 * @param ignoreBefore 监听会把之前的都发一遍，所以加此变量
 *                      false：默认效果，如果resume过会立即收到
 *                      true：等下一次resume
 */
fun ComponentActivity.doOnResumed(
    ignoreBefore: Boolean = false,
    callback: Runnable
): LifecycleEventObserver {
    return lifecycle.doOnResumed(ignoreBefore, callback)
}

fun ComponentActivity.doOnDestroyed(callback: Runnable): LifecycleEventObserver? {
    return lifecycle.doOnDestroyed(callback)
}