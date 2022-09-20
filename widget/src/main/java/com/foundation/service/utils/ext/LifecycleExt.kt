package com.foundation.service.utils.ext

import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.foundation.service.utils.ext.global.postMain

/**
 * @param ignoreBefore 监听会把之前的都发一遍，所以加此变量
 *                      false：默认效果，之前的生命周期都会收到
 *                      true：之前的都忽略，只有之后的
 */
@MainThread
fun Lifecycle.addObserver(
    ignoreBefore: Boolean = false,
    callback: (thisObs: LifecycleEventObserver, owner: LifecycleOwner, event: Lifecycle.Event) -> Unit
): LifecycleEventObserver {
    if (ignoreBefore) {
        var init = false
        val obs = object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (!init) {
                    //忽略之前的
                    return
                }
                callback(this, source, event)
            }
        }
        addObserver(obs)
        init = true
        return obs
    } else {
        val obs = object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                callback(this, source, event)
            }
        }
        addObserver(obs)
        return obs
    }
}

/**
 * create之后执行，只执行一次
 */
fun Lifecycle.doOnCreated(callback: Runnable): LifecycleEventObserver? {
    return if (currentState.isAtLeast(Lifecycle.State.CREATED)) {
        //本来就是create
        callback.run()
        null
    } else {
        addObserver { thisObs, _, event ->
            when {
                event.targetState.isAtLeast(Lifecycle.State.CREATED) -> {
                    //回调是在create之前，所以要post一下
                    postMain(run = callback)
                    removeObserver(thisObs)
                }
                event == Lifecycle.Event.ON_DESTROY -> {
                    removeObserver(thisObs)
                }
            }
        }
    }
}

/**
 * 执行在resume时（如果已经resume过会立即执行）
 */
fun Lifecycle.doOnResumed(
    callback: Runnable
): LifecycleEventObserver {
    return addObserver(false) { thisObs, _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                //回调是在resume之前，所以要post一下
                postMain(run = callback)
                removeObserver(thisObs)
            }
            Lifecycle.Event.ON_DESTROY -> {
                removeObserver(thisObs)
            }
            else -> {
                //暂时没逻辑
            }
        }
    }
}

/**
 * 执行在下一次resume后（忽略之前的resume）
 */
fun Lifecycle.doOnNextResumed(
    callback: Runnable
): LifecycleEventObserver {
    return addObserver(true) { thisObs, _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                //回调是在resume之前，所以要post一下
                postMain(run = callback)
                removeObserver(thisObs)
            }
            Lifecycle.Event.ON_DESTROY -> {
                removeObserver(thisObs)
            }
            else -> {
                //暂时没逻辑
            }
        }
    }
}

fun Lifecycle.doOnDestroyed(callback: Runnable): LifecycleEventObserver? {
    return if (currentState == Lifecycle.State.DESTROYED) {
        callback.run()
        null
    } else {
        addObserver { thisObs, _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                callback.run()
                removeObserver(thisObs)
            }
        }
    }
}