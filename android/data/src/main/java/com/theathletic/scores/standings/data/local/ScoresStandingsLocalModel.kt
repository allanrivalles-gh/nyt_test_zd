package com.theathletic.scores.standings.data.local

import com.theathletic.data.LocalModel
import com.theathletic.entity.main.League
import com.theathletic.gamedetail.data.local.GameDetailLocalModel

data class ScoresStandingsLocalModel(
    val id: String,
    val seasonName: String,
    val groupings: List<StandingsGrouping>,
) : LocalModel

data class TeamStandingsLocalModel(
    val id: String,
    val seasonName: String,
    val league: League,
    val groupings: List<StandingsGrouping>,
)

data class StandingsGrouping(
    val id: String,
    val type: StandingsGroupingType,
    val groups: List<StandingsGroup>,
    val headers: List<StandingsGroupHeader?>?,
    val showRank: Boolean,
    val groupLabel: String
)

data class StandingsGroup(
    val id: String,
    val name: String?,
    val columns: Map<String, String>,
    val segments: List<StandingSegment>,
    val standings: List<Standing>,
)

data class StandingsGroupHeader(
    val id: String,
    val headerName: String?,
    val groupIds: List<String>
)

data class Standing(
    val id: String,
    val team: GameDetailLocalModel.Team,
    val rank: Int,
    val relegationStatus: RelegationStatus,
    val points: String?,
    val played: String?,
    val won: String?,
    val lost: String?,
    val drawn: String?,
    val pointsFor: String?,
    val pointsAgainst: String?,
    val difference: String?,
    val winPct: String?,
    val divRecord: String?,
    val confRecord: String?,
    val streak: String?,
    val lostOvertime: String?,
    val awayRecord: String?,
    val homeRecord: String?,
    val lastTenRecord: String?,
    val lastSix: String?,
    val gamesBehind: String?,
    val eliminationNumber: String?
)

interface StandingSegment {
    val id: String
    val type: StandingsSegmentType
}

data class StandingRangeClosedSegment(
    override val id: String,
    override val type: StandingsSegmentType,
    val fromRank: Int,
    val toRank: Int
) : StandingSegment

data class StandingRangeFromSegment(
    override val id: String,
    override val type: StandingsSegmentType,
    val fromRank: Int
) : StandingSegment

data class StandingRangeToSegment(
    override val id: String,
    override val type: StandingsSegmentType,
    val toRank: Int
) : StandingSegment

enum class StandingsGroupingType {
    CONFERENCE,
    DIVISION,
    GROUP,
    LEAGUE,
    WILDCARD,
    UNKNOWN,
}

enum class RelegationStatus {
    FINALS,
    FINAL_PLAYOFFS,
    PROMOTION,
    PROMOTION_PLAYOFF,
    RELEGATION,
    RELEGATION_PLAYOFF,
    UEFA_CHAMPIONS_LEAGUE,
    UEFA_EUROPA_LEAGUE,
    R16,
    UEFA_CONFERENCE_LEAGUE_QUALIFIERS,
    UNKNOWN
}

enum class StandingsSegmentType {
    PLAYOFF_QUALIFICATION,
    PLAY_IN_QUALIFICATION,
    PLAYOFF_WILDCARD,
    UNKNOWN
}