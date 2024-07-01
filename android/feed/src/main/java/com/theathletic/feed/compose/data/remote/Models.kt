package com.theathletic.feed.compose.data.remote

import com.apollographql.apollo3.api.Optional
import com.theathletic.NewFeedQuery
import com.theathletic.feed.compose.data.FeedFilter
import com.theathletic.feed.compose.data.FeedRequest
import com.theathletic.feed.compose.data.Layout
import com.theathletic.type.FeedFilterV2
import com.theathletic.type.LayoutFilterV2

// Enums to represent network types. These may be replaced by enums on the backend.
internal enum class FeedType {
    APP_AUTHOR,
    APP_FOLLOWING,
    APP_LEAGUE,
    APP_LOHP,
    APP_PLAYER,
    APP_TAG,
    APP_TEAM,
    APP_TEAM_PODCASTS;

    // Lowercase values to match API expectation
    override fun toString(): String {
        return super.toString().lowercase()
    }
}

internal enum class LayoutType {
    ONE_CONTENT_CURATED,
    TWO_CONTENT_CURATED,
    THREE_HERO_CURATION,
    FOUR_HERO_CURATION,
    FIVE_HERO_CURATION,
    SIX_HERO_CURATION,
    FOUR_CONTENT_CURATED,
    HEADLINE,
    FOR_YOU,
    HIGHLIGHT_THREE_CONTENT,
    TOPPER,
    MOST_POPULAR_ARTICLES,
    SEVEN_PLUS_HERO_CURATION,
    PODCAST_EPISODES_LIST,
    A1,
    FEATURED_GAME,
    SCORES,
    DROPZONE;

    // Lowercase values to match API expectation
    override fun toString(): String {
        return super.toString().lowercase()
    }
}

internal enum class ConsumableType {
    ACTION,
    ANNOUNCEMENT,
    ARTICLE,
    BRIEF,
    DISCUSSION,
    DROPZONE,
    EVERGREEN,
    FEATURED_FEED_GAME,
    FEED_GAME,
    GAME,
    GAME_V2,
    INSIDER,
    LIVEBLOG,
    LIVEBLOGBANNER,
    LIVEROOM,
    NEWS,
    PODCAST,
    PODCAST_EPISODE,
    QANDA,
    SPOTLIGHT,
    STATS_SCORES,
    TEAM_WIDGETS,
    TOPIC;

    // Lowercase values to match API expectation
    override fun toString(): String {
        return super.toString().lowercase()
    }
}

internal fun FeedRequest.toRemote(page: Int) = NewFeedQuery(
    feedType.toRemote().toString(),
    Optional.presentIfNotNull(feedId),
    Optional.presentIfNotNull(feedUrl),
    Optional.presentIfNotNull(filter?.toRemote()),
    Optional.presentIfNotNull(locale),
    page,
)

internal fun com.theathletic.feed.compose.data.FeedType.toRemote(): FeedType = when (this) {
    com.theathletic.feed.compose.data.FeedType.AUTHOR -> FeedType.APP_AUTHOR
    com.theathletic.feed.compose.data.FeedType.FOLLOWING -> FeedType.APP_FOLLOWING
    com.theathletic.feed.compose.data.FeedType.LEAGUE -> FeedType.APP_LEAGUE
    com.theathletic.feed.compose.data.FeedType.DISCOVER -> FeedType.APP_LOHP
    com.theathletic.feed.compose.data.FeedType.PLAYER -> FeedType.APP_PLAYER
    com.theathletic.feed.compose.data.FeedType.TAG -> FeedType.APP_TAG
    com.theathletic.feed.compose.data.FeedType.TEAM -> FeedType.APP_TEAM
    com.theathletic.feed.compose.data.FeedType.TEAM_PODCASTS -> FeedType.APP_TEAM_PODCASTS
}

internal fun FeedFilter.toRemote(): FeedFilterV2 = FeedFilterV2(
    Optional.presentIfNotNull(
        this.layouts.map {
            LayoutFilterV2(
                consumable_types = it.value.map { type -> type.toRemote().toString() },
                layout_type = it.key.toRemote().toString()
            )
        }
    )
)

internal fun Layout.Type.toRemote(): LayoutType = when (this) {
    Layout.Type.ONE_CONTENT_CURATED -> LayoutType.ONE_CONTENT_CURATED
    Layout.Type.TWO_CONTENT_CURATED -> LayoutType.TWO_CONTENT_CURATED
    Layout.Type.THREE_HERO_CURATION -> LayoutType.THREE_HERO_CURATION
    Layout.Type.FOUR_HERO_CURATION -> LayoutType.FOUR_HERO_CURATION
    Layout.Type.FIVE_HERO_CURATION -> LayoutType.FIVE_HERO_CURATION
    Layout.Type.SIX_HERO_CURATION -> LayoutType.SIX_HERO_CURATION
    Layout.Type.FOUR_CONTENT_CURATED -> LayoutType.FOUR_CONTENT_CURATED
    Layout.Type.HEADLINE -> LayoutType.HEADLINE
    Layout.Type.FOR_YOU -> LayoutType.FOR_YOU
    Layout.Type.HIGHLIGHT_THREE_CONTENT -> LayoutType.HIGHLIGHT_THREE_CONTENT
    Layout.Type.TOPPER -> LayoutType.TOPPER
    Layout.Type.SEVEN_PLUS_HERO_CURATION -> LayoutType.SEVEN_PLUS_HERO_CURATION
    Layout.Type.MOST_POPULAR -> LayoutType.MOST_POPULAR_ARTICLES
    Layout.Type.MY_PODCASTS -> LayoutType.PODCAST_EPISODES_LIST
    Layout.Type.A1 -> LayoutType.A1
    Layout.Type.FEATURE_GAME -> LayoutType.FEATURED_GAME
    Layout.Type.SCORES -> LayoutType.SCORES
    Layout.Type.DROPZONE -> LayoutType.DROPZONE
}

internal fun com.theathletic.feed.compose.data.ConsumableType.toRemote(): ConsumableType = when (this) {
    com.theathletic.feed.compose.data.ConsumableType.ACTION -> ConsumableType.ACTION
    com.theathletic.feed.compose.data.ConsumableType.ANNOUNCEMENT -> ConsumableType.ANNOUNCEMENT
    com.theathletic.feed.compose.data.ConsumableType.ARTICLE -> ConsumableType.ARTICLE
    com.theathletic.feed.compose.data.ConsumableType.BRIEF -> ConsumableType.BRIEF
    com.theathletic.feed.compose.data.ConsumableType.DISCUSSION -> ConsumableType.DISCUSSION
    com.theathletic.feed.compose.data.ConsumableType.DROPZONE -> ConsumableType.DROPZONE
    com.theathletic.feed.compose.data.ConsumableType.EVERGREEN -> ConsumableType.EVERGREEN
    com.theathletic.feed.compose.data.ConsumableType.FEATURED_FEED_GAME -> ConsumableType.FEATURED_FEED_GAME
    com.theathletic.feed.compose.data.ConsumableType.FEED_GAME -> ConsumableType.FEED_GAME
    com.theathletic.feed.compose.data.ConsumableType.GAME -> ConsumableType.GAME
    com.theathletic.feed.compose.data.ConsumableType.GAME_V2 -> ConsumableType.GAME_V2
    com.theathletic.feed.compose.data.ConsumableType.INSIDER -> ConsumableType.INSIDER
    com.theathletic.feed.compose.data.ConsumableType.LIVEBLOG -> ConsumableType.LIVEBLOG
    com.theathletic.feed.compose.data.ConsumableType.LIVEBLOGBANNER -> ConsumableType.LIVEBLOGBANNER
    com.theathletic.feed.compose.data.ConsumableType.LIVEROOM -> ConsumableType.LIVEROOM
    com.theathletic.feed.compose.data.ConsumableType.NEWS -> ConsumableType.NEWS
    com.theathletic.feed.compose.data.ConsumableType.PODCAST -> ConsumableType.PODCAST
    com.theathletic.feed.compose.data.ConsumableType.PODCAST_EPISODE -> ConsumableType.PODCAST_EPISODE
    com.theathletic.feed.compose.data.ConsumableType.QANDA -> ConsumableType.QANDA
    com.theathletic.feed.compose.data.ConsumableType.SPOTLIGHT -> ConsumableType.SPOTLIGHT
    com.theathletic.feed.compose.data.ConsumableType.STATS_SCORES -> ConsumableType.STATS_SCORES
    com.theathletic.feed.compose.data.ConsumableType.TEAM_WIDGETS -> ConsumableType.TEAM_WIDGETS
    com.theathletic.feed.compose.data.ConsumableType.TOPIC -> ConsumableType.TOPIC
}