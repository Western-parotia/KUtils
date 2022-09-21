package com.foundation.widget.utils.ext

import androidx.lifecycle.MutableLiveData
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