package com.foundation.widget.utils

import android.os.Looper

object MjThreadUtils {
    fun isMainThread() = Looper.myLooper() == Looper.getMainLooper()
}