package com.theathletic.scores.data.di

import com.theathletic.scores.data.local.Schedule

fun scheduleFixture(
    groups: List<Schedule.Group> = emptyList()
) = Schedule(
    id = "scheduleId",
    groups = groups,
    filters = null
)

fun scheduleGroupFixture(
    filterId: String? = null,
    navItemId: String = "navItem",
    navItem: Schedule.NavItem = scheduleNavItemFixture(navItemId, filterId),
    sections: List<Schedule.Section> = listOf(scheduleSectionFixture())
) = Schedule.Group(
    navItem = navItem,
    sections = sections
)

private fun scheduleNavItemFixture(navItemId: String, filterId: String? = null) = Schedule.NavItem(
    id = navItemId,
    isDefault = true,
    day = null,
    primaryLabel = "Primary",
    secondaryLabel = "Secondary",
    filterSelected = filterId
)

fun scheduleSectionFixture(
    games: List<Schedule.Game> = emptyList()
) = Schedule.Section(
    id = "sectionDefault",
    title = null,
    subTitle = null,
    games = games,
    widget = null,
    league = null
)

fun scheduleGameFixture(
    team1: Schedule.Team,
    team2: Schedule.Team,
    gameId: String = "gameId",
    state: Schedule.GameState = Schedule.GameState.PREGAME,
    gameInfo: Schedule.GameInfo = scheduleGameInfoFixture(),
    willUpdate: Boolean = false
) = Schedule.Game(
    gameId = gameId,
    state = state,
    team1 = team1,
    team2 = team2,
    info = gameInfo,
    widget = null,
    header = null,
    footer = null,
    willUpdate = willUpdate
)

fun scheduleTeamFixture(name: String) = Schedule.Team(
    name = name,
    logos = emptyList(),
    ranking = null,
    isTbd = false,
    icons = emptyList(),
    info = null
)

private fun scheduleGameInfoFixture() = Schedule.GameInfo(
    textContent = emptyList(),
    widget = null
)

fun scheduleGamesWithSomeWillUpdateGames(): List<Schedule.Game> {
    val team1 = scheduleTeamFixture("team1")
    val team2 = scheduleTeamFixture("team2")

    return listOf(
        scheduleGameFixture(
            team1 = team1,
            team2 = team2,
            gameId = "game-1",
            willUpdate = true
        ),
        scheduleGameFixture(
            team1 = team1,
            team2 = team2,
            gameId = "game-2",
            willUpdate = false
        ),
        scheduleGameFixture(
            team1 = team1,
            team2 = team2,
            gameId = "game-3",
            willUpdate = true
        ),
        scheduleGameFixture(
            team1 = team1,
            team2 = team2,
            gameId = "game-4",
            willUpdate = true
        ),
        scheduleGameFixture(
            team1 = team1,
            team2 = team2,
            gameId = "game-5",
            willUpdate = false
        ),
        scheduleGameFixture(
            team1 = team1,
            team2 = team2,
            gameId = "game-6",
            willUpdate = true
        ),
        scheduleGameFixture(
            team1 = team1,
            team2 = team2,
            gameId = "game-7",
            willUpdate = true
        ),
    )
}