package com.theathletic.scores

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameDetailTabParams(
    val initialTab: GameDetailTab,
    val extras: Map<GameDetailTabParamKey, String?> = emptyMap()
) : Parcelable

@Parcelize
sealed class GameDetailTabParamKey(val key: String) : Parcelable {
    object PostId : GameDetailTabParamKey("postId")
    object LiveBlogId : GameDetailTabParamKey("liveBlogId")
}