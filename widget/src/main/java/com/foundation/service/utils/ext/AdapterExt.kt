package com.foundation.service.utils.ext

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.foundation.widget.crvadapter.viewbinding.ViewBindingViewHolder
import com.foundation.widget.utils.R

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
    onItemChildClickListener?.onItemChildClick(
        holder.bindingAdapter as? BaseQuickAdapter<*, *>,
        view,
        holder.getListPosition()
    )
}

fun BaseQuickAdapter<*, *>.dispatchItemChildLongClick(
    view: View,
    holder: ViewBindingViewHolder<*>,
    tag: String
) {
    view.setTag(R.id.tag_adapter_view_holder, holder)
    view.setTag(R.id.tag_adapter_child_view_long_click, tag)
    onItemChildLongClickListener?.onItemChildLongClick(
        holder.bindingAdapter as? BaseQuickAdapter<*, *>,
        view,
        holder.getListPosition()
    )
}


/**
 * 点击事件也不写了
 */
fun BaseQuickAdapter<*, *>.setItemChildTagClick(
    view: View,
    holder: ViewBindingViewHolder<*>,
    tag: String
) {
    view.setTag(R.id.tag_adapter_view_holder, holder)
    view.setTag(R.id.tag_adapter_child_view_click, tag)
    view.setOnShakeLessClickListener {
        onItemChildClickListener?.onItemChildClick(
            holder.bindingAdapter as? BaseQuickAdapter<*, *>,
            view,
            holder.getListPosition()
        )
    }
}


fun BaseQuickAdapter<*, *>.setItemChildTagLongClick(
    view: View,
    holder: ViewBindingViewHolder<*>,
    tag: String
) {
    view.setTag(R.id.tag_adapter_view_holder, holder)
    view.setTag(R.id.tag_adapter_child_view_long_click, tag)
    view.setOnLongClickListener {
        onItemChildLongClickListener?.onItemChildLongClick(
            holder.bindingAdapter as? BaseQuickAdapter<*, *>,
            view,
            holder.getListPosition()
        )
        return@setOnLongClickListener true
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
 * 避免快速点击
 * @param block
 * @receiver
 */
fun BaseQuickAdapter<*, *>.setOnItemShakeLessClickListener(
    clickInterval: Long = CLICK_INTERVAL,
    block: (view: View, position: Int) -> Unit
) {
    var timestamp = System.currentTimeMillis()
    setOnItemClickListener { _, view, position ->
        val interval = System.currentTimeMillis() - timestamp
        if (interval >= clickInterval) {
            block(view, position)
        }
        timestamp = System.currentTimeMillis()
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