package com.foundation.widget.utils.other

import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.foundation.widget.utils.ext.view.FrameLayoutLayoutParams
import com.foundation.widget.utils.ext.view.getSpannableSpace
import com.foundation.widget.utils.ext.view.postLifecycle

object ViewImageSpanUtils {
    /**
     * 仿ImageSpan并居中的效果
     *
     * [targetTv]和[spanView]必须都在一个[FrameLayout]里
     *
     * @param targetTv 加span的tv
     * @param spanView 仿span居住效果的view
     * @param spanMarginPx tv和span间距
     */
    fun viewToSpan(targetTv: TextView, spanView: View, spanMarginPx: Int) {
        if (targetTv.parent !is FrameLayout || targetTv.parent != spanView.parent) {
            throw IllegalArgumentException("[target]和[spanView]必须都在一个[FrameLayout]里")
        }
        val targetLp = targetTv.layoutParams as FrameLayoutLayoutParams
        val spanLp = spanView.layoutParams as FrameLayoutLayoutParams
        spanView.postLifecycle {
            //获得空格和单个文本高度
            val spaceBean = targetTv.paint.getSpannableSpace(spanView.width + spanMarginPx)

            //把空格设置进去
            targetTv.text = spaceBean.spaces + targetTv.text.toString()

            //设置margin使span居中
            val spanTopMargin = targetLp.topMargin + spaceBean.textHeight / 2 - spanView.height / 2
            if (spanTopMargin >= 0) {
                //设置span的margin值
                spanLp.topMargin = spanTopMargin
                spanView.requestLayout()
            } else {
                //span margin不够用target的margin
                val targetTopMargin =
                    spanLp.topMargin + spanView.height / 2 - spaceBean.textHeight / 2
                targetLp.topMargin = targetTopMargin
                targetTv.requestLayout()
            }
        }
    }
}