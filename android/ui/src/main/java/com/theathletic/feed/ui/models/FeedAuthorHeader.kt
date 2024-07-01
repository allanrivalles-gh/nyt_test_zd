package com.theathletic.feed.ui.models

import com.theathletic.ui.UiModel

data class FeedAuthorHeader(
    val id: Long,
    val name: String,
    val imageUrl: String,
    val description: String,
    val twitterHandle: String,
    val isUserFollowing: Boolean
) : UiModel {
    override val stableId = "FeedAuthorHeader:$id"

    interface Interactor {
        fun onFeaturedAuthorTwitterClick(twitterHandle: String)
        fun onFollowAuthorClick()
    }
}