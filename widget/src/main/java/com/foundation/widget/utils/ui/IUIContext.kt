package com.foundation.widget.utils.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import com.foundation.widget.utils.ext.observeOnce
import com.foundation.widget.utils.ext.view.doOnNextResumed
import com.foundation.widget.utils.ext.view.toUIContext

/**
 * 一个Context统一工具，将Activity和Fragment的api统一方便基本使用
 * 拓展见Activity、Fragment的[toUIContext]
 * 共有拓展见UIContextExt：[doOnNextResumed]等
 */
interface IUIContext : LifecycleOwner, ViewModelStoreOwner {
    companion object {
        fun createWithActivity(activity: FragmentActivity): IUIContext =
            ActivityUIContextWrapper(activity)

        fun createWithFragment(fragment: Fragment): IUIContext =
            FragmentUIContextWrapper(fragment)
    }

    /**
     * 当前ui是否关闭了
     */
    val isFinished: Boolean

    val activity: FragmentActivity?

    val supportFragmentManager: FragmentManager

    val rootView: View?

    /**
     * view的生命周期监听，注意Fragment立即获取会崩溃
     */
    fun requireViewLifecycle(): Lifecycle

    /**
     * 等待view完成再获取，不会崩溃
     */
    fun viewLifecycleWithCallback(run: (Lifecycle) -> Unit)

    fun startActivity(intent: Intent, options: Bundle? = null)

    fun startActivityForResult(intent: Intent, requestCode: Int, options: Bundle? = null)

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // 内部实现了Activity和Fragment
    /////////////////////////////////////////////////////////////////////////////////////////////////

    class ActivityUIContextWrapper(private val ui: FragmentActivity) : IUIContext {
        override val isFinished = ui.isFinishing
        override val activity = ui
        override val supportFragmentManager = ui.supportFragmentManager
        override val rootView = ui.window?.decorView

        /**
         * view同Activity的生命周期
         */
        override fun requireViewLifecycle() = lifecycle

        override fun viewLifecycleWithCallback(run: (Lifecycle) -> Unit) {
            run.invoke(lifecycle)
        }

        override fun startActivity(intent: Intent, options: Bundle?) {
            ui.startActivity(intent, options)
        }

        override fun startActivityForResult(intent: Intent, requestCode: Int, options: Bundle?) {
            ui.startActivityForResult(intent, requestCode, options)
        }

        override fun getLifecycle() = ui.lifecycle

        override fun getViewModelStore() = ui.viewModelStore
    }

    class FragmentUIContextWrapper(private val ui: Fragment) : IUIContext {
        override val isFinished = !ui.isAdded
        override val activity = ui.activity
        override val supportFragmentManager = ui.childFragmentManager
        override val rootView = ui.view

        override fun requireViewLifecycle() = ui.viewLifecycleOwner.lifecycle

        override fun viewLifecycleWithCallback(run: (Lifecycle) -> Unit) {
            ui.viewLifecycleOwnerLiveData.observeOnce(ui) {
                run.invoke(it.lifecycle)
            }
        }

        override fun startActivity(intent: Intent, options: Bundle?) {
            ui.startActivity(intent, options)
        }

        override fun startActivityForResult(intent: Intent, requestCode: Int, options: Bundle?) {
            ui.startActivityForResult(intent, requestCode, options)
        }

        override fun getLifecycle() = ui.lifecycle

        override fun getViewModelStore() = ui.viewModelStore
    }
}