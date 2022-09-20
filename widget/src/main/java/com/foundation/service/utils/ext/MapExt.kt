package com.foundation.service.utils.ext

/**
 * 安全的遍历删除
 */
inline fun <K, V> MutableMap<K, V>?.removeIf(filter: (k: K, v: V) -> Boolean) {
    if (isNullOrEmpty()) {
        return
    }
    val iterator = entries.iterator()
    while (iterator.hasNext()) {
        val next = iterator.next()
        if (filter(next.key, next.value)) {
            iterator.remove()
        }
    }
}