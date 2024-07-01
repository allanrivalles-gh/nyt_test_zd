package com.theathletic.main.ui

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.theathletic.followable.FollowableId
import timber.log.Timber

/**
 * Models a Primary navigation view in the MainActivity design. In particular, a primary
 * navigation is usually correlated with a bottom navigation tab in our app (i.e. Feed, Podcasts,
 * Community, Scores). This Navigation Item is responsible for providing everything necessary
 * to display a series of views.
 */
interface PrimaryNavigationItem {
    /**
     * title which can be displayed atop the view (e.g. AppBar)
     * Note: Because the title is used to create a stable id,
     * please use a getter to avoid issues with order of operations
     */
    @get:StringRes
    val title: Int

    /**
     * If true, tabs will distribute evenly and fill up the top bar. Otherwise they will just be
     * measured as wrap_content and be scrollable.
     */
    val fixedWidthTabs get() = false

    /**
     * If true, we disable swiping between tabs. This is necessary for tabs which use Compose
     * for now since the touch interactions between Android View and Compose gets janky.
     */
    val disableViewPagerSwiping get() = false

    val currentlySelectedItem: MutableLiveData<Int>

    /**
     * Triggers any clear events necessary when a PrimaryNavigationItem may be destroyed.
     */
    fun onDestroy() {
        // no-op in the default implementation
    }

    /**
     * Creates a Fragment for the given position. lifecycle will be managed by the consumer
     */
    fun createFragment(position: Int): Fragment

    /**
     * Exposes an ordered list of [SecondaryNavigationItem] values for display navigation.
     * This may be backed by any data source (e.g. API results or hardcoded values)
     */
    val secondaryNavigationItems: LiveData<List<SecondaryNavigationItem>>

    /**
     * The number of tabs in the secondary navigation bar. This defaults to the same number of
     * fragments in [fragmentCount] but can differ in the case of the Feed where we have a "More"
     * tab but no corresponding fragment.
     */
    val tabCount: Int get() = secondaryNavigationItems.value?.size ?: 0

    /**
     * The number of fragments in the main [ViewPager2]. These fragments can be swiped between.
     */
    val fragmentCount: Int get() = secondaryNavigationItems.value?.size ?: 0

    fun secondaryNavItem(
        uniqueId: String,
        title: String
    ) = SecondaryNavigationItem.StringBased(this, uniqueId, title)

    fun secondaryNavItem(
        @StringRes title: Int,
        showLiveIndicator: Boolean = false,
    ) = SecondaryNavigationItem.ResourceBased(this, title, showLiveIndicator)

    // Analytics
    @Deprecated("Secondary navigation is no longer provided by the MainActivity, implement in each view")
    fun trackSecondaryItemClickedEvent(position: Int) {
        // To avoid detekt
        Timber.v("Unimplemented")
    }

    fun onPrimaryTabReselection() {
        // To avoid detekt
        Timber.v("onPrimaryTabReselection -unimplemented")
    }

    suspend fun setBrowseItem(followableId: FollowableId?): Boolean {
        // Override to receive user topic search result
        return false
    }
}