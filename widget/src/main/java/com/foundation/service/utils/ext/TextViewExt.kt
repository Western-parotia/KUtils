package com.foundation.service.utils.ext

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.util.TypedValue
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.foundation.widget.utils.R

/**
 * TextView 拓展
 * create by zhusw on 6/8/21 13:42
 */
fun TextView.setTextColorRes(@ColorRes resId: Int) {
    setTextColor(ContextCompat.getColor(context, resId))
}

/**
 * 设置text
 * null为空字符串
 */
fun TextView.setTextString(obj: Any?) {
    text = when (obj) {
        is StringBuilder, is StringBuffer -> {
            obj.toString()
        }
        is CharSequence -> {
            obj
        }
        null, "null", "Null", "NULL" -> {
            ""
        }
        else -> {
            obj.toString()
        }
    }
}

/**
 * 方便代码统一用屏幕比例：
 * textSizePx = 14.screenRatio
 */
var TextView.textSizePx: Int
    set(value) {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, value.toFloat())
    }
    get() = textSize.toInt()

/**
 * 设置字体加粗，和xml效果一致
 */
var TextView.isTextBold: Boolean
    set(value) {
        if (value) {
            setTypeface(Typeface.create(typeface, Typeface.BOLD), Typeface.BOLD)
            invalidate()
        } else {
            setTypeface(Typeface.create(typeface, Typeface.NORMAL), Typeface.NORMAL)
            invalidate()
        }
    }
    get() = typeface?.isBold ?: false

/**
 * CompoundDrawable默认缺省
 */
fun TextView.setCompoundDrawablesWIB(
    left: Drawable? = null,
    top: Drawable? = null,
    right: Drawable? = null,
    bottom: Drawable? = null
) {
    setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
}

fun TextView.setCompoundDrawablesWIB(
    @DrawableRes left: Int = 0,
    @DrawableRes top: Int = 0,
    @DrawableRes right: Int = 0,
    @DrawableRes bottom: Int = 0
) {
    setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
}

/**
 * 超出指定长度加…
 * @param newEllipsize 目前只支持START,MIDDLE,END
 */
@SuppressLint("SetTextI18n")//text拼接警告
@Suppress("LocalVariableName")//名字带_警告
fun TextView.setTextEllipsize(
    st: CharSequence?,
    newMaxLength: Int,
    newEllipsize: TextUtils.TruncateAt
) {
    if (st.isNullOrEmpty() || newMaxLength <= 0 || st.length <= newMaxLength + 1) {
        text = st
        return
    }
    ellipsize = null
    if (maxLength < newMaxLength + 1) {
        maxLength = newMaxLength + 1
    }

    when (newEllipsize) {
        TextUtils.TruncateAt.START -> {
            text = "…${st.substring(st.length - newMaxLength, st.length)}"
        }
        TextUtils.TruncateAt.MIDDLE -> {
            val size_2 = newMaxLength / 2
            text = "${st.substring(0, size_2)}…${
                st.substring(
                    st.length - (newMaxLength - size_2),
                    st.length
                )
            }"
        }
        else -> {
            text = "${st.substring(0, newMaxLength)}…"
        }
    }
}

/**
 * 获取maxLength
 */
var TextView.maxLength: Int
    set(value) {
        val oldMaxLength =
            (filters.getOrNull { it is InputFilter.LengthFilter } as? InputFilter.LengthFilter)?.max
        if (oldMaxLength == value) {
            return
        }

        val fs = filters.toMutableList()
        fs.removeIfIterator { it is InputFilter.LengthFilter }
        fs.add(InputFilter.LengthFilter(value))
        filters = fs.toTypedArray()
    }
    get() {
        filters.forEach {
            if (it is InputFilter.LengthFilter) {
                return it.max
            }
        }
        return Int.MAX_VALUE
    }

/**
 * 可多次调用，每次都删除上一个然后添加新的
 */
fun TextView.addTextChangedSingleListener(
    afterTextChanged: (text: Editable?) -> Unit
): TextWatcher {
    val tagId = R.id.tag_text_changed_listener
    var listener = getTag(tagId) as? TextWatcher
    if (listener != null) {
        removeTextChangedListener(listener)
    }
    listener = addTextChangedListener(afterTextChanged = afterTextChanged)
    setTag(tagId, listener)
    return listener
}