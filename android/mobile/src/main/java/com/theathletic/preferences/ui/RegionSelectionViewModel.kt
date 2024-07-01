package com.theathletic.preferences.ui

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.entity.settings.UserContentEdition
import com.theathletic.profile.ui.RegionOptions
import com.theathletic.region.UserContentEditionRepository
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.DataState
import com.theathletic.ui.Transformer
import com.theathletic.user.IUserManager
import com.theathletic.utility.FeedPreferences
import java.util.Locale
import kotlinx.coroutines.launch

class RegionSelectionViewModel @AutoKoin constructor(
    transformer: RegionSelectionTransformer,
    private val userContentEditionRepository: UserContentEditionRepository,
    private val userManager: IUserManager,
    private val feedPreferences: FeedPreferences,
) : AthleticViewModel<RegionSelectionState, RegionSelectionContract.RegionSelectionViewState>(),
    RegionSelectionContract.ViewModelInteractor,
    DefaultLifecycleObserver,
    Transformer<RegionSelectionState, RegionSelectionContract.RegionSelectionViewState> by transformer {

    override val initialState by lazy {
        RegionSelectionState(
            regions = listOf(RegionOptions.NORTH_AMERICA, RegionOptions.INTERNATIONAL),
            currentRegion = getCurrentUserRegion()
        )
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        loadRegions()
    }

    private fun loadRegions() {
        viewModelScope.launch {
            userContentEditionRepository.fetchUserContentEdition(UserContentEdition.US).join()
            val region = userManager.getUserContentEdition()
            updateState {
                copy(
                    currentRegion = region.toLocalRegion,
                )
            }
        }
    }

    override fun onBackClick() {
        sendEvent(RegionSelectionContract.Event.NavigateBackToProfile)
    }

    override fun regionOptionSelected(region: RegionOptions) {
        userContentEditionRepository.setUserContentEdition(region.toUserContent)
        feedPreferences.setFeedLastFetchDate(com.theathletic.feed.FeedType.Frontpage, 0L)
        updateState { copy(currentRegion = region) }
    }

    private fun getCurrentUserRegion(): RegionOptions {
        return userManager.getUserContentEdition().toLocalRegion
    }

    private val RegionOptions.toUserContent: UserContentEdition
        get() = when (this) {
            RegionOptions.NORTH_AMERICA -> UserContentEdition.US
            RegionOptions.INTERNATIONAL -> UserContentEdition.UK
            else -> Locale.getDefault().toLanguageTag().toUserContentEdition
        }

    private val String.toUserContentEdition: UserContentEdition
        get() = when (this) {
            UserContentEdition.US.value -> UserContentEdition.US
            UserContentEdition.UK.value -> UserContentEdition.UK
            else -> UserContentEdition.UK
        }

    private val String.toRegionOption: RegionOptions
        get() = when (this) {
            UserContentEdition.US.value -> RegionOptions.NORTH_AMERICA
            UserContentEdition.UK.value -> RegionOptions.INTERNATIONAL
            else -> RegionOptions.INTERNATIONAL
        }

    private val UserContentEdition?.toLocalRegion: RegionOptions
        get() = when (this) {
            UserContentEdition.US -> RegionOptions.NORTH_AMERICA
            UserContentEdition.UK -> RegionOptions.INTERNATIONAL
            else -> Locale.getDefault().toLanguageTag().toRegionOption
        }
}

data class RegionSelectionState(
    val currentRegion: RegionOptions,
    val regions: List<RegionOptions>,
) : DataState