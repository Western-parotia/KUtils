package com.foundation.widget.utils.ext.view

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.foundation.widget.crvadapter.viewbinding.ViewBindingQuickAdapter
import com.foundation.widget.crvadapter.viewbinding.ViewBindingViewHolder
import com.foundation.widget.utils.R
import com.foundation.widget.utils.bean.ISelectedListBean
import com.foundation.widget.utils.bean.selectedPosition

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