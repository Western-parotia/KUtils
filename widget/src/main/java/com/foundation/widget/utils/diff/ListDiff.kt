package com.foundation.widget.utils.diff

import androidx.recyclerview.widget.DiffUtil

class ListDiff<T>(private val oldList: List<T>, private val newList: List<T>) :
    DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val od = oldList.getOrNull(oldItemPosition)
        val nd = newList.getOrNull(newItemPosition)
        if (od == null || nd == null) {
            return false
        }
        if (od is IDiffId && nd is IDiffId) {
            return od.diffId == nd.diffId
        }
        return od == nd
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val od = oldList.getOrNull(oldItemPosition)
        val nd = newList.getOrNull(newItemPosition)
        return od == nd
    }
}