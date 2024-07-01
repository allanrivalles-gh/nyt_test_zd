package com.theathletic.ui

import com.theathletic.analytics.impressions.ImpressionPayload

interface UiModel {

    /**
     * An ID used to represent the data models the [UiModel] represents. This is used
     * by [DiffUtil] to determine whether an items has changed or moved. Use the data model id if
     * possible, or a combination of ids if this [UiModel] contains multiple data models.
     */
    val stableId: String

    val impressionPayload: ImpressionPayload? get() = null
}

/**
 * A [UiModel] that supports a nested set of presentation models. This is use in feeds
 * where we want a [RecyclerView] inside another [RecyclerView].
 */
interface CarouselUiModel : UiModel {

    val carouselItemModels: List<UiModel>
}

val List<UiModel>.stableId: String get() = joinToString(":") { it.stableId }