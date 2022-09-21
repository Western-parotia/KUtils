package com.foundation.widget.utils.touch

import android.graphics.Rect
import android.view.View
import android.widget.ListView
import android.widget.ScrollView
import androidx.annotation.IntRange
import androidx.core.view.ScrollingView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.foundation.widget.utils.ext.global.dp

object TouchUtils {
    /**
     * view的事件区域拓展，支持跨parent调用，解决一级parent仍然不够的问题
     *
     * 有了它就可以愉快的写margin了，不用再因为点击区域过小而烦恼
     *
     * @param parentDeep 附着在第几层父级（注意自己层级）
     * @param sizePx     四周拓展大小
     */
    @JvmOverloads
    fun expandTouchArea(
        view: View,
        @IntRange(from = 1, to = 8) parentDeep: Int = 1,
        sizePx: Int = 10.dp
    ) {
        if (sizePx <= 0 || parentDeep <= 0 || parentDeep >= 9) {
            return
        }
        view.post {
            //最终parent的前一个
            var previousParentView: View = view
            //在最终parent的触摸范围
            val newViewRange = Rect()
            view.getHitRect(newViewRange)
            for (i in 1 until parentDeep) {
                previousParentView = previousParentView.parent as View
                when (previousParentView) {
                    is ScrollingView, is ScrollView, is ListView, is ViewPager, is ViewPager2 -> {
                        //可滑动的view都会覆盖touch，所以会失效
                        throw RuntimeException("deep不要包含可滑动的view：${previousParentView.javaClass}")
                    }
                    else -> {
                        val rect = Rect()
                        previousParentView.getHitRect(rect)
                        newViewRange.offset(rect.left, rect.top) //相对平移
                    }
                }
            }
            newViewRange.left -= sizePx
            newViewRange.top -= sizePx
            newViewRange.right += sizePx
            newViewRange.bottom += sizePx

            LinkedTouchDelegate.newDelegate(
                newViewRange,
                view,
                previousParentView.parent as View,
                parentDeep,
                sizePx
            )
        }
    }

    /**
     * @param parentDeep 必须和你之前add的一致
     */
    fun removeTouchArea(view: View, @IntRange(from = 1, to = 8) parentDeep: Int = 1) {
        if (parentDeep <= 0 || parentDeep >= 9) {
            return
        }
        var targetParent: View = view
        for (i in 0 until parentDeep) {
            targetParent = targetParent.parent as View
        }
        LinkedTouchDelegate.removeDelegate(view, targetParent)
    }
}