package com.theathletic.scores.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.theathletic.scores.ui.ScoresFeedScreen
import com.theathletic.scores.ui.ScoresFeedViewModel
import com.theathletic.scores.ui.search.SearchComposeViewModel
import com.theathletic.scores.ui.search.SearchScreen
import org.koin.androidx.compose.koinViewModel

private const val KEY_ANIMATE_SEARCH_BAR = "animateSearchBar"

fun NavGraphBuilder.scoresNavGraph(navController: NavHostController) {
    navigation(
        route = ScoresNavigation.Graph.route,
        startDestination = ScoresNavigation.Scores.route
    ) {
        composable(
            route = ScoresNavigation.Scores.route,
            arguments = listOf(
                navArgument(KEY_ANIMATE_SEARCH_BAR) {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) {
            val animateSearchBar = it.arguments?.getBoolean(KEY_ANIMATE_SEARCH_BAR) ?: false
            val viewModel = koinViewModel<ScoresFeedViewModel>()
            ScoresFeedScreen(
                viewModel = viewModel,
                onSearchBarClick = {
                    navController.navigate(route = ScoresNavigation.Search.route)
                    viewModel.trackSearchBarClicked()
                },
                animateSearchBar = animateSearchBar
            )
        }

        composable(route = ScoresNavigation.Search.route) {
            val viewModel = koinViewModel<SearchComposeViewModel>()
            SearchScreen(
                viewModel = viewModel,
                onCancelClick = {
                    viewModel.trackSearchScreenCancel()
                    navController.navigate(route = ScoresNavigation.Scores.addParams(true)) {
                        launchSingleTop = true
                        popUpTo(ScoresNavigation.Scores.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}

sealed class ScoresNavigation(val route: String) {
    object Graph : ScoresNavigation("scores_graph")

    object Scores : ScoresNavigation("scores?animateSearchBar={animateSearchBar}") {
        fun addParams(animateSearchBar: Boolean): String = "scores?animateSearchBar=$animateSearchBar"
    }

    object Search : ScoresNavigation("search")
}