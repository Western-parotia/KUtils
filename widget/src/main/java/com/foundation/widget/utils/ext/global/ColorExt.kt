package com.foundation.widget.utils.ext.global

import android.content.Context
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.foundation.widget.utils.MjUtils

val Int.toColorInt
    @ColorInt
    get() = this.toColorInt()

@ColorInt
@JvmOverloads
fun Int.toColorInt(context: Context = MjUtils.app) = ContextCompat.getColor(context, this)
