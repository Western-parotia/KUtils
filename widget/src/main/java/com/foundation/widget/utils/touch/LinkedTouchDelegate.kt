package com.foundation.widget.utils.touch

import android.graphics.Rect
import android.os.Build
import android.view.MotionEvent
import android.view.TouchDelegate
import android.view.View
import androidx.core.view.isVisible

/**
 * 多个Delegate可共用同一个parent
 * @param sharedList 共享集合，倒序，从最后一个开始
 */
internal class LinkedTouchDelegate private constructor(
    private val bounds: Rect,
    private val delegateView: View,
    private val sharedList: ArrayList<LinkedTouchDelegate>,
    private val targetParent: View,
    private val sizePx: Int
) : TouchDelegate(bounds, delegateView) {
    private val mOnLayoutChangeListener = View.OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
        TouchUtils.expandTouchArea(delegateView, targetParent, sizePx)
    }

    companion object {
        /**
         * 添加事件代理
         */
        fun newDelegate(
            bounds: Rect,
            delegateView: View,
            targetParent: View,
            sizePx: Int
        ) {
            //取集合
            val list = (targetParent.touchDelegate as? LinkedTouchDelegate)?.sharedList
                ?: ArrayList(2)

            val index = list.indexOfFirst { it.delegateView == delegateView }
            if (index < 0) {
                //没有就添加到最后
                list.add(
                    LinkedTouchDelegate(
                        bounds,
                        delegateView,
                        list,
                        targetParent,
                        sizePx
                    )
                )
            } else {
                val old = list[index]
                if (old.bounds == bounds && old.targetParent == targetParent && old.sizePx == sizePx) {
                    //完全一致，不需要动
                    return
                }
                //不一致则改掉
                old.targetParent.removeOnLayoutChangeListener(old.mOnLayoutChangeListener)
                list[index] = LinkedTouchDelegate(
                    bounds,
                    delegateView,
                    list,
                    targetParent,
                    sizePx
                )
            }
            targetParent.touchDelegate = list.lastOrNull()
        }

        /**
         * 删除事件代理
         */
        fun removeDelegate(delegateView: View, targetParent: View) {
            (targetParent.touchDelegate as? LinkedTouchDelegate)?.sharedList?.let { sharedList ->
                val index = sharedList.indexOfFirst { it.delegateView == delegateView }
                if (index >= 0) {
                    val old = sharedList.removeAt(index)
                    old.targetParent.removeOnLayoutChangeListener(old.mOnLayoutChangeListener)
                    targetParent.touchDelegate = sharedList.lastOrNull()
                }
            }
        }
    }

    init {
        //当布局变化时跟随刷新
        targetParent.addOnLayoutChangeListener(mOnLayoutChangeListener)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            fixDelegateFieldBug()
        }
        val isInterceptor: Boolean = delegateView.isVisible && super.onTouchEvent(event)
        if (!isInterceptor) {
            val next = sharedList.indexOfFirst { it.delegateView == delegateView } - 1
            if (next >= 0) {
                return sharedList[next].onTouchEvent(event)
            }
        }
        return isInterceptor
    }

    private fun fixDelegateFieldBug() {
        //O以下mDelegateTargeted忘了置成false，这里反射修正
        if (Build.VERSION.SDK_INT < 28) {
            try {
                val dtFiled = TouchDelegate::class.java.getDeclaredField("mDelegateTargeted")
                dtFiled.isAccessible = true
                dtFiled.setBoolean(this, false)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}