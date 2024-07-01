package com.theathletic.main.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.theathletic.main.ui.BottomTabItem

@Composable
fun Navigation(
    navController: NavHostController,
    fragmentData: FragmentData,
    tabState: TabState,
    secondaryTabIndex: Int
) {
    NavHost(navController = navController, startDestination = BottomTabItem.FEED.name) {
        enumValues<BottomTabItem>().forEach { item ->
            composable(item.name, createSubRoute(secondaryTabIndex)) {
                AndroidViewWrapper(
                    fragmentData.uniqueId,
                    tabState = tabState,
                    modifier = Modifier.fillMaxSize(),
                    commit = {
                        saveLoadNewFragment(
                            fragmentManager = fragmentData.fragmentManager,
                            fragmentId = fragmentData.uniqueId,
                            tabState = tabState
                        )
                        replace(
                            it,
                            fragmentData.fragment.apply {
                                setInitialSavedState(tabState.savedStates[fragmentData.uniqueId])
                            }
                        )
                    }
                )
            }
        }
    }
}

private fun createSubRoute(secondaryTabIndex: Int = 0) = listOf(
    navArgument("id") {
        type = NavType.IntType
        defaultValue = secondaryTabIndex
    }
)

private fun saveLoadNewFragment(
    fragmentManager: FragmentManager,
    fragmentId: Int,
    tabState: TabState,
) {
    val currentFragment = fragmentManager.findFragmentById(tabState.selectedTabId.value)
    if (currentFragment != null) {
        tabState.savedStates.put(
            tabState.selectedTabId.value,
            fragmentManager.saveFragmentInstanceState(currentFragment)
        )
    }

    tabState.selectedTabId.value = fragmentId
}