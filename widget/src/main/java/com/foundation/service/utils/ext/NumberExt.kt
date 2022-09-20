package com.foundation.service.utils.ext

import java.text.DecimalFormat

/**
 * 精确到2位小数
 */
fun <T : Number?> T?.toStringTo2Decimal(): String {
    if (this == null) {
        return "0.00"
    }
    //使用0.00不足位补0，#.##仅保留有效位
    return DecimalFormat("0.00").format(this.toDouble())
}