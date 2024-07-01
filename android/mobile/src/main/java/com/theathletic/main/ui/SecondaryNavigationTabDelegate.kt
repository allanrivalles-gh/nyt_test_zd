package com.theathletic.main.ui

import android.content.Context
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout

class SecondaryNavigationTabDelegate(
    val context: Context,
    tabLayout: TabLayout,
    viewPager: ViewPager2,
    val primaryNavigationItemGetter: () -> PrimaryNavigationItem,
    private val showUserTopicSearchDialog: ((PrimaryNavigationItem) -> Unit)? = null
) {
    private val tabMediator = AthleticTabLayoutMediator(
        tabLayout,
        viewPager,
        object : AthleticTabLayoutMediator.TabConfigurationStrategy {
            override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
                configureTab(tab, position)
            }
        },
        primaryNavigationItemGetter
    )

    private val primaryTabClickListener: (Int) -> Unit = { position ->
        val primaryNavigationItem = primaryNavigationItemGetter()
        if (primaryNavigationItem.currentlySelectedItem.value == position) {
            showUserTopicSearchDialog?.invoke(primaryNavigationItem)
        } else {
            primaryNavigationItem.currentlySelectedItem.value = position
            primaryNavigationItem.trackSecondaryItemClickedEvent(position)
        }
    }

    private fun configureTab(tab: TabLayout.Tab, position: Int) {
        val primaryNavigationItem = primaryNavigationItemGetter()

        primaryNavigationItem.secondaryNavigationItems.value?.get(position)?.let { secondaryItem ->
            tab.tag = null

            tab.text = when (secondaryItem) {
                is SecondaryNavigationItem.StringBased -> secondaryItem.title
                is SecondaryNavigationItem.ParameterizedStringBased -> {
                    val pString = secondaryItem.parameterizedString
                    context.getString(pString.stringRes, *pString.parameters.toTypedArray())
                }
                is SecondaryNavigationItem.ResourceBased -> context.getString(secondaryItem.title)
                is SecondaryNavigationItem.SingleWithoutNavigation -> ""
                is SecondaryNavigationItem.DefaultBrowsingItem -> secondaryItem.title
                is SecondaryNavigationItem.DefaultBrowsingResourceBaseItem -> context.getString(secondaryItem.title)
                is SecondaryNavigationItem.BrowsingItem -> secondaryItem.title
            }

            tab.customView = customViewForTab(tab, primaryNavigationItem, secondaryItem, position)
        }
    }

    private fun customViewForTab(
        tab: TabLayout.Tab,
        primaryNavigationItem: PrimaryNavigationItem,
        secondaryNavigationItem: SecondaryNavigationItem,
        position: Int
    ) = NavTabView(context).apply {

        setText(tab.text)
        setFixedWidth(primaryNavigationItem.fixedWidthTabs)
        setHasMoreIcon(false)
        showLiveIndicator(secondaryNavigationItem.showLiveIndicator)

        when (secondaryNavigationItem) {
            is SecondaryNavigationItem.DefaultBrowsingItem,
            is SecondaryNavigationItem.DefaultBrowsingResourceBaseItem -> {
                setOnClickListener { primaryTabClickListener(position) }
                setHasMoreIcon(true)
            }
            is SecondaryNavigationItem.BrowsingItem -> {
                setOnClickListener { primaryTabClickListener(position) }
                setHasMoreIcon(true)
            }
            else -> setOnClickListener {
                primaryNavigationItem.currentlySelectedItem.value = position
                primaryNavigationItem.trackSecondaryItemClickedEvent(position)
            }
        }
    }

    fun attach() {
        tabMediator.attach()
    }

    fun detach() {
        tabMediator.detach()
    }
}