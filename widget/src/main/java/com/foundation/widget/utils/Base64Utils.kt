package com.foundation.widget.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64

object Base64Utils {
    /**
     *
     *
     * BASE64字符串解码为二进制数据
     *
     */
    @Throws(Exception::class)
    fun decode(base64: String): ByteArray {
        return Base64.decode(base64, Base64.DEFAULT)
    }

    /**
     *
     *
     * 二进制数据编码为BASE64字符串
     *
     */
    @Throws(Exception::class)
    fun encode(bytes: ByteArray): String {
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    /**
     * 将base64字符串转换成Bitmap类型
     *
     */
    fun base64StrToBitmap(string: String): Bitmap? {
        return try {
            val bitmapArray = decode(string)
            BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}