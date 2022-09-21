package com.foundation.widget.utils.ext.global

import android.content.res.Resources
import com.foundation.widget.utils.MjUtils

/**
 * create by zhusw on 6/9/21 21:08
 */

/**
 *
 * @param T
 * @param R
 * @param block
 * @return
 */
inline fun <T, R> T.onNull(block: (T) -> R): R {
    return block(this)
}

val Float.dp get():Int = this.dpF.toInt()
val Float.dpF get():Float = this * Resources.getSystem().displayMetrics.widthPixels / MjUtils.uiScreenWidth

val Int.dpF get() = this.dp.toFloat()

/**
 * 和xml的@dimen/dp_5一致（按ui比例来）
 */
val Int.dp get():Int = this * Resources.getSystem().displayMetrics.widthPixels / MjUtils.uiScreenWidth

fun getCurrentMillis() = System.currentTimeMillis()
