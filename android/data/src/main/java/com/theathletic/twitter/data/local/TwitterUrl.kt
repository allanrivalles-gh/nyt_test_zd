package com.theathletic.twitter.data.local

data class TwitterUrl(
    val url: String,
    val html: String,
    val theme: String,
    val authorName: String? = ""
)