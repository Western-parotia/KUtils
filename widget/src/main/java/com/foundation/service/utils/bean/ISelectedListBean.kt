package com.foundation.service.utils.bean

import androidx.annotation.IntRange
import com.foundation.service.utils.ext.global.allTrue
import com.foundation.service.utils.ext.global.removeIfReverseSequence

/**
 * 基于后端服务器返回，继承即可
 * 可以方便使用下方list.selected...的拓展
 */
interface ISelectedListBean {
    var isSelected: Boolean

    /**
     * 取反
     */
    fun inverterSelected() {
        isSelected = !isSelected
    }
}

/**
 * 当前选择的position（单选逻辑）
 */
var <T : ISelectedListBean> List<T>?.selectedPosition: Int
    set(value) {
        this?.forEachIndexed { index, bean ->
            bean.isSelected = index == value
        }
    }
    @IntRange(from = -1L)
    get() {
        this?.forEachIndexed { index, bean ->
            if (bean.isSelected) {
                return index
            }
        }
        return -1
    }

/**
 * 根据bean选中（单选逻辑）
 */
fun <T : ISelectedListBean> List<T>?.setSelected(bean: T) {
    this?.forEach {
        it.isSelected = it == bean
    }
}

/**
 * 全选（多选逻辑）
 */
fun <T : ISelectedListBean> List<T>?.selectedAll() {
    this?.forEach {
        it.isSelected = true
    }
}

/**
 * 获取选择的那条数据（单选逻辑）
 */
fun <T : ISelectedListBean> List<T>?.getSelectedData(): T? {
    return this?.getOrNull(selectedPosition)
}

/**
 * 获取全部选中的数据（多选逻辑）
 */
fun <T : ISelectedListBean> List<T>?.getAllSelectData(): ArrayList<T> {
    val list = arrayListOf<T>()
    this?.forEach {
        if (it.isSelected) {
            list.add(it)
        }
    }
    return list
}

/**
 * 获取选择的数量（多选逻辑）
 */
val <T : ISelectedListBean> List<T>?.selectCount: Int
    get() {
        var count: Int = 0
        this?.forEach {
            if (it.isSelected) {
                count++
            }
        }
        return count
    }

/**
 * 重置为未选中状态
 */
fun <T : ISelectedListBean> List<T>?.reseatData() {
    this?.forEach {
        it.isSelected = false
    }
}

/**
 * 是否有选中
 * @return true至少有一个选中
 */
fun <T : ISelectedListBean> List<T>?.hasSelected() = selectedPosition >= 0

/**
 * 是否全选（多选逻辑）
 */
fun <T : ISelectedListBean> List<T>?.isAllSelected() = this.allTrue { it.isSelected }

/**
 * 指定item取反
 */
fun <T : ISelectedListBean> List<T>?.inverterSelected(index: Int) {
    this?.getOrNull(index)?.inverterSelected()
}

/**
 * 删除未选中的数据
 */
fun <T : ISelectedListBean> MutableList<T>?.removeUnselectData() {
    this?.removeIfReverseSequence { _, item -> !item.isSelected }
}