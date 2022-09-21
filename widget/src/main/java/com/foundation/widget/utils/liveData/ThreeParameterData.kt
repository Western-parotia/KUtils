package com.foundation.widget.utils.liveData

import androidx.lifecycle.MutableLiveData
import com.foundation.widget.utils.ext.smartPost


/**
 * 可发三个数据
 * 不要再加四个数据的了，再多就难以阅读了，请自行创建对象写清楚字段名
 *
 * @作者 王能
 * @时间 2022/5/6
 */
class ThreeParameterData<DATA, T1, T2>(val data: DATA, val flowData1: T1, val flowData2: T2) {
    companion object {
        fun <DATA, T1, T2> MutableLiveData<ThreeParameterData<DATA, T1, T2>>.smartPost(
            data: DATA,
            flowData1: T1,
            flowData2: T2
        ) {
            smartPost(ThreeParameterData(data, flowData1, flowData2))
        }

        fun <DATA, T1, T2> MutableLiveData<ThreeParameterData<DATA, T1, T2>>.setValue(
            data: DATA,
            flowData1: T1,
            flowData2: T2
        ) {
            value = ThreeParameterData(data, flowData1, flowData2)
        }

        fun <DATA, T1, T2> MutableLiveData<ThreeParameterData<DATA, T1, T2>>.postValue(
            data: DATA,
            flowData1: T1,
            flowData2: T2
        ) {
            postValue(ThreeParameterData(data, flowData1, flowData2))
        }
    }
}
