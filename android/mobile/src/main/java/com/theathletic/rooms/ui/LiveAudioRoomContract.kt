package com.theathletic.rooms.ui

import com.theathletic.chat.data.local.ChatMessageReportReason
import com.theathletic.presenter.Interactor
import com.theathletic.ui.binding.LinkableTag
import com.theathletic.ui.widgets.ModalBottomSheetType

interface LiveAudioRoomContract {

    interface Presenter :
        Interactor,
        LiveRoomUi.Interactor,
        LinkableTag.Interactor {
        fun onPermissionsAccepted()

        override fun onLeaveRoomClicked()
        override fun onRequestToSpeakClicked()
        override fun onCancelRequestClicked()
        override fun onLeaveStageClicked()
        override fun onMuteControlClicked(mute: Boolean)
        override fun onAudienceButtonClicked()

        override fun onBackButtonClicked()
        override fun onShareClicked()
        fun onRecordingIndicatorClicked()
        fun onRecordingWarningApproved()
        fun onViewCodeOfConduct()
        fun onCodeOfConductApproved()

        override fun onHostMenuClicked()
        fun onEndRoomSelected()
        fun onEditRoomSelected()

        fun onBottomSheetModalDismissed()
        fun onDeleteMessageClicked(messageId: String)
        fun onMessageReported(messageId: String, reason: ChatMessageReportReason)
        fun onLockUserClicked(userId: String)
        fun onUnlockUserClicked(userId: String)
        fun onDemoteUserClicked(userId: String)
        fun onMuteUserClicked(userId: String)

        fun trackSlideDetailsSheet(slideUp: Boolean)
    }

    data class ViewState(
        val selectedTab: LiveRoomTab,
        val roomTitle: String = "",
        val roomDescription: String = "",
        val hosts: List<LiveRoomUi.HostInfo>,
        val tags: List<LiveRoomUi.TagInfo>,
        val chatEnabled: Boolean,
        val recording: Boolean,
        val linkableTags: List<LinkableTag> = emptyList(),
        val isMuted: Boolean,
        val isOnStage: Boolean,
        val isHost: Boolean,
        val isModerator: Boolean,
        val isLocked: Boolean,
        val hasPendingRequest: Boolean,
        val showSpinner: Boolean,
        val audienceControlsBadgeCount: Int,
        val speakers: List<LiveRoomUi.Speaker>,
        val audience: List<LiveRoomUi.AudienceCell>,
        val totalAudienceSize: Int,
        val chatInput: String = "",
        val messages: List<LiveRoomUi.ChatMessage> = emptyList(),
        val currentBottomSheetModal: ModalSheetType? = null,
    ) : com.theathletic.ui.ViewState

    sealed class ModalSheetType : ModalBottomSheetType {
        data class UserProfile(
            val userId: String,
            val roomId: String,
            val messageId: String? = null,
        ) : ModalSheetType()
        data class StaffModeration(
            val userId: String,
            val showDemoteOption: Boolean = false,
            val showMuteOption: Boolean = false,
            val isUserLocked: Boolean = false,
            val messageId: String? = null,
        ) : ModalSheetType()
        data class UserChatModeration(val userId: String, val messageId: String) : ModalSheetType()
    }

    sealed class Event : com.theathletic.utility.Event() {
        data class JoinRoom(val roomId: String, val token: String) : Event()
        data class ShowHostControls(val roomId: String) : Event()
        data class ShowHostProfile(val userId: String, val roomId: String) : Event()
        object ShowHostLeaveWarning : Event()
        object ShowHostToolbarMenu : Event()
        object ShowRecordingIndicatorDialog : Event()
        object ShowRecordingWarningDialog : Event()
        object RequestMicPermissions : Event()
        object ShowRoomEndedDialog : Event()
        object ShowRoomErrorDialog : Event()
        object ShowRoomAtCapacityDialog : Event()
        object ShowCodeOfConductDialog : Event()
    }
}