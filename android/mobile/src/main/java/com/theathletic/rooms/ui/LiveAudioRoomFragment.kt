package com.theathletic.rooms.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.insets.statusBarsPadding
import com.theathletic.R
import com.theathletic.chat.data.local.ChatMessageReportReason
import com.theathletic.fragment.AthleticComposeFragment
import com.theathletic.fragment.compose.rememberViewModel
import com.theathletic.rooms.LiveAudioRoomServiceConnection
import com.theathletic.rooms.ui.dialog.AthleticAlertDialogFragment
import com.theathletic.service.LiveAudioRoomService
import com.theathletic.ui.asString
import com.theathletic.ui.menu.BottomSheetMenu
import com.theathletic.ui.menu.BottomSheetMenuItem
import com.theathletic.ui.observe
import com.theathletic.ui.widgets.ModalBottomSheetLayout
import com.theathletic.ui.widgets.dialog.MenuSheetBuilder
import kotlinx.coroutines.flow.map
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class LiveAudioRoomFragment : AthleticComposeFragment<
    LiveAudioRoomViewModel,
    LiveAudioRoomContract.ViewState
    >() {

    companion object {
        private const val EXTRA_LIVE_ROOM_ID = "extra_live_room_id"

        private const val PERMISSION_REQ_ID_RECORD_AUDIO = 22
        private const val RECORD_PERMISSION = Manifest.permission.RECORD_AUDIO

        fun newInstance(liveRoomId: String) = LiveAudioRoomFragment().apply {
            arguments = bundleOf(EXTRA_LIVE_ROOM_ID to liveRoomId)
        }
    }

    private val serviceConnection = LiveAudioRoomServiceConnection()

    override fun setupViewModel() = getViewModel<LiveAudioRoomViewModel> {
        parametersOf(
            LiveAudioRoomViewModel.Params(arguments?.getString(EXTRA_LIVE_ROOM_ID) ?: ""),
            navigator,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        serviceConnection.connect(context)

        viewModel.observe<LiveAudioRoomContract.Event>(this) { event ->
            when (event) {
                is LiveAudioRoomContract.Event.JoinRoom -> {
                    initializeAndJoinChannel(event.roomId, event.token)
                }
                LiveAudioRoomContract.Event.RequestMicPermissions -> {
                    if (checkMicrophonePermission()) {
                        viewModel.onPermissionsAccepted()
                    }
                }
                LiveAudioRoomContract.Event.ShowHostToolbarMenu -> showHostToolbarMenu()
                LiveAudioRoomContract.Event.ShowHostLeaveWarning -> showHostLeaveWarning()
                is LiveAudioRoomContract.Event.ShowHostControls -> showHostControls(event.roomId)
                is LiveAudioRoomContract.Event.ShowHostProfile -> showHostProfile(
                    event.userId,
                    event.roomId,
                )
                is LiveAudioRoomContract.Event.ShowRecordingIndicatorDialog -> showRecordingIndicatorDialog()
                is LiveAudioRoomContract.Event.ShowRecordingWarningDialog -> showRecordingWarningDialog()
                is LiveAudioRoomContract.Event.ShowRoomEndedDialog -> showRoomEndedDialog()
                is LiveAudioRoomContract.Event.ShowRoomErrorDialog -> showErrorDialog()
                is LiveAudioRoomContract.Event.ShowRoomAtCapacityDialog -> showRoomAtCapacityDialog()
                is LiveAudioRoomContract.Event.ShowCodeOfConductDialog -> showCodeOfConductDialog()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceConnection.disconnect(context)
    }

    @Composable
    override fun Compose(state: LiveAudioRoomContract.ViewState) {
        val volumeProvider = remember(viewModel.liveState) {
            viewModel.liveState.map { it.userIdToVolume }
        }

        ModalBottomSheetLayout(
            currentModal = state.currentBottomSheetModal,
            onDismissed = { viewModel.onBottomSheetModalDismissed() },
            modalSheetContent = { ModalSheetContent(modal = it) },
            modifier = Modifier.fillMaxSize()
                .statusBarsPadding()
                .navigationBarsWithImePadding()
        ) {
            LiveRoomScreen(
                state = state,
                interactor = viewModel,
                volumeProvider = volumeProvider,
            )
        }
    }

    @Composable
    private fun ModalSheetContent(modal: LiveAudioRoomContract.ModalSheetType) = when (modal) {
        is LiveAudioRoomContract.ModalSheetType.UserProfile -> UserProfileBottomSheet(
            userId = modal.userId,
            roomId = modal.roomId,
            messageId = modal.messageId,
        )
        is LiveAudioRoomContract.ModalSheetType.StaffModeration -> LiveRoomStaffModerationMenu(
            userId = modal.userId,
            showMuteOption = modal.showMuteOption,
            showDemoteOption = modal.showDemoteOption,
            isUserLocked = modal.isUserLocked,
            messageId = modal.messageId,
            onMuteUser = viewModel::onMuteUserClicked,
            onDemoteUser = viewModel::onDemoteUserClicked,
            onLockUser = viewModel::onLockUserClicked,
            onUnlockUser = viewModel::onUnlockUserClicked,
            onDeleteMessage = viewModel::onDeleteMessageClicked,
        )
        is LiveAudioRoomContract.ModalSheetType.UserChatModeration -> BottomSheetMenu {
            BottomSheetMenuItem(
                icon = R.drawable.ic_report,
                text = stringResource(R.string.chat_moderation_flag_comment),
                onClick = {
                    showFlagReasonDialog(modal.messageId)
                    viewModel.onBottomSheetModalDismissed()
                }
            )
        }
    }

    @Composable
    fun UserProfileBottomSheet(
        userId: String,
        roomId: String,
        messageId: String?,
    ) {
        val sheetPresenter: LiveRoomUserProfileSheetViewModel = rememberViewModel(
            LiveRoomUserProfileSheetViewModel.Params(
                userId = userId,
                roomId = roomId,
            ),
            navigator,
        )

        val viewState by sheetPresenter.viewState.collectAsState(initial = null)
        val state = viewState ?: return

        LiveRoomUserProfile(
            showSpinner = state.showSpinner,
            name = state.name?.asString().orEmpty(),
            initials = state.initials?.asString().orEmpty(),
            isLocked = state.isLocked,
            showStaffControls = state.showStaffControls,
            messageId = messageId,
            currentUserFollowedItems = state.currentUserFollowedIds,
            followedItems = state.followedItems,
            onFlagMessageClicked = { messageId ->
                showFlagReasonDialog(messageId)
                viewModel.onBottomSheetModalDismissed()
            },
            interactor = sheetPresenter,
        )
    }

    private fun initializeAndJoinChannel(roomId: String, token: String) {
        ContextCompat.startForegroundService(
            context,
            Intent(requireActivity(), LiveAudioRoomService::class.java)
        )
        serviceConnection.onControlsAvailable = { controls -> controls.joinRoom(roomId, token) }
    }

    private fun checkMicrophonePermission(): Boolean {
        val permissionStatus = ContextCompat.checkSelfPermission(context, RECORD_PERMISSION)
        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(RECORD_PERMISSION),
                PERMISSION_REQ_ID_RECORD_AUDIO
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQ_ID_RECORD_AUDIO -> {
                val index = permissions.indexOf(RECORD_PERMISSION)
                if (index in permissions.indices && grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                    viewModel.onPermissionsAccepted()
                }
            }
        }
    }

    private fun showHostToolbarMenu() {
        MenuSheetBuilder().apply {
            addEntry(
                iconRes = R.drawable.ic_edit,
                textRes = R.string.rooms_host_menu_edit_room,
                onSelected = viewModel::onEditRoomSelected,
            )
            addEntry(
                iconRes = R.drawable.ic_x,
                textRes = R.string.rooms_host_menu_end_room,
                onSelected = viewModel::onEndRoomSelected,
            )
        }.build().show(requireActivity().supportFragmentManager, null)
    }

    private fun showHostLeaveWarning() {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.rooms_host_menu_leave_room_warning)
            .setPositiveButton(R.string.rooms_host_menu_end_room) { _, _ ->
                viewModel.onEndRoomSelected()
            }
            .setNegativeButton(R.string.global_action_cancel) { dialog, _ -> dialog.cancel() }
            .show()
    }

    private fun showHostControls(roomId: String) {
        LiveRoomHostControlsFragment.newInstance(roomId = roomId)
            .show(requireActivity().supportFragmentManager, null)
    }

    private fun showHostProfile(userId: String, roomId: String) {
        LiveRoomHostProfileSheetFragment.newInstance(
            userId = userId,
            roomId = roomId
        ).show(requireActivity().supportFragmentManager, null)
    }

    private fun showRecordingIndicatorDialog() {
        AthleticAlertDialogFragment(
            title = R.string.rooms_recording_indicator_title,
            message = R.string.rooms_recording_indicator_message,
        ).show(requireActivity().supportFragmentManager, null)
    }

    private fun showRecordingWarningDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.rooms_recording_indicator_title)
            .setMessage(R.string.rooms_recording_indicator_message)
            .setPositiveButton(android.R.string.ok) { _, _ -> viewModel.onRecordingWarningApproved() }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showRoomEndedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.rooms_ended_dialog_title)
            .setMessage(R.string.rooms_ended_dialog_message)
            .setPositiveButton(android.R.string.ok) { _, _ -> viewModel.onLeaveRoomClicked() }
            .setCancelable(false)
            .show()
    }

    private fun showErrorDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.global_error)
            .setPositiveButton(android.R.string.ok) { _, _ -> viewModel.onLeaveRoomClicked() }
            .setCancelable(false)
            .show()
    }

    private fun showRoomAtCapacityDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.rooms_at_capacity_dialog_title)
            .setMessage(R.string.rooms_at_capacity_dialog_message)
            .setPositiveButton(android.R.string.ok) { _, _ -> viewModel.onLeaveRoomClicked() }
            .setCancelable(false)
            .show()
    }

    private fun showLockUserConfirmationDialog(userId: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.chat_moderation_lock_user_confirmation_title)
            .setMessage(R.string.chat_moderation_lock_user_confirmation_description)
            .setPositiveButton(android.R.string.ok) { _, _ -> viewModel.onLockUserClicked(userId) }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showCodeOfConductDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.rooms_chat_code_of_conduct_title)
            .setMessage(R.string.rooms_chat_code_of_conduct)
            .setPositiveButton(R.string.rooms_chat_code_of_conduct_accept) { _, _ ->
                viewModel.onCodeOfConductApproved()
            }
            .setNeutralButton(R.string.rooms_chat_code_of_conduct_view) { dialog, _ ->
                viewModel.onViewCodeOfConduct()
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showFlagReasonDialog(messageId: String) {
        var selectedOption = -1

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.chat_flag_title)
            .setSingleChoiceItems(R.array.chat_flag_reasons, selectedOption) { _, which ->
                selectedOption = which
            }
            .setPositiveButton(android.R.string.ok) { _, _ ->
                if (selectedOption >= 0) {
                    viewModel.onMessageReported(
                        messageId,
                        when (selectedOption) {
                            0 -> ChatMessageReportReason.ABUSE
                            1 -> ChatMessageReportReason.TROLLING
                            else -> ChatMessageReportReason.SPAM
                        }
                    )
                }
            }
            .show()
    }
}