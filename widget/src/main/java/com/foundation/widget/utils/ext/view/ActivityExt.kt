package com.foundation.widget.utils.ext.view

import androidx.fragment.app.FragmentActivity
import com.foundation.widget.utils.ui.IUIContext

/**
 * create by zhusw on 6/9/21 18:29
 */

fun FragmentActivity.toUIContext() = IUIContext.createWithActivity(this)