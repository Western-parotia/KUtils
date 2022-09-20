@file:Suppress("PackageDirectoryMismatch")//包名只能是Android

package android.view

import androidx.recyclerview.widget.RecyclerView

/**
 * Android包专用
 * 其他拓展不要写在这里
 */


/**
 * generateDefaultLayoutParams是protected的，所以有此拓展
 */
fun ViewGroup.getDefLayoutParams(): ViewGroup.LayoutParams {
    return if (this is RecyclerView) {
        this.layoutManager?.generateDefaultLayoutParams() ?: RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    } else {
        generateDefaultLayoutParams()
    }
}