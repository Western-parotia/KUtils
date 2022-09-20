package com.foundation.service.utils

import android.app.Application
import com.foundation.service.utils.MjUtils.init
import com.foundation.service.utils.MjUtils.setUiScreenWidth

/**
 * 初始化类[init]
 * [setUiScreenWidth]修改dp计算时默认的宽值
 */
object MjUtils {
    internal var _app: Application? = null
    internal var isDebug = false
    internal var uiScreenWidth = 375

    fun init(app: Application, isDebug: Boolean) {
        _app = app
        this.isDebug = isDebug
    }

    /**
     * 修改dp计算时默认的宽值，默认375
     */
    fun setUiScreenWidth(width: Int) {
        uiScreenWidth = width
    }
}

internal val app get() = MjUtils._app!!
