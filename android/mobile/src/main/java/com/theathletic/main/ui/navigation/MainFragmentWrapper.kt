package com.theathletic.main.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.fragment.app.FragmentManager
import androidx.navigation.compose.rememberNavController
import com.theathletic.fragment.AthleticFragment
import com.theathletic.main.ui.BottomTabItem
import com.theathletic.main.ui.FeedPrimaryNavigationItem
import com.theathletic.main.ui.MainNavigationEvent
import com.theathletic.main.ui.MainNavigationEventConsumer
import com.theathletic.main.ui.PrimaryNavigationItem
import kotlin.math.abs
import kotlinx.coroutines.launch

@Composable
@Suppress("LongParameterList")
fun FragmentWrapper(
    fragment: () -> AthleticFragment,
    fragmentManager: () -> FragmentManager,
    tab: BottomTabItem,
    mainNavEventConsumer: MainNavigationEventConsumer,
    mainPrimaryNavigationItem: PrimaryNavigationItem? = null,
    tabState: TabState,
    secondaryTabIndex: Int = 0
) {
    val uniqueId = rememberSaveable(tab, secondaryTabIndex) { abs(tab.titleId.hashCode()) + secondaryTabIndex }

    LaunchedEffect(Unit) {
        mainNavEventConsumer.collect { event ->
            when (event) {
                MainNavigationEvent.ScrollToTopOfFeed -> launch {
                    mainPrimaryNavigationItem?.onPrimaryTabReselection()
                }
                MainNavigationEvent.ScrollToTopHeadlines -> {
                    if (mainPrimaryNavigationItem is FeedPrimaryNavigationItem) {
                        mainPrimaryNavigationItem.onTopHeadlinesReceived()
                    }
                }
                else -> {}
            }
        }
    }

    val navController = rememberNavController()
    val fragmentData = FragmentData(uniqueId, fragment(), fragmentManager())
    Navigation(
        navController = navController,
        fragmentData = fragmentData,
        tabState = tabState,
        secondaryTabIndex = secondaryTabIndex
    )
}