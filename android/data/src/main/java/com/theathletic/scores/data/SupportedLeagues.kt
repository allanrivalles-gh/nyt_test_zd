package com.theathletic.scores.data

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.entity.main.League
import com.theathletic.scores.remote.toLocalLeague
import com.theathletic.type.LeagueCode

class SupportedLeagues @AutoKoin(Scope.SINGLE) constructor() {

    private val leaguesSupportingGraphql = setOf(
        // Football - Soccer
        League.EPL,
        League.INTERNATIONAL,
        League.INTERNATIONAL_FRIENDLIES,
        League.CHAMPIONS_LEAGUE,
        League.MLS,
        League.UEL,
        League.SCOTTISH_PREMIERE,
        League.NWSL,
        League.UWC,
        League.WORLD_CUP,
        League.WOMENS_WORLD_CUP,
        League.EFL,
        League.LEAGUE_TWO,
        League.LEAGUE_TWO,
        League.FA_CUP,
        League.CARABAO_CUP,
        League.LA_LIGA,
        League.COPA_DEL_REY,
        // American Football
        League.NFL,
        League.NCAA_FB,
        // Basketball
        League.NBA,
        League.WNBA,
        League.NCAA_BB,
        League.NCAA_WB,
        // Hockey
        League.NHL,
        // Baseball
        League.MLB
    )
    val collegeLeagues = setOf(
        League.NCAA_FB,
        League.NCAA_BB,
        League.NCAA_WB
    )

    private val euroLeagues = setOf(
        League.CHAMPIONS_LEAGUE,
        League.UEL,
        League.UWC
    )

    private val rankSupportingLeagues = setOf(
        League.NCAA_FB,
        League.NCAA_BB,
        League.NCAA_WB,
        League.NBA
    )

    private val recentFormSupportingLeagues = setOf(
        League.EPL,
        League.LEAGUE_TWO,
        League.LEAGUE_ONE,
        League.SCOTTISH_PREMIERE,
        League.EFL,
        League.NWSL,
        League.LA_LIGA,
        League.MLS
    )

    private val graphLeagueIds = leaguesSupportingGraphql.map { it.leagueId }

    fun isSupportedId(leagueId: Long?): Boolean {
        return leagueId?.let { graphLeagueIds.contains(leagueId) } ?: false
    }

    fun isSupported(league: League) = league in leaguesSupportingGraphql

    fun isRankSupportedLeague(league: League) = league in rankSupportingLeagues

    fun isSupported(leagueCode: LeagueCode) = leagueCode.toLocalLeague in leaguesSupportingGraphql

    fun isCollegeLeague(league: League) = league in collegeLeagues

    fun isEuroLeague(league: League) = league in euroLeagues

    fun isRecentFormSupportingLeague(league: League) = league in recentFormSupportingLeagues
}