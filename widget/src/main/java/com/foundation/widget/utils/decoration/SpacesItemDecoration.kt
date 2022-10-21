package com.foundation.widget.utils.decoration

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/**
 * 加间隔的
 * 注意：暂时没有处理header、footer，后续需要时添加
 * 如果效果不太好，你可以使用rv的margin+item的margin来实现
 * @param space 两个之间的间隔
 * @param isLastSpace 最后是否留白（用于分页加载：上拉加载时不会刷新最后一个，如果不提前留好会比较难看）
 */
class SpacesItemDecoration(@Px private val space: Int, private val isLastSpace: Boolean = false) :
    ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val manager = parent.layoutManager
        if (manager == null || space <= 0) {
            return
        }

        val spanCount: Int
        val orientation: Int
        when (manager) {
            is GridLayoutManager -> {
                orientation = manager.orientation
                spanCount = manager.spanCount
            }
            is LinearLayoutManager -> {
                orientation = manager.orientation
                spanCount = 1
            }
            else -> {
                return
            }
        }

        val position = parent.getChildAdapterPosition(view)

        val maxLine = ((parent.adapter?.itemCount ?: 0) + spanCount - 1) / spanCount
        val line = (position + spanCount) / spanCount//从1开始
        val column = position % spanCount + 1//从1开始

        val gapWidth2 = space / 2
        val emptyLeft: Boolean
        val emptyRight: Boolean
        val emptyTop: Boolean
        val emptyBottom: Boolean
        if (orientation == RecyclerView.HORIZONTAL) {
            emptyLeft = line == 1
            emptyTop = column == spanCount
            emptyRight = if (isLastSpace) false else line == maxLine
            emptyBottom = column == 1
        } else {
            emptyLeft = column == 1
            emptyTop = line == 1
            emptyRight = column == spanCount
            emptyBottom = if (isLastSpace) false else line == maxLine
        }

        outRect.set(
            if (emptyLeft) 0 else gapWidth2,
            if (emptyTop) 0 else gapWidth2,
            if (emptyRight) 0 else gapWidth2,
            if (emptyBottom) 0 else gapWidth2
        )
    }

    override fun equals(other: Any?): Boolean {
        if (other is SpacesItemDecoration) {
            return other.space == space && other.isLastSpace == isLastSpace
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = space
        result = 31 * result + isLastSpace.hashCode()
        return result
    }
}