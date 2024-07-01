package com.theathletic.scores.ui

import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.datetime.DateUtility
import com.theathletic.datetime.Datetime
import com.theathletic.datetime.formatter.DisplayFormat
import com.theathletic.extension.toStringOrEmpty
import com.theathletic.scores.data.local.Schedule
import com.theathletic.scores.ui.gamecells.GameCellModel
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asResourceString
import java.text.SimpleDateFormat
import java.util.Locale

fun Schedule.mapToTabUiModel(): List<ScoresFeedUI.DayTabItem> {
    // If we have a single group and no label data for that nav tab then send back
    // an empty nav tabs list which will be hidden by the UI
    if (groups.size == 1) {
        val navItem = groups[0].navItem
        if (navItem.day == null && navItem.primaryLabel == null && navItem.secondaryLabel == null) return emptyList()
    }
    return groups.map { group -> group.navItem.toTabUi() }
}

fun List<Schedule.FilterValue>.mapToUiModel(): List<ScoresFeedUI.ScheduleFilter> {
    return this.map { it.scheduleFilterUi() }
}

fun Schedule.FilterValue.scheduleFilterUi() = ScoresFeedUI.ScheduleFilter(
    id = this.id,
    isDefault = this.isDefault,
    label = this.label
)

fun Schedule.defaultSelectedTab(currentIndex: Int): Int {
    if (currentIndex != -1) return currentIndex
    val defaultIndex = groups.indexOfFirst { it.navItem.isDefault }
    return if (defaultIndex == -1) 0 else defaultIndex
}

fun Schedule.mapToFeedUiModel(
    selectedTab: Int,
    isUnitedStatesOrCanada: Boolean,
    dateUtility: DateUtility
): List<ScoresFeedUI.FeedGroup> {
    if (selectedTab < 0 || groups.isEmpty()) return emptyList()
    return groups[selectedTab].toUiModel(isUnitedStatesOrCanada, dateUtility)
}

private fun Schedule.NavItem.toTabUi(): ScoresFeedUI.DayTabItem {
    return if (day != null) {
        // Day based schedule group
        val (topLabel, bottomLabel) = formatDay(day)
        ScoresFeedUI.DayTabItem(
            id = id,
            labelTop = topLabel.asResourceString(),
            labelBottom = bottomLabel.asResourceString(),
            payload = ScoresFeedUI.AnalyticsPayload(
                slate = "$topLabel $bottomLabel"
            )
        )
    } else {
        ScoresFeedUI.DayTabItem(
            id = id,
            labelTop = ResourceString.StringWrapper(primaryLabel.orEmpty()),
            labelBottom = ResourceString.StringWrapper(secondaryLabel.orEmpty()),
            payload = ScoresFeedUI.AnalyticsPayload(
                slate = "${primaryLabel.orEmpty()} ${secondaryLabel.orEmpty()}"
            )
        )
    }
}

fun Schedule.Group.toUiModel(
    isUnitedStatesOrCanada: Boolean,
    dateUtility: DateUtility
): List<ScoresFeedUI.FeedGroup> {
    return sections.mapIndexed { sectionIndex, section ->
        val leagueId = section.league?.leagueId
        ScoresFeedUI.FeedGroup(
            header = ScoresFeedUI.SectionHeader(
                id = section.id,
                title = section.title,
                subTitle = section.subTitle,
                canNavigate = leagueId != null,
                leagueId = leagueId,
                index = sectionIndex,
            ),
            games = section.games.mapIndexed { gameIndex, game ->
                val isGameADraw = game.isGameADraw()
                GameCellModel(
                    gameId = game.gameId,
                    firstTeam = game.team1.toUiModel(game.state, isGameADraw),
                    secondTeam = game.team2.toUiModel(game.state, isGameADraw),
                    title = game.header.orEmpty(),
                    showTitle = game.header.isNullOrBlank().not(),
                    discussionLinkText = game.widget?.toDiscussionText,
                    infoWidget = game.info.toUiModel(isUnitedStatesOrCanada, dateUtility),
                    impressionPayload = game.toImpressionsPayload(sectionIndex, gameIndex),
                    showTeamRanking = game.team1.ranking != null || game.team2.ranking != null
                )
            },
            footer = section.widget?.let { widget ->
                when (widget) {
                    is Schedule.Widget.AllGamesLink -> ScoresFeedUI.SectionFooter(
                        id = section.id,
                        label = widget.linkText,
                        leagueId = leagueId,
                        index = sectionIndex,
                    )
                    else -> null
                }
            },
            widget = section.widget?.let { widget ->
                when (widget) {
                    is Schedule.Widget.GameTickets -> ScoresFeedUI.GameTicketsWidget(
                        logosDark = widget.logosDark,
                        logosLight = widget.logosLight,
                        text = widget.text,
                        uri = widget.uri,
                        provider = widget.provider
                    )
                    else -> null
                }
            }
        )
    }
}

private fun Schedule.Team.toUiModel(
    gameState: Schedule.GameState,
    isGameADraw: Boolean
) = GameCellModel.Team(
    logo = logos,
    name = name,
    teamDetails = toUiModel() ?: GameCellModel.TeamDetails.PreGame(pregameLabel = ""),
    ranking = ranking.toStringOrEmpty(),
    isDimmed = dimTeamContent(gameState, isGameADraw),
)

private fun Schedule.Team.toUiModel(): GameCellModel.TeamDetails? {
    val info = info ?: return null
    return when (info) {
        is Schedule.LivePostGame -> info.toUiModel(icons)
        is Schedule.PreGame -> GameCellModel.TeamDetails.PreGame(pregameLabel = info.text)
        else -> null
    }
}

private fun Schedule.LivePostGame.toUiModel(icons: List<Schedule.TeamIcon>) =
    GameCellModel.TeamDetails.InAndPostGame(
        score = score.orEmpty(),
        penaltyGoals = penaltyScore,
        icon = icons.toUiIcon(),
        isWinner = isWinner
    )

// Only care about first icon as there will be only one. List is for future proofing.
private fun List<Schedule.TeamIcon>.toUiIcon() = when (firstOrNull()) {
    Schedule.TeamIcon.AMERICAN_FOOTBALL_POSSESSION -> GameCellModel.EventIcon.POSSESSION
    Schedule.TeamIcon.SOCCER_RED_CARD -> GameCellModel.EventIcon.RED_CARD
    else -> null
}

private fun Schedule.GameInfo.toUiModel(
    isUnitedStatesOrCanada: Boolean,
    dateUtility: DateUtility
): GameCellModel.InfoWidget {
    return if (widget != null && widget is Schedule.Widget.BaseballBases) {
        GameCellModel.InfoWidget.BaseballWidget(
            infos = textContent.mapNotNull { it.toUiModel(isUnitedStatesOrCanada, dateUtility) },
            occupiedBases = widget.loadedBases
        )
    } else {
        GameCellModel.InfoWidget.LabelWidget(
            infos = textContent.mapNotNull { it.toUiModel(isUnitedStatesOrCanada, dateUtility) }
        )
    }
}

private fun Schedule.TextContent.toUiModel(
    isUnitedStatesOrCanada: Boolean,
    dateUtility: DateUtility
) = when (this) {
    is Schedule.TextContent.DateTime -> toUiModel(dateUtility)
    is Schedule.TextContent.Odds -> toUiModel(isUnitedStatesOrCanada)
    is Schedule.TextContent.Standard -> type.toUiModel(text)
    else -> null
}

private fun Schedule.TextContent.DateTime.toUiModel(dateUtility: DateUtility) =
    when (format) {
        Schedule.TextContent.DateType.DATE_AND_TIME -> type.toUiModel(formatGameDateTime(dateTime, dateUtility))
        Schedule.TextContent.DateType.DATE -> type.toUiModel(formatGameDate(dateTime, dateUtility))
        Schedule.TextContent.DateType.TIME -> type.toUiModel(formatGameTime(dateTime, dateUtility))
        else -> null
    }

private fun Schedule.TextContent.Odds.toUiModel(isUnitedStatesOrCanada: Boolean) =
    type.toUiModel(if (isUnitedStatesOrCanada) usOdds else fractionOdds)

private fun Schedule.TextContent.TextType.toUiModel(value: String): GameCellModel.GameInfo {
    return when (this) {
        Schedule.TextContent.TextType.DATE_TIME -> GameCellModel.GameInfo.DateTimeStatus(value)
        Schedule.TextContent.TextType.DEFAULT -> GameCellModel.GameInfo.Default(value)
        Schedule.TextContent.TextType.LIVE -> GameCellModel.GameInfo.Live(value)
        Schedule.TextContent.TextType.SITUATION -> GameCellModel.GameInfo.Situation(value)
        Schedule.TextContent.TextType.STATUS -> GameCellModel.GameInfo.Status(value)
        else -> GameCellModel.GameInfo.Default(value)
    }
}

private val Schedule.Widget.toDiscussionText: String?
    get() = if (this is Schedule.Widget.DiscussionLink) text else null

private fun Schedule.Game.toImpressionsPayload(groupIndex: Int, gameIndex: Int) =
    ImpressionPayload(
        objectType = "game_id",
        objectId = gameId,
        element = "home",
        pageOrder = groupIndex,
        vIndex = gameIndex.toLong()
    )

private fun formatDay(day: String): Pair<String, String> {
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(day)?.let { convertedDate ->
        val formattedDate = SimpleDateFormat("EEE:MMM d", Locale.getDefault())
            .format(convertedDate).split(":")
        if (formattedDate.size == 2) {
            Pair(formattedDate[0], formattedDate[1])
        } else {
            Pair("-", "-")
        }
    } ?: Pair("-", "-")
}

private fun formatGameDateTime(dateTime: Datetime, dateUtility: DateUtility): String {
    // Doesn't seem like we will get this from the backend but if we do we do the same as iOS in this instance
    return "${formatGameDate(dateTime, dateUtility)}\n${formatGameTime(dateTime, dateUtility)}"
}

private fun formatGameDate(dateTime: Datetime, dateUtility: DateUtility) =
    dateUtility.formatGMTDate(dateTime, DisplayFormat.WEEKDAY_MONTH_DATE_ABBREVIATED)

private fun formatGameTime(dateTime: Datetime, dateUtility: DateUtility) =
    dateUtility.formatGMTDate(dateTime, DisplayFormat.HOURS_MINUTES)