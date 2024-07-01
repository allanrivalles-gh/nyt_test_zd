package com.theathletic.main.ui

import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import java.lang.ref.WeakReference
import kotlin.math.min

/**
 * Copied from Google's [TabLayoutMediator] but checks for a [TabSelectionListener] on [Tab.tag] to
 * notify [Tab] when it's been select, unselected, or reselected.
 *
 * In the case of selected, if there is no listener on the tab, then it uses its default behavior
 * of setting the [ViewPager] to the appropriate item.
 */
class AthleticTabLayoutMediator(
    private val tabLayout: TabLayout,
    private val viewPager: ViewPager2,
    private val tabConfigurationStrategy: TabConfigurationStrategy,
    val primaryNavigationItemGetter: () -> PrimaryNavigationItem,
    private val autoRefresh: Boolean = true
) {
    private var attached = false
    private var adapter: SecondaryNavigationAdapter? = viewPager.adapter as SecondaryNavigationAdapter
    private var onPageChangeCallback: TabLayoutOnPageChangeCallback? =
        TabLayoutOnPageChangeCallback(primaryNavigationItemGetter, tabLayout)
    private var onTabSelectedListener: ViewPagerOnTabSelectedListener? = ViewPagerOnTabSelectedListener()
    private var pagerAdapterObserver: AdapterDataObserver? = null

    interface TabConfigurationStrategy {
        fun onConfigureTab(tab: TabLayout.Tab, position: Int)
    }

    fun attach() {
        check(!attached) { "TabLayoutMediator is already attached" }
        adapter = viewPager.adapter as SecondaryNavigationAdapter
        checkNotNull(adapter) { "TabLayoutMediator attached before ViewPager2 has an " + "adapter" }
        attached = true
        // Add our custom OnPageChangeCallback to the ViewPager
        viewPager.registerOnPageChangeCallback(onPageChangeCallback!!)
        tabLayout.addOnTabSelectedListener(onTabSelectedListener!!)

        if (autoRefresh) {
            pagerAdapterObserver = PagerAdapterObserver()
            adapter!!.registerAdapterDataObserver(pagerAdapterObserver!!)
        }
        populateTabsFromPagerAdapter()
        tabLayout.setScrollPosition(viewPager.currentItem, 0f, true)
    }

    fun detach() {
        if (autoRefresh && adapter != null) {
            adapter!!.unregisterAdapterDataObserver(pagerAdapterObserver!!)
            pagerAdapterObserver = null
        }
        tabLayout.removeOnTabSelectedListener(onTabSelectedListener!!)
        viewPager.unregisterOnPageChangeCallback(onPageChangeCallback!!)
        onTabSelectedListener = null
        onPageChangeCallback = null
        adapter = null
        attached = false
    }

    fun populateTabsFromPagerAdapter() {
        tabLayout.removeAllTabs()
        if (adapter != null) {
            val adapterCount = adapter!!.tabCount
            for (i in 0 until adapterCount) {
                val tab = tabLayout.newTab()
                tabConfigurationStrategy.onConfigureTab(tab, i)
                tabLayout.addTab(tab, false)
            }
            // Make sure we reflect the currently set ViewPager item
            if (adapterCount > 0) {
                val lastItem = tabLayout.tabCount - 1
                val currItem = min(primaryNavigationItemGetter().currentlySelectedItem.value!!, lastItem)
                if (currItem != tabLayout.selectedTabPosition) {
                    tabLayout.selectTab(tabLayout.getTabAt(currItem), false)
                }
            }
        }
    }

    private class TabLayoutOnPageChangeCallback internal constructor(
        val primaryNavigationItemGetter: () -> PrimaryNavigationItem,
        tabLayout: TabLayout
    ) : OnPageChangeCallback() {

        private val tabLayoutRef: WeakReference<TabLayout> = WeakReference(tabLayout)
        private var previousScrollState = 0
        private var scrollState = 0

        init {
            reset()
        }

        fun reset() {
            scrollState = ViewPager2.SCROLL_STATE_IDLE
            previousScrollState = scrollState
        }

        override fun onPageScrollStateChanged(state: Int) {
            previousScrollState = scrollState
            scrollState = state
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            val tabLayout = tabLayoutRef.get()
            if (tabLayout != null) {
                val updateText = scrollState != ViewPager2.SCROLL_STATE_SETTLING || previousScrollState == ViewPager2.SCROLL_STATE_DRAGGING
                val updateIndicator = !(scrollState == ViewPager2.SCROLL_STATE_SETTLING && previousScrollState == ViewPager2.SCROLL_STATE_IDLE)
                tabLayout.setScrollPosition(position, positionOffset, updateText, updateIndicator)
            }
        }

        override fun onPageSelected(position: Int) {
            val tabLayout = tabLayoutRef.get()
            if (tabLayout != null && tabLayout.selectedTabPosition != position && position < tabLayout.tabCount) {
                if (primaryNavigationItemGetter().currentlySelectedItem.value != position) {
                    primaryNavigationItemGetter().currentlySelectedItem.value = position
                }
            }
        }
    }

    private inner class ViewPagerOnTabSelectedListener : OnTabSelectedListener {

        override fun onTabSelected(tab: TabLayout.Tab) {
            updateSelectionState(tab, true)
            viewPager.setCurrentItem(tab.position, true)
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
            updateSelectionState(tab, false)
        }

        override fun onTabReselected(tab: TabLayout.Tab?) {
            // empty
        }

        private fun updateSelectionState(tab: TabLayout.Tab, selected: Boolean) {
            (tab.customView as? NavTabView)?.setActive(selected)
        }
    }

    private inner class PagerAdapterObserver internal constructor() : AdapterDataObserver() {
        override fun onChanged() {
            populateTabsFromPagerAdapter()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            populateTabsFromPagerAdapter()
        }

        override fun onItemRangeChanged(
            positionStart: Int,
            itemCount: Int,
            payload: Any?
        ) {
            populateTabsFromPagerAdapter()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            populateTabsFromPagerAdapter()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            populateTabsFromPagerAdapter()
        }

        override fun onItemRangeMoved(
            fromPosition: Int,
            toPosition: Int,
            itemCount: Int
        ) {
            populateTabsFromPagerAdapter()
        }
    }
}