package com.theathletic.profile.ui

import com.theathletic.presenter.Interactor
import com.theathletic.ui.UiModel
import com.theathletic.ui.ViewState

interface ProfileContract {

    interface ProfileInteractor :
        Interactor,
        ProfileAnonymousHeaderItem.Interactor,
        ProfileListItem.Interactor,
        ProfileFooterItem.Interactor,
        ProfileFollowingListItem.Interactor,
        ProfileFollowingCarouselAddMoreItem.Interactor,
        ProfileSubscribeItem.Interactor,
        ProfileHeaderItem.Interactor,
        ProfileLoginItem.Interactor,
        ProfileFollowingCarouselItem.Interactor,
        DayNightToggleItem.Interactor

    data class ProfileViewState(
        val listModels: List<UiModel> = emptyList(),
        val displayLoginMenuItem: Boolean = false
    ) : ViewState

    sealed class Event : com.theathletic.utility.Event() {
        object ScrollToTopOfFeed : ProfileContract.Event()
    }
}