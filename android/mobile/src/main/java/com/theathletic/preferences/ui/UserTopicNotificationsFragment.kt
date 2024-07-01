package com.theathletic.preferences.ui

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import com.theathletic.R
import com.theathletic.ui.UiModel
import com.theathletic.ui.list.AthleticListFragment
import com.theathletic.ui.list.ListSectionTitleItem
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class UserTopicNotificationsFragment :
    AthleticListFragment<UserTopicNotificationsViewModel, UserTopicNotificationsView>(),
    UserTopicNotificationsView {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarBrand.mainAppbar.background = ColorDrawable(resources.getColor(R.color.ath_grey_70, null))
    }

    override val backgroundColorRes = R.color.ath_grey_70

    override fun setupViewModel() = getViewModel<UserTopicNotificationsViewModel> {
        parametersOf(activity?.intent?.extras ?: Bundle())
    }

    override fun getLayoutForModel(model: UiModel): Int {
        return when (model) {
            is ListSectionTitleItem -> R.layout.list_item_notification_preferences_section_title
            is PreferenceSwitchItem -> R.layout.list_item_preferences_switch
            else -> throw IllegalArgumentException("${model.javaClass} not supported")
        }
    }

    override fun onPreferenceToggled(item: PreferenceSwitchItem, isOn: Boolean) {
        viewModel.onItemToggled(item, isOn)
    }
}