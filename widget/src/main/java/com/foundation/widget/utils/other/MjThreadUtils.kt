package com.foundation.widget.utils.other

import android.os.Looper

object MjThreadUtils {
    fun isMainThread() = Looper.myLooper() == Looper.getMainLooper()
}