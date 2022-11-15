package com.foundation.widget.utils.anim

import android.view.View
import androidx.core.view.updateLayoutParams
import com.foundation.widget.utils.ext.view.ViewGroupLayoutParams
import com.foundation.widget.utils.ext.view.WRAP_CONTENT
import kotlin.math.abs

object MjAnimUtils {
    /**
     * 高度由0变wrap的动画
     * 宽必须固定，高必须wrap（如果不是此条件，自己写简单动画就行了）
     */
    @JvmStatic
    fun animHeightWrap(view: View, duration: Long = 300L) {
        val width = view.measuredWidth
        val oldHeight = view.measuredHeight
        view.measure(
            View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(9999, View.MeasureSpec.UNSPECIFIED),
        )

        val newHeight = view.measuredHeight
        if (abs(newHeight - oldHeight) < 10) {
            return
        }

        val anim = MjValueAnimator.ofInt(oldHeight, newHeight)
        anim.addListener {
            view.updateLayoutParams<ViewGroupLayoutParams> {
                height = WRAP_CONTENT
            }
        }
        anim.addUpdateListener { _, h ->
            view.updateLayoutParams<ViewGroupLayoutParams> {
                height = h
            }
        }
        anim.duration = duration
        anim.startForSingleView(view)
    }
}