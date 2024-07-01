package com.theathletic.profile.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.os.bundleOf
import com.theathletic.R
import com.theathletic.activity.BaseActivity
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.databinding.FragmentProfileBinding
import com.theathletic.extension.betterSmoothScrollToPosition
import com.theathletic.fragment.AthleticMvpBindingFragment
import com.theathletic.profile.ui.ProfileContract.ProfileViewState
import com.theathletic.ui.DisplayPreferences
import com.theathletic.ui.list.BindingDiffAdapter
import com.theathletic.ui.observe
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class ProfileFragment : AthleticMvpBindingFragment<
    ProfileViewModel,
    FragmentProfileBinding,
    ProfileViewState
    >() {

    val analytics by inject<Analytics>()
    val displayPreferences by inject<DisplayPreferences>()

    var adapter: BindingDiffAdapter? = null

    private var viewState: ProfileViewState = ProfileViewState()

    companion object {
        const val ARG_HIDE_TOOLBAR = "hide_toolbar"

        fun newInstance(hideToolbar: Boolean = false) = ProfileFragment().apply {
            arguments = bundleOf(ARG_HIDE_TOOLBAR to hideToolbar)
        }
    }

    override fun setupViewModel() = getViewModel<ProfileViewModel> {
        parametersOf(
            ProfileViewModel.Params(displayTheme = displayPreferences.dayNightMode),
            navigator
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as? BaseActivity)?.setupActionBar(
            title = resources.getString(R.string.profile_account),
            binding.toolbarProfile.toolbar
        )
        if (arguments?.getBoolean(ARG_HIDE_TOOLBAR) == true) {
            binding.toolbarProfile.toolbar.visibility = View.GONE
        }
        presenter.observe<ProfileContract.Event>(this) { event ->
            if (event == ProfileContract.Event.ScrollToTopOfFeed) {
                binding.recyclerView.betterSmoothScrollToPosition(0)
            }
        }
    }

    override fun inflateBindingLayout(inflater: LayoutInflater): FragmentProfileBinding {
        val binding = FragmentProfileBinding.inflate(inflater)

        adapter = ProfileAdapter(viewLifecycleOwner, presenter)
        binding.recyclerView.adapter = adapter

        return binding
    }

    override fun renderState(viewState: ProfileViewState) {
        this.viewState = viewState
        adapter?.submitList(viewState.listModels)
    }
}