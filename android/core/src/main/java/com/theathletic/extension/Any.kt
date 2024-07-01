package com.theathletic.extension

val Any?.safe get() = Unit

fun Any?.toStringOrEmpty() = this?.toString() ?: ""
fun Any?.toStringOrShortDash() = this?.toString() ?: "-"