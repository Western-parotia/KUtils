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
 * @param deduplication 去重
 * @param run 必须显示指定为Runnable类：Runnable {xxx}
 */
@JvmOverloads
fun postMain(deduplication: Boolean = true, run: Runnable) {
    if (deduplication) {
        globalHandler.removeCallbacks(run)
    }
    globalHandler.post(run)
}

/**
 * 子线程会post，主线程则直接调用
 */
fun postMainSmart(run: Runnable) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        run.run()
    } else {
        globalHandler.post(run)
    }
}

/**
 * 注意：无生命周期，自动移除见[postMainDelayedLifecycle]
 * @param deduplication 是否去重
 */
@JvmOverloads
fun postMainDelayed(mills: Long, deduplication: Boolean = true, run: Runnable) {
    if (deduplication) {
        removeGlobalRunnable(run)
    }
    globalHandler.postDelayed(mills, run)
}

/**
 * destroy时自动移除
 * @param deduplication 是否去重
 */
@JvmOverloads
fun postMainDelayedLifecycle(
    owner: LifecycleOwner,
    mills: Long,
    deduplication: Boolean = true,
    run: Runnable
) {
    if (deduplication) {
        removeGlobalRunnable(run)
    }
    globalHandler.postDelayedLifecycle(owner, mills, run)
}

/**
 * 用于延迟执行某个后台任务
 */
fun postMainOnIdle(mills: Long = 0, run: () -> Unit) {
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
