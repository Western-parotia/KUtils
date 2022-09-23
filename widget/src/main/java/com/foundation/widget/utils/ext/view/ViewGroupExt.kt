package com.foundation.widget.utils.ext.view

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * generateDefaultLayoutParams是protected的，所以有此拓展
 */
fun ViewGroup.getDefLayoutParams(): ViewGroupLayoutParams {
    return if (this is RecyclerView) {
        this.layoutManager?.generateDefaultLayoutParams() ?: RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    } else {
        val method = this.javaClass.getDeclaredMethod("generateDefaultLayoutParams")
        method.isAccessible = true
        method.invoke(this) as ViewGroupLayoutParams
    }
}