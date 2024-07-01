package com.theathletic.author.data

import androidx.databinding.ObservableBoolean
import com.google.gson.annotations.SerializedName
import com.theathletic.ui.UiModel
import java.io.Serializable

data class AuthorDetailResponse(@SerializedName("author") var author: AuthorDetailEntity) : Serializable

data class AuthorDetailEntity(
    @SerializedName("id") var id: Long,
    @SerializedName("display_name") var displayName: String,
    @SerializedName("featured_photo") var featuredPhoto: String,
    @SerializedName("description") var description: String,
    @SerializedName("twitter") var twitter: String,
    @Transient var articleRelatedAuthorFollowed: ObservableBoolean
) : Serializable, UiModel {
    override val stableId get() = "AuthorDetailEntity:$id"
}