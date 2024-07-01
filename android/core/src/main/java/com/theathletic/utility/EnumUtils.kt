package com.theathletic.utility

inline fun <reified T : Enum<T>> safeValueOf(type: String?): T? {
    return try {
        type?.let { java.lang.Enum.valueOf(T::class.java, it) }
    } catch (e: Exception) {
        null
    }
}