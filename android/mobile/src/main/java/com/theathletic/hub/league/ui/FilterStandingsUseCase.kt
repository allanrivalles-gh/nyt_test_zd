package com.theathletic.hub.league.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.scores.standings.data.local.StandingsGrouping

class FilterStandingsUseCase @AutoKoin constructor() {

    // Filter out standing groups that don't actually have any standings
    operator fun invoke(groupings: List<StandingsGrouping>): List<StandingsGrouping> {
        return groupings.mapNotNull { it.validateStandings }
    }

    private val StandingsGrouping.validateStandings: StandingsGrouping?
        get() = if (groups.all { it.standings.isEmpty() }) null else this
}