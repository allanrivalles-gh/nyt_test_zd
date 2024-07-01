package com.theathletic.rooms.ui

import android.view.LayoutInflater
import androidx.core.os.bundleOf
import com.theathletic.databinding.FragmentLiveRoomHostControlsBinding
import com.theathletic.fragment.AthleticBottomSheetBindingFragment
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class LiveRoomHostControlsFragment : AthleticBottomSheetBindingFragment<
    LiveRoomHostControlsViewModel,
    FragmentLiveRoomHostControlsBinding,
    LiveRoomHostControlsContract.ViewState
    >() {

    companion object {
        private const val ARG_ROOM_ID = "room_id"

        fun newInstance(roomId: String) = LiveRoomHostControlsFragment().apply {
            arguments = bundleOf(
                ARG_ROOM_ID to roomId,
            )
        }
    }

    lateinit var adapter: LiveRoomHostControlsAdapter

    override fun setupViewModel() = getViewModel<LiveRoomHostControlsViewModel> {
        val params = LiveRoomHostControlsContract.Params(
            // TODO (matt) Handle the empty case
            roomId = arguments?.getString(ARG_ROOM_ID).orEmpty()
        )
        parametersOf(params)
    }

    override fun inflateBindingLayout(
        inflater: LayoutInflater
    ): FragmentLiveRoomHostControlsBinding {
        return FragmentLiveRoomHostControlsBinding.inflate(inflater).apply {
            adapter = LiveRoomHostControlsAdapter(viewLifecycleOwner, presenter)
            recyclerView.adapter = adapter
        }
    }

    override fun renderState(viewState: LiveRoomHostControlsContract.ViewState) {
        adapter.submitList(viewState.uiModels)
    }
}