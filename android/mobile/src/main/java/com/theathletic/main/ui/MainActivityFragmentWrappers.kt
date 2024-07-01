package com.theathletic.main.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentManager
import com.theathletic.feed.FeedType
import com.theathletic.feed.UserFeedStateObserver
import com.theathletic.feed.compose.FeedGraph
import com.theathletic.feed.compose.FeedGraphFeed
import com.theathletic.feed.ui.FeedListPlaceholder
import com.theathletic.followable.Followable
import com.theathletic.fragment.AthleticFragment
import com.theathletic.main.ui.navigation.FragmentWrapper
import com.theathletic.main.ui.navigation.TabState
import com.theathletic.profile.ui.ProfileFragment
import org.koin.androidx.compose.get
import timber.log.Timber

@Composable
@Suppress("LongParameterList")
fun MainFeedScreen(
    navItems: NavigationItems,
    onEditClick: () -> Unit,
    onNavigationItemClick: (Followable.Id, Int) -> Unit,
    route: String,
    fragmentManager: () -> FragmentManager,
    feedPrimaryNavigationItem: FeedPrimaryNavigationItem,
    mainNavEventConsumer: () -> MainNavigationEventConsumer,
    feedStateConsumer: UserFeedStateObserver = get(),
    tabState: TabState,
    isComposeFeedEnabled: Boolean,
) {
    Column {
        FollowableNavigationBar(
            navItems = navItems.items,
            showPlaceholder = navItems.showPlaceholder,
            onEditClick = onEditClick,
            onFollowableClick = onNavigationItemClick,
        )

        val showPlaceholder = rememberSaveable { mutableStateOf(true) }
        LaunchedEffect(Unit) {
            feedStateConsumer.collect { event ->
                Timber.v("[FEED] Placeholder: $event")
                showPlaceholder.value = event.isEmptyAndLoading
            }
        }
        Box(modifier = Modifier.fillMaxSize()) {
            if (isComposeFeedEnabled) {
                FeedGraph(feed = FeedGraphFeed.FOLLOWING, route = route, ads = FeedType.User.adsConfig())
            } else {
                FragmentWrapper(
                    fragment = {
                        feedPrimaryNavigationItem.createFragment(-1) as AthleticFragment
                    },
                    fragmentManager = fragmentManager,
                    tab = BottomTabItem.FEED,
                    mainNavEventConsumer = mainNavEventConsumer(),
                    mainPrimaryNavigationItem = feedPrimaryNavigationItem,
                    tabState = tabState,
                )
                this@Column.AnimatedVisibility(
                    visible = showPlaceholder.value,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    FeedListPlaceholder()
                }
            }
        }
    }
}

@Composable
fun AccountScreen(
    accountPrimaryNavigationItem: AccountPrimaryNavigationItem,
    fragmentManager: () -> FragmentManager,
    mainNavEventConsumer: () -> MainNavigationEventConsumer,
    tabState: TabState
) {
    FragmentWrapper(
        fragment = { ProfileFragment.newInstance(hideToolbar = true) },
        fragmentManager = fragmentManager,
        tab = BottomTabItem.ACCOUNT,
        mainNavEventConsumer = mainNavEventConsumer(),
        mainPrimaryNavigationItem = accountPrimaryNavigationItem,
        tabState = tabState
    )
}