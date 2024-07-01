package com.theathletic.hub.team.ui

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.entity.main.League
import com.theathletic.entity.main.Sport
import com.theathletic.feed.FeedType
import com.theathletic.followable.Followable
import com.theathletic.hub.HubTabType
import com.theathletic.hub.ui.HubHomeModule
import com.theathletic.hub.ui.HubScheduleFeedModule
import com.theathletic.hub.ui.HubUi
import com.theathletic.hub.ui.TeamHubRosterModule
import com.theathletic.hub.ui.TeamHubStandingsModule
import com.theathletic.hub.ui.TeamHubStatsModule
import com.theathletic.scores.remote.toGraphqlLeagueCode
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.Transformer
import com.theathletic.utility.orShortDash

class TeamHubTransformer @AutoKoin constructor() :
    Transformer<TeamHubState, TeamHubContract.ViewState> {

    override fun transform(data: TeamHubState): TeamHubContract.ViewState {
        val tabs = if (data.isLegacyTeam) {
            createTabForLegacyTeam(data)
        } else {
            createTabs(data)
        }
        val tabIndexes = tabs.withIndex().associate { it.value.type to it.index }
        val currentHubTabUi = data.currentTab

        return TeamHubContract.ViewState(
            teamHub = HubUi.Team(
                teamHeader = HubUi.Team.TeamHeader(
                    teamLogos = data.teamLogos,
                    teamName = data.teamName.orShortDash(),
                    currentStanding = data.teamStanding.orEmpty(),
                    backgroundColor = data.teamContrastColor,
                    isFollowed = data.isFollowed,
                ),
                tabs = tabs,
                tabIndexes = tabIndexes,
                currentTab = if (tabIndexes.contains(currentHubTabUi)) {
                    currentHubTabUi
                } else {
                    HubTabType.Home
                },
                showLoadingSpinner = data.loadingState.isFreshLoadingState
            )
        )
    }

    private fun createTabs(data: TeamHubState): List<HubUi.HubTab> {
        if (data.teamId == null) return emptyList()
        return listOfNotNull(
            createHomeTab(
                teamId = data.teamId,
                leagueId = data.league.toGraphqlLeagueCode.rawValue,
                feedType = data.feedType
            ),

            createScheduleTab(
                teamId = data.teamId,
                leagueId = data.league.toGraphqlLeagueCode.rawValue
            ),

            createStandingsTab(
                teamId = data.teamId,
                league = data.league,
                sport = data.sport,
                hasAutoScrolled = data.hasAutoScrolled
            ),

            createStatsTab(
                teamId = data.teamId,
                leagueId = data.league.toGraphqlLeagueCode.rawValue
            ).takeIf { data.showStatsAndRosterTabs },

            createRosterTab(
                teamId = data.teamId,
                leagueId = data.league.toGraphqlLeagueCode.rawValue,
                sport = data.sport
            ).takeIf { data.showStatsAndRosterTabs },
        )
    }

    private fun createHomeTab(
        teamId: String,
        leagueId: String?,
        feedType: FeedType
    ) = HubUi.HubTab(
        type = HubTabType.Home,
        label = StringWithParams(R.string.team_hub_tab_home_label),
        module = HubHomeModule(
            entityId = teamId,
            leagueId = leagueId,
            feedType = feedType
        )
    )

    private fun createScheduleTab(
        teamId: String,
        leagueId: String?
    ) = HubUi.HubTab(
        type = HubTabType.Schedule,
        label = StringWithParams(R.string.team_hub_tab_schedule_label),
        module = HubScheduleFeedModule(
            folowableId = Followable.Id(
                id = teamId,
                type = Followable.Type.TEAM
            )
        )
    )

    private fun createStandingsTab(
        teamId: String,
        league: League,
        sport: Sport,
        hasAutoScrolled: Boolean
    ) = HubUi.HubTab(
        type = HubTabType.Standings,
        label = StringWithParams(
            if (sport == Sport.SOCCER) {
                R.string.team_hub_tab_table_label
            } else {
                R.string.team_hub_tab_standings_label
            }
        ),
        module = TeamHubStandingsModule(
            teamId = teamId,
            league = league,
            hasAutoScrolled = hasAutoScrolled
        )
    )

    private fun createStatsTab(
        teamId: String,
        leagueId: String
    ) = HubUi.HubTab(
        type = HubTabType.Stats,
        label = StringWithParams(R.string.team_hub_tab_stats_label),
        module = TeamHubStatsModule(
            teamId = teamId,
            leagueId = leagueId
        )
    )

    private fun createRosterTab(
        teamId: String,
        leagueId: String,
        sport: Sport
    ) = HubUi.HubTab(
        type = HubTabType.Roster,
        label = StringWithParams(
            if (sport == Sport.SOCCER) {
                R.string.team_hub_tab_squad_label
            } else {
                R.string.team_hub_tab_roster_label
            }
        ),
        module = TeamHubRosterModule(
            teamId = teamId,
            leagueId = leagueId
        )
    )

    private fun createTabForLegacyTeam(data: TeamHubState): List<HubUi.HubTab> {
        return listOf(
            HubUi.HubTab(
                type = HubTabType.Home,
                label = StringWithParams(R.string.team_hub_tab_home_label),
                module = HubHomeModule(
                    entityId = data.teamFollowable?.id?.toString().orEmpty(),
                    leagueId = null,
                    feedType = data.feedType
                )
            ),
        )
    }
}