package com.theathletic.boxscore.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.boxscore.ui.BoxScorePreviewData.getRecentGamesMockData
import com.theathletic.data.SizedImages
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString
import com.theathletic.ui.UiModel
import com.theathletic.ui.asResourceString
import com.theathletic.ui.asString
import com.theathletic.ui.widgets.TeamLogo
import com.theathletic.ui.widgets.buttons.TwoItemToggleButton

data class BoxScoreRecentGamesUiModel(
    val id: String,
    val includeDivider: Boolean,
    val teams: RecentGamesUi.Teams,
    val firstTeamRecentGames: List<RecentGamesUi.RecentGame>,
    val secondTeamRecentGames: List<RecentGamesUi.RecentGame>

) : UiModel {
    override val stableId = "BoxScoreRecentGames:$id"
}

sealed class RecentGamesUi {
    data class Teams(
        val firstTeamName: String,
        val secondTeamName: String,
    )

    data class RecentGame(
        val id: String,
        val teamId: String,
        val date: String,
        val opponentLogoUrlList: SizedImages,
        val opponentTeamAlias: ResourceString,
        val firstTeamScore: String,
        val secondTeamScore: String,
        val isFirstTeamWinners: Boolean,
        val isSecondTeamWinners: Boolean,
        val result: ResourceString,
        val resultColor: Color,
    )

    interface Interaction {
        data class OnRecentGameClick(
            val id: String
        ) : FeedInteraction
    }
}

@Composable
fun RecentGames(
    includeDivider: Boolean,
    teams: RecentGamesUi.Teams,
    firstTeamRecentGames: List<RecentGamesUi.RecentGame>,
    secondTeamRecentGames: List<RecentGamesUi.RecentGame>,
    @StringRes titleId: Int = R.string.box_score_last_games_title,
    @StringRes noGamesTitleId: Int = R.string.box_score_recent_games_not_available,
    leagueName: String?,
    onRecentGameClick: (String) -> Unit
) {
    var isFirstTeamSelected by remember { mutableStateOf(firstTeamRecentGames.isNotEmpty()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
    ) {
        BoxScoreHeaderTitle(titleId, leagueName)

        TwoItemToggleButton(
            modifier = Modifier.padding(horizontal = 16.dp),
            itemOneLabel = teams.firstTeamName.asResourceString(),
            itemTwoLabel = teams.secondTeamName.asResourceString(),
            isFirstItemSelected = isFirstTeamSelected,
            onTwoItemToggleSelected = { isFirstTeamSelected = !isFirstTeamSelected }
        )

        Spacer(modifier = Modifier.height(20.dp))

        val recentGames = if (isFirstTeamSelected) firstTeamRecentGames else secondTeamRecentGames
        var recentGameCellHeight by remember { mutableStateOf(0) }
        val density = LocalDensity.current.density
        if (recentGames.isEmpty()) {
            val placeholderBoxHeight = calculatePlaceholderBoxHeight(recentGameCellHeight, firstTeamRecentGames, secondTeamRecentGames)
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .height(placeholderBoxHeight.dp)
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(8.dp)
                    )
                    .background(color = AthTheme.colors.dark300)
            ) {
                Text(
                    text = stringResource(id = noGamesTitleId),
                    color = AthTheme.colors.dark700,
                    textAlign = TextAlign.Center,
                    style = AthTextStyle.Calibre.Utility.Medium.Large,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            recentGames.forEach { recentGame ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onRecentGameClick(recentGame.id) }
                        .onGloballyPositioned {
                            recentGameCellHeight = (it.size.height / density).toInt()
                        }
                ) {
                    RecentGameRow(recentGame)
                }
            }
        }
        BoxScoreFooterDivider(includeDivider)
    }
}

@Composable
private fun calculatePlaceholderBoxHeight(
    recentGameCellHeight: Int,
    firstTeamRecentGames: List<RecentGamesUi.RecentGame>,
    secondTeamRecentGames: List<RecentGamesUi.RecentGame>
) = (recentGameCellHeight * if (firstTeamRecentGames.isNotEmpty()) firstTeamRecentGames.size else secondTeamRecentGames.size)

@Composable
private fun RecentGameRow(recentGame: RecentGamesUi.RecentGame) {
    Row(
        modifier = Modifier
            .padding(
                horizontal = 16.dp,
                vertical = 16.dp
            )
            .fillMaxWidth(),
        verticalAlignment = CenterVertically,
    ) {
        DateCell(
            recentGame.date,
            modifier = Modifier
                .weight(0.275f)
                .align(CenterVertically)
        )

        TeamCell(
            recentGame.opponentTeamAlias,
            recentGame.opponentLogoUrlList,
            modifier = Modifier.weight(0.275f)
        )

        DetailsCell(
            recentGame.firstTeamScore,
            recentGame.isFirstTeamWinners,
            recentGame.secondTeamScore,
            recentGame.isSecondTeamWinners,
            recentGame.result,
            recentGame.resultColor,
            modifier = Modifier.weight(0.425f)
        )

        ChevronCell(
            modifier = Modifier.weight(0.1f)
        )
    }
    RowDivider()
}

@Composable
private fun RowDivider() {
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        color = AthTheme.colors.dark300
    )
}

@Composable
private fun ChevronCell(modifier: Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = CenterEnd
    ) {
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = AthTheme.colors.dark700,
            modifier = Modifier.size(30.dp)
        )
    }
}

@Composable
private fun DateCell(date: String, modifier: Modifier) {
    Box(
        modifier = modifier
    ) {
        Text(
            text = date,
            color = AthTheme.colors.dark700,
            style = AthTextStyle.Calibre.Utility.Regular.Large,
        )
    }
}

@Composable
private fun TeamCell(opponentTeamAlias: ResourceString, opponentLogoUrlList: SizedImages, modifier: Modifier) {

    Box(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TeamLogo(
                teamUrls = opponentLogoUrlList,
                preferredSize = 24.dp,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = opponentTeamAlias.asString(),
                color = AthTheme.colors.dark700,
                style = AthTextStyle.Calibre.Utility.Regular.Large,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun DetailsCell(
    firstTeamScore: String,
    isFirstTeamWinners: Boolean,
    secondTeamScore: String,
    isSecondTeamWinners: Boolean,
    result: ResourceString,
    resultColor: Color,
    modifier: Modifier
) {
    val highlightTeam = isFirstTeamWinners.not() && isSecondTeamWinners.not()
    Box(
        modifier = modifier,
        contentAlignment = CenterStart
    ) {
        Row {
            Row(
                modifier = Modifier.weight(0.6f),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = firstTeamScore,
                    color = if (isFirstTeamWinners || highlightTeam) AthTheme.colors.dark700 else AthTheme.colors.dark500,
                    textAlign = TextAlign.End,
                    style = AthTextStyle.Calibre.Utility.Regular.Large
                )
                Text(
                    text = "-",
                    color = AthTheme.colors.dark500,
                    textAlign = TextAlign.End,
                    style = AthTextStyle.Calibre.Utility.Regular.Large,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Text(
                    text = secondTeamScore,
                    color = if (isSecondTeamWinners || highlightTeam) AthTheme.colors.dark700 else AthTheme.colors.dark500,
                    textAlign = TextAlign.End,
                    style = AthTextStyle.Calibre.Utility.Regular.Large,
                )
            }

            Text(
                text = result.asString(),
                color = resultColor,
                textAlign = TextAlign.Center,
                maxLines = 1,
                style = AthTextStyle.Calibre.Utility.Medium.Large,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(0.4f)
            )
        }
    }
}

@Preview
@Composable
private fun RecentGames_Preview() {
    val recentGamesMock = getRecentGamesMockData()
    RecentGames(
        includeDivider = recentGamesMock.includeDivider,
        teams = recentGamesMock.teams,
        firstTeamRecentGames = recentGamesMock.firstTeamRecentGames,
        secondTeamRecentGames = recentGamesMock.secondTeamRecentGames,
        titleId = R.string.box_score_last_games_title,
        noGamesTitleId = R.string.box_score_recent_matches_not_available,
        onRecentGameClick = { },
        leagueName = null
    )
}

@Preview(device = Devices.PIXEL)
@Composable
private fun RecentGames_PreviewSmallDevice() {
    val recentGamesMock = getRecentGamesMockData()
    RecentGames(
        includeDivider = recentGamesMock.includeDivider,
        teams = recentGamesMock.teams,
        firstTeamRecentGames = recentGamesMock.firstTeamRecentGames,
        secondTeamRecentGames = recentGamesMock.secondTeamRecentGames,
        titleId = R.string.box_score_last_games_title,
        noGamesTitleId = R.string.box_score_recent_matches_not_available,
        onRecentGameClick = { },
        leagueName = null
    )
}

@Preview(device = Devices.PIXEL_4_XL)
@Composable
private fun RecentGames_PreviewLargeDevice() {
    val recentGamesMock = getRecentGamesMockData()
    RecentGames(
        includeDivider = recentGamesMock.includeDivider,
        teams = recentGamesMock.teams,
        firstTeamRecentGames = recentGamesMock.firstTeamRecentGames,
        secondTeamRecentGames = recentGamesMock.secondTeamRecentGames,
        titleId = R.string.box_score_last_games_title,
        noGamesTitleId = R.string.box_score_recent_matches_not_available,
        onRecentGameClick = { },
        leagueName = null
    )
}

@Preview
@Composable
private fun RecentGames_PreviewLight() {
    AthleticTheme(lightMode = true) {
        val recentGamesMock = getRecentGamesMockData()
        RecentGames(
            includeDivider = recentGamesMock.includeDivider,
            teams = recentGamesMock.teams,
            firstTeamRecentGames = recentGamesMock.firstTeamRecentGames,
            secondTeamRecentGames = recentGamesMock.secondTeamRecentGames,
            titleId = R.string.box_score_last_games_title,
            noGamesTitleId = R.string.box_score_recent_matches_not_available,
            onRecentGameClick = { },
            leagueName = null
        )
    }
}