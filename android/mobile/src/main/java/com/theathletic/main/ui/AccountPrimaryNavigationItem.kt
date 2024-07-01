package com.theathletic.main.ui

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.theathletic.R
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.profile.ui.ProfileFragment
import com.theathletic.profile.ui.ProfileNavigationEvent
import com.theathletic.profile.ui.ProfileNavigationEventProducer
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AccountPrimaryNavigationItem @AutoKoin constructor(
    private val profileEventBus: ProfileNavigationEventProducer,
    private val analytics: Analytics,
    dispatcherProvider: DispatcherProvider
) : PrimaryNavigationItem {

    override val secondaryNavigationItems = MutableLiveData<List<SecondaryNavigationItem>>(
        listOf(SecondaryNavigationItem.SingleWithoutNavigation(this))
    )

    private val coroutineScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + dispatcherProvider.main)

    override val title: Int
        get() = R.string.main_navigation_account

    override val currentlySelectedItem = MutableLiveData(0)

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ProfileFragment.newInstance(hideToolbar = true)
            else -> throw IllegalArgumentException("")
        }
    }

    override fun onPrimaryTabReselection() {
        coroutineScope.launch {
            profileEventBus.emit(ProfileNavigationEvent.ScrollToTopOfFeed)
        }
    }
}