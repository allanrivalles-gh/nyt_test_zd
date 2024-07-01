package com.theathletic.gamedetail.data.local

import com.theathletic.data.LocalModel
import com.theathletic.entity.main.League
import com.theathletic.scores.data.local.BoxScoreEntity

data class TodaysGamesLocalModel(
    val groups: List<TodaysGamesGrouping>,
    val gameEntities: List<BoxScoreEntity>
) : LocalModel {

    data class TodaysGamesGrouping(
        val id: String,
        val filter: GroupingFilter,
        val type: GroupingType,
        val league: League?,
        val leagueDisplayName: String?,
        val gameIds: List<String>,
        val isInPostSeason: Boolean
    )
}

enum class GroupingFilter {
    NONE,
    TOP25,
    UNKNOWN
}

enum class GroupingType {
    NONE,
    FOLLOWING,
    LEAGUE,
    UNKNOWN
}