package com.foundation.widget.utils.anim

import android.animation.Animator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.*
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.foundation.widget.utils.R
import com.foundation.widget.utils.ext.view.doOnDetachCanCancel

/**
 * ValueAnimator的优化版
 * 1.增加泛型，对值可直接获取，无需强转
 * 2.增加[startForSingleView]，自动detach，自动取消之前的动画
 */
@SuppressLint("Recycle")//没有调用start这个警告目前去不掉
class MjValueAnimator<T> private constructor(private val valueAnimator: ValueAnimator) :
    Animator() {
    private var onAnimEndForViewListener: AnimatorListener? = null

    companion object {

        const val RESTART = ValueAnimator.RESTART
        const val REVERSE = ValueAnimator.REVERSE

        const val INFINITE = ValueAnimator.INFINITE

        /**
         * @param values 2个及以上
         */
        fun ofInt(vararg values: Int): MjValueAnimator<Int> {
            return MjValueAnimator(ValueAnimator.ofInt(*values))
        }

        fun ofFloat(vararg values: Float): MjValueAnimator<Float> {
            return MjValueAnimator(ValueAnimator.ofFloat(*values))
        }

        fun ofArgb(vararg values: Int): MjValueAnimator<Int> {
            return MjValueAnimator(ValueAnimator.ofArgb(*values))
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // Animator方法实现
    /////////////////////////////////////////////////////////////////////////////////////////////////

    override fun clone(): MjValueAnimator<T> {
        return MjValueAnimator(valueAnimator.clone())
    }

    override fun start() {
        valueAnimator.start()
    }

    /**
     * 1.和view一一对应，animator绑多个view或view绑多个animator都会取消前者
     * 2.view detach时会自动取消
     */
    fun startForSingleView(view: View) {
        //取消上一次
        (view.getTag(R.id.tag_anim) as? Animator)?.cancel()
        removeViewListener()

        //添加新的
        view.setTag(R.id.tag_anim, this)
        val attachListener = view.doOnDetachCanCancel { cancel() }
        onAnimEndForViewListener = addListener {
            view.removeOnAttachStateChangeListener(attachListener)
            removeViewListener()
        }

        start()
    }

    private fun removeViewListener() {
        removeListener(onAnimEndForViewListener)
        onAnimEndForViewListener = null
    }

    override fun cancel() {
        valueAnimator.cancel()
    }

    override fun end() {
        valueAnimator.end()
    }

    override fun pause() {
        valueAnimator.pause()
    }

    override fun resume() {
        valueAnimator.resume()
    }

    override fun isPaused() = valueAnimator.isPaused

    /**
     * 和下面一样，方便统一api
     */
    var startDelay: Long
        @JvmName("setStartDelay2")
        set(value) {
            setStartDelay(value)
        }
        @JvmName("getStartDelay2")
        get() = getStartDelay()

    override fun getStartDelay() = valueAnimator.startDelay

    override fun setStartDelay(startDelay: Long) {
        valueAnimator.startDelay = startDelay
    }

    /**
     * 和下面一样，方便统一api
     */
    var duration: Long
        @JvmName("setDuration2")
        set(value) {
            setDuration(value)
        }
        @JvmName("getDuration2")
        get() = getDuration()

    override fun setDuration(duration: Long): MjValueAnimator<T> {
        valueAnimator.duration = duration
        return this
    }

    override fun getDuration() = valueAnimator.duration

    @TargetApi(Build.VERSION_CODES.N)
    override fun getTotalDuration() = valueAnimator.totalDuration

    /**
     * 和下面一样，方便统一api
     */
    var interpolator: TimeInterpolator?
        @JvmName("setInterpolator2")
        set(value) {
            setInterpolator(value)
        }
        @JvmName("getInterpolator2")
        get() = getInterpolator()

    override fun setInterpolator(value: TimeInterpolator?) {
        valueAnimator.interpolator = value
    }

    override fun getInterpolator(): TimeInterpolator? = valueAnimator.interpolator

    override fun isRunning() = valueAnimator.isRunning

    override fun isStarted() = valueAnimator.isStarted

    override fun addListener(listener: AnimatorListener?) {
        valueAnimator.addListener(listener)
    }

    /**
     * @param onEnd cancel时也会被立即调用
     */
    fun addListener(
        onStart: ((anim: MjValueAnimator<T>) -> Unit)? = null,
        onCancel: ((anim: MjValueAnimator<T>) -> Unit)? = null,
        onRepeat: ((anim: MjValueAnimator<T>) -> Unit)? = null,
        onEnd: ((anim: MjValueAnimator<T>) -> Unit)? = null
    ): AnimatorListener {
        val obj = object : AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                onStart?.invoke(this@MjValueAnimator)
            }

            override fun onAnimationEnd(animation: Animator?) {
                onEnd?.invoke(this@MjValueAnimator)
            }

            override fun onAnimationCancel(animation: Animator?) {
                onCancel?.invoke(this@MjValueAnimator)
            }

            override fun onAnimationRepeat(animation: Animator?) {
                onRepeat?.invoke(this@MjValueAnimator)
            }

        }
        addListener(obj)
        return obj
    }

    override fun removeListener(listener: AnimatorListener?) {
        valueAnimator.removeListener(listener)
    }

    override fun getListeners(): ArrayList<AnimatorListener>? = valueAnimator.listeners

    override fun addPauseListener(listener: AnimatorPauseListener?) {
        valueAnimator.addPauseListener(listener)
    }

    override fun removePauseListener(listener: AnimatorPauseListener?) {
        valueAnimator.removePauseListener(listener)
    }

    override fun removeAllListeners() {
        valueAnimator.removeAllListeners()
    }

    override fun setupStartValues() {
        valueAnimator.setupStartValues()
    }

    override fun setupEndValues() {
        valueAnimator.setupEndValues()
    }

    override fun setTarget(view: Any?) {
        valueAnimator.setTarget(view)
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // ValueAnimator专有方法
    /////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 立即跳到当前时间点
     */
    var currentPlayTime: Long
        set(value) {
            valueAnimator.currentPlayTime = value
        }
        get() = valueAnimator.currentPlayTime

    /**
     * 立即跳到百分比时间点
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun setCurrentFraction(fraction: Float) {
        valueAnimator.setCurrentFraction(fraction)
    }

    val animatedValue: T
        get() = valueAnimator.animatedValue as T

    /**
     * 重复次数，>0或[INFINITE]
     */
    var repeatCount: Int
        set(value) {
            valueAnimator.repeatCount = value
        }
        get() = valueAnimator.repeatCount

    /**
     * 重复模式：
     * [RESTART]：每次都相当于重新开始
     * [REVERSE]：正序倒序反复
     */
    var repeatMode: Int
        set(value) {
            valueAnimator.repeatMode = value
        }
        get() = valueAnimator.repeatMode

    fun addUpdateListener(listener: AnimatorUpdateListener) {
        valueAnimator.addUpdateListener(listener)
    }

    fun addUpdateListener(listener: (anim: MjValueAnimator<T>, value: T) -> Unit): AnimatorUpdateListener {
        val l = AnimatorUpdateListener { listener(this, animatedValue) }
        addUpdateListener(l)
        return l
    }

    fun removeAllUpdateListeners() {
        valueAnimator.removeAllUpdateListeners()
    }

    fun removeUpdateListener(listener: AnimatorUpdateListener) {
        valueAnimator.removeUpdateListener(listener)
    }

    /**
     * 倒序播放
     */
    fun reverse() {
        valueAnimator.reverse()
    }
}