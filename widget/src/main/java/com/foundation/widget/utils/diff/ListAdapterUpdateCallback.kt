package com.foundation.widget.utils.diff

import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView

/**
 * @param offsetCount 偏移量，一般为你的header数量或者局部刷新的偏移值
 */
class ListAdapterUpdateCallback(
    private val adapter: RecyclerView.Adapter<*>,
    private val offsetCount: Int = 0
) : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {
        adapter.notifyItemRangeInserted(position + offsetCount, count)
    }

    override fun onRemoved(position: Int, count: Int) {
        adapter.notifyItemRangeRemoved(position + offsetCount, count)
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        adapter.notifyItemMoved(fromPosition + offsetCount, toPosition + offsetCount)
    }

    override fun onChanged(position: Int, count: Int, payload: Any?) {
        adapter.notifyItemRangeChanged(position + offsetCount, count, payload)
    }
}