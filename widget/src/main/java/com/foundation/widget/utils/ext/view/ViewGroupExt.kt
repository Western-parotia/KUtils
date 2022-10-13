package com.foundation.widget.utils.ext.view

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

private val dlpMethod = run {
    val method = ViewGroup::class.java.getDeclaredMethod("generateDefaultLayoutParams")
    method.isAccessible = true
    return@run method
}

/**
 * [ViewGroup.generateDefaultLayoutParams]是protected的，所以有此拓展
 */
fun ViewGroup.getDefLayoutParams(): ViewGroupLayoutParams {
    return if (this is RecyclerView) {
        this.layoutManager?.generateDefaultLayoutParams() ?: RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    } else {
        dlpMethod.invoke(this) as ViewGroupLayoutParams
    }
}