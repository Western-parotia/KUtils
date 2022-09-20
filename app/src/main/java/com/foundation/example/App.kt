package com.foundation.example

import android.app.Application
import android.widget.Toast
import com.foundation.service.utils.MjUtils

/**
 * @作者 王能
 * @时间 2022/4/28
 */
class App : Application() {
    companion object {
        var _app: App? = null
        val app get() = _app!!
    }

    override fun onCreate() {
        super.onCreate()
        _app = this
        MjUtils.init(this, BuildConfig.DEBUG)
    }
}

fun String.toast() {
    Toast.makeText(App.app, this, Toast.LENGTH_SHORT)
        .show()
}

fun getApplication() = App.app