package com.foundation.example

import com.foundation.widget.utils.ext.global.add
import com.foundation.widget.utils.ext.global.sub

fun main() {
    val add = 10.add(1.1, 2.2, 9.9)
    println(add)
    val sub = 10.sub(1.1, 2.2, 9.9)
    println(sub)
}