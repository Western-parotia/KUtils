package com.foundation.widget.utils.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
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
interface IUIContext : LifecycleOwner, ViewModelStoreOwner, ActivityResultCaller, IUIFieldOpt {
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

    /**
     * 当前自己的FragmentManager
     * Activity为[FragmentActivity.getSupportFragmentManager]
     * Fragment为[Fragment.getChildFragmentManager]
     */
    val currentFragmentManager: FragmentManager

    val rootView: View?

    /**
     * 实际代理类，如：Fragment、Activity
     */
    val delegate: Any

    /**
     * view的生命周期监听，注意Fragment立即获取会崩溃
     */
    fun requireViewLifecycle(): Lifecycle

    /**
     * 等待view完成再获取Lifecycle，不会崩溃（主要是Fragment）
     * @param run destroy之前返回有值，destroy之后返回null
     */
    fun viewLifecycleWithCallback(run: (Lifecycle?) -> Unit)

    fun startActivity(intent: Intent, options: Bundle? = null)

    @Deprecated("建议使用registerForActivityResult")
    fun startActivityForResult(intent: Intent, requestCode: Int, options: Bundle? = null)

    /**
     * @param contract 如原始的intent跳转：[ActivityResultContracts.StartActivityForResult]
     */
    override fun <I, O> registerForActivityResult(
        contract: ActivityResultContract<I, O>,
        callback: ActivityResultCallback<O>
    ): ActivityResultLauncher<I>

    override fun <I, O> registerForActivityResult(
        contract: ActivityResultContract<I, O>,
        registry: ActivityResultRegistry,
        callback: ActivityResultCallback<O>
    ): ActivityResultLauncher<I>

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // 内部实现了Activity和Fragment
    /////////////////////////////////////////////////////////////////////////////////////////////////

    class ActivityUIContextWrapper(private val ui: FragmentActivity) : IUIContext {
        override val isFinished = ui.isFinishing
        override fun getActivity() = ui
        override val currentFragmentManager = ui.supportFragmentManager
        override val rootView = ui.window?.decorView
        override val delegate = ui

        /**
         * view同Activity的生命周期
         */
        override fun requireViewLifecycle() = lifecycle

        override fun viewLifecycleWithCallback(run: (Lifecycle?) -> Unit) =
            run.invoke(lifecycle)

        override fun startActivity(intent: Intent, options: Bundle?) =
            ui.startActivity(intent, options)

        override fun startActivityForResult(intent: Intent, requestCode: Int, options: Bundle?) =
            ui.startActivityForResult(intent, requestCode, options)

        override fun <I, O> registerForActivityResult(
            contract: ActivityResultContract<I, O>,
            callback: ActivityResultCallback<O>
        ): ActivityResultLauncher<I> =
            ui.registerForActivityResult(contract, callback)

        override fun <I, O> registerForActivityResult(
            contract: ActivityResultContract<I, O>,
            registry: ActivityResultRegistry,
            callback: ActivityResultCallback<O>
        ): ActivityResultLauncher<I> =
            ui.registerForActivityResult(contract, registry, callback)

        override fun getLifecycle() = ui.lifecycle

        override fun getViewModelStore() = ui.viewModelStore

        override fun toString() = "$this,delegate:$delegate"
    }

    class FragmentUIContextWrapper(private val ui: Fragment) : IUIContext {
        override val isFinished = !ui.isAdded
        override fun getActivity() = ui.activity
        override val currentFragmentManager = ui.childFragmentManager
        override val rootView = ui.view
        override val delegate = ui

        override fun requireViewLifecycle() = ui.viewLifecycleOwner.lifecycle

        override fun viewLifecycleWithCallback(run: (Lifecycle?) -> Unit) {
            ui.viewLifecycleOwnerLiveData.observeOnce(ui) { owner: LifecycleOwner? ->
                run.invoke(owner?.lifecycle)
            }
        }

        override fun startActivity(intent: Intent, options: Bundle?) =
            ui.startActivity(intent, options)

        override fun startActivityForResult(intent: Intent, requestCode: Int, options: Bundle?) =
            ui.startActivityForResult(intent, requestCode, options)

        override fun <I, O> registerForActivityResult(
            contract: ActivityResultContract<I, O>,
            callback: ActivityResultCallback<O>
        ): ActivityResultLauncher<I> =
            ui.registerForActivityResult(contract, callback)

        override fun <I, O> registerForActivityResult(
            contract: ActivityResultContract<I, O>,
            registry: ActivityResultRegistry,
            callback: ActivityResultCallback<O>
        ): ActivityResultLauncher<I> =
            ui.registerForActivityResult(contract, registry, callback)

        override fun getLifecycle() = ui.lifecycle

        override fun getViewModelStore() = ui.viewModelStore

        override fun toString() = "$this,delegate:$delegate"
    }
}