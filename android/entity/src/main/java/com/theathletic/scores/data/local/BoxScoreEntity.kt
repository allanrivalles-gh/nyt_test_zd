package com.theathletic.scores.data.local

import com.squareup.moshi.JsonClass
import com.theathletic.datetime.Datetime
import com.theathletic.entity.local.AthleticEntity
import com.theathletic.entity.main.League

// The order of these entries reflects the order in which they are shown in the scores tab. Don't
// change order without verifying we want to switch how we display game lists.
enum class GameState {
    LIVE,
    FINAL,
    UPCOMING,
    CANCELED,
    POSTPONED,
    SUSPENDED,
    IF_NECESSARY,
    DELAYED,
    UNKNOWN
}

enum class GameCoverageType {
    ALL,
    LINE_UP,
    PLAYER_STATS,
    SCORES,
    TEAM_STATS,
    TEAM_SPECIFIC_COMMENTS,
    PLAYS,
    DISCOVERABLE_COMMENTS,
    COMMENTS,
    COMMENTS_NAVIGATION,
    LIVE_BLOG,
    UNKNOWN
}

@JsonClass(generateAdapter = true)
data class BoxScoreEntity(
    override val id: String = "",

    val state: GameState = GameState.UPCOMING,
    val leagueIds: List<Long> = emptyList(),
    val leagueDisplayName: String? = "",

    val scoreStatusText: String? = "",
    val gameTime: Datetime = Datetime(0L),

    val awayTeam: TeamStatus = TeamStatus(),
    val homeTeam: TeamStatus = TeamStatus(),

    val timeTBA: Boolean = false,

    val graphGameId: String? = "",

    val requestStatsWhenLive: Boolean = false,

    val groupLabel: String? = null,
    val availableGameCoverage: List<GameCoverageType> = emptyList()
) : AthleticEntity {

    @JsonClass(generateAdapter = true)
    data class TeamStatus(
        val teamId: String = "",
        val name: String = "",
        val shortName: String = "",
        val score: Int = 0,
        val penaltyGoals: Int = 0,
        val record: String? = "",
        val details: String? = "",
        val logo: String? = "",
        val ranking: Int = 0,
        val isTbd: Boolean = false,
        val currentRecord: String = ""
    )

    override val type = AthleticEntity.Type.BOX_SCORE

    val league: League
        get() = League.parseFromId(leagueIds.firstOrNull())

    val firstTeam = when {
        league.sport.homeTeamFirst -> homeTeam
        else -> awayTeam
    }

    val secondTeam = when {
        league.sport.homeTeamFirst -> awayTeam
        else -> homeTeam
    }

    val sport = league.sport
}

data class FirebaseLiveScore(
    val scoreStatusText: String? = "",
    val firstDetail: String,
    val secondDetail: String,
    val isFirstDetailGreen: Boolean,
    val isSecondDetailRed: Boolean,
    val firstDetailState: DetailTextState,
    val secondDetailState: DetailTextState,

    val firstTeamName: String,
    val firstTeamScore: Int,
    val firstTeamPossession: Boolean,

    val secondTeamName: String,
    val secondTeamScore: Int,
    val secondTeamPossession: Boolean
)

enum class DetailTextState {
    DETAIL,
    DATE,
    TIME
}