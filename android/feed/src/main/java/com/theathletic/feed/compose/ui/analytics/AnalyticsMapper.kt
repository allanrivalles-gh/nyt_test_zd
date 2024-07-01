package com.theathletic.feed.compose.ui.analytics

import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.feed.compose.data.Layout

data class AnalyticsData(
    val layoutId: String,
    val layoutIndex: Int,
    val container: String,
    val verticalIndex: Long = -1,
    val horizontalIndex: Long = -1,
    val parentType: String? = null,
    val parentId: String? = null,
    val gameId: String? = null
) {
    fun toImpressionsPayload(objectId: String, objectType: String) = ImpressionPayload(
        objectType = objectType,
        objectId = objectId,
        pageOrder = layoutIndex,
        container = container,
        element = container,
        vIndex = verticalIndex,
        hIndex = horizontalIndex,
        parentObjectType = parentType,
        parentObjectId = parentId
    )

    fun toClickPayload(objectType: String) = ClickPayload(
        objectType = objectType,
        moduleIndex = layoutIndex,
        container = container,
        vIndex = verticalIndex,
        hIndex = horizontalIndex,
        parentType = parentType,
        parentId = parentId,
        gameId = gameId
    )

    fun toNavLinkClickPayload(objectType: String, boxScoreTab: String? = null) = NavLinkAnalyticsPayload(
        objectType = objectType,
        moduleIndex = layoutIndex,
        container = container,
        boxScoreTab = boxScoreTab ?: "",
        vIndex = verticalIndex,
        hIndex = horizontalIndex,
        parentType = parentType,
        parentId = parentId
    )
}

data class ClickPayload(
    val objectType: String,
    val moduleIndex: Int,
    val container: String,
    val vIndex: Long = -1,
    val hIndex: Long = -1,
    val parentType: String? = null,
    val parentId: String? = null,
    val gameId: String? = null,
)

data class NavLinkAnalyticsPayload(
    val objectType: String,
    val moduleIndex: Int,
    val container: String,
    val boxScoreTab: String = "",
    val vIndex: Long = -1,
    val hIndex: Long = -1,
    val parentType: String? = null,
    val parentId: String? = null
)

internal fun Layout.createAnalyticsData(
    container: String = type.container,
    layoutIndex: Int = index,
    verticalIndex: Int = -1,
    horizontalIndex: Int = -1
) = AnalyticsData(
    layoutId = id,
    layoutIndex = layoutIndex,
    verticalIndex = verticalIndex.toLong(),
    horizontalIndex = horizontalIndex.toLong(),
    container = container
)

internal val Layout.Type.container: String
    get() = when (this) {
        Layout.Type.A1 -> "a1"
        Layout.Type.TOPPER -> "content_topper_hero"
        Layout.Type.THREE_HERO_CURATION -> "three_hero"
        Layout.Type.FOUR_HERO_CURATION -> "three_hero"
        Layout.Type.FIVE_HERO_CURATION -> "five_hero"
        Layout.Type.SIX_HERO_CURATION -> "six_hero"
        Layout.Type.SEVEN_PLUS_HERO_CURATION -> "sevenplus_hero"
        Layout.Type.FOUR_CONTENT_CURATED -> "four_content"
        Layout.Type.HEADLINE -> "headline_multiple"
        Layout.Type.FOR_YOU -> "four_content"
        Layout.Type.MOST_POPULAR -> "popular"
        Layout.Type.FEATURE_GAME -> "game_module"
        Layout.Type.SCORES -> "box_score"
        else -> ""
    }