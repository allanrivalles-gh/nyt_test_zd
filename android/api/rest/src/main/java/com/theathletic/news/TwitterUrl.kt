package com.theathletic.news

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class TwitterUrl(
    @SerializedName("url")
    var url: String? = "",
    @SerializedName("author_name")
    var authorName: String? = "",
    @SerializedName("html")
    var html: String? = ""
)