package com.theathletic.rooms.ui

import android.view.LayoutInflater
import androidx.core.os.bundleOf
import com.theathletic.databinding.FragmentLiveRoomHostProfileSheetBinding
import com.theathletic.fragment.AthleticBottomSheetBindingFragment
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class LiveRoomHostProfileSheetFragment : AthleticBottomSheetBindingFragment<
    LiveRoomHostProfileSheetViewModel,
    FragmentLiveRoomHostProfileSheetBinding,
    LiveRoomHostProfileSheetContract.ViewState
    >() {

    companion object {
        private const val EXTRA_USER_ID = "user_id"
        private const val EXTRA_ROOM_ID = "room_id"

        fun newInstance(
            userId: String,
            roomId: String,
        ) = LiveRoomHostProfileSheetFragment().apply {
            arguments = bundleOf(
                EXTRA_USER_ID to userId,
                EXTRA_ROOM_ID to roomId,
            )
        }
    }

    override fun setupViewModel() = getViewModel<LiveRoomHostProfileSheetViewModel>() {
        parametersOf(
            LiveRoomHostProfileSheetViewModel.Params(
                authorId = arguments?.getString(EXTRA_USER_ID) ?: "",
                roomId = arguments?.getString(EXTRA_ROOM_ID) ?: "",
            ),
            navigator,
        )
    }

    override fun inflateBindingLayout(
        inflater: LayoutInflater
    ) = FragmentLiveRoomHostProfileSheetBinding.inflate(inflater)

    override fun renderState(viewState: LiveRoomHostProfileSheetContract.ViewState) {
        // Do nothing
    }
}