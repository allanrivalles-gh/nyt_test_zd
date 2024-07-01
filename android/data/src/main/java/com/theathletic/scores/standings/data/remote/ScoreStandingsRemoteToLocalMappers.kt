package com.theathletic.scores.standings.data.remote

import com.theathletic.GetStandingsQuery
import com.theathletic.TeamStandingsQuery
import com.theathletic.gamedetail.data.remote.toLocalModel
import com.theathletic.scores.remote.toLocalLeague
import com.theathletic.scores.standings.data.local.RelegationStatus
import com.theathletic.scores.standings.data.local.ScoresStandingsLocalModel
import com.theathletic.scores.standings.data.local.Standing
import com.theathletic.scores.standings.data.local.StandingRangeClosedSegment
import com.theathletic.scores.standings.data.local.StandingRangeFromSegment
import com.theathletic.scores.standings.data.local.StandingRangeToSegment
import com.theathletic.scores.standings.data.local.StandingSegment
import com.theathletic.scores.standings.data.local.StandingsGroup
import com.theathletic.scores.standings.data.local.StandingsGroupHeader
import com.theathletic.scores.standings.data.local.StandingsGrouping
import com.theathletic.scores.standings.data.local.StandingsGroupingType.CONFERENCE
import com.theathletic.scores.standings.data.local.StandingsGroupingType.DIVISION
import com.theathletic.scores.standings.data.local.StandingsGroupingType.GROUP
import com.theathletic.scores.standings.data.local.StandingsGroupingType.LEAGUE
import com.theathletic.scores.standings.data.local.StandingsGroupingType.UNKNOWN
import com.theathletic.scores.standings.data.local.StandingsGroupingType.WILDCARD
import com.theathletic.scores.standings.data.local.StandingsSegmentType.PLAYOFF_QUALIFICATION
import com.theathletic.scores.standings.data.local.StandingsSegmentType.PLAYOFF_WILDCARD
import com.theathletic.scores.standings.data.local.StandingsSegmentType.PLAY_IN_QUALIFICATION
import com.theathletic.scores.standings.data.local.TeamStandingsLocalModel
import com.theathletic.type.RankStatus
import com.theathletic.type.StandingsGroupingType
import com.theathletic.type.StandingsSegmentType

fun GetStandingsQuery.Data.toLocalModel() = currentSeason?.let { season ->
    ScoresStandingsLocalModel(
        id = season.id,
        seasonName = season.name,
        groupings = season.standings.toLocalGrouping()
    )
}

fun TeamStandingsQuery.Data.toLocalModel() =
    teamv2?.league_standings?.firstOrNull()?.let { standings ->
        TeamStandingsLocalModel(
            id = standings.season.id,
            seasonName = standings.season.name,
            league = standings.season.league.fragments.league.id.toLocalLeague,
            groupings = standings.standings.toTeamLocalGrouping()
        )
    }

fun List<GetStandingsQuery.Standing>.toLocalGrouping() = map {
    StandingsGrouping(
        id = it.fragments.standingsGrouping.id,
        type = it.fragments.standingsGrouping.grouping_type.toLocal(),
        groups = it.fragments.standingsGrouping.groups.toLocalGroup(),
        showRank = it.fragments.standingsGrouping.show_rank,
        headers = it.fragments.standingsGrouping.headers.toLocalHeaders(),
        groupLabel = it.fragments.standingsGrouping.grouping_label.orEmpty()
    )
}

fun List<TeamStandingsQuery.Standing>.toTeamLocalGrouping() = map {
    StandingsGrouping(
        id = it.fragments.standingsGrouping.id,
        type = it.fragments.standingsGrouping.grouping_type.toLocal(),
        groups = it.fragments.standingsGrouping.groups.toLocalGroup(),
        showRank = it.fragments.standingsGrouping.show_rank,
        headers = it.fragments.standingsGrouping.headers.toLocalHeaders(),
        groupLabel = it.fragments.standingsGrouping.grouping_label.orEmpty()
    )
}

fun List<com.theathletic.fragment.StandingsGrouping.Group>.toLocalGroup() = map {
    StandingsGroup(
        id = it.fragments.standingsGroup.id,
        name = it.fragments.standingsGroup.name,
        columns = it.fragments.standingsGroup.columns.toLocalColumns(),
        standings = it.fragments.standingsGroup.standings.toLocalStanding(),
        segments = it.fragments.standingsGroup.segments.mapNotNull { segment -> segment.toLocalSegment() }
    )
}

fun List<com.theathletic.fragment.StandingsGrouping.Header?>?.toLocalHeaders() = this?.map { header ->
    header?.let {
        StandingsGroupHeader(
            id = it.fragments.standingsGroupHeader.id,
            headerName = it.fragments.standingsGroupHeader.header,
            groupIds = it.fragments.standingsGroupHeader.group_ids
        )
    }
}

fun List<com.theathletic.fragment.StandingsGroup.Column>.toLocalColumns(): Map<String, String> {
    val columns = mutableMapOf<String, String>()
    forEach {
        columns[it.fragments.standingsColumn.field] = it.fragments.standingsColumn.label
    }
    return columns
}

fun List<com.theathletic.fragment.StandingsGroup.Standing>.toLocalStanding() = map {
    val standing = it.fragments.standing
    Standing(
        id = standing.id,
        team = standing.team.fragments.team.toLocalModel(),
        rank = standing.rank,
        relegationStatus = standing.rank_status?.toLocal() ?: RelegationStatus.UNKNOWN,
        points = standing.points,
        played = standing.played,
        won = standing.won,
        lost = standing.lost,
        drawn = standing.drawn,
        pointsFor = standing.`for`,
        pointsAgainst = standing.against,
        difference = standing.difference,
        winPct = standing.win_pct,
        divRecord = standing.div_record,
        confRecord = standing.conf_record,
        streak = standing.streak,
        lostOvertime = standing.lost_overtime,
        awayRecord = standing.away_record,
        homeRecord = standing.home_record,
        lastTenRecord = standing.last_ten_record,
        gamesBehind = standing.games_behind,
        eliminationNumber = standing.elimination_number,
        lastSix = standing.last_six,
    )
}

fun com.theathletic.fragment.StandingsGroup.Segment.toLocalSegment(): StandingSegment? {
    fragments.standingsSegment.fragments.standingsRangeClosedSegment?.let { segment ->
        return StandingRangeClosedSegment(
            id = segment.id,
            type = segment.segment_type.toLocal(),
            fromRank = segment.from_rank,
            toRank = segment.to_rank
        )
    }
    fragments.standingsSegment.fragments.standingsRangeFromSegment?.let { segment ->
        return StandingRangeFromSegment(
            id = segment.id,
            type = segment.segment_type.toLocal(),
            fromRank = segment.from_rank
        )
    }
    fragments.standingsSegment.fragments.standingsRangeToSegment?.let { segment ->
        return StandingRangeToSegment(
            id = segment.id,
            type = segment.segment_type.toLocal(),
            toRank = segment.to_rank
        )
    }
    return null
}

fun StandingsGroupingType.toLocal(): com.theathletic.scores.standings.data.local.StandingsGroupingType {
    return when (this) {
        StandingsGroupingType.conference -> CONFERENCE
        StandingsGroupingType.division -> DIVISION
        StandingsGroupingType.group -> GROUP
        StandingsGroupingType.league -> LEAGUE
        StandingsGroupingType.wildcard -> WILDCARD
        else -> UNKNOWN
    }
}

private fun RankStatus.toLocal(): RelegationStatus {
    return when (this) {
        RankStatus.finals -> RelegationStatus.FINALS
        RankStatus.final_playoffs -> RelegationStatus.FINAL_PLAYOFFS
        RankStatus.relegation -> RelegationStatus.RELEGATION
        RankStatus.uefa_champions_league -> RelegationStatus.UEFA_CHAMPIONS_LEAGUE
        RankStatus.uefa_europa_league -> RelegationStatus.UEFA_EUROPA_LEAGUE
        RankStatus.r16 -> RelegationStatus.R16
        RankStatus.promotion -> RelegationStatus.PROMOTION
        RankStatus.promotion_playoff -> RelegationStatus.PROMOTION_PLAYOFF
        RankStatus.relegation_playoff -> RelegationStatus.RELEGATION_PLAYOFF
        RankStatus.uefa_conference_league_qualifiers -> RelegationStatus.UEFA_CONFERENCE_LEAGUE_QUALIFIERS
        else -> RelegationStatus.UNKNOWN
    }
}

private fun StandingsSegmentType?.toLocal(): com.theathletic.scores.standings.data.local.StandingsSegmentType {
    return when (this) {
        StandingsSegmentType.playoff_qualification -> PLAYOFF_QUALIFICATION
        StandingsSegmentType.play_in_qualification -> PLAY_IN_QUALIFICATION
        StandingsSegmentType.playoff_wildcard -> PLAYOFF_WILDCARD
        else ->
            com.theathletic.scores.standings.data.local.StandingsSegmentType.UNKNOWN
    }
}