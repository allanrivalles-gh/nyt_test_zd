package com.theathletic.ads.data.local

enum class ViewPortSize {
    SMALL,
    MEDIUM,
    LARGE;

    val value: String = this.name.lowercase()
}