package com.theathletic.hub.league.ui

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.entity.main.Sport
import com.theathletic.featureswitches.FeatureSwitch
import com.theathletic.featureswitches.FeatureSwitches
import com.theathletic.followable.Followable
import com.theathletic.hub.HubTabType
import com.theathletic.hub.ui.HubHomeModule
import com.theathletic.hub.ui.HubScheduleFeedModule
import com.theathletic.hub.ui.HubUi
import com.theathletic.hub.ui.LeagueHubStandingsModule
import com.theathletic.hub.ui.TeamHubBracketsModule
import com.theathletic.scores.data.remote.toGraphqlLeagueCode
import com.theathletic.type.LeagueCode
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.Transformer
import com.theathletic.utility.LocaleUtility
import com.theathletic.utility.orLongDash

private const val NCAAMB_SEASON_ID_FOR_TEST = "GThxVRjmTY7npprU"
private const val NCAAWB_SEASON_ID_FOR_TEST = "W0SfakirD8I6yurJ"

class LeagueHubTransformer @AutoKoin constructor(
    private val featureSwitches: FeatureSwitches,
    private val localeUtility: LocaleUtility
) :
    Transformer<LeagueHubState, LeagueHubContract.ViewState> {

    override fun transform(data: LeagueHubState): LeagueHubContract.ViewState {
        val tabs = createTabs(data)
        val tabIndexes = tabs.withIndex().associate { it.value.type to it.index }
        val currentHubTabUi = data.currentTab

        return LeagueHubContract.ViewState(
            leagueHub = HubUi.League(
                leagueHeader = HubUi.League.LeagueHeader(
                    logoUrl = data.leagueLogoUrl.orEmpty(),
                    name = data.leagueName.orLongDash(),
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

    private fun createTabs(data: LeagueHubState): List<HubUi.HubTab> {
        if (data.leagueId == null || data.legacyLeagueId == null) return emptyList()
        return listOfNotNull(
            HubUi.HubTab(
                type = HubTabType.Home,
                label = StringWithParams(R.string.team_hub_tab_home_label),
                module = HubHomeModule(
                    entityId = data.leagueId,
                    leagueId = null,
                    feedType = data.feedType
                )
            ),
            HubUi.HubTab(
                type = HubTabType.Schedule,
                label = StringWithParams(R.string.team_hub_tab_schedule_label),
                module = HubScheduleFeedModule(
                    folowableId = Followable.Id(
                        id = data.legacyLeagueId.toString(),
                        type = Followable.Type.LEAGUE
                    )
                )
            ).takeIf { data.showScheduleAndStandingTabs },
            HubUi.HubTab(
                type = HubTabType.Standings,
                label = StringWithParams(
                    if (data.sport == Sport.SOCCER) {
                        R.string.team_hub_tab_table_label
                    } else {
                        R.string.team_hub_tab_standings_label
                    }
                ),
                module = LeagueHubStandingsModule(
                    league = data.league
                )
            ).takeIf { data.showScheduleAndStandingTabs },
            createBracketsTabIfItShouldBeAvailable(data),
        )
    }

    private fun createBracketsTabIfItShouldBeAvailable(data: LeagueHubState): HubUi.HubTab? {
        val leagueCode = data.league.toGraphqlLeagueCode
        val hardcodedSeasonId = featureSwitches.hardcodedSeasonId(leagueCode)

        // we don't want to show the tab when the server does not want to
        // but we still want it to be visible if we are providing some hardcoded season id for testing
        return if ((data.hasActiveBracket || hardcodedSeasonId != null) && areBracketsSupportedForLeague(leagueCode)) {
            HubUi.HubTab(
                type = HubTabType.Brackets,
                label = StringWithParams(
                    if (localeUtility.isUnitedStatesOrCanada()) {
                        R.string.league_hub_tab_bracket_label_north_america
                    } else {
                        R.string.league_hub_tab_bracket_label_world
                    }
                ),
                module = TeamHubBracketsModule(leagueCode, hardcodedSeasonId)
            )
        } else {
            null
        }
    }

    private fun areBracketsSupportedForLeague(leagueCode: LeagueCode): Boolean {
        return leagueCode in listOf(
            LeagueCode.ncaamb,
            LeagueCode.ncaawb,
            LeagueCode.wwc,
        )
    }
}

// This is for testing the brackets
private fun FeatureSwitches.hardcodedSeasonId(leagueCode: LeagueCode): String? {
    if (isFeatureEnabled(FeatureSwitch.HARDCODED_NCAAMB_BRACKETS_SEASON)) {
        return when (leagueCode) {
            LeagueCode.ncaamb -> NCAAMB_SEASON_ID_FOR_TEST
            LeagueCode.ncaawb -> NCAAWB_SEASON_ID_FOR_TEST
            else -> null
        }
    }
    return null
}