package com.theathletic.preferences.ui

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import com.theathletic.R
import com.theathletic.feed.search.ui.UserTopicListItem
import com.theathletic.preferences.ui.models.PodcastNotificationsEmptyItem
import com.theathletic.ui.UiModel
import com.theathletic.ui.list.AthleticMvpListFragment
import com.theathletic.ui.list.ListSectionTitleItem
import com.theathletic.ui.list.SimpleListViewState
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class NotificationPreferenceFragment :
    AthleticMvpListFragment<SimpleListViewState, NotificationPreferenceViewModel>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarBrand.mainAppbar.background = ColorDrawable(resources.getColor(R.color.ath_grey_70, null))
    }

    override fun setupViewModel() = getViewModel<NotificationPreferenceViewModel> {
        parametersOf(navigator)
    }

    override fun getLayoutForModel(model: UiModel): Int {
        return when (model) {
            is UserTopicListItem -> R.layout.list_item_preferences_user_topic
            is PushNotificationSwitchItem -> R.layout.list_item_preferences_switch
            is ListSectionTitleItem -> R.layout.list_item_notification_preferences_section_title
            is PodcastNotificationsEmptyItem -> R.layout.list_item_notification_preferences_empty_podcast
            else -> throw IllegalArgumentException("$model not supported")
        }
    }
}