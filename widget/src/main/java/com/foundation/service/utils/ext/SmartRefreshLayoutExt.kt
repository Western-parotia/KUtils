@file:Suppress("PackageDirectoryMismatch")//同一包名方便得到protect属性

package com.scwang.smart.refresh.layout

import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener

fun SmartRefreshLayout.setOnRefreshLoadMoreListener(
    refreshListener: () -> Unit,
    loadMoreListener: () -> Unit
) {
    setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
        override fun onRefresh(refreshLayout: RefreshLayout) {
            refreshListener()
        }

        override fun onLoadMore(refreshLayout: RefreshLayout) {
            loadMoreListener()
        }
    })
}

/**
 * 上拉下拉都关掉
 */
fun SmartRefreshLayout.finishRefreshLoadMore() {
    finishRefresh()
    finishLoadMore()
}

var SmartRefreshLayout.isEnabledRefresh
    get() = mEnableRefresh
    set(value) {
        setEnableRefresh(value)
    }

var SmartRefreshLayout.isEnabledLoadMore
    get() = mEnableLoadMore
    set(value) {
        setEnableLoadMore(value)
    }