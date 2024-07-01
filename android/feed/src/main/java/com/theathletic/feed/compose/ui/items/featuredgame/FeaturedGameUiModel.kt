package com.theathletic.feed.compose.ui.items.featuredgame

import com.theathletic.entity.main.Sport
import com.theathletic.feed.compose.SOURCE_FEED
import com.theathletic.feed.compose.ui.LayoutUiModel
import com.theathletic.feed.compose.ui.analytics.AnalyticsData
import com.theathletic.links.deep.Deeplink
import com.theathletic.ui.ResourceString

data class FeaturedGameUiModel(
    override val id: String,
    val title: String,
    val firstTeam: Team,
    val secondTeam: Team,
    val gameStatus: GameStatus,
    val navLinks: List<NavLink>,
    val sport: Sport,
    val relatedContent: LayoutUiModel.Item?,
    override val analyticsData: AnalyticsData,
    override val permalink: String? = null
) : LayoutUiModel.Item {

    override fun deepLink(): Deeplink = Deeplink.boxScore(id).addSource(SOURCE_FEED)

    data class Team(
        val id: String, // GQL team id
        val alias: String,
        val colors: String?,
        val logoUrl: String?,
        val score: String? = null,
        val currentRecord: String? = null,
        val winLossRecord: String? = null,
        val isWinLossReversed: Boolean = false,
        val hasPossession: Boolean = false
    )

    data class GameStatus(
        val state: GameState,
        val gameDate: String? = null,
        val gameTime: String? = null,
        val clock: String? = null,
        val period: String? = null,
        val aggregate: ResourceString? = null
    )

    data class NavLink(
        val label: String,
        val appLink: String
    )

    enum class GameState {
        PREGAME,
        LIVE_GAME,
        POSTGAME
    }
}