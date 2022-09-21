package com.foundation.widget.utils.ext.view

import androidx.fragment.app.Fragment
import com.foundation.widget.utils.ext.doOnCreated
import com.foundation.widget.utils.ext.doOnDestroyed
import com.foundation.widget.utils.ext.doOnNextResumed
import com.foundation.widget.utils.ext.doOnResumed

/**
 * create之后执行，只执行一次
 */
fun Fragment.doOnCreated(callback: Runnable) {
    //view的viewLifecycleOwner可能没有初始化，所以先等resume再调用
    lifecycle.doOnResumed {
        if (view != null) {
            viewLifecycleOwner.lifecycle.doOnCreated(callback)
        }
    }
}

/**
 * 执行在resume时（如果已经resume过会立即执行）
 */
fun Fragment.doOnResumed(callback: Runnable) {
    //view的viewLifecycleOwner可能没有初始化，所以先等resume再调用
    lifecycle.doOnResumed {
        if (view != null) {
            viewLifecycleOwner.lifecycle.doOnResumed(callback)
        }
    }
}

/**
 * 执行在下一次resume后（忽略之前的resume）
 */
fun Fragment.doOnNextResumed(callback: Runnable) {
    //view的viewLifecycleOwner可能没有初始化，所以先等resume再调用
    lifecycle.doOnResumed {
        if (view != null) {
            viewLifecycleOwner.lifecycle.doOnNextResumed(callback)
        }
    }
}

fun Fragment.doOnDestroyed(callback: Runnable) {
    lifecycle.doOnResumed {
        if (view != null) {
            viewLifecycleOwner.lifecycle.doOnDestroyed(callback)
        }
    }
}