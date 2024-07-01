package com.theathletic.podcast.ui

import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.theathletic.entity.main.PodcastItem
import com.theathletic.ui.CarouselUiModel
import com.theathletic.ui.UiModel

data class PodcastListItem(
    val id: Long,
    val title: String,
    val imageUrl: String,
    val badge: Int = 0,
    val category: String? = null,
    val listIndex: Int
) : UiModel {
    override val stableId = "PodcastListItem:$id"

    companion object {
        fun fromDataModel(model: PodcastItem, listIndex: Int) =
            PodcastListItem(
                id = model.id,
                title = model.title,
                imageUrl = model.imageUrl ?: "",
                badge = model.badge.get(),
                category = model.metadataString,
                listIndex = listIndex
            )
    }
}

data class PodcastCarouselItem(
    override val carouselItemModels: List<UiModel>
) : CarouselUiModel {
    override val stableId = "FOLLOWING_PODCASTS"
}

data class PodcastEpisodeListItem(
    val id: Long,
    val sectionId: String,
    val title: String,
    val formattedDate: String,
    val formattedDuration: String,
    val imageUrl: String,
    val duration: Long,
    val finished: Boolean,
    val playDrawable: Drawable?,
    @DrawableRes val downloadDrawable: Int,
    @ColorRes val downloadTint: Int,
    val downloadProgress: Int,
    val formattedCommentCount: String,
    val showCommentCount: Boolean,
    val isPlayClickable: Boolean,
    val showDivider: Boolean,
    val analyticsInfo: AnalyticsInfo
) : UiModel {

    override val stableId = "PodcastEpisodeListItem:$sectionId:$id"

    data class AnalyticsInfo(val podcastId: Long)
}

object PodcastFollowingEmptyItem : UiModel {
    override val stableId = "FOLLOWING_PODCAST_EMPTY"
}