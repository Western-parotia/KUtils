package com.foundation.widget.utils.ext.view

import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.foundation.widget.utils.R

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
        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            if (e.action == MotionEvent.ACTION_UP) {
                performClick()
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