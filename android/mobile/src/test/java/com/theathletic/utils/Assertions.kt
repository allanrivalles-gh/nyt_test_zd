package com.theathletic.utils

import kotlin.test.assertTrue

inline fun <reified T> assertInstance(any: Any) {
    assertTrue(any is T, "Expected:${T::class.java} Actual: ${any.javaClass}")
}