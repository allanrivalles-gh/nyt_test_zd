package com.theathletic.extension

fun Boolean.toInt() = if (this) 1 else 0

fun Boolean.toLong() = if (this) 1L else 0L

fun Boolean.toAnalyticsString() = if (this) "1" else "0"