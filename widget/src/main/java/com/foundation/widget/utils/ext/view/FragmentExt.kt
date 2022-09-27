package com.foundation.widget.utils.ext.view

import androidx.fragment.app.Fragment
import com.foundation.widget.utils.ui.IUIContext

fun Fragment.toUIContext() =
    if (this is IUIContext) this else IUIContext.createWithFragment(this)