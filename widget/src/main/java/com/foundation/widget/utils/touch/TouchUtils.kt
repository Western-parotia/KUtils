package com.foundation.widget.utils.touch

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.AdapterView
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import androidx.annotation.IntRange
import androidx.core.view.ScrollingView
import androidx.core.view.doOnLayout
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
     * @throws ClassCastException 到root了还是没有找到
     * @throws IllegalArgumentException 不正确的传参，如：中间包含可滑动的view
     */
    @JvmOverloads
    fun expandTouchArea(
        expandView: View,
        @IntRange(from = 1, to = 8) parentDeep: Int = 1,
        sizePx: Int = 10.dp
    ) {
        if (parentDeep <= 0 || parentDeep >= 9) {
            return
        }
        val parentView = run {
            var p = expandView
            for (i in 1..parentDeep) {
                p = p.parent as View
            }
            p
        }
        expandTouchArea(expandView, parentView, sizePx)
    }

    /**
     * 拓展在可滑动view之下（或者到最外层view停止）
     */
    @JvmOverloads
    fun expandTouchAreaForScrollingView(
        expandView: View,
        sizePx: Int = 10.dp
    ) {
        if (sizePx <= 0) {
            return
        }
        var parentView = expandView
        while (true) {
            parentView = parentView.parent as View
            when (parentView) {
                //到可滑动的view停止
                is ScrollingView, is ScrollView, is HorizontalScrollView, is AdapterView<*>, is ViewPager, is ViewPager2 -> {
                    expandTouchArea(expandView, parentView, sizePx)
                    return
                }
                else -> {
                    //或者到最外层view停止（一般是没有add）
                    if (parentView.parent !is View) {
                        expandTouchArea(expandView, parentView, sizePx)
                        return
                    }
                }
            }
        }
    }

    @JvmOverloads
    fun expandTouchArea(
        expandView: View,
        parentView: View,
        sizePx: Int = 10.dp
    ) {
        if (parentView == expandView) {
            throw IllegalArgumentException("parent就是自己，无法拓展")
        }
        if (sizePx <= 0) {
            return
        }
        parentView.doOnLayout {
            //循环遍历，临时parent
            var pv = expandView
            //在最终parent的触摸范围
            val newViewRange = Rect()
            expandView.getHitRect(newViewRange)
            while (true) {
                pv = pv.parent as View
                if (pv == parentView) {
                    break
                }
                when (pv) {
                    is ScrollingView, is ScrollView, is HorizontalScrollView, is AdapterView<*>, is ViewPager, is ViewPager2 -> {
                        //可滑动的view都会覆盖touch，所以会失效
                        throw IllegalArgumentException("deep不要包含可滑动的view：${pv.javaClass}")
                    }
                    else -> {
                        val rect = Rect()
                        pv.getHitRect(rect)
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
                expandView,
                parentView,
                sizePx
            )
        }
    }

    fun removeTouchArea(view: View) {
        var targetParent: ViewParent? = view.parent
        while (targetParent is ViewGroup) {
            LinkedTouchDelegate.removeDelegate(view, targetParent)
            targetParent = targetParent.parent
        }
    }
}