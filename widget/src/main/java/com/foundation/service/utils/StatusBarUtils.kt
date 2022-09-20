package com.foundation.service.utils

import android.app.Activity
import android.os.Build
import android.view.View
import com.foundation.service.utils.ext.dp

/**
 * Created by victor on 2018/3/14.
 * Email : victorhhl@163.com
 * Description :
 */
object StatusBarUtils {

    /**
     * 默认的，请使用[statusBarHeight]
     */
    private var defStatusBarHeight: Int = -1
        get() {
            if (field < 0) {
                val res = app.resources
                val resId = res.getIdentifier("status_bar_height", "dimen", "android")
                field = res.getDimensionPixelSize(resId)
            }
            return field
        }

    /**
     * 获取状态栏高度
     */
    @JvmStatic
    val statusBarHeight: Int
        get() {
            return if (defStatusBarHeight > 0) defStatusBarHeight else 25.dp
        }

    /**
     * 需要手动配主题，参考[SplashActivity]、[TranslucentActivity]的主题
     * @param isDark true字体黑色，false 字体白色
     */
    @JvmStatic
    fun switchStatusBarTextColor(activity: Activity?, isDark: Boolean) {
        if (null == activity) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decorView = activity.window.decorView
            val vis = decorView.systemUiVisibility
            val newVis =
                if (isDark) vis or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR else vis and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            decorView.systemUiVisibility = newVis
        }
    }
}