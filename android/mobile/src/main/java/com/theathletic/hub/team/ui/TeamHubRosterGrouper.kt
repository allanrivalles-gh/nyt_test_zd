package com.theathletic.hub.team.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.entity.main.Sport
import com.theathletic.gamedetail.data.local.PlayerPosition
import com.theathletic.hub.team.data.local.TeamHubRosterLocalModel
import com.theathletic.hub.ui.SortablePlayerValuesTableUi

class TeamHubRosterGrouper @AutoKoin constructor() {

    fun groupAndDefaultSort(
        sport: Sport,
        roster: List<TeamHubRosterLocalModel.PlayerDetails>
    ): List<TeamHubRosterState.Category> {
        return when (sport) {
            Sport.FOOTBALL -> groupAmericanFootballRoster(roster)
            Sport.SOCCER -> groupSoccerRoster(roster)
            Sport.HOCKEY -> groupHockeyRoster(roster)
            Sport.BASEBALL -> groupBaseballRoster(roster)
            Sport.BASKETBALL -> groupBasketballRoster(roster)
            else -> emptyList()
        }
    }

    fun resortColumn(
        rosters: List<TeamHubRosterState.Category>,
        categoryType: TeamHubRosterState.CategoryType,
        sortType: TeamHubRosterState.SortType,
        currentOrder: SortablePlayerValuesTableUi.ColumnOrder,
    ): List<TeamHubRosterState.Category> {
        val newOrder = when (currentOrder) {
            SortablePlayerValuesTableUi.ColumnOrder.None,
            SortablePlayerValuesTableUi.ColumnOrder.Ascending -> SortablePlayerValuesTableUi.ColumnOrder.Descending
            SortablePlayerValuesTableUi.ColumnOrder.Descending -> SortablePlayerValuesTableUi.ColumnOrder.Ascending
        }
        return rosters.map {
            if (it.type == categoryType) {
                TeamHubRosterState.Category(
                    type = it.type,
                    roster = it.roster.columnSortedBy(
                        sortType = sortType,
                        isDescending = newOrder == SortablePlayerValuesTableUi.ColumnOrder.Descending,
                    ),
                    sortType = sortType,
                    order = newOrder,
                )
            } else {
                it
            }
        }
    }

    private fun groupAmericanFootballRoster(
        roster: List<TeamHubRosterLocalModel.PlayerDetails>
    ): List<TeamHubRosterState.Category> {
        return listOf(
            TeamHubRosterState.Category(
                type = TeamHubRosterState.CategoryType.Offense,
                roster = roster.filter { americanFootballOffensePositions.contains(it.position) }
                    .sortedBy { it.displayName }
            ),
            TeamHubRosterState.Category(
                type = TeamHubRosterState.CategoryType.Defense,
                roster = roster.filter { americanFootballDefensePositions.contains(it.position) }
                    .sortedBy { it.displayName }
            ),
            TeamHubRosterState.Category(
                type = TeamHubRosterState.CategoryType.SpecialTeams,
                roster = roster.filter { americanFootballSpecialTeamPositions.contains(it.position) }
                    .sortedBy { it.displayName }
            ),
        )
    }

    private fun List<TeamHubRosterLocalModel.PlayerDetails>.columnSortedBy(
        sortType: TeamHubRosterState.SortType,
        isDescending: Boolean,
    ) = when (sortType) {
        TeamHubRosterState.SortType.Default -> sortColumn(isDescending) { it.displayName }
        TeamHubRosterState.SortType.Position -> sortColumn(isDescending) { it.position }
        TeamHubRosterState.SortType.Height -> sortColumn(isDescending) { it.height }
        TeamHubRosterState.SortType.Weight -> sortColumn(isDescending) { it.weight }
        TeamHubRosterState.SortType.DateOfBirth -> sortColumn(isDescending) { it.dateOfBirth }
        TeamHubRosterState.SortType.Age -> sortColumn(isDescending) { it.dateOfBirth }
    }

    private inline fun <T, R : Comparable<R>> Iterable<T>.sortColumn(
        isDescending: Boolean,
        crossinline selector: (T) -> R?
    ): List<T> {
        return if (isDescending) sortedWith(compareByDescending(selector)) else sortedWith(compareBy(selector))
    }

    private fun groupSoccerRoster(
        roster: List<TeamHubRosterLocalModel.PlayerDetails>
    ): List<TeamHubRosterState.Category> {
        return listOf(
            TeamHubRosterState.Category(
                type = TeamHubRosterState.CategoryType.GoalKeepers,
                roster = roster.filterAndSort(PlayerPosition.GOALKEEPER)
            ),
            TeamHubRosterState.Category(
                type = TeamHubRosterState.CategoryType.OutfieldPlayers,
                roster = roster.filterNotAndSort(PlayerPosition.GOALKEEPER)
            ),
        )
    }

    private fun groupHockeyRoster(
        roster: List<TeamHubRosterLocalModel.PlayerDetails>
    ): List<TeamHubRosterState.Category> {
        return listOf(
            TeamHubRosterState.Category(
                type = TeamHubRosterState.CategoryType.Centers,
                roster = roster.filterAndSort(PlayerPosition.CENTER)
            ),
            TeamHubRosterState.Category(
                type = TeamHubRosterState.CategoryType.LeftWings,
                roster = roster.filterAndSort(PlayerPosition.LEFT_WING)
            ),
            TeamHubRosterState.Category(
                type = TeamHubRosterState.CategoryType.RightWings,
                roster = roster.filterAndSort(PlayerPosition.RIGHT_WING)
            ),
            TeamHubRosterState.Category(
                type = TeamHubRosterState.CategoryType.Defense,
                roster = roster.filterAndSort(PlayerPosition.DEFENSE)
            ),
            TeamHubRosterState.Category(
                type = TeamHubRosterState.CategoryType.Goalies,
                roster = roster.filterAndSort(PlayerPosition.GOALIE)
            ),
        )
    }

    private fun groupBaseballRoster(
        roster: List<TeamHubRosterLocalModel.PlayerDetails>
    ): List<TeamHubRosterState.Category> {
        val dhRoster = roster.filter { baseballDesignatedHitterPositions.contains(it.position) }
            .sortedBy { it.displayName }
        return listOfNotNull(
            TeamHubRosterState.Category(
                type = TeamHubRosterState.CategoryType.Pitchers,
                roster = roster.filter { baseballPitchersPositions.contains(it.position) }
                    .sortedBy { it.displayName }
            ),
            TeamHubRosterState.Category(
                type = TeamHubRosterState.CategoryType.Catchers,
                roster = roster.filterAndSort(PlayerPosition.CATCHER)
            ),
            TeamHubRosterState.Category(
                type = TeamHubRosterState.CategoryType.Infielders,
                roster = roster.filter { baseballInfielderPositions.contains(it.position) }
                    .sortedBy { it.displayName }
            ),
            TeamHubRosterState.Category(
                type = TeamHubRosterState.CategoryType.Outfielders,
                roster = roster.filter { baseballOutfielderPositions.contains(it.position) }
                    .sortedBy { it.displayName }
            ),
            if (dhRoster.isNotEmpty()) {
                TeamHubRosterState.Category(
                    type = TeamHubRosterState.CategoryType.DesignatedHitter,
                    roster = dhRoster
                )
            } else null
        )
    }

    private fun groupBasketballRoster(
        roster: List<TeamHubRosterLocalModel.PlayerDetails>
    ): List<TeamHubRosterState.Category> {
        return listOf(
            TeamHubRosterState.Category(
                type = TeamHubRosterState.CategoryType.NoCategories,
                roster = roster.sortedBy { it.displayName }
            )
        )
    }

    private fun List<TeamHubRosterLocalModel.PlayerDetails>.filterAndSort(
        position: PlayerPosition
    ) = filter { it.position == position }.sortedBy { it.displayName }

    private fun List<TeamHubRosterLocalModel.PlayerDetails>.filterNotAndSort(
        position: PlayerPosition
    ) = filter { it.position != position }.sortedBy { it.displayName }

    private val americanFootballOffensePositions = listOf(
        PlayerPosition.QUARTERBACK,
        PlayerPosition.RUNNING_BACK,
        PlayerPosition.OFFENSIVE_GUARD,
        PlayerPosition.OFFENSIVE_LINEMAN,
        PlayerPosition.OFFENSIVE_TACKLE,
        PlayerPosition.WIDE_RECEIVER,
        PlayerPosition.TIGHT_END,
        PlayerPosition.FULLBACK,
    )

    private val americanFootballDefensePositions = listOf(
        PlayerPosition.DEFENSIVE_BACK,
        PlayerPosition.DEFENSIVE_END,
        PlayerPosition.DEFENSIVE_LINEMAN,
        PlayerPosition.DEFENSIVE_TACKLE,
        PlayerPosition.CORNER_BACK,
        PlayerPosition.SAFETY,
        PlayerPosition.INSIDE_LINEBACKER,
        PlayerPosition.LINEBACKER,
        PlayerPosition.MIDDLE_LINEBACKER,
        PlayerPosition.OUTSIDE_LINEBACKER,
        PlayerPosition.FREE_SAFETY,
        PlayerPosition.STRONG_SAFETY,
        PlayerPosition.NOSE_TACKLE,
    )

    private val americanFootballSpecialTeamPositions = listOf(
        PlayerPosition.KICKER,
        PlayerPosition.PUNTER,
        PlayerPosition.LONG_SNAPPER,
    )

    private val baseballPitchersPositions = listOf(
        PlayerPosition.PITCHER,
        PlayerPosition.RELIEF_PITCHER,
        PlayerPosition.STARTING_PITCHER,
    )

    private val baseballInfielderPositions = listOf(
        PlayerPosition.FIRST_BASE,
        PlayerPosition.SECOND_BASE,
        PlayerPosition.THIRD_BASE,
        PlayerPosition.SHORTSTOP,
    )

    private val baseballOutfielderPositions = listOf(
        PlayerPosition.CENTER_FIELD,
        PlayerPosition.LEFT_FIELD,
        PlayerPosition.RIGHT_FIELD,
    )

    private val baseballDesignatedHitterPositions = listOf(
        PlayerPosition.DESIGNATED_HITTER,
        PlayerPosition.PINCH_HITTER,
        PlayerPosition.PINCH_RUNNER,
    )
}