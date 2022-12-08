package com.foundation.widget.utils.ui

import com.foundation.widget.utils.ext.global.log
import com.foundation.widget.utils.ext.view.doOnDestroyed
import java.io.Serializable

private object UNINITIALIZED_VALUE

/**
 * 跟着ui的生命周期走，当destroy时会销毁，当再次create时会创建新的
 */
class UILazyDelegate<out T>(
    private val ui: IUIContext,
    private val initializer: () -> T
) : Lazy<T>, Serializable {
    private var _value: Any? = UNINITIALIZED_VALUE

    override val value: T
        get() {
            if (!isInitialized()) {
                _value = initializer()
                ui.doOnDestroyed {
                    _value = UNINITIALIZED_VALUE
                }
                "ui delegate初始化：$_value".log("UILazyDelegate")
                if (!isInitialized()) {
                    throw IllegalStateException("不允许在create前或destroy后调用")
                }
            }
            return _value as T
        }

    override fun isInitialized(): Boolean = _value !== UNINITIALIZED_VALUE

    override fun toString(): String =
        if (isInitialized()) value.toString() else "Lazy value not initialized yet."
}