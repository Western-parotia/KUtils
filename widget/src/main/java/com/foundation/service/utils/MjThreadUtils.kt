package com.foundation.service.utils

import android.os.Looper

object MjThreadUtils {
    fun isMainThread() = Looper.myLooper() == Looper.getMainLooper()
}