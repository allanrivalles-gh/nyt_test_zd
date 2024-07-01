package com.theathletic.boxscore.ui.modules

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import com.theathletic.boxscore.ui.RecentGames
import com.theathletic.boxscore.ui.RecentGamesUi
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.feed.ui.LocalFeedInteractor
import com.theathletic.ui.R

data class RecentGamesModule(
    val id: String,
    val teams: RecentGamesUi.Teams,
    val firstTeamRecentGames: List<RecentGamesUi.RecentGame>,
    val secondTeamRecentGames: List<RecentGamesUi.RecentGame>,
    @StringRes val titleId: Int = R.string.box_score_last_games_title,
    @StringRes val noGamesTitleId: Int,
    val leagueName: String?
) : FeedModuleV2 {

    override val moduleId: String = "RecentGamesModule:$id"

    @Composable
    override fun Render() {
        val interactor = LocalFeedInteractor.current
        RecentGames(
            includeDivider = true,
            teams = teams,
            firstTeamRecentGames = firstTeamRecentGames,
            secondTeamRecentGames = secondTeamRecentGames,
            titleId = titleId,
            onRecentGameClick = { interactor.send(RecentGamesUi.Interaction.OnRecentGameClick(it)) },
            leagueName = leagueName,
            noGamesTitleId = noGamesTitleId
        )
    }
}