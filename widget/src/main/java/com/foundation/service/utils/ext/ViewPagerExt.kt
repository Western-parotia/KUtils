package com.foundation.service.utils.ext

import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

/**
 * 注册pager监听
 */
fun ViewPager2.registerOnPageChangeCallback(
    onScrolled: ((position: Int, positionOffset: Float, positionOffsetPixels: Int) -> Unit)? = null,
    onScrollStateChanged: ((state: Int) -> Unit)? = null,
    onSelected: ((position: Int) -> Unit)? = null,
): ViewPager2.OnPageChangeCallback {
    val callback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            onScrolled?.invoke(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            onScrollStateChanged?.invoke(state)
        }

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            onSelected?.invoke(position)
        }
    }
    registerOnPageChangeCallback(callback)
    return callback
}

/**
 * 将vp或rv的高/宽与第一个child高/宽一致（相当于wrap）
 * 仅会设置一次，如果发生变化，请自行更新
 * @param isWidth 修改宽还是高
 */
fun RecyclerView.wrapToChild(isWidth: Boolean) {
    wrap(this, this, isWidth)
}

/**
 * 将vp或rv的高/宽与第一个child高/宽一致（相当于wrap）
 * 仅会设置一次，如果发生变化，请自行更新
 * @param isWidth 修改宽还是高
 */
fun ViewPager2.wrapToChild(isWidth: Boolean) {
    val rv = getChildAt(0) as RecyclerView
    wrap(this, rv, isWidth)
}

private fun wrap(vg: ViewGroup, rv: RecyclerView, isWidth: Boolean) {
    rv.post {
        if (rv.childCount > 0) {
            setMeasureSize(vg, rv.getChildAt(0), isWidth)
        } else {
            rv.addOnChildAttachStateChangeListener(object :
                RecyclerView.OnChildAttachStateChangeListener {
                override fun onChildViewAttachedToWindow(view: View) {
                    if (rv.childCount > 0) {
                        rv.removeOnChildAttachStateChangeListener(this)
                        setMeasureSize(vg, rv.getChildAt(0), isWidth)
                    }
                }

                override fun onChildViewDetachedFromWindow(view: View) {}
            })
        }
    }
}

private fun setMeasureSize(vg: ViewGroup, child: View, isWidth: Boolean) {
    child.post {
        val dm = Resources.getSystem().displayMetrics
        if (isWidth) {
            child.measure(
                View.MeasureSpec.makeMeasureSpec(dm.widthPixels * 5, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(child.measuredHeight, View.MeasureSpec.EXACTLY)
            )
            vg.layoutParams.width = child.measuredWidth + vg.paddingLeft + vg.paddingRight
        } else {
            child.measure(
                View.MeasureSpec.makeMeasureSpec(child.measuredWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(dm.heightPixels * 5, View.MeasureSpec.AT_MOST)
            )
            vg.layoutParams.height = child.measuredHeight + vg.paddingTop + vg.paddingBottom
        }
        vg.layoutParams = vg.layoutParams
    }
}