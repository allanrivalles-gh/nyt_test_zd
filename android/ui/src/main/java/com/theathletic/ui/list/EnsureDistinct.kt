package com.theathletic.ui.list

import com.theathletic.ui.UiModel
import timber.log.Timber

fun List<UiModel>.ensureDistinct(onDuplicateFound: ((String) -> Unit)? = null): List<UiModel> {
    val duplicates = groupingBy { it.stableId }.eachCount().filter { it.value > 1 }
    for ((key, _) in duplicates) {
        Timber.e("Stable ID Duplicated: $key")
        onDuplicateFound?.invoke(key)
    }
    return distinctBy { it.stableId }
}