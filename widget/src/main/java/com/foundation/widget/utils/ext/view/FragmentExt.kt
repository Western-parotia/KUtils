package com.foundation.widget.utils.ext.view

import androidx.fragment.app.Fragment
import com.foundation.widget.utils.ui.IUIContext

fun Fragment.toUIContext() = IUIContext.createWithFragment(this)