package com.theathletic.main.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentManager
import com.theathletic.R
import com.theathletic.feed.FeedType
import com.theathletic.feed.compose.FeedGraph
import com.theathletic.feed.compose.FeedGraphFeed
import com.theathletic.fragment.AthleticFragment
import com.theathletic.main.ui.navigation.FragmentWrapper
import com.theathletic.main.ui.navigation.TabState
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.widgets.ResourceIcon

@Composable
fun MainDiscoverScreen(
    discoverPrimaryNavigationItem: DiscoverPrimaryNavigationItem,
    fragmentManager: () -> FragmentManager,
    onSearchClick: () -> Unit,
    route: String,
    mainNavEventConsumer: () -> MainNavigationEventConsumer,
    tabState: TabState,
    isComposeFeedEnabled: Boolean
) {
    Column(Modifier.fillMaxSize()) {
        DiscoverToolbar(onSearchClick = onSearchClick)
        if (isComposeFeedEnabled) {
            FeedGraph(feed = FeedGraphFeed.DISCOVER, ads = FeedType.Frontpage.adsConfig(), route = route)
        } else {
            FragmentWrapper(
                fragment = { discoverPrimaryNavigationItem.createFragment(0) as AthleticFragment },
                fragmentManager = fragmentManager,
                tab = BottomTabItem.DISCOVER,
                mainNavEventConsumer = mainNavEventConsumer(),
                mainPrimaryNavigationItem = discoverPrimaryNavigationItem,
                tabState = tabState
            )
        }
    }
}

@Composable
private fun DiscoverToolbar(
    onSearchClick: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
            .padding(horizontal = 16.dp),
    ) {
        Text(
            text = stringResource(R.string.main_navigation_discover),
            color = AthTheme.colors.dark800,
            style = AthTextStyle.Slab.Bold.Small,
        )

        ResourceIcon(
            resourceId = R.drawable.ic_nav2_search,
            tint = AthTheme.colors.dark800,
            modifier = Modifier
                .size(32.dp)
                .background(AthTheme.colors.dark300, CircleShape)
                .clickable(onClick = onSearchClick)
                .padding(4.dp)
        )
    }
}

@Preview
@Composable
private fun DiscoverToolbar_Preview() {
    DiscoverToolbar(onSearchClick = {})
}