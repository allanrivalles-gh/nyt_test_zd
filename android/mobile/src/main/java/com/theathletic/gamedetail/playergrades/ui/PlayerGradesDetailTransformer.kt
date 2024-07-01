package com.theathletic.gamedetail.playergrades.ui

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.playergrades.PlayerGradesDetailUi
import com.theathletic.datetime.DateUtility
import com.theathletic.datetime.Datetime
import com.theathletic.datetime.formatter.DisplayFormat
import com.theathletic.entity.main.Sport
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreCommonRenderers
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.GameStatus
import com.theathletic.gamedetail.data.local.GradeStatus
import com.theathletic.gamedetail.data.local.PlayerGradesLocalModel
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.Transformer
import com.theathletic.utility.orShortDash

class PlayerGradesDetailTransformer @AutoKoin constructor(
    private val dateUtility: DateUtility,
    private val commonRenderers: BoxScoreCommonRenderers,
) :
    Transformer<PlayerGradesDetailState, PlayerGradesDetailContract.ViewState> {

    override fun transform(data: PlayerGradesDetailState): PlayerGradesDetailContract.ViewState {
        return PlayerGradesDetailContract.ViewState(
            showSpinner = data.loadingState.isFreshLoadingState,
            uiModel = data.toPlayerGradesUi(data.sport),
        )
    }

    private fun PlayerGradesDetailState.toPlayerGradesUi(sport: Sport): PlayerGradesDetailUi? {
        return playerGrades?.let { playerGrades ->
            val isLocked = playerGrades.gradeStatus == GradeStatus.LOCKED
            (if (isHomeTeam) playerGrades.homeTeam else playerGrades.awayTeam)?.let { team ->
                PlayerGradesDetailUi(
                    teamBackground = team.backgroundColor,
                    teamLogos = team.logos,
                    gameStatus = playerGrades.toGameStatus(sport),
                    players = team.players.map {
                        it.toUi(
                            teamAlias = team.alias.orShortDash(),
                            isLocked = isLocked,
                            isCurrentlySubmittingGrade = playersCurrentlySubmittingGrade.contains(it.playerId)
                        )
                    },
                    initialPlayerIndex = playerIndex,
                    isLocked = isLocked
                )
            }
        }
    }

    private fun PlayerGradesLocalModel.toGameStatus(sport: Sport): PlayerGradesDetailUi.GameStatus {
        val primaryTitle = if (sport == Sport.SOCCER && gameStatus == GameStatus.IN_PROGRESS) gameStateSecondary else gameStatePrimary
        val secondaryTitle = if (sport == Sport.SOCCER) gameStatePrimary else gameStateSecondary

        return PlayerGradesDetailUi.GameStatus(
            firstTeamLogos = awayTeam?.logos ?: emptyList(),
            firstTeamScore = awayTeam?.score ?: 0,
            secondTeamLogos = homeTeam?.logos ?: emptyList(),
            secondTeamScore = homeTeam?.score ?: 0,
            gameStatePrimary = primaryTitle.orShortDash(),
            gameStateSecondary = secondaryTitle,
            scheduledDate = scheduledAt.formattedStartDate,
            showLiveGameDetails = gameStatus == GameStatus.IN_PROGRESS,
            showGameStatePrimary = primaryTitle.isNullOrBlank().not(),
            showGameStateSecondary = secondaryTitle.isNullOrBlank().not()
        )
    }

    private fun PlayerGradesLocalModel.Player.toUi(
        teamAlias: String,
        isLocked: Boolean,
        isCurrentlySubmittingGrade: Boolean,
    ) = PlayerGradesDetailUi.Player(
        id = playerId,
        name = displayName,
        headshots = headshots,
        details = toPlayerDetails(teamAlias),
        statisticsSummaryList = defaultStatistics.toDefaultStatsUi(),
        statisticsFullList = (defaultStatistics + extraStatistics).toExtendedStatsUi(),
        grade = grading.toUi(isLocked, isCurrentlySubmittingGrade)
    )

    private fun PlayerGradesLocalModel.Grading?.toUi(
        isLocked: Boolean,
        isCurrentlySubmittingGrade: Boolean
    ): PlayerGradesDetailUi.PlayerGrade {
        if (this == null) return createDefaultPlayerGrade(isLocked)

        val grade = grade ?: 0
        return PlayerGradesDetailUi.PlayerGrade(
            state = getGradingState(grade, isLocked, isCurrentlySubmittingGrade),
            grading = grade,
            averageGrade = averageGradeDisplay,
            totalGradings = totalGrades
        )
    }

    private fun createDefaultPlayerGrade(isLocked: Boolean) = PlayerGradesDetailUi.PlayerGrade(
        state = getGradingState(
            grade = 0,
            isLocked = isLocked,
            isCurrentlySubmittingGrade = false
        ),
        grading = 0,
        averageGrade = "0.0",
        totalGradings = 0
    )

    private fun getGradingState(
        grade: Int,
        isLocked: Boolean,
        isCurrentlySubmittingGrade: Boolean
    ) = when {
        grade == 0 && isLocked -> PlayerGradesDetailUi.GradingState.LOCKED_UNGRADED
        grade == 0 -> PlayerGradesDetailUi.GradingState.UNGRADED
        isCurrentlySubmittingGrade -> PlayerGradesDetailUi.GradingState.SUBMITTING
        grade > 0 && isLocked -> PlayerGradesDetailUi.GradingState.LOCKED_GRADED
        grade > 0 -> PlayerGradesDetailUi.GradingState.GRADED
        else -> PlayerGradesDetailUi.GradingState.UNGRADED
    }

    private fun List<GameDetailLocalModel.Statistic>.toDefaultStatsUi(): List<PlayerGradesDetailUi.StatisticsSummary> {
        // Only a max of 4 stats are shown in the Summary Section
        return take(4).map {
            PlayerGradesDetailUi.StatisticsSummary(
                label = it.longHeaderLabel ?: it.label,
                value = commonRenderers.formatStatisticValue(it).orShortDash()
            )
        }
    }

    private fun List<GameDetailLocalModel.Statistic>.toExtendedStatsUi(): List<PlayerGradesDetailUi.StatisticsSummary> {
        return map {
            PlayerGradesDetailUi.StatisticsSummary(
                label = it.label.orShortDash(),
                value = commonRenderers.formatStatisticValue(it).orShortDash()
            )
        }
    }

    private fun PlayerGradesLocalModel.Player.toPlayerDetails(teamAlias: String) = StringWithParams(
        R.string.player_grades_players_details,
        teamAlias,
        position.alias,
        jerseyNumber
    )

    private val Datetime.formattedStartDate: String
        get() = dateUtility.formatGMTDate(
            this,
            DisplayFormat.WEEKDAY_MONTH_DATE_SHORT
        )
}