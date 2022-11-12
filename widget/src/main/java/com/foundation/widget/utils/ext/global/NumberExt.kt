package com.foundation.widget.utils.ext.global

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * 精确到2位小数，并保留.00
 * 提示：本方法主要用于展示，计算请使用[BigDecimal.setScale]
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
 * 提示：本方法主要用于展示，计算请使用[BigDecimal.setScale]
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
 * 提示：list集合相加请使用[sumOf]
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

@JvmOverloads
fun <T : Number> T.mul(
    mul: Number,
    scale: Int = -1,
    mode: RoundingMode = RoundingMode.HALF_UP
): Double {
    val bd = this.toBigDecimal() * mul.toBigDecimal()
    val result = if (scale < 0) bd else bd.setScale(scale, mode)
    return result.toDouble()
}

/**
 * div单词被占用太多了
 *
 * @param scale 精度
 */
@JvmOverloads
fun <T : Number> T.divide(
    div: Number,
    scale: Int = -1,
    mode: RoundingMode = RoundingMode.HALF_UP
): Double {
    val result = if (scale < 0) {
        this.toBigDecimal().divide(div.toBigDecimal(), mode)
    } else {
        this.toBigDecimal().divide(div.toBigDecimal(), scale, mode)
    }
    return result.toDouble()
}

/**
 * 求和，主要给java使用
 */
fun <T : Number> List<T>.sumAll(): Double {
    return this.sumOf { it.toBigDecimal() }.toDouble()
}