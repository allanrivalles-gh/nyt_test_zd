package com.theathletic.gamedetail.ui

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.GameDetailUi
import com.theathletic.boxscore.ui.formatters.BoxScoreBaseballInningFormatter
import com.theathletic.datetime.DateUtility
import com.theathletic.datetime.Datetime
import com.theathletic.datetime.formatter.DisplayFormat
import com.theathletic.entity.main.Sport
import com.theathletic.gamedetail.boxscore.ui.common.toHeaderPeriodLabel
import com.theathletic.gamedetail.data.local.GameStatus
import com.theathletic.gamedetail.data.local.GameSummaryLocalModel
import com.theathletic.ui.ResourceString
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.ResourceString.StringWrapper
import com.theathletic.ui.asResourceString
import com.theathletic.ui.orShortDash
import com.theathletic.utility.safeLet
import java.util.Collections.emptyList

class GameSummaryGameStatusRenderer @AutoKoin constructor(
    private val dateUtility: DateUtility,
    private val inningFormatter: BoxScoreBaseballInningFormatter
) {

    fun getGameStatus(game: GameSummaryLocalModel?): GameDetailUi.GameStatus {
        return game?.let {
            when (it.status) {
                GameStatus.FINAL -> {
                    when (it.sport) {
                        Sport.SOCCER -> it.toSoccerPostGameStatus
                        else -> it.toPostGameStatus(it.sport)
                    }
                }
                GameStatus.IN_PROGRESS -> {
                    when (it.sport) {
                        Sport.BASEBALL -> it.toBaseballInGameStatus
                        Sport.SOCCER -> it.toSoccerInGameStatus
                        else -> it.toInGameStatus()
                    }
                }
                else -> it.toPregameStatus
            }
        } ?: GameDetailUi.GameStatus.PregameStatus(
            scheduledDate = "-",
            scheduledTime = "-".asResourceString(),
        )
    }

    private fun GameSummaryLocalModel.toPostGameStatus(sport: Sport): GameDetailUi.GameStatus.PostGameStatus {
        return GameDetailUi.GameStatus.PostGameStatus(
            gamePeriod = period.toHeaderPeriodLabel(sport, status).orShortDash(),
            scheduledDate = formattedStartDate
        )
    }

    private fun GameSummaryLocalModel.toInGameStatus(): GameDetailUi.GameStatus.InGameStatus {
        return GameDetailUi.GameStatus.InGameStatus(
            isGameDelayed = status == GameStatus.DELAYED,
            gameStatePrimary = this.gameStatePrimary,
            gameStateSecondary = this.gameStateSecondary
        )
    }

    private val GameSummaryLocalModel.toPregameStatus: GameDetailUi.GameStatus.PregameStatus
        get() = GameDetailUi.GameStatus.PregameStatus(
            scheduledDate = formattedStartDate,
            scheduledTime = formattedStartTime
        )

    private val GameSummaryLocalModel.toSoccerInGameStatus: GameDetailUi.GameStatus.SoccerInGameStatus
        get() {
            val soccerExtras = (extras as GameSummaryLocalModel.SportExtras.Soccer)
            return GameDetailUi.GameStatus.SoccerInGameStatus(
                gameStatePrimary = this.gameStatePrimary,
                showAggregate = showAggregateScore(
                    currentGameScheduleAt = scheduleAt,
                    relatedGameScheduleAt = soccerExtras.relatedGameScheduleAt ?: Datetime(0L)
                ),
                aggregate = toAggregateScore(),
                isGameDelayed = status == GameStatus.DELAYED
            )
        }

    private val GameSummaryLocalModel.toSoccerPostGameStatus: GameDetailUi.GameStatus.SoccerPostGameStatus
        get() {
            val soccerExtras = (extras as GameSummaryLocalModel.SportExtras.Soccer)
            return GameDetailUi.GameStatus.SoccerPostGameStatus(
                gamePeriod = period.toHeaderPeriodLabel(sport, status).orShortDash(),
                scheduledDate = formattedStartDate,
                showAggregate = showAggregateScore(
                    currentGameScheduleAt = scheduleAt,
                    relatedGameScheduleAt = soccerExtras.relatedGameScheduleAt ?: Datetime(0L)
                ),
                aggregate = toAggregateScore()
            )
        }

    private val GameSummaryLocalModel.toBaseballInGameStatus: GameDetailUi.GameStatus.BaseballInGameStatus
        get() {
            val outcome = (extras as? GameSummaryLocalModel.SportExtras.Baseball)?.outcome
            return GameDetailUi.GameStatus.BaseballInGameStatus(
                inningHalf = inningFormatter.format(outcome?.inning ?: 0, outcome?.inningHalf),
                occupiedBases = outcome?.occupiedBases ?: emptyList(),
                status = outcome?.toStatus() ?: "--".asResourceString(),
                isGameDelayed = status == GameStatus.DELAYED
            )
        }

    private fun GameSummaryLocalModel.SportExtras.Baseball.BaseballOutcome.toStatus(): ResourceString {
        return when {
            strikes == 3 -> StringWithParams(
                R.string.box_score_baseball_live_status_strikeout_formatter,
                outs ?: 0
            )
            balls == 4 -> StringWithParams(
                R.string.box_score_baseball_live_status_walk_formatter,
                outs ?: 0
            )
            else -> StringWithParams(
                R.string.box_score_baseball_live_status_formatter,
                balls ?: 0,
                strikes ?: 0,
                outs ?: 0
            )
        }
    }

    private val GameSummaryLocalModel.formattedStartTime: ResourceString
        get() = when {
            status == com.theathletic.gamedetail.data.local.GameStatus.CANCELED -> StringWithParams(R.string.game_detail_pre_game_canceled_label)
            status == com.theathletic.gamedetail.data.local.GameStatus.POSTPONED -> StringWithParams(R.string.game_detail_pre_game_postponed_label)
            status == com.theathletic.gamedetail.data.local.GameStatus.SUSPENDED -> StringWithParams(R.string.game_detail_pre_game_suspended_label)
            status == com.theathletic.gamedetail.data.local.GameStatus.IF_NECESSARY -> StringWithParams(R.string.game_detail_pre_game_if_necessary_label)
            isScheduledTimeTbd -> StringWithParams(R.string.global_tbd)
            else -> StringWrapper(
                dateUtility.formatGMTDate(
                    scheduleAt,
                    DisplayFormat.HOURS_MINUTES
                )
            )
        }

    private val GameSummaryLocalModel.formattedStartDate: String
        get() = dateUtility.formatGMTDate(
            scheduleAt,
            DisplayFormat.WEEKDAY_MONTH_DATE_SHORT
        )
}

private fun GameSummaryLocalModel.toAggregateScore(): ResourceString {
    val firstTeamScore = (firstTeam as GameSummaryLocalModel.SoccerGameSummaryTeam).aggregateScore ?: 0
    val secondTeamScore = (secondTeam as GameSummaryLocalModel.SoccerGameSummaryTeam).aggregateScore ?: 0
    return StringWithParams(R.string.box_score_soccer_header_aggregate_score, firstTeamScore, secondTeamScore)
}

// If there is aggregate score, show it but only if the game is the Leg 2 game.
private fun GameSummaryLocalModel.showAggregateScore(
    currentGameScheduleAt: Datetime,
    relatedGameScheduleAt: Datetime
): Boolean {
    safeLet(
        (firstTeam as GameSummaryLocalModel.SoccerGameSummaryTeam).aggregateScore,
        (secondTeam as GameSummaryLocalModel.SoccerGameSummaryTeam).aggregateScore
    ) { _, _ ->
        return currentGameScheduleAt > relatedGameScheduleAt
    }
    return false
}