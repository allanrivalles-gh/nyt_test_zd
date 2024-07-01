package com.theathletic.hub.game.data.local

import com.theathletic.data.SizedImages
import com.theathletic.datetime.Datetime
import com.theathletic.entity.main.Sport
import com.theathletic.gamedetail.data.local.CoverageDataType
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.GameStatus
import com.theathletic.gamedetail.data.local.GradeStatus
import com.theathletic.gamedetail.data.local.HockeyStrength
import com.theathletic.gamedetail.data.local.InningHalf
import com.theathletic.gamedetail.data.local.Period
import com.theathletic.liveblog.data.local.LiveBlogLinks

data class GameSummary(
    val id: String,
    val firstTeam: Team?,
    val secondTeam: Team?,
    val scheduleAt: Datetime,
    val isScheduledTimeTbd: Boolean,
    val sport: Sport,
    val league: GameDetailLocalModel.League,
    val status: GameStatus,
    val period: Period,
    val clock: String?,
    val coverage: List<CoverageDataType>,
    val permalink: String?,
    val baseballOutcome: BaseballOutcome?,
    val soccerInfo: SoccerInfo?,
    val liveBlog: LiveBlogLinks?,
    val gradeStatus: GradeStatus,
    val isFirstTeamTbd: Boolean,
    val isSecondTeamTbd: Boolean,
    val areCommentsDiscoverable: Boolean,
    val gameStatePrimary: String?,
    val gameStateSecondary: String?,
    val gameTitle: String?,
) {
    interface Team {
        val id: String
        val legacyId: Long
        val alias: String
        val displayName: String
        val logos: SizedImages
        val score: Int?
        val currentRecord: String?
    }

    data class AmericanFootballTeam(
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
    ) : Team

    data class SoccerTeam(
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
    ) : Team

    data class BasketballTeam(
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
    ) : Team

    data class BaseballTeam(
        override val id: String,
        override val legacyId: Long,
        override val alias: String,
        override val displayName: String,
        override val logos: SizedImages,
        override val score: Int?,
        override val currentRecord: String?
    ) : Team

    data class HockeyTeam(
        override val id: String,
        override val legacyId: Long,
        override val alias: String,
        override val displayName: String,
        override val logos: SizedImages,
        override val score: Int?,
        override val currentRecord: String?,
        val strength: HockeyStrength
    ) : Team

    data class BaseballOutcome(
        val balls: Int?,
        val inning: Int?,
        val inningHalf: InningHalf?,
        val outs: Int?,
        val strikes: Int?,
        val occupiedBases: List<Int>
    )

    data class SoccerInfo(
        val relatedGameScheduleAt: Datetime?,
        val aggregateWinnerName: String?
    )

    val isGameInProgress: Boolean
        get() = status == GameStatus.IN_PROGRESS

    val isGameInProgressOrCompleted: Boolean
        get() = status == GameStatus.IN_PROGRESS || status == GameStatus.FINAL
}