package com.foundation.service.utils

import androidx.annotation.IntRange
import com.foundation.service.utils.ext.postMain
import com.foundation.service.utils.ext.removeGlobalRunnable
import java.io.Serializable

/**
 * 分页类
 * @param defPageSize PAGE_SIZE_AUTO表示自动变化（拉的越多，变得越大）
 */
class MjPage(@IntRange(from = 0) private val defPageSize: Int = PAGE_SIZE_AUTO) : Serializable {

    companion object {
        const val PAGE_SIZE_DEFAULT = 10
        const val PAGE_SIZE_AUTO = 0

        /**
         * 需要传pageStart时第一页的默认值
         */
        const val PAGE_START_DEFAULT = "0"
    }

    private var defPageNum = 1

    /**
     * 记录下一次是否是刷新全部[setRefreshAllState]
     */
    private var refreshAllState = 0

    /**
     * 是否是初始化状态
     * 如果使用[nextPage]或[pageNumber]将自动为false
     */
    var isInit = true
        private set

    @Transient
    private val changeInit = Runnable { isInit = false }

    val pageNumber: Int
        get() {
            if (refreshAllState != 0) {
                refreshAllState = refreshAllState and 0x1
                return 1
            }
            if (isInit) {
                postMain(run = changeInit)//延时设置为false
            }
            return internalPageNum()
        }

    val pageSize: Int
        get() {
            val internalPageSize = internalPageSize()
            if (refreshAllState != 0) {
                refreshAllState = refreshAllState and 0x10
                return internalPageNum() * internalPageSize
            }
            return internalPageSize
        }

    /**
     * 重置为1
     */
    fun reset() {
        defPageNum = 1
    }

    /**
     * 重置为1，并且状态改为init
     */
    fun reInit() {
        removeGlobalRunnable(changeInit)
        isInit = true
        reset()
    }

    fun nextPage() {
        defPageNum++
        isInit = false
    }

    /**
     * 回退
     */
    fun previousPage() {
        if (defPageNum > 1) {
            defPageNum--
        }
    }

    /**
     * 方便统一方法时当参数调用
     */
    fun changeState(state: MjPageState?): MjPage {
        when (state) {
            MjPageState.STATE_RESET -> reset()
            MjPageState.STATE_RE_INIT -> reInit()
            MjPageState.STATE_NEXT_PAGE -> nextPage()
            MjPageState.STATE_PREVIOUS_PAGE -> previousPage()
        }
        return this
    }

    fun isFirst() = defPageNum == 1

    /**
     * 标记刷新所有条目（解决刷新时页数从1开始的体验问题）
     * 调用[pageNumber]、[pageSize]后会自动恢复
     *
     *
     * 如现在是5,20，则调用toMap时会传1,100来刷新所有
     */
    @Deprecated("实验性api，受数据量影响，需要确定")
    fun setRefreshAllState(): MjPage {
        refreshAllState = 0x11
        return this
    }

    private fun internalPageNum(): Int {
        var newPageNum = defPageNum
        if (defPageSize == PAGE_SIZE_AUTO) {
            var newPageSize = PAGE_SIZE_DEFAULT
            while (newPageNum > 8 && newPageSize < PAGE_SIZE_DEFAULT * 8) {
                newPageNum -= 4
                newPageSize *= 2
            }
        }
        return newPageNum
    }

    private fun internalPageSize(): Int {
        var newPageSize = defPageSize
        if (defPageSize == PAGE_SIZE_AUTO) {
            var newPageNum = defPageNum
            newPageSize = PAGE_SIZE_DEFAULT
            while (newPageNum > 8 && newPageSize < PAGE_SIZE_DEFAULT * 8) {
                newPageNum -= 4
                newPageSize *= 2
            }
        }
        return newPageSize
    }
}

enum class MjPageState {
    STATE_RESET, STATE_RE_INIT, STATE_NEXT_PAGE, STATE_PREVIOUS_PAGE
}