package com.theathletic.feed.data.remote

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.theathletic.FeedQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.entity.settings.UserContentEdition
import com.theathletic.feed.FeedType
import com.theathletic.type.FeedConsumableType
import com.theathletic.type.FeedRequest
import com.theathletic.type.LayoutFilter
import com.theathletic.type.LayoutType
import com.theathletic.type.Platform
import java.util.Locale
import com.theathletic.type.FeedType as GqlFeedType

class FeedGraphqlApi @AutoKoin(Scope.SINGLE) constructor(
    private val client: ApolloClient
) {

    suspend fun getFeed(
        id: Long,
        type: FeedType,
        page: Int,
        contentEdition: UserContentEdition?,
        layouts: Map<LayoutType, List<FeedConsumableType>>,
        adsEnabled: Boolean = false
    ): ApolloResponse<FeedQuery.Data>? {
        val queryFeedType = type.asGqlFeedType
        if (queryFeedType == GqlFeedType.UNKNOWN__) {
            return null
        }
        val filter = FeedRequest(
            feed_id = Optional.presentIfNotNull(id.toInt()),
            feed_type = Optional.presentIfNotNull(queryFeedType),
            page = page,
            platform = Optional.presentIfNotNull(Platform.android),
            locale = Optional.presentIfNotNull(getLanguage(contentEdition)),
            layouts = Optional.presentIfNotNull(
                layouts.map {
                    LayoutFilter(layout_type = it.key, consumable_types = Optional.presentIfNotNull(it.value))
                }
            )
        )

        return client.query(
            FeedQuery(filter = Optional.presentIfNotNull(filter), isAdsEnabled = Optional.present(adsEnabled))
        ).execute()
    }

    private fun getLanguage(contentEdition: UserContentEdition?): String {
        return when (contentEdition) {
            UserContentEdition.US -> UserContentEdition.US.value
            UserContentEdition.UK -> UserContentEdition.UK.value
            else -> Locale.getDefault().toLanguageTag()
        }
    }

    private val FeedType.asGqlFeedType get() = when (this) {
        FeedType.User -> GqlFeedType.following
        is FeedType.Team -> GqlFeedType.team
        is FeedType.League -> GqlFeedType.league
        is FeedType.Author -> GqlFeedType.author
        is FeedType.Category -> GqlFeedType.topic
        is FeedType.Tag -> GqlFeedType.topic
        is FeedType.Frontpage -> GqlFeedType.frontpage
        else -> GqlFeedType.UNKNOWN__
    }
}