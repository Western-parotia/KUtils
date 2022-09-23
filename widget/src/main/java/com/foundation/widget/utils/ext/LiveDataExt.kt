package com.foundation.widget.utils.ext

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.foundation.widget.utils.MjThreadUtils

/**
 * 子线程post，主线程直接set
 */
fun <T> MutableLiveData<T>.smartPost(t: T) {
    if (MjThreadUtils.isMainThread()) {
        value = t
    } else {
        postValue(t)
    }
}

/**
 * 只订阅获取一次
 * @return 方便主动取消
 */
fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, obs: (T) -> Unit): Observer<T> {
    val o = object : Observer<T> {
        override fun onChanged(t: T) {
            obs.invoke(t)
            this@observeOnce.removeObserver(this)
        }

    }
    this.observe(owner, obs)
    return o
}