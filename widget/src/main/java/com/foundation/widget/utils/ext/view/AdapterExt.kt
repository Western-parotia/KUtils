package com.foundation.widget.utils.ext.view

import android.view.View
import androidx.recyclerview.widget.*
import com.chad.library.adapter.base.BaseQuickAdapter
import com.foundation.widget.crvadapter.viewbinding.ViewBindingQuickAdapter
import com.foundation.widget.crvadapter.viewbinding.ViewBindingViewHolder
import com.foundation.widget.utils.MjUtils
import com.foundation.widget.utils.R
import com.foundation.widget.utils.bean.ISelectedListBean
import com.foundation.widget.utils.bean.selectedPosition
import com.foundation.widget.utils.diff.ListAdapterUpdateCallback
import com.foundation.widget.utils.diff.ListDiff
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * 加了tag，方便使用
 */
fun BaseQuickAdapter<*, *>.dispatchItemChildClick(
    view: View,
    holder: ViewBindingViewHolder<*>,
    tag: String
) {
    view.setTag(R.id.tag_adapter_view_holder, holder)
    view.setTag(R.id.tag_adapter_child_view_click, tag)
    onItemChildClickListener?.onItemChildClick(this, view, holder.getListPosition())
}

fun BaseQuickAdapter<*, *>.dispatchItemChildLongClick(
    view: View,
    holder: ViewBindingViewHolder<*>,
    tag: String
): Boolean {
    view.setTag(R.id.tag_adapter_view_holder, holder)
    view.setTag(R.id.tag_adapter_child_view_long_click, tag)
    return onItemChildLongClickListener?.onItemChildLongClick(this, view, holder.getListPosition())
        ?: false
}

/**
 * 点击事件也不写了
 */
fun BaseQuickAdapter<*, *>.setItemChildTagClick(
    view: View,
    holder: ViewBindingViewHolder<*>,
    tag: String
) {
    view.setOnShakeLessClickListener {
        dispatchItemChildClick(view, holder, tag)
    }
}

fun BaseQuickAdapter<*, *>.setItemChildTagLongClick(
    view: View,
    holder: ViewBindingViewHolder<*>,
    tag: String
) {
    view.setOnLongClickListener {
        dispatchItemChildLongClick(view, holder, tag)
    }
}

/**
 * 配合上面，tag回调
 */
fun BaseQuickAdapter<*, *>.setOnItemChildClickWithTagListener(
    listener: (View, holder: ViewBindingViewHolder<*>, tag: String) -> Unit
) {
    setOnItemChildClickListener { _, view, _ ->
        listener(
            view,
            view.getTag(R.id.tag_adapter_view_holder) as ViewBindingViewHolder<*>,
            view.getTag(R.id.tag_adapter_child_view_click) as String
        )
    }
}

fun BaseQuickAdapter<*, *>.setOnItemChildLongClickWithTagListener(
    listener: (View, holder: ViewBindingViewHolder<*>, tag: String) -> Unit
) {
    setOnItemChildLongClickListener { _, view, _ ->
        listener(
            view,
            view.getTag(R.id.tag_adapter_view_holder) as ViewBindingViewHolder<*>,
            view.getTag(R.id.tag_adapter_child_view_long_click) as String
        )
        true
    }
}

private const val CLICK_INTERVAL = 300L

/**
 * 避免快速点击注意区分代码
 * @param isDistinguishPosition 是否区分position，如果是true则不同position可连续点击
 * @receiver
 */
@JvmOverloads
fun BaseQuickAdapter<*, *>.setOnItemShakeLessClickListener(
    isDistinguishPosition: Boolean = false,
    clickInterval: Long = CLICK_INTERVAL,
    block: (view: View, position: Int) -> Unit
) {
    var timestamp = System.currentTimeMillis()
    var lastClickPosition = -1
    setOnItemClickListener { _, view, position ->
        val interval = System.currentTimeMillis() - timestamp
        if (interval >= clickInterval || (isDistinguishPosition && lastClickPosition != position)) {
            block(view, position)
        }
        timestamp = System.currentTimeMillis()
        lastClickPosition = position
    }

}

/**
 * 自动加了header的count防止忘记
 */
fun BaseQuickAdapter<*, *>.notifyListItemChanged(listPosition: Int) {
    if (listPosition < 0 || listPosition >= data.size) {
        return
    }
    notifyItemChanged(listPosition + headerLayoutCount)
}

fun <T> BaseQuickAdapter<T, *>.notifyDataItemChanged(item: T) {
    notifyListItemChanged(data.indexOf(item))
}

/**
 * 自动加了header的count防止忘记
 */
fun BaseQuickAdapter<*, *>.notifyListItemRemoved(listPosition: Int) {
    if (listPosition < 0) {
        return
    }
    notifyItemRemoved(listPosition + headerLayoutCount)
}

/**
 * 自动加了header的count防止忘记
 */
fun BaseQuickAdapter<*, *>.notifyListItemInserted(listPosition: Int) {
    if (listPosition < 0 || listPosition >= data.size) {
        return
    }
    notifyItemInserted(listPosition + headerLayoutCount)
}

/**
 * 自动加了header的count防止忘记
 */
fun BaseQuickAdapter<*, *>.removeList(listPosition: Int) {
    if (listPosition < 0 || listPosition >= data.size) {
        return
    }
    remove(listPosition + headerLayoutCount)
}

/**
 * 修改选中状态
 */
var <T : ISelectedListBean> ViewBindingQuickAdapter<*, T>.selectedPosition
    get() = data.selectedPosition
    set(value) {
        val oldPosition = data.selectedPosition
        data.selectedPosition = value
        notifyListItemChanged(oldPosition)
        notifyListItemChanged(value)
    }


/**
 * 更新adapter和data，用于替换setNewData引起的notifyDataSetChanged
 */
fun <T> BaseQuickAdapter<T, *>.setNewDiffData(
    newList: List<T>,
    rv: RecyclerView? = null,
) {
    setNewDiffData(data, newList, headerLayoutCount, rv)
}

/**
 * 更新adapter和[targetList]，用于替换notifyDataSetChanged
 *
 * @param targetList 会自行更新
 * @param offsetCount 偏移量，一般为你的header数量
 */
fun <T> RecyclerView.Adapter<*>.setNewDiffData(
    targetList: MutableList<T>,
    newList: List<T>,
    offsetCount: Int = 0,
    rv: RecyclerView? = null,
) {
    if (targetList == newList) {
        if (MjUtils.isDebug) {
            throw IllegalArgumentException("两个list相同无法对比差异")
        }
        notifyDataSetChanged()
        return
    }
    val oldList = targetList.toList()
    targetList.clear()
    targetList.addAll(newList)
    refreshDiff(oldList, newList, offsetCount, rv)
}

/**
 * 更新diff出来的item，不会修改list，建议使用[setNewDiffData]
 */
fun <T> RecyclerView.Adapter<*>.refreshDiff(
    oldList: List<T>,
    newList: List<T>,
    offsetCount: Int = 0,
    rv: RecyclerView? = null,
) {
    val lm = rv?.layoutManager
    val oldListSize = oldList.size
    val newListSize = newList.size
    val maxSize = 10_000
    if (
        (oldListSize > maxSize || newListSize > maxSize || oldListSize * newListSize > maxSize)
        && (lm is LinearLayoutManager || lm is StaggeredGridLayoutManager)
    ) {
        //如果两个数据过大，则只对比ui可见的数据以减少性能损耗
        //当然这也带来的问题：特殊情况下刷新效果可能不友好（划到最底部，删除最前面一批，会出现全部删除又出现的动画）
        //也可以根据数据命中最相似的数据范围来解决刷新不符的问题，但考虑到成本和收益暂不处理
        //（目前仅支持LinearLayoutManager、GridLayoutManager、StaggeredGridLayoutManager）

        val rangePositions =
            rv.getCachedViewArray().map { it.bindingAdapterPosition }.toMutableList()
        when (lm) {
            is LinearLayoutManager -> {//包含GridLayoutManager
                rangePositions.add(lm.findFirstVisibleItemPosition())
                rangePositions.add(lm.findLastVisibleItemPosition())
            }
            is StaggeredGridLayoutManager -> {
                lm.findFirstVisibleItemPositions(null).forEach {
                    rangePositions.add(it)
                }
                lm.findLastVisibleItemPositions(null).forEach {
                    rangePositions.add(it)
                }
            }
        }
        val startIndex = (rangePositions.minOrNull() ?: -1) - offsetCount
        val endIndex = (rangePositions.maxOrNull() ?: -1) - offsetCount
        var diffRemovedCount = 0
        if (endIndex >= 0) {
            //如果绑定了view，则处理绑定数据的差异部分
            val subStart = max(startIndex, 0)
            val oldListSubEnd = min(endIndex, oldListSize)
            val newListSubEnd = min(endIndex, newListSize)
            if (newListSubEnd >= subStart) {
                diffRemovedCount = oldListSubEnd - newListSubEnd
                DiffUtil.calculateDiff(
                    ListDiff(
                        oldList.subList(subStart, oldListSubEnd),
                        newList.subList(subStart, newListSubEnd)
                    )
                )
                    .dispatchUpdatesTo(
                        ListAdapterUpdateCallback(
                            this,
                            offsetCount + subStart
                        )
                    )
            }
        }
        //对后续数据不比较差异直接增删对应count
        val removeCount = oldListSize - newListSize - diffRemovedCount
        when {
            removeCount > 0 -> {
                notifyItemRangeRemoved(newListSize + offsetCount, removeCount)
            }
            removeCount < 0 -> {
                notifyItemRangeInserted(oldListSize + offsetCount, abs(removeCount))
            }
        }
    } else {
        DiffUtil.calculateDiff(ListDiff(oldList, newList))
            .dispatchUpdatesTo(ListAdapterUpdateCallback(this, offsetCount))
    }
}