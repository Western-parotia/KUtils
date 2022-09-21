package com.foundation.widget.utils

import android.net.Uri
import androidx.collection.ArraySet
import androidx.core.net.toUri
import com.foundation.widget.utils.ext.global.removeIfIterator

/**
 * uri拼接相关操作
 */
class UriSplice(uri: String) {

    private val allParameterKeySet = ArraySet<String>()
    private val uriBuilder: Uri.Builder

    init {
        //基本容错处理，去掉空白字和结尾的&
        var uriTemp = uri.trim()
        if (uriTemp.endsWith("&")) {
            uriTemp = uriTemp.substring(0, uriTemp.length - 1)
        }

        val newUri = uriTemp.toUri()
        allParameterKeySet.addAll(newUri.queryParameterNames)
        //系统bug，带?会解析出空字符串
        allParameterKeySet.removeIfIterator { it.isEmpty() }
        uriBuilder = newUri.buildUpon()
    }

    /**
     * 没有才会添加
     * @param value null当作空字符串
     */
    fun addParameterIfNo(key: String, value: String?) {
        if (!allParameterKeySet.contains(key)) {
            allParameterKeySet.add(key)
            uriBuilder.appendQueryParameter(key, value ?: "")
        }
    }

    /**
     * 如果有则替换
     */
    fun addParameterReplace(key: String, value: String?) {
        if (allParameterKeySet.contains(key)) {
            val newUri = toUri()
            val parameterMap = hashMapOf<String, String?>()
            //重新保存所有的k、v
            allParameterKeySet.forEach {
                if (key == it) {
                    parameterMap[it] = value
                } else {
                    parameterMap[it] = newUri.getQueryParameter(it)
                }
            }

            clearParameter()
            //逐个重新添加
            parameterMap.forEach { (k, v) ->
                addParameterIfNo(k, v)
            }
        } else {
            addParameterIfNo(key, value)
        }
    }

    fun clearParameter() {
        allParameterKeySet.clear()
        uriBuilder.clearQuery()
    }

    /**
     * 直接修改掉整个path（/）的值
     * 会进行url转码
     *
     * 不转码方法：uriBuilder.encodedPath(path)（应该没用处）
     */
    fun setPath(path: String) {
        uriBuilder.path(path)
    }

    /**
     * 再加一层path
     * 会进行url转码
     */
    fun appendPath(path: String) {
        uriBuilder.appendPath(path)
    }

    fun toUri(): Uri = uriBuilder.build()

    override fun toString(): String = uriBuilder.toString()
}