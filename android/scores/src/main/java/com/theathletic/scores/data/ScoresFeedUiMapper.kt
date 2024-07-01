package com.theathletic.scores.data

import com.theathletic.analytics.impressions.ImpressionPayload
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.datetime.DateUtility
import com.theathletic.datetime.Datetime
import com.theathletic.datetime.formatter.DisplayFormat
import com.theathletic.extension.toStringOrEmpty
import com.theathletic.gamedetail.data.local.GameStatus
import com.theathletic.scores.R
import com.theathletic.scores.data.local.ScoresFeedAllGamesWidgetBlock
import com.theathletic.scores.data.local.ScoresFeedBaseballWidgetBlock
import com.theathletic.scores.data.local.ScoresFeedBlock
import com.theathletic.scores.data.local.ScoresFeedDateTimeFormat
import com.theathletic.scores.data.local.ScoresFeedDateTimeTextBlock
import com.theathletic.scores.data.local.ScoresFeedDiscussionWidgetBlock
import com.theathletic.scores.data.local.ScoresFeedInfoBlock
import com.theathletic.scores.data.local.ScoresFeedLeagueGroup
import com.theathletic.scores.data.local.ScoresFeedLocalModel
import com.theathletic.scores.data.local.ScoresFeedOddsTextBlock
import com.theathletic.scores.data.local.ScoresFeedStandardTextBlock
import com.theathletic.scores.data.local.ScoresFeedTeamBlock
import com.theathletic.scores.data.local.ScoresFeedTeamGameInfoBlock
import com.theathletic.scores.data.local.ScoresFeedTeamIcon
import com.theathletic.scores.data.local.ScoresFeedTeamPregameInfoBlock
import com.theathletic.scores.data.local.ScoresFeedTextBlock
import com.theathletic.scores.data.local.ScoresFeedTextType
import com.theathletic.scores.data.local.ScoresFeedWidgetBlock
import com.theathletic.scores.ui.ScoresFeedUI
import com.theathletic.scores.ui.gamecells.GameCellModel
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asResourceString
import com.theathletic.utility.LocaleUtility
import com.theathletic.utility.safeLet
import java.text.SimpleDateFormat
import java.util.Locale

class ScoresFeedUiMapper @AutoKoin constructor(
    private val dateUtility: DateUtility,
    private val localeUtility: LocaleUtility,
) {
    fun mapToDayTabBarUi(model: ScoresFeedLocalModel?): List<ScoresFeedUI.DayTabItem> {
        model ?: return emptyList()
        return model.days.map { feedDay ->
            val (labelTop, labelBottom) = if (feedDay.isTopGames) {
                Pair(
                    ResourceString.StringWithParams(R.string.scores_feed_top_games_top_label),
                    ResourceString.StringWithParams(R.string.scores_feed_top_games_games_label),
                )
            } else {
                formatDay(feedDay.day)
            }
            ScoresFeedUI.DayTabItem(
                id = feedDay.day,
                labelTop = labelTop,
                labelBottom = labelBottom,
            )
        }
    }

    fun mapToDayFeedUi(model: ScoresFeedLocalModel?, selectedDayIndex: Int): List<ScoresFeedUI.FeedGroup> {
        if (model == null || selectedDayIndex < 0) return emptyList()

        return model.days[selectedDayIndex].groups.mapIndexed { groupIndex, group ->
            val leagueId = if (group is ScoresFeedLeagueGroup) group.league.legacyId else null
            ScoresFeedUI.FeedGroup(
                header = ScoresFeedUI.SectionHeader(
                    id = group.id,
                    title = group.title,
                    subTitle = group.subTitle,
                    canNavigate = leagueId != null,
                    leagueId = leagueId,
                    index = groupIndex,
                ),
                games = group.blocks
                    .distinctBy { it.gameId }
                    .mapIndexed { index, block ->
                        GameCellModel(
                            gameId = block.gameId,
                            firstTeam = block.gameBlock.firstTeam.toUi(
                                gameStatus = block.gameBlock.gameStatus, otherTeam = block.gameBlock.secondTeam
                            ),
                            secondTeam = block.gameBlock.secondTeam.toUi(
                                gameStatus = block.gameBlock.gameStatus, otherTeam = block.gameBlock.firstTeam
                            ),
                            title = block.header.orEmpty(),
                            showTitle = block.header.isNullOrBlank().not(),
                            discussionLinkText = block.widget?.toDiscussionText,
                            infoWidget = block.infoBlock.toUi(),
                            impressionPayload = block.toImpressionsPayload(groupIndex, index),
                            showTeamRanking = block.gameBlock.firstTeam.ranking != null || block.gameBlock.secondTeam.ranking != null
                        )
                    },
                footer = group.widget?.let { widget ->
                    when (widget) {
                        is ScoresFeedAllGamesWidgetBlock -> ScoresFeedUI.SectionFooter(
                            id = widget.id,
                            label = widget.linkText,
                            leagueId = leagueId,
                            index = groupIndex,
                        )
                        else -> null
                    }
                },
                widget = null
            )
        }
    }

    private fun ScoresFeedTeamBlock.toUi(
        gameStatus: GameStatus,
        otherTeam: ScoresFeedTeamBlock
    ) = GameCellModel.Team(
        logo = logos,
        name = name,
        teamDetails = toDetailsUi() ?: GameCellModel.TeamDetails.PreGame(pregameLabel = ""),
        ranking = ranking.toStringOrEmpty(),
        isDimmed = isTeamContentDimmed(isTbd, gameStatus, otherTeam),
    )

    private fun ScoresFeedTeamBlock.isTeamContentDimmed(
        isTbd: Boolean,
        gameStatus: GameStatus,
        otherTeam: ScoresFeedTeamBlock
    ): Boolean {
        return when {
            isTbd -> true
            gameStatus == GameStatus.CANCELED -> true
            gameStatus == GameStatus.FINAL && isTeamTheWinner.not() && isGameADraw(otherTeam).not() -> true
            else -> false
        }
    }

    private fun ScoresFeedTeamBlock.isGameADraw(otherTeam: ScoresFeedTeamBlock): Boolean {
        val thisTeamInfo = if (teamInfo is ScoresFeedTeamGameInfoBlock) teamInfo else null
        val otherTeamInfo = if (otherTeam.teamInfo is ScoresFeedTeamGameInfoBlock) otherTeam.teamInfo else null

        return safeLet(thisTeamInfo, otherTeamInfo) { safeThisTeamInfo, safeOtherTeamInfo ->
            safeThisTeamInfo.isWinner.not() && safeOtherTeamInfo.isWinner.not()
        } ?: false
    }

    private val ScoresFeedTeamBlock.isTeamTheWinner: Boolean
        get() = teamInfo is ScoresFeedTeamGameInfoBlock && teamInfo.isWinner

    private fun ScoresFeedTeamBlock.toDetailsUi(): GameCellModel.TeamDetails? {
        val info = teamInfo ?: return null
        return when (info) {
            is ScoresFeedTeamGameInfoBlock -> info.toDetailsUi(icons)
            is ScoresFeedTeamPregameInfoBlock -> GameCellModel.TeamDetails.PreGame(pregameLabel = info.text)
            else -> null
        }
    }

    private fun ScoresFeedTeamGameInfoBlock.toDetailsUi(icons: List<ScoresFeedTeamIcon>) =
        GameCellModel.TeamDetails.InAndPostGame(
            score = score.orEmpty(), penaltyGoals = penaltyScore, icon = icons.toUiIcon(), isWinner = isWinner
        )

    // Only care about first icon as there will be only one. List is for future proofing.
    private fun List<ScoresFeedTeamIcon>.toUiIcon() = when (firstOrNull()) {
        ScoresFeedTeamIcon.AmericanFootballPossession -> GameCellModel.EventIcon.POSSESSION
        ScoresFeedTeamIcon.SoccerRedCard -> GameCellModel.EventIcon.RED_CARD
        else -> null
    }

    private fun ScoresFeedInfoBlock.toUi(): GameCellModel.InfoWidget {
        return if (widget != null && widget is ScoresFeedBaseballWidgetBlock) {
            GameCellModel.InfoWidget.BaseballWidget(
                infos = text.mapNotNull { it.toUi() }, occupiedBases = widget.loadedBases
            )
        } else {
            GameCellModel.InfoWidget.LabelWidget(infos = text.mapNotNull { it.toUi() })
        }
    }

    private val ScoresFeedWidgetBlock.toDiscussionText: String?
        get() = when (this) {
            is ScoresFeedDiscussionWidgetBlock -> text
            else -> null
        }

    private fun ScoresFeedTextBlock.toUi() = when (this) {
        is ScoresFeedDateTimeTextBlock -> toDateTimeUi
        is ScoresFeedOddsTextBlock -> toOddsUi
        is ScoresFeedStandardTextBlock -> toTextUi
        else -> null
    }

    private val ScoresFeedDateTimeTextBlock.toDateTimeUi: GameCellModel.GameInfo?
        get() = when (format) {
            ScoresFeedDateTimeFormat.DateAndTime -> type.toUi(formatGameDateTime(dateTime))
            ScoresFeedDateTimeFormat.Date -> type.toUi(formatGameDate(dateTime))
            ScoresFeedDateTimeFormat.Time -> type.toUi(formatGameTime(dateTime))
            else -> null
        }

    private val ScoresFeedOddsTextBlock.toOddsUi: GameCellModel.GameInfo?
        get() = if (localeUtility.isUnitedStatesOrCanada()) {
            type.toUi(usOdds)
        } else {
            type.toUi(fractionOdds)
        }

    private val ScoresFeedStandardTextBlock.toTextUi: GameCellModel.GameInfo?
        get() = type.toUi(text)

    private fun ScoresFeedTextType.toUi(value: String): GameCellModel.GameInfo? {
        return when (this) {
            ScoresFeedTextType.DateTime -> GameCellModel.GameInfo.DateTimeStatus(value)
            ScoresFeedTextType.Default -> GameCellModel.GameInfo.Default(value)
            ScoresFeedTextType.Live -> GameCellModel.GameInfo.Live(value)
            ScoresFeedTextType.Situation -> GameCellModel.GameInfo.Situation(value)
            ScoresFeedTextType.Status -> GameCellModel.GameInfo.Status(value)
            else -> null
        }
    }

    private fun ScoresFeedBlock.toImpressionsPayload(groupIndex: Int, gameIndex: Int) =
        ImpressionPayload(
            objectType = "game_id",
            objectId = gameId,
            element = "home",
            pageOrder = groupIndex,
            vIndex = gameIndex.toLong()
        )

    private fun formatDay(day: String): Pair<ResourceString, ResourceString> {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(day)?.let { convertedDate ->
            val formattedDate = SimpleDateFormat("EEE:MMM d", Locale.getDefault())
                .format(convertedDate).split(":")
            if (formattedDate.size == 2) {
                Pair(formattedDate[0].asResourceString(), formattedDate[1].asResourceString())
            } else {
                Pair("-".asResourceString(), "-".asResourceString())
            }
        } ?: Pair("-".asResourceString(), "-".asResourceString())
    }

    private fun formatGameDateTime(dateTime: Datetime): String {
        // Doesn't seem like we will get this from the backend but if we do we do the same as iOS in this instance
        return "${formatGameDate(dateTime)}\n${formatGameTime(dateTime)}"
    }

    private fun formatGameDate(dateTime: Datetime): String {
        return dateUtility.formatGMTDate(dateTime, DisplayFormat.WEEKDAY_MONTH_DATE_ABBREVIATED)
    }

    private fun formatGameTime(dateTime: Datetime): String {
        return dateUtility.formatGMTDate(dateTime, DisplayFormat.HOURS_MINUTES)
    }
}