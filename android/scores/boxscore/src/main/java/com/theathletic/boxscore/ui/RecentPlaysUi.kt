package com.theathletic.boxscore.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.boxscore.ui.modules.RecentPlaysModule
import com.theathletic.boxscore.ui.playbyplay.AmericanFootballRecentPlay
import com.theathletic.boxscore.ui.playbyplay.HockeyShootoutPlay
import com.theathletic.boxscore.ui.playbyplay.Play
import com.theathletic.boxscore.ui.playbyplay.StoppagePlay
import com.theathletic.boxscore.ui.playbyplay.TimeoutPlay
import com.theathletic.data.SizedImages
import com.theathletic.feed.ui.FeedInteractor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString
import com.theathletic.ui.UiModel

class BoxScoreRecentPlays {
    data class BoxScoreRecentPlaysUiModel(
        val id: String,
        val includeDivider: Boolean,
        val recentPlayList: List<RecentPlays>
    ) : UiModel {
        override val stableId = "BoxScoreRecentPlays:$id"
    }

    interface Interactor {
        fun onFullPlayByPlayClick()
    }

    sealed class RecentPlays

    data class Timeout(
        val id: String,
        val title: String,
        val showDivider: Boolean
    ) : RecentPlays()

    data class Stoppage(
        val id: String,
        val title: String,
        val description: String,
        val showDivider: Boolean
    ) : RecentPlays()

    data class HockeyShootout(
        val id: String,
        val headshots: SizedImages,
        val teamLogos: SizedImages,
        val teamColor: Color,
        val playerName: String,
        val teamAlias: String,
        val description: String,
        val isGoal: Boolean,
        val showDivider: Boolean
    ) : RecentPlays()

    data class Play(
        val id: String,
        val teamLogos: SizedImages,
        val title: String?,
        val teamColor: String?,
        val description: String,
        val clock: String,
        val awayTeamAlias: String? = null,
        val homeTeamAlias: String? = null,
        val awayTeamScore: String? = null,
        val homeTeamScore: String? = null,
        val showScores: Boolean = false,
        val showDivider: Boolean,
        val possession: ResourceString? = null
    ) : RecentPlays()

    data class AmericanFootballPlay(
        val title: String,
        val description: String?,
        val possession: ResourceString?,
        val showDivider: Boolean,
        val teamLogos: SizedImages,
        val awayTeamAlias: String?,
        val homeTeamAlias: String?,
        val awayTeamScore: String?,
        val homeTeamScore: String?,
        val isScoringPlay: Boolean,
        val clock: String,
    ) : RecentPlays()
}

@Suppress("LongMethod")
@Composable
fun RecentPlays(
    recentPlays: List<RecentPlaysModule.RecentPlay>,
    interactor: FeedInteractor
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = AthTheme.colors.dark200)
    ) {
        BoxScoreHeaderTitle(R.string.box_score_recent_plays_title)
        recentPlays.forEach { play ->
            when (play) {
                is RecentPlaysModule.RecentPlay.Play -> {
                    Play(
                        teamLogos = play.teamLogos,
                        teamColor = null,
                        title = play.title,
                        description = play.description,
                        clock = play.clock,
                        awayTeamAlias = play.awayTeamAlias,
                        homeTeamAlias = play.homeTeamAlias,
                        awayTeamScore = play.awayTeamScore,
                        homeTeamScore = play.homeTeamScore,
                        showScores = play.showScores,
                        showDivider = true
                    )
                }
                is RecentPlaysModule.RecentPlay.HockeyShootout -> {
                    HockeyShootoutPlay(
                        headshots = play.headshots,
                        teamLogos = play.teamLogos,
                        teamColor = play.teamColor,
                        playerName = play.playerName,
                        teamAlias = play.teamAlias,
                        description = play.description,
                        isGoal = play.isGoal,
                        showDivider = true
                    )
                }
                is RecentPlaysModule.RecentPlay.Stoppage -> {
                    StoppagePlay(
                        title = play.title,
                        description = play.description,
                        showDivider = true
                    )
                }
                is RecentPlaysModule.RecentPlay.Timeout -> {
                    TimeoutPlay(
                        title = play.title,
                        showDivider = true
                    )
                }
                is RecentPlaysModule.RecentPlay.AmericanFootballPlay -> {
                    AmericanFootballRecentPlay(
                        title = play.title,
                        teamLogos = play.teamLogos,
                        teamColor = play.teamColor,
                        description = play.description,
                        possession = play.possession,
                        homeTeamScore = play.homeTeamScore,
                        homeTeamAlias = play.homeTeamAlias,
                        awayTeamAlias = play.awayTeamAlias,
                        awayTeamScore = play.awayTeamScore,
                        isScoringPlay = play.isScoringPlay,
                        showDivider = play.showDivider,
                        clock = play.clock
                    )
                }
            }
        }
        RecentPlaysFooter(interactor)
        BoxScoreFooterDivider(includeBottomBar = false)
    }
}

@Composable
internal fun RecentPlaysFooter(interactor: FeedInteractor) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
            .padding(top = 15.dp)
            .clickable {
                interactor.send(RecentPlaysModule.Interaction.OnFullPlayByPlayClick)
            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(id = R.string.box_score_recent_plays_full_play),
            style = AthTextStyle.Calibre.Utility.Regular.ExtraLarge,
            color = AthTheme.colors.dark500,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 10.dp)
        )
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = AthTheme.colors.dark500,
            modifier = Modifier.padding(vertical = 10.dp)
        )
    }
}

@Suppress("LongMethod")
@Deprecated("Use RecentPlaysModule")
@Composable
fun RecentPlays(
    includeHeaderDivider: Boolean,
    includeFooterDivider: Boolean,
    recentPlays: List<BoxScoreRecentPlays.RecentPlays>,
    interactor: BoxScoreRecentPlays.Interactor
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = AthTheme.colors.dark200)
    ) {

        if (includeHeaderDivider) {
            BoxScoreHeaderDivider()
        }

        BoxScoreHeaderTitle(R.string.box_score_recent_plays_title)

        recentPlays.forEach { row ->
            when (row) {
                is BoxScoreRecentPlays.Play -> {
                    Play(
                        teamLogos = row.teamLogos,
                        teamColor = null,
                        title = row.title,
                        description = row.description,
                        clock = row.clock,
                        awayTeamAlias = row.awayTeamAlias,
                        homeTeamAlias = row.homeTeamAlias,
                        awayTeamScore = row.awayTeamScore,
                        homeTeamScore = row.homeTeamScore,
                        showScores = row.showScores,
                        showDivider = row.showDivider
                    )
                }
                is BoxScoreRecentPlays.HockeyShootout -> {
                    HockeyShootoutPlay(
                        headshots = row.headshots,
                        teamLogos = row.teamLogos,
                        teamColor = row.teamColor,
                        playerName = row.playerName,
                        teamAlias = row.teamAlias,
                        description = row.description,
                        isGoal = row.isGoal,
                        showDivider = row.showDivider
                    )
                }
                is BoxScoreRecentPlays.Stoppage -> {
                    StoppagePlay(
                        title = row.title,
                        description = row.description,
                        showDivider = row.showDivider
                    )
                }
                is BoxScoreRecentPlays.Timeout -> {
                    TimeoutPlay(
                        title = row.title,
                        showDivider = row.showDivider
                    )
                }
                is BoxScoreRecentPlays.AmericanFootballPlay -> {
                    AmericanFootballRecentPlay(
                        title = row.title,
                        teamLogos = row.teamLogos,
                        teamColor = null,
                        description = row.description,
                        possession = row.possession,
                        homeTeamScore = row.homeTeamScore,
                        homeTeamAlias = row.homeTeamAlias,
                        awayTeamAlias = row.awayTeamAlias,
                        awayTeamScore = row.awayTeamScore,
                        isScoringPlay = row.isScoringPlay,
                        showDivider = row.showDivider,
                        clock = row.clock
                    )
                }
            }
        }

        RecentPlaysFooter(interactor)

        BoxScoreFooterDivider(includeBottomBar = includeFooterDivider)
    }
}

@Deprecated("UiModel version no longer required - will be deleted")
@Composable
internal fun RecentPlaysFooter(interactor: BoxScoreRecentPlays.Interactor) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
            .padding(top = 15.dp)
            .clickable {
                interactor.onFullPlayByPlayClick()
            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Text(
            text = stringResource(id = R.string.box_score_recent_plays_full_play),
            style = AthTextStyle.Calibre.Utility.Regular.ExtraLarge,
            color = AthTheme.colors.dark500,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 10.dp)
        )

        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = AthTheme.colors.dark500,
            modifier = Modifier.padding(vertical = 10.dp)
        )
    }
}

@Preview
@Composable
private fun RecentPlays_Preview() {
    RecentPlays(
        true,
        true,
        BoxScorePreviewData.playByPlayList,
        BoxScorePreviewData.fullPlayByPlayInteractor
    )
}

@Preview
@Composable
private fun RecentPlays_PreviewLight() {
    AthleticTheme(lightMode = true) {
        RecentPlays(
            true,
            true,
            BoxScorePreviewData.playByPlayList,
            BoxScorePreviewData.fullPlayByPlayInteractor
        )
    }
}