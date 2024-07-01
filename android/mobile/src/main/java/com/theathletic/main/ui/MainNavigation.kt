package com.theathletic.main.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.navigation.NavController
import com.theathletic.R
import com.theathletic.scores.navigation.ScoresNavigation

sealed class MainNavigation(
    val route: String,
    @StringRes val title: Int,
    @DrawableRes val icon: Int
) {

    object Feed : MainNavigation(
        route = "feed",
        title = R.string.main_navigation_feed,
        icon = R.drawable.ic_tab_for_you,
    )

    object Scores : MainNavigation(
        route = ScoresNavigation.Scores.route,
        title = R.string.main_navigation_scores,
        icon = R.drawable.ic_tab_scores,
    )

    object Discover : MainNavigation(
        route = "discover",
        title = R.string.main_navigation_discover,
        icon = R.drawable.ic_tab_discover
    )

    object Listen : MainNavigation(
        route = "listen",
        title = R.string.main_navigation_listen,
        icon = R.drawable.ic_listen
    )

    object Account : MainNavigation(
        route = "account",
        title = R.string.main_navigation_account,
        icon = R.drawable.ic_account
    )

    companion object {
        val destinations by lazy {
            listOf(Feed, Scores, Discover, Listen, Account)
        }
        val routes by lazy {
            destinations.map {
                it.route
            }.toHashSet()
        }
    }
}

val NavController.currentRouteState: State<String>
    @Composable get() = produceState("", this) {
        currentBackStackEntryFlow.collect {
            value = it.destination.route.orEmpty()
        }
    }