package com.theathletic.entity.user

import com.squareup.moshi.JsonClass
import com.theathletic.core.R

@JsonClass(generateAdapter = true)
data class CommentsSortType(
    val article: SortType,
    val discussion: SortType,
    val headline: SortType,
    val game: SortType,
    val podcast: SortType,
    val qanda: SortType,
)

enum class SortType(val value: String) {
    MOST_LIKED("likes"),
    NEWEST("recent"),
    OLDEST("time"),
    TRENDING("trending");

    companion object {
        private val values = values()
        fun getByValue(value: String) = values.firstOrNull { it.value == value } ?: OLDEST
        fun getByIndex(index: Int) = values.getOrNull(index) ?: OLDEST
    }
}

val SortType.stringResId: Int
    get() = when (this) {
        SortType.OLDEST -> R.string.comments_sort_oldest
        SortType.MOST_LIKED -> R.string.comments_sort_most_liked
        SortType.NEWEST -> R.string.comments_sort_newest
        SortType.TRENDING -> R.string.comments_sort_trending
    }