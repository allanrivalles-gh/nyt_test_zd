package com.theathletic.boxscore.ui.modules

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.boxscore.ui.RecentForms
import com.theathletic.boxscore.ui.SoccerRecentFormHeaderModel
import com.theathletic.data.SizedImages
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.feed.ui.LocalFeedInteractor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asString
import com.theathletic.ui.utility.conditional
import com.theathletic.ui.widgets.TeamLogo

data class StandingsTableModule(
    val id: String,
    val teamsColumn: List<TeamColumnItem>,
    val statsColumns: List<List<StatsColumnItem>>,
) : FeedModuleV2 {

    override val moduleId: String = "StandingsTableModule:$id"

    sealed class TeamColumnItem {
        data class Category(
            val label: ResourceString
        ) : TeamColumnItem()

        data class Team(
            val id: String,
            val alias: String,
            val logos: SizedImages,
            val ranking: String,
            val showRanking: Boolean,
            val relegationColor: Color,
            val seeding: String,
            val showSeeding: Boolean,
            val highlighted: Boolean,
            val dividerType: DividerType,
            val isFollowable: Boolean
        ) : TeamColumnItem()
    }

    sealed class StatsColumnItem {
        data class Label(
            val text: String
        ) : StatsColumnItem()

        data class Statistic(
            val value: String,
            val highlighted: Boolean,
            val dividerType: DividerType,
            val valueType: ValueType
        ) : StatsColumnItem()

        data class RecentForm(
            val lastSix: List<SoccerRecentFormHeaderModel.SoccerRecentFormIcons>,
            val isReversed: Boolean,
            val highlighted: Boolean,
            val dividerType: DividerType
        ) : StatsColumnItem()

        enum class ValueType {
            Default,
            Win,
            Loss,
            GreaterThan,
            LessThan,
            RecentForm
        }
    }

    enum class DividerType {
        Standard,
        SolidPlayoff,
        DottedPlayOff
    }

    @Composable
    override fun Render() {
        val interactor = LocalFeedInteractor.current

        StandingsTable(
            teamsColumn = teamsColumn,
            statsColumns = statsColumns,
            onTeamClick = { teamId -> interactor.send(Interaction.OnTeamClick(teamId)) }
        )
    }

    interface Interaction {
        data class OnTeamClick(val teamId: String) : FeedInteraction
    }
}

@Composable
private fun StandingsTable(
    teamsColumn: List<StandingsTableModule.TeamColumnItem>,
    statsColumns: List<List<StandingsTableModule.StatsColumnItem>>,
    onTeamClick: (teamId: String) -> Unit
) {
    Column(modifier = Modifier.background(color = AthTheme.colors.dark200)) {
        Row {
            TeamsColumn(teamsColumn, onTeamClick)
            StatisticsTable(statsColumns)
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun TeamsColumn(
    teamsColumn: List<StandingsTableModule.TeamColumnItem>,
    onTeamClick: (teamId: String) -> Unit,
) {
    Column(modifier = Modifier.width(140.dp)) {
        teamsColumn.forEach { item ->
            when (item) {
                is StandingsTableModule.TeamColumnItem.Category -> TableCategory(item)
                is StandingsTableModule.TeamColumnItem.Team -> Team(item, onTeamClick)
            }
        }
    }
}

@Composable
private fun StatisticsTable(
    statsColumns: List<List<StandingsTableModule.StatsColumnItem>>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        statsColumns.forEach { column ->
            Column(
                modifier = Modifier
                    .width(IntrinsicSize.Max)
                    .defaultMinSize(minWidth = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                column.forEach { item ->
                    when (item) {
                        is StandingsTableModule.StatsColumnItem.Label -> StatisticLabel(item)
                        is StandingsTableModule.StatsColumnItem.Statistic -> StatisticValue(item)
                        is StandingsTableModule.StatsColumnItem.RecentForm -> RecentFormIndicators(item)
                    }
                }
            }
        }
    }
}

private val MIN_ROW_HEIGHT = 40.dp

@Composable
private fun TableCategory(category: StandingsTableModule.TeamColumnItem.Category) {
    Box(modifier = Modifier.defaultMinSize(minHeight = MIN_ROW_HEIGHT)) {
        Text(
            text = category.label.asString().uppercase(),
            style = AthTextStyle.Calibre.Utility.Medium.ExtraSmall,
            color = AthTheme.colors.dark500,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(
                    start = 16.dp,
                    end = 8.dp
                )
        )
        Divider(
            color = AthTheme.colors.dark300,
            thickness = 1.dp,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun Team(
    team: StandingsTableModule.TeamColumnItem.Team,
    onTeamClick: (teamId: String) -> Unit
) {
    Box(
        modifier = Modifier
            .defaultMinSize(minHeight = MIN_ROW_HEIGHT)
            .background(team.highlighted.toHighlightColor())
            .conditional(team.isFollowable) {
                clickable { onTeamClick(team.id) }
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(
                    start = 16.dp,
                    top = 8.dp,
                    bottom = 8.dp,
                ),
        ) {
            ShowRanking(team)
            TeamLogo(
                teamUrls = team.logos,
                preferredSize = 20.dp,
                modifier = Modifier.size(20.dp)
            )
            ShowSeeding(team)
            Text(
                text = team.alias,
                style = AthTextStyle.Calibre.Utility.Medium.Small,
                color = AthTheme.colors.dark800,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 6.dp)
            )
        }

        Box(
            modifier = Modifier
                .defaultMinSize(minHeight = MIN_ROW_HEIGHT)
                .width(4.dp)
                .background(team.relegationColor)
                .align(Alignment.CenterStart)
        )

        StandingsDivider(
            dividerType = team.dividerType,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun RowScope.ShowRanking(team: StandingsTableModule.TeamColumnItem.Team) {
    if (team.showRanking) {
        Text(
            modifier = Modifier.Companion
                .align(Alignment.CenterVertically)
                .width(24.dp)
                .padding(end = 8.dp),
            text = team.ranking,
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            color = AthTheme.colors.dark500
        )
    }
}

@Composable
private fun RowScope.ShowSeeding(team: StandingsTableModule.TeamColumnItem.Team) {
    if (team.showSeeding) {
        Text(
            text = team.seeding,
            style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
            color = AthTheme.colors.dark500,
            modifier = Modifier.Companion
                .align(Alignment.CenterVertically)
                .width(20.dp)
                .padding(start = 6.dp)
        )
    }
}

@Composable
private fun StatisticLabel(label: StandingsTableModule.StatsColumnItem.Label) {
    Box(
        modifier = Modifier.defaultMinSize(minHeight = MIN_ROW_HEIGHT)
    ) {
        Text(
            text = label.text,
            style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
            textAlign = TextAlign.Center,
            color = AthTheme.colors.dark500,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 6.dp)
                .align(Alignment.Center)
        )
        Divider(
            color = AthTheme.colors.dark300,
            thickness = 1.dp,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun StatisticValue(statistic: StandingsTableModule.StatsColumnItem.Statistic) {
    Box(
        modifier = Modifier
            .defaultMinSize(minHeight = MIN_ROW_HEIGHT)
            .background(statistic.highlighted.toHighlightColor())
    ) {
        Text(
            text = statistic.value,
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            textAlign = TextAlign.Center,
            color = statistic.valueType.toColor(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 6.dp)
                .align(Alignment.Center)
        )
        StandingsDivider(
            dividerType = statistic.dividerType,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun RecentFormIndicators(recentForm: StandingsTableModule.StatsColumnItem.RecentForm) {
    Box(
        modifier = Modifier
            .defaultMinSize(minHeight = MIN_ROW_HEIGHT)
            .background(recentForm.highlighted.toHighlightColor())
    ) {
        RecentForms(
            teamRecentForms = recentForm.lastSix,
            horizontalArrangement = Arrangement.Start,
            isReverse = recentForm.isReversed,
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .align(Alignment.CenterStart)
        )
        StandingsDivider(
            dividerType = recentForm.dividerType,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun StandingsTableModule.StatsColumnItem.ValueType.toColor() = when (this) {
    StandingsTableModule.StatsColumnItem.ValueType.Win,
    StandingsTableModule.StatsColumnItem.ValueType.GreaterThan -> AthTheme.colors.green
    StandingsTableModule.StatsColumnItem.ValueType.Loss,
    StandingsTableModule.StatsColumnItem.ValueType.LessThan -> AthTheme.colors.red
    else -> AthTheme.colors.dark800
}

@Composable
private fun Boolean.toHighlightColor() = if (this) {
    AthTheme.colors.dark300
} else {
    AthTheme.colors.dark200
}

@Composable
private fun StandingsDivider(
    dividerType: StandingsTableModule.DividerType,
    modifier: Modifier = Modifier
) {
    when (dividerType) {
        StandingsTableModule.DividerType.SolidPlayoff ->
            Divider(
                color = AthTheme.colors.dark700,
                thickness = 2.dp,
                modifier = modifier,
            )

        StandingsTableModule.DividerType.DottedPlayOff ->
            DottedPlayOffDivider(
                color = AthTheme.colors.dark700,
                modifier = modifier
            )
        else ->
            Divider(
                color = AthTheme.colors.dark300,
                thickness = 1.dp,
                modifier = modifier,
            )
    }
}

@Composable
private fun DottedPlayOffDivider(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        drawLine(
            color = color,
            start = Offset.Zero,
            end = Offset(size.width, 0f),
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(5.dp.toPx(), 5.dp.toPx()),
                phase = 0f
            ),
            strokeWidth = 2.dp.toPx()
        )
    }
}

@Preview
@Composable
private fun StandingsTableModulePreview() {
    StandingsTableModule(
        id = "uniqueId",
        teamsColumn = StandingsTableModulePreviewData.teamsColumnMock,
        statsColumns = StandingsTableModulePreviewData.statsColumnsMock,
    ).Render()
}