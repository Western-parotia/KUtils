package com.foundation.widget.utils.ext.view

import android.webkit.ValueCallback
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.lifecycle.LifecycleOwner
import com.foundation.widget.utils.ext.doOnDestroyed
import com.foundation.widget.utils.ext.global.log
import com.google.gson.JsonPrimitive

fun WebView.refreshTitle(callback: (title: String) -> Unit) {
    postDelayedLifecycle(500) {
        val tagTitle: String = title ?: ""
        val urlStr = url ?: ""
        val pageTitle = if (tagTitle.isNotEmpty() && !urlStr.contains(tagTitle)) {
            tagTitle
        } else {
            null
        }
        "getTitle:$title  pageTitle:$pageTitle url:$url".log("Web")
        pageTitle?.let {
            callback(it)
        }
    }
}

/**
 * 默认配置
 * @param owner 监听destroy时销毁
 */
fun WebView.settingsDefault(owner: LifecycleOwner) {
    val set = settings
    set.javaScriptEnabled = true
    set.setAppCacheEnabled(false)
    set.loadWithOverviewMode = true //避免白屏
    set.domStorageEnabled = true //支持页面存储
    set.setSupportMultipleWindows(false)
    set.defaultTextEncodingName = "UTF-8"
    set.javaScriptCanOpenWindowsAutomatically = true
    set.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW//允许http和https混合
    isHorizontalScrollBarEnabled = false
    isVerticalScrollBarEnabled = false
    //加默认app标识
    set.userAgentString = "${set.userAgentString} isFromXPXApp"

    owner.lifecycle.doOnDestroyed {
        try {
            resetAll()
            destroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

/**
 * 复原重置
 */
fun WebView.resetAll() {
    stopLoading()
    clearHistory()
    clearCache(true)
    loadUrl("about:blank")
}

/**
 * 调用js方法
 * @param parameters 暂时只支持string
 * @param parametersEscape 是否将参数转义（如{"a":1}就必须转义）
 * @param resultCallback null表示无返回值
 */
@JvmOverloads
fun WebView.callJS(
    functionSt: String,
    vararg parameters: String,
    parametersEscape: Boolean = true,
    resultCallback: ValueCallback<String>? = null
) {
    val builder = StringBuilder()
    parameters.forEach {
        val parameter = if (parametersEscape) {
            JsonPrimitive(it).toString()//会变成"{\"a\":1...}"
        } else {
            it
        }
        if (builder.isNotEmpty()) {
            builder.append(",")
        }
//        兼容逻辑，如果开头有"则不添加
        if (parameter.startsWith("\"")) {
            builder.append(parameter)
        } else {
            builder.append("\"").append(parameter).append("\"")
        }
    }
    val callSt: String
    if (resultCallback == null) {
        callSt = "javascript:${functionSt}(${builder})"
        loadUrl(callSt)
    } else {
        callSt = "${functionSt}(${builder})"
        evaluateJavascript(callSt, resultCallback)
    }
    "js调用方法: callSt".log("Web")
}