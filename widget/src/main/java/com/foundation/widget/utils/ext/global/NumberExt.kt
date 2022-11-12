package com.foundation.widget.utils.ext.global

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * 精确到2位小数，并保留.00
 * @param mode 舍入模式（如果超出2位），默认向下取值
 */
@JvmOverloads
fun <T : Number?> T?.toStringTo2Decimal(mode: RoundingMode = RoundingMode.DOWN): String {
    if (this == null || this == 0) {
        return "0.00"
    }
    //使用0.00不足位补0，#.##仅保留有效位
    val df = DecimalFormat("0.00")
    df.roundingMode = mode
    return df.format(this.toDouble())
}

/**
 * 精确到2位小数，并抹去多余的小数0
 * @param mode 舍入模式（如果超出2位），默认向下取值
 */
fun <T : Number?> T?.toStringEraseZero(mode: RoundingMode = RoundingMode.DOWN): String {
    if (this == null || this == 0) {
        return "0"
    }
    //使用0.00不足位补0，#.##仅保留有效位
    val df = DecimalFormat("#.##")
    df.roundingMode = mode
    return df.format(this.toDouble())
}

fun <T : Number> T.toBigDecimal(): BigDecimal {
    return when (this) {
        is Int -> {
            BigDecimal.valueOf(this.toLong())
        }
        is Long -> {
            BigDecimal.valueOf(this)
        }
        is BigDecimal -> {
            this
        }
        else -> {
            this.toString().toBigDecimal()
        }
    }
}

/**
 * 相加，无精度丢失
 * 注意：如果要再计算请继续调用此方法
 * @param adds 所有被加数
 */
fun <T : Number> T.add(vararg adds: Number): Double {
    return this.add(adds.sumOf { it.toBigDecimal() })
}

fun <T : Number> T.add(add: Number): Double {
    return (this.toBigDecimal() + add.toBigDecimal()).toDouble()
}

/**
 * 相减，无精度丢失
 * 注意：如果要再计算请继续调用此方法
 * @param subs 所有被减数
 */
fun <T : Number> T.sub(vararg subs: Number): Double {
    return this.sub(subs.sumOf { it.toBigDecimal() })
}

fun <T : Number> T.sub(sub: Number): Double {
    return (this.toBigDecimal() - sub.toBigDecimal()).toDouble()
}