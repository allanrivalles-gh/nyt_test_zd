package com.theathletic.preferences.ui

import com.theathletic.feed.search.ui.UserTopicListItem
import com.theathletic.presenter.Interactor

interface NotificationPreferenceContract {

    interface ViewModelInteractor : Interactor, UserTopicListItem.Interactor, IPreferenceToggleView
}