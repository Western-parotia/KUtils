package com.foundation.service.utils.liveData

import androidx.lifecycle.MutableLiveData
import com.foundation.service.utils.ext.smartPost

/**
 * 可发两个数据，主要用于数据流转时，需要声明成员变量，没有唯一可信源的问题
 *
 * @作者 王能
 * @时间 2022/5/6
 *
 * 以前：
 * var selectedPosition = -1
 * ...
 * //请求数据
 * selectedPosition = holder.getListPosition()
 * vm.loadData()
 * ...
 * //获取数据
 * vm.data.observe(this) {
 *     if(selectedPosition>=0){
 *         adapter.notify...
 *     }
 * }
 *
 * 现在:
 * //请求数据
 * vm.loadData(holder.getListPosition())
 * ...
 * //获取数据
 * vm.data.observe(this) {
 *     adapter.notify...(it.flowData)
 * }
 */
class TwoParameterData<DATA, T>(val data: DATA, val flowData: T) {
    companion object {
        fun <DATA, T> MutableLiveData<TwoParameterData<DATA, T>>.smartPost(
            data: DATA,
            flowData: T
        ) {
            smartPost(TwoParameterData(data, flowData))
        }

        fun <DATA, T> MutableLiveData<TwoParameterData<DATA, T>>.setValue(
            data: DATA,
            flowData: T
        ) {
            value = TwoParameterData(data, flowData)
        }

        fun <DATA, T> MutableLiveData<TwoParameterData<DATA, T>>.postValue(
            data: DATA,
            flowData: T
        ) {
            postValue(TwoParameterData(data, flowData))
        }
    }
}
