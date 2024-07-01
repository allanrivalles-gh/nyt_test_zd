package com.theathletic.utility.extensions

/**
 * Removes duplicates from an [Iterable] only when those duplicates occur consecutively.
 *
 * Given a list of: [1, 2, 2, 3, 2]
 * This function will return: [1, 2, 3, 2]
 */
inline fun <T, K> Iterable<T>.distinctByConsecutive(selector: (T) -> K): List<T> {
    val newList = mutableListOf<T>()

    var lastItemValue: K? = null

    for (item in this) {
        val itemValue = selector(item)

        if (itemValue != lastItemValue) {
            newList.add(item)
            lastItemValue = itemValue
        }
    }

    return newList
}