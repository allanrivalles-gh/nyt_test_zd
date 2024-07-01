package com.theathletic.gamedetail.data.local

import com.theathletic.data.LocalModel
import com.theathletic.data.SizedImages
import com.theathletic.datetime.Datetime
import com.theathletic.entity.main.Sport
import com.theathletic.liveblog.data.local.LiveBlogLinks
import com.theathletic.scores.GameUtil
import com.theathletic.utility.orShortDash

data class GameSummaryLocalModel(
    val id: String,
    val awayTeam: GameSummaryTeam?,
    val homeTeam: GameSummaryTeam?,
    val scheduleAt: Datetime,
    val isScheduledTimeTbd: Boolean,
    val sport: Sport,
    val league: GameDetailLocalModel.League,
    val status: GameStatus,
    val period: Period,
    val clock: String?,
    val coverage: List<CoverageDataType>,
    val permalink: String?,
    val extras: SportExtras?,
    val liveBlog: LiveBlogLinks?,
    val gradeStatus: GradeStatus,
    val isAwayTeamTbd: Boolean,
    val isHomeTeamTbd: Boolean,
    val areCommentsDiscoverable: Boolean,
    val gameStatePrimary: String?,
    val gameStateSecondary: String?,
    val gameTitle: String?,
) : LocalModel {
    interface GameSummaryTeam {
        val id: String
        val legacyId: Long
        val alias: String
        val displayName: String
        val logos: SizedImages
        val score: Int?
        val currentRecord: String?
    }

    data class AmericanFootballGameSummaryTeam(
        override val id: String,
        override val legacyId: Long,
        override val alias: String,
        override val displayName: String,
        override val logos: SizedImages,
        override val score: Int?,
        override val currentRecord: String?,
        val remainingTimeouts: Int?,
        val currentRanking: String?,
        val usedTimeouts: Int?,
        val hasPossession: Boolean
    ) : GameSummaryTeam

    data class SoccerGameSummaryTeam(
        override val id: String,
        override val legacyId: Long,
        override val alias: String,
        override val displayName: String,
        override val logos: SizedImages,
        override val score: Int?,
        override val currentRecord: String?,
        val currentRanking: String?,
        val aggregateScore: Int?,
        val lastSix: String,
        val expectedGoals: GameDetailLocalModel.Statistic?,
        val penaltyScore: Int?,
    ) : GameSummaryTeam

    data class BasketballGameSummaryTeam(
        override val id: String,
        override val legacyId: Long,
        override val alias: String,
        override val displayName: String,
        override val logos: SizedImages,
        override val score: Int?,
        override val currentRecord: String?,
        val currentRanking: String?,
        val remainingTimeouts: Int?,
        val usedTimeouts: Int?
    ) : GameSummaryTeam

    data class BaseballGameSummaryTeam(
        override val id: String,
        override val legacyId: Long,
        override val alias: String,
        override val displayName: String,
        override val logos: SizedImages,
        override val score: Int?,
        override val currentRecord: String?
    ) : GameSummaryTeam

    data class HockeyGameSummaryTeam(
        override val id: String,
        override val legacyId: Long,
        override val alias: String,
        override val displayName: String,
        override val logos: SizedImages,
        override val score: Int?,
        override val currentRecord: String?,
        val strength: HockeyStrength
    ) : GameSummaryTeam

    sealed class SportExtras {
        data class Baseball(
            val outcome: BaseballOutcome?
        ) : SportExtras() {
            data class BaseballOutcome(
                val balls: Int?,
                val inning: Int?,
                val inningHalf: InningHalf?,
                val outs: Int?,
                val strikes: Int?,
                val occupiedBases: List<Int>
            )
        }

        data class Soccer(
            val relatedGameScheduleAt: Datetime?,
            val aggregateWinnerName: String?
        ) : SportExtras()
    }

    val firstTeam = when {
        sport.homeTeamFirst -> homeTeam
        else -> awayTeam
    }

    val secondTeam = when {
        sport.homeTeamFirst -> awayTeam
        else -> homeTeam
    }

    val isGameInProgressOrCompleted =
        status == GameStatus.IN_PROGRESS || status == GameStatus.FINAL

    val isGameScheduled = status == GameStatus.SCHEDULED

    val isGameInProgress = status == GameStatus.IN_PROGRESS

    val isGameCompleted = status == GameStatus.FINAL

    val isFirstTeamTbd = if (sport.homeTeamFirst) isHomeTeamTbd else isAwayTeamTbd

    val isSecondTeamTbd = if (sport.homeTeamFirst) isAwayTeamTbd else isHomeTeamTbd

    val fallbackGameTitle = GameUtil.buildGameTitle(
        firstTeamDisplayString = firstTeam?.alias.orShortDash(),
        secondTeamDisplayString = secondTeam?.alias.orShortDash(),
        firstTeamTbd = isFirstTeamTbd,
        secondTeamTbd = isSecondTeamTbd,
        isSoccer = (sport == Sport.SOCCER)
    )
}

fun List<CoverageDataType>.supportsCoverageType(type: CoverageDataType) =
    contains(type) || contains(CoverageDataType.ALL)