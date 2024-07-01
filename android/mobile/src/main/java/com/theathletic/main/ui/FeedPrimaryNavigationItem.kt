package com.theathletic.main.ui

import androidx.lifecycle.MutableLiveData
import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.featureswitches.FeatureSwitches
import com.theathletic.feed.FeedNavItemEvent
import com.theathletic.feed.FeedNavItemEventProducer
import com.theathletic.feed.FeedType
import com.theathletic.feed.ui.FeedFragment
import com.theathletic.followable.FollowableId
import com.theathletic.manager.UserDataManager
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class FeedPrimaryNavigationItem @AutoKoin constructor(
    private val feedNavEventBus: FeedNavItemEventProducer,
    featureSwitches: FeatureSwitches
) : PrimaryNavigationItem, CoroutineScope {

    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Main.immediate

    init {
        UserDataManager.userDataDisposable?.dispose()
        UserDataManager.loadUserData()
    }

    override val title: Int
        get() = R.string.main_navigation_feed

    override val currentlySelectedItem = MutableLiveData(0)

    override val secondaryNavigationItems = MutableLiveData(emptyList<SecondaryNavigationItem>())

    override fun createFragment(position: Int) = FeedFragment.newInstance(FeedType.User)

    override fun onDestroy() {
        coroutineContext.cancel()
    }

    override suspend fun setBrowseItem(followableId: FollowableId?) = false

    override fun onPrimaryTabReselection() {
        launch {
            feedNavEventBus.emit(FeedNavItemEvent.ScrollToTopOfFeed(FeedType.User))
        }
    }

    fun onTopHeadlinesReceived() {
        launch {
            feedNavEventBus.emit(FeedNavItemEvent.ScrollToTopHeadlines)
        }
    }
}