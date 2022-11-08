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
    private var _isDebug: Boolean? = null
    internal var uiScreenWidth = 375

    val app get() = _app ?: throw UninitializedPropertyAccessException("未调用init")

    val isInit: Boolean
        get() = _app != null

    val isDebug: Boolean
        get() {
            _isDebug?.let {
                return it
            }

            //如果没有设置debug状态，将尝试反射获取
            if (_app != null) {
                try {
                    val bcCls = Class.forName("${app.packageName}.BuildConfig")
                    val bcField = bcCls.getField("DEBUG")
                    (bcField.get(null) as? Boolean)?.let {
                        _isDebug = it
                        return it
                    }
                } catch (ignore: Exception) {
                }
            }

            //没获取到默认false
            _isDebug = false
            return false
        }

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
