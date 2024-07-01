package com.theathletic.brackets.ui

import com.theathletic.analytics.IAnalytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Exposes

interface BracketsAnalytics {
    // Navigation
    fun trackTabToBracketsClick(leagueId: String, fromTab: Int)
    fun trackBracketsToTabClick(leagueId: String, toTab: Int)

    // Brackets
    fun viewBracketsTab(leagueId: String)
    fun trackGameBoxScoreClick(leagueId: String, gameId: String, phase: Int)

    // Brackets Tabs
    fun trackRoundTabClick(leagueId: String, round: Int)

    object View {
        const val BRACKETS = "brackets"
        const val HOME = "home"
        const val SCHEDULE = "schedule"
        const val STANDINGS = "standings"
    }

    object Element {
        const val BRACKETS_NAV = "brackets_nav"
        const val FEED_NAVIGATION = "feed_navigation"
        const val PREGAME = "pregame_box_score"
        const val INGAME = "ingame_box_score"
        const val POSTGAME = "postgame_box_score"
        const val EMPTY = ""
    }

    object ObjectType {
        const val BRACKETS = "brackets"
        const val HOME = "home"
        const val SCHEDULE = "schedule"
        const val STANDINGS = "standings"
        const val LEAGUE_ID = "league_id"
        const val GAME_ID = "game_id"
        const val FIRST_ROUND = "first_round_tab"
        const val SECOND_ROUND = "second_round_tab"
        const val THIRD_ROUND = "third_round_tab"
        const val FOURTH_ROUND = "fourth_round_tab"
        const val FIFTH_ROUND = "fifth_round_tab"
        const val SIXTH_ROUND = "sixth_round_tab"
        const val SEVENTH_ROUND = "seventh_round_tab"
    }
}

@Exposes(BracketsAnalytics::class)
class BracketsAnalyticsHandler @AutoKoin constructor(
    val analytics: IAnalytics
) : BracketsAnalytics {

    private fun eventNavigateToBracketsFromTab(leagueId: String, view: String) = Event.Brackets.Click(
        view = view,
        element = BracketsAnalytics.Element.FEED_NAVIGATION,
        object_type = BracketsAnalytics.ObjectType.BRACKETS,
        league_id = leagueId
    )

    private fun eventNavigateToTabFromBrackets(leagueId: String, objectType: String) = Event.Brackets.Click(
        view = BracketsAnalytics.View.BRACKETS,
        element = BracketsAnalytics.Element.FEED_NAVIGATION,
        object_type = objectType,
        league_id = leagueId
    )

    private fun eventClickGame(leagueId: String, gameId: String, element: String) = Event.Brackets.Click(
        view = BracketsAnalytics.View.BRACKETS,
        element = element,
        object_type = BracketsAnalytics.ObjectType.GAME_ID,
        object_id = gameId,
        league_id = leagueId
    )

    private fun eventClickRound(leagueId: String, objectType: String) = Event.Brackets.Click(
        view = BracketsAnalytics.View.BRACKETS,
        element = BracketsAnalytics.Element.BRACKETS_NAV,
        object_type = objectType,
        league_id = leagueId
    )

    override fun trackTabToBracketsClick(leagueId: String, fromTab: Int) {
        val event = when (fromTab) {
            0 -> eventNavigateToBracketsFromTab(leagueId, BracketsAnalytics.View.HOME)
            1 -> eventNavigateToBracketsFromTab(leagueId, BracketsAnalytics.View.SCHEDULE)
            2 -> eventNavigateToBracketsFromTab(leagueId, BracketsAnalytics.View.STANDINGS)
            else -> return
        }
        analytics.track(event)
    }

    override fun trackBracketsToTabClick(leagueId: String, toTab: Int) {
        val event = when (toTab) {
            0 -> eventNavigateToTabFromBrackets(leagueId, BracketsAnalytics.ObjectType.HOME)
            1 -> eventNavigateToTabFromBrackets(leagueId, BracketsAnalytics.ObjectType.SCHEDULE)
            2 -> eventNavigateToTabFromBrackets(leagueId, BracketsAnalytics.ObjectType.STANDINGS)
            else -> return
        }
        analytics.track(event)
    }

    override fun viewBracketsTab(leagueId: String) {
        analytics.track(
            Event.Brackets.View(
                object_type = BracketsAnalytics.ObjectType.LEAGUE_ID,
                object_id = leagueId
            )
        )
    }

    override fun trackGameBoxScoreClick(leagueId: String, gameId: String, phase: Int) {
        val element = when (phase) {
            0 -> BracketsAnalytics.Element.PREGAME
            1 -> BracketsAnalytics.Element.INGAME
            2 -> BracketsAnalytics.Element.POSTGAME
            else -> BracketsAnalytics.Element.EMPTY
        }
        analytics.track(
            eventClickGame(
                leagueId = leagueId,
                gameId = gameId,
                element = element
            )
        )
    }

    override fun trackRoundTabClick(leagueId: String, round: Int) {
        val objectType = when (round) {
            0 -> BracketsAnalytics.ObjectType.FIRST_ROUND
            1 -> BracketsAnalytics.ObjectType.SECOND_ROUND
            2 -> BracketsAnalytics.ObjectType.THIRD_ROUND
            3 -> BracketsAnalytics.ObjectType.FOURTH_ROUND
            4 -> BracketsAnalytics.ObjectType.FIFTH_ROUND
            5 -> BracketsAnalytics.ObjectType.SIXTH_ROUND
            6 -> BracketsAnalytics.ObjectType.SEVENTH_ROUND
            else -> return
        }
        analytics.track(
            eventClickRound(
                leagueId = leagueId,
                objectType = objectType
            )
        )
    }
}