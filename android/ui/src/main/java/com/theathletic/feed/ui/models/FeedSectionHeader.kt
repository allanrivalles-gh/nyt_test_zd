package com.theathletic.feed.ui.models

import com.theathletic.ui.UiModel

data class BasicSectionHeader(
    val id: Int,
    val title: String,
    val actionText: String = "",
    val deeplink: String = "",
    val imageUrl: String,
    val showImage: Boolean,
    val analyticsPayload: SeeAllAnalyticsPayload? = null
) : UiModel {
    override val stableId = "BasicSectionHeader:$id:$title"

    val showSeeAll = actionText.isNotBlank() && deeplink.isNotBlank()

    interface Interactor : SeeAllInteractor
}

data class SectionHeaderWithDescription(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String,
    val showImage: Boolean
) : UiModel {
    override val stableId = "SectionHeaderDescription:$id:$title"

    val isDescriptionVisible = description.isNotBlank()
}

data class FeedSeeAllButton(
    val id: Long,
    val actionText: String,
    val deeplink: String,
    val analyticsPayload: SeeAllAnalyticsPayload
) : UiModel {
    override val stableId = "SeeAll:$id"

    interface Interactor : SeeAllInteractor
}

data class SeeAllAnalyticsPayload(
    val container: String,
    val moduleIndex: Int,
    val parentObjectType: String = "",
    val parentObjectId: String = ""
)

interface SeeAllInteractor {
    fun onSeeAllClicked(deeplink: String, analyticsPayload: SeeAllAnalyticsPayload?)
}

class FeedEndOfFeed : UiModel {
    override val stableId = "FeedEndOfFeed"

    interface Interactor {
        fun onEndOfFeedClicked()
    }
}