package com.foundation.widget.utils

import android.app.Application
import com.foundation.widget.utils.MjUtils.init
import com.foundation.widget.utils.MjUtils.setUiScreenWidth

/**
 * 初始化类[init]
 * [setUiScreenWidth]修改dp计算时默认的宽值
 */
object MjUtils {
    private var _app: Application? = null
    private var _isDebug = false
    internal var uiScreenWidth = 375

    val app get() = _app!!
    val isDebug get() = _isDebug

    fun init(app: Application) {
        _app = app
    }

    /**
     * 设置全局debug状态
     */
    fun setDebug(isDebug: Boolean) {
        this._isDebug = isDebug
    }

    /**
     * 修改dp计算时默认的宽值，默认375
     */
    fun setUiScreenWidth(width: Int) {
        uiScreenWidth = width
    }
}
