package com.foundation.widget.utils.ext.view

import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.foundation.widget.utils.R
import com.foundation.widget.utils.decoration.SpacesItemDecoration
import com.foundation.widget.utils.ext.global.dp
import kotlin.math.abs

/**
 * rv点击事件拓展
 * @param listener null表示删除
 */
fun RecyclerView.setOnClickListenerByTouch(listener: View.OnClickListener?) {
    //删除以前的
    (getTag(R.id.tag_rv_click) as? RecyclerView.OnItemTouchListener)?.let {
        removeOnItemTouchListener(it)
    }
    setOnClickListener(listener)

    if (listener == null) {
        return
    }
    val newListener = object : RecyclerView.OnItemTouchListener {
        private var downX = 0f
        private var downY = 0f
        private var downMillis = 0L
        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            when (e.action) {
                MotionEvent.ACTION_UP -> {
                    val upX = e.x
                    val upY = e.y
                    val now = System.currentTimeMillis()
                    if (abs(upX - downX) < 10.dp && abs(upY - downY) < 10.dp && now - downMillis < 1000) {
                        performClick()
                    }
                }
                MotionEvent.ACTION_DOWN -> {
                    downX = e.x
                    downY = e.y
                    downMillis = System.currentTimeMillis()
                }
            }
            return false
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        }
    }
    addOnItemTouchListener(newListener)
    setTag(R.id.tag_rv_click, newListener)
}

/**
 * 如果class相等则删除旧的（主要用于复用里的rv）
 * [SpacesItemDecoration]
 */
fun RecyclerView.replaceItemDecorationWithClass(decor: RecyclerView.ItemDecoration) {
    if (itemDecorationCount > 0) {
        ((itemDecorationCount - 1)..0).forEach {
            val oldDecor = getItemDecorationAt(it)
            if (oldDecor == decor) {
                //完全相等就不需要重复设置了
                return
            }
            if (oldDecor.javaClass == decor.javaClass) {
                removeItemDecorationAt(it)
            }
        }
    }
    addItemDecoration(decor)
}