package com.theathletic.preferences.ui

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import com.theathletic.R
import com.theathletic.ui.UiModel
import com.theathletic.ui.list.AthleticMvpListFragment
import com.theathletic.ui.list.SimpleListViewState
import org.koin.android.ext.android.get

class NewsletterPreferencesFragment : AthleticMvpListFragment<
    SimpleListViewState,
    NewsletterPreferencesViewModel>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarBrand.mainAppbar.apply {
            background = ColorDrawable(resources.getColor(R.color.ath_grey_70, null))
        }
    }

    override fun setupViewModel() = get<NewsletterPreferencesViewModel>()

    override fun getLayoutForModel(model: UiModel): Int {
        return when (model) {
            is NewsletterSwitchItem -> R.layout.list_item_preferences_newsletter_switch
            else -> throw IllegalArgumentException("${model.javaClass} not supported")
        }
    }
}