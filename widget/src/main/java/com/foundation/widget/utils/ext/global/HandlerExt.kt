package com.foundation.widget.utils.ext.global

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LifecycleOwner
import com.foundation.widget.utils.ext.doOnDestroyed

/**
 * 注意：无生命周期，自动移除见[postDelayedLifecycle]
 */
fun Handler.postDelayed(mills: Long, run: Runnable) {
    postDelayed(run, mills)
}

/**
 * destroy时自动移除
 */
fun Handler.postDelayedLifecycle(
    owner: LifecycleOwner,
    mills: Long,
    run: Runnable
) {
    postDelayed(mills, run)
    owner.lifecycle.doOnDestroyed {
        removeCallbacks(run)
    }
}

/////////////////////////////////////////////////////////////////////////////////////////////////
// 全局handler
/////////////////////////////////////////////////////////////////////////////////////////////////

private val globalHandler = Handler(Looper.getMainLooper())

fun removeGlobalRunnable(runnable: Runnable) {
    globalHandler.removeCallbacks(runnable)
}

/**
 * @param run 必须显示指定为Runnable类：Runnable {xxx}
 */
fun postMain(run: Runnable) {
    //默认去重
    removeGlobalRunnable(run)
    globalHandler.post(run)
}

/**
 * 子线程会post，主线程则直接调用
 */
fun postMainSmart(run: Runnable) {
    //默认去重
    removeGlobalRunnable(run)
    if (Looper.myLooper() == Looper.getMainLooper()) {
        run.run()
    } else {
        globalHandler.post(run)
    }
}

/**
 * inline版实现
 */
inline fun postMainSmart(crossinline call: () -> Unit) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        call.invoke()
    } else {
        postMain { call.invoke() }
    }
}

/**
 * 注意：无生命周期，自动移除见[postMainDelayedLifecycle]
 */
fun postMainDelayed(mills: Long, run: Runnable) {
    //默认去重
    removeGlobalRunnable(run)
    globalHandler.postDelayed(mills, run)
}

/**
 * destroy时自动移除
 */
fun postMainDelayedLifecycle(
    owner: LifecycleOwner,
    mills: Long,
    run: Runnable
) {
    //默认去重
    removeGlobalRunnable(run)
    globalHandler.postDelayedLifecycle(owner, mills, run)
}

/**
 * 用于延迟执行某个后台任务
 */
inline fun postMainOnIdle(mills: Long = 0, crossinline run: () -> Unit) {
    val r = Runnable {
        Looper.myQueue().addIdleHandler {
            run.invoke()
            false
        }
    }
    if (mills <= 0) {
        postMainSmart(r)
    } else {
        postMainDelayed(mills, run = r)
    }
}
