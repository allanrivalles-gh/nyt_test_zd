package com.theathletic.viewmodel.main

import com.theathletic.followable.legacyId
import com.theathletic.followables.data.domain.FollowableItem
import com.theathletic.search.data.local.SearchArticleItem
import com.theathletic.search.data.local.SearchAuthorItem
import com.theathletic.search.data.local.SearchBaseItem
import com.theathletic.search.data.local.SearchLeagueItem
import com.theathletic.search.data.local.SearchPopularItem
import com.theathletic.search.data.local.SearchTeamItem

fun List<FollowableItem>.toSearchItem(seachType: SearchType): List<SearchBaseItem> {
    return mapIndexedNotNull { index, followable ->
        val followableId = followable.followableId.legacyId ?: return@mapIndexedNotNull null
        when (seachType) {
            SearchType.TEAM -> SearchTeamItem(
                id = followableId,
                name = followable.name,
                imageUrl = followable.imageUrl
            )
            SearchType.LEAGUE -> SearchLeagueItem(
                id = followableId,
                name = followable.name,
                imageUrl = followable.imageUrl
            )
            SearchType.AUTHOR -> SearchAuthorItem(
                id = followableId,
                name = followable.name,
                imageUrl = followable.imageUrl
            )
            else -> null
        }?.apply { adapterId = 40000L + index }
    }
}

fun SearchBaseItem.v2AnalyticsObjectType() = when (this) {
    is SearchPopularItem -> "article_id"
    is SearchArticleItem -> "article_id"
    is SearchTeamItem -> "team_id"
    is SearchAuthorItem -> "author_id"
    is SearchLeagueItem -> "league_id"
    else -> "unknown"
}