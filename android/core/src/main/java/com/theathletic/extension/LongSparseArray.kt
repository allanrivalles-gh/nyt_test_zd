package com.theathletic.extension

import androidx.collection.LongSparseArray
import androidx.collection.forEach

/**
 * Returns the first element matching the given [predicate].
 * @throws [NoSuchElementException] if no such element is found.
 */
inline fun <T> LongSparseArray<T>.first(predicate: (T) -> Boolean): T {
    this.forEach { _, value ->
        if (predicate(value)) return value
    }
    throw NoSuchElementException("Collection contains no element matching the predicate.")
}

/**
 * Returns the first element matching the given [predicate].
 * Returns `null` if the list is empty, or the element is not found.
 */
inline fun <T> LongSparseArray<T>.firstOrNull(predicate: (T) -> Boolean): T? {
    this.forEach { _, value ->
        if (predicate(value)) return value
    }
    return null
}