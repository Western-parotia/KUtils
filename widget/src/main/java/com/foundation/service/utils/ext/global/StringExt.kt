package com.foundation.service.utils.ext.global

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import com.foundation.service.utils.app

/**
 * @param toastText 吐司文案，空表示不吐司
 */
@JvmOverloads
fun CharSequence?.clipboardAndToast(toastText: CharSequence = "已复制到剪切板") {
    if (this.isNullOrEmpty()) {
        return
    }
    val cbm = app.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("xpx copy", this)
    cbm.setPrimaryClip(clipData)
    Toast.makeText(app, toastText, Toast.LENGTH_SHORT).show()
}

/**
 * 会丢弃小数点，不会崩溃
 */
fun CharSequence?.toSafeInt(): Int {
    when (this) {
        null, "null", "Null", "NULL", "" -> {
            return 0
        }
        else -> {
            try {
                return split(".")[0].toInt()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return 0
        }
    }
}

/**
 * 不会崩溃
 * @param newScale 精度，直接删掉（方便去掉多余小数）
 */
@JvmOverloads
fun CharSequence?.toSafeDouble(newScale: Int = -1): Double {
    var st = this
    if (st.isNullOrEmpty()) {
        return 0.0
    }
    when {
        st.endsWith(".") -> {
            st = "${st}0"
        }
        st.startsWith(".") -> {
            st = "0${st}"
        }
    }
    try {
        if (newScale >= 0) {
            var endIndex = st.indexOf(".")
            if (endIndex >= 0) {
                if (newScale > 0) {
                    endIndex += newScale + 1
                }
                if (endIndex <= st.length) {
                    return st.substring(0, endIndex).toDouble()
                }
            }
        }
        return st.toString().toDouble()
    } catch (e: Exception) {
        e.printStackTrace()
        return 0.0
    }
}

/**
 * 开头添加人民币符号
 */
fun String.addRmb() = "¥$this"

/**
 * 是否以http开头
 */
fun String?.isHttp() = this?.startsWith("http") ?: false

/**
 * 如果为空则为另一个值
 */
infix fun <T : CharSequence?> T?.emptyThen(to: T): T {
    return if (isNullOrEmpty()) to else this
}