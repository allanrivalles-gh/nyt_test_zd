package com.theathletic.rooms.create.ui

import android.view.LayoutInflater
import androidx.core.os.bundleOf
import com.theathletic.databinding.FragmentLiveRoomTaggingBinding
import com.theathletic.fragment.AthleticMvpBindingFragment
import com.theathletic.rooms.create.data.local.LiveRoomCreationSearchMode
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class LiveRoomTaggingFragment : AthleticMvpBindingFragment<
    LiveRoomTaggingViewModel,
    FragmentLiveRoomTaggingBinding,
    LiveRoomTaggingContract.ViewState
    >() {

    companion object {
        private const val ARG_SEARCH_MODE = "arg_search_mode"

        fun newInstance(
            searchMode: LiveRoomCreationSearchMode
        ) = LiveRoomTaggingFragment().apply {
            arguments = bundleOf(ARG_SEARCH_MODE to searchMode)
        }
    }

    private lateinit var adapter: LiveRoomTagSearchAdapter
    private var viewStateHandler: LiveRoomTaggingViewStateHandler? = null

    override fun setupViewModel() = getViewModel<LiveRoomTaggingViewModel> {
        parametersOf(
            LiveRoomTaggingViewModel.Params(
                searchMode = (arguments?.getSerializable(ARG_SEARCH_MODE) as? LiveRoomCreationSearchMode) ?: LiveRoomCreationSearchMode.TAGS
            )
        )
    }

    override fun inflateBindingLayout(inflater: LayoutInflater): FragmentLiveRoomTaggingBinding {
        val binding = FragmentLiveRoomTaggingBinding.inflate(inflater)
        viewStateHandler = LiveRoomTaggingViewStateHandler(binding, presenter, layoutInflater)

        adapter = LiveRoomTagSearchAdapter(viewLifecycleOwner, presenter)
        binding.recyclerView.adapter = adapter

        return binding
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewStateHandler = null
    }

    override fun renderState(viewState: LiveRoomTaggingContract.ViewState) {
        adapter.submitList(viewState.resultsUiModels)
        viewStateHandler?.renderState(viewState)
    }
}