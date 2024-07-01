package com.theathletic.rooms.schedule.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.theathletic.R
import com.theathletic.fragment.AthleticComposeFragment
import com.theathletic.rooms.ui.ScheduledRoomsScreen
import com.theathletic.ui.menu.BottomSheetMenu
import com.theathletic.ui.menu.BottomSheetMenuItem
import com.theathletic.ui.widgets.ModalBottomSheetLayout
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class ScheduledLiveRoomsFragment : AthleticComposeFragment<
    ScheduledLiveRoomsViewModel,
    ScheduledLiveRoomsContract.ViewState
    >() {

    companion object {
        fun newInstance() = ScheduledLiveRoomsFragment()
    }

    override fun setupViewModel() = getViewModel<ScheduledLiveRoomsViewModel> {
        parametersOf(navigator)
    }

    @Composable
    override fun Compose(state: ScheduledLiveRoomsContract.ViewState) {
        ModalBottomSheetLayout(
            currentModal = state.currentBottomSheetModal,
            onDismissed = { viewModel.showModal(null) },
            modalSheetContent = { ModalSheetContent(modal = it) },
        ) {
            ScheduledRoomsScreen(
                uiModel = state.uiModel,
                interactor = viewModel,
            )
        }
    }

    @Composable
    private fun ModalSheetContent(modal: ScheduledLiveRoomsContract.ModalSheetType) {
        return when (modal) {
            is ScheduledLiveRoomsContract.ModalSheetType.LinksMenu -> BottomSheetMenu {
                BottomSheetMenuItem(
                    icon = R.drawable.ic_copy,
                    text = stringResource(R.string.rooms_scheduled_copy_deeplink),
                    onClick = {
                        copyToClipboard(modal.deeplink.value)
                        viewModel.showModal(null)
                    }
                )
                BottomSheetMenuItem(
                    icon = R.drawable.ic_copy,
                    text = stringResource(R.string.rooms_scheduled_copy_universal_link),
                    onClick = {
                        copyToClipboard(modal.universalLink)
                        viewModel.showModal(null)
                    }
                )
            }
        }
    }

    private fun copyToClipboard(value: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE)
            as? ClipboardManager ?: return

        val clip = ClipData.newPlainText("", value)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireContext(), R.string.rooms_scheduled_copied_toast, Toast.LENGTH_SHORT).show()
    }
}