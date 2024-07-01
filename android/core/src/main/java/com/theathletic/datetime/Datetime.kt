package com.theathletic.datetime

data class Datetime(
    val timeMillis: Long
) : Comparable<Datetime> {
    override operator fun compareTo(other: Datetime) = timeMillis.compareTo(other.timeMillis)
}

fun Long?.asDatetimeOrNull(): Datetime? = this?.let { Datetime(it) }