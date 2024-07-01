package com.theathletic.scores.standings.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.followables.data.FollowableRepository
import com.theathletic.scores.standings.data.local.StandingsGrouping

class NonNavigableStandingsTeamsUseCase @AutoKoin constructor(
    private val followableRepository: FollowableRepository,
) {

    suspend operator fun invoke(standingsGrouping: List<StandingsGrouping>): List<String> {
        val standingTeams = getTeamsFromStandings(standingsGrouping)
        val allTeams = followableRepository.getAllTeams().map { it.graphqlId }
        return ((standingTeams.map { it } subtract allTeams.map { it })).toList()
    }

    private fun getTeamsFromStandings(standingsGrouping: List<StandingsGrouping>) =
        standingsGrouping.flatMap { groupings -> groupings.groups }
            .flatMap { groups -> groups.standings }
            .map { standing -> standing.team.id }
}