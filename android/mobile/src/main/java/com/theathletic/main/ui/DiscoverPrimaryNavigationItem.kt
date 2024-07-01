package com.theathletic.main.ui

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.featureswitches.FeatureSwitches
import com.theathletic.feed.FeedNavItemEvent
import com.theathletic.feed.FeedNavItemEventProducer
import com.theathletic.feed.FeedType
import com.theathletic.feed.ui.FeedFragment
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class DiscoverPrimaryNavigationItem @AutoKoin constructor(
    private val feedNavEventBus: FeedNavItemEventProducer,
    featureSwitches: FeatureSwitches,
    dispatcherProvider: DispatcherProvider,
) : PrimaryNavigationItem {
    override val secondaryNavigationItems = MutableLiveData<List<SecondaryNavigationItem>>(
        listOf(SecondaryNavigationItem.SingleWithoutNavigation(this))
    )

    private val coroutineScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + dispatcherProvider.main)

    override val currentlySelectedItem = MutableLiveData(0)

    override val title = R.string.main_navigation_news

    override fun createFragment(position: Int): Fragment {
        if (position != 0) throw IllegalArgumentException("Loading feed with invalid position: $position")
        return FeedFragment.newInstance(FeedType.Frontpage)
    }

    override fun onPrimaryTabReselection() {
        coroutineScope.launch {
            feedNavEventBus.emit(FeedNavItemEvent.ScrollToTopOfFeed(FeedType.Frontpage))
        }
    }
}