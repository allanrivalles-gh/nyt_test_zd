package com.theathletic.rooms.ui

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import com.theathletic.analytics.data.ClickSource
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.audio.LiveAudioStageUser
import com.theathletic.chat.data.ChatRepository
import com.theathletic.chat.data.local.ChatMessageReportReason
import com.theathletic.entity.chat.ChatRoomEntity
import com.theathletic.entity.room.LiveAudioRoomEntity
import com.theathletic.entity.user.UserEntity
import com.theathletic.event.SnackbarEvent
import com.theathletic.links.deep.DeeplinkEventProducer
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.rooms.LiveAudioRoomStateManager
import com.theathletic.rooms.RoomsRepository
import com.theathletic.rooms.analytics.LiveRoomAnalyticsContext
import com.theathletic.rooms.analytics.LiveRoomEntryPoint
import com.theathletic.rooms.local.LiveAudioRoomUserDetails
import com.theathletic.rooms.ui.LiveAudioRoomContract.ViewState
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.DataState
import com.theathletic.ui.LoadingState
import com.theathletic.ui.Transformer
import com.theathletic.user.IUserManager
import com.theathletic.user.data.UserRepository
import com.theathletic.utility.coroutines.collectIn
import com.theathletic.viewmodel.LiveViewModelState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class LiveAudioRoomViewModel @AutoKoin constructor(
    @Assisted val params: Params,
    @Assisted private val screenNavigator: ScreenNavigator,
    transformer: LiveAudioRoomTransformer,
    analytics: LiveAudioRoomAnalytics,
    private val userManager: IUserManager,
    private val roomsRepository: RoomsRepository,
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    private val liveAudioRoomStateManager: LiveAudioRoomStateManager,
    private val liveAudioEventProducer: LiveAudioEventProducer,
    private val liveAudioEventConsumer: LiveAudioEventConsumer,
    private val deeplinkEventProducer: DeeplinkEventProducer,
    private val liveRoomAnalyticsContext: LiveRoomAnalyticsContext,
) : AthleticViewModel<LiveAudioRoomState, ViewState>(),
    LiveAudioRoomContract.Presenter,
    LiveAudioRoomAnalytics by analytics,
    Transformer<LiveAudioRoomState, ViewState> by transformer {

    companion object {
        const val LOCAL_USER_AUDIO_ID = "0"
    }

    data class Params(
        val liveRoomId: String,
        val entryPoint: LiveRoomEntryPoint? = null,
    )

    override val initialState by lazy {
        LiveAudioRoomState(
            isStaff = userManager.isStaff,
            currentUserId = userManager.getCurrentUserId().toString(),
            currentUser = userManager.getCurrentUser(),
        )
    }

    private val _liveState = MutableStateFlow(LiveState())
    val liveState: Flow<LiveState> = _liveState

    data class LiveState(
        val userIdToVolume: Map<String, Int> = mutableMapOf(),
    ) : LiveViewModelState

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun initialize() {
        listenForRenderUpdates()
        fetchData()
    }

    private fun listenForRenderUpdates() {
        roomsRepository.getLiveAudioRoomFlow(params.liveRoomId).collectIn(viewModelScope) {
            updateState { copy(liveAudioRoom = it) }
        }

        liveAudioRoomStateManager.userIdToVolume.collectIn(viewModelScope) { idsToVolume ->
            _liveState.value = _liveState.value.copy(
                // Agora reports the local user audio as from user "0" so we need to map that to
                // the current user's ID
                userIdToVolume = when (val localUserVolume = idsToVolume[LOCAL_USER_AUDIO_ID]) {
                    null -> idsToVolume
                    else -> idsToVolume.toMutableMap().apply {
                        put(state.currentUserId, localUserVolume)
                    }
                }
            )
        }

        liveAudioRoomStateManager.audioEngineState.collectIn(viewModelScope) { state ->
            state ?: return@collectIn
            updateState {
                copy(
                    usersOnStage = state.usersOnStage,
                    userIsMuted = state.isCurrentUserMuted,
                    isOnStage = state.isCurrentUserOnStage
                )
            }
        }

        liveAudioRoomStateManager.userDetailsForRoom(params.liveRoomId)
            .collectIn(viewModelScope) { userDetails ->
                updateState { copy(userInRoomDetails = userDetails) }
            }

        liveAudioEventConsumer.collectIn(viewModelScope, action = ::handleLiveAudioEvents)
    }

    private fun handleLiveAudioEvents(event: LiveAudioEvent) {
        when (event) {
            is LiveAudioEvent.SwapStageStatus -> {
                trackCustom(
                    verb = if (event.onStage) "accepted" else "removed",
                    element = if (event.onStage) "speak" else "leave_stage",
                    roomId = params.liveRoomId,
                )
                event.showToaster()
            }
            is LiveAudioEvent.LeaveRoom ->
                when {
                    event.roomEnded -> {
                        trackClick(
                            roomId = params.liveRoomId,
                            element = "room_end_acknowledge",
                        )
                        sendEvent(LiveAudioRoomContract.Event.ShowRoomEndedDialog)
                    }
                    else -> screenNavigator.finishActivity()
                }
            is LiveAudioEvent.AutoPushSent -> {
                if (currentUserIsHost) {
                    sendEvent(LiveRoomToasterEvent.AutoPushSent)
                }
            }
            else -> { /* do nothing */ }
        }
    }

    private fun fetchData() {
        viewModelScope.launch {
            val room = roomsRepository.getLiveAudioRoom(params.liveRoomId, forceFetch = true)
            updateState { copy(liveAudioRoom = room) }

            if (room == null) {
                sendEvent(LiveAudioRoomContract.Event.ShowRoomErrorDialog)
                return@launch
            }

            trackView(
                roomId = params.liveRoomId,
                entryPoint = liveRoomAnalyticsContext.roomIdToEntryPoint[params.liveRoomId],
                isLive = if (room.isLive) "true" else "false",
            )

            if (room.endedAt != null) {
                sendEvent(LiveAudioRoomContract.Event.ShowRoomEndedDialog)
                return@launch
            }

            if (room.audienceSize >= room.maxCapacity && !currentUserIsHost) {
                sendEvent(LiveAudioRoomContract.Event.ShowRoomAtCapacityDialog)
                return@launch
            }

            fetchToken()
            room.chatRoomId?.let { listenForChatRoomUpdates(it) }
        }
    }

    private fun fetchToken() {
        viewModelScope.launch {
            val token = roomsRepository.fetchTokenForRoom(params.liveRoomId)
            Timber.v("Got token for room ${params.liveRoomId}: $token")
            updateState {
                copy(
                    token = token,
                    loadingState = LoadingState.FINISHED,
                )
            }

            if (!token.isNullOrEmpty()) {
                sendEvent(
                    LiveAudioRoomContract.Event.JoinRoom(roomId = params.liveRoomId, token = token)
                )
            } else {
                sendEvent(SnackbarEvent("Error fetching token: $token"))
            }
        }
    }

    private fun listenForChatRoomUpdates(chatRoomId: String) {
        chatRepository.getChatRoomFlow(chatRoomId).collectIn(viewModelScope) {
            updateState { copy(chatRoom = it) }
        }
    }

    private fun LiveAudioEvent.SwapStageStatus.showToaster() {
        if (!fromHost) return
        val event = when {
            onStage -> LiveRoomToasterEvent.RequestApproved
            else -> LiveRoomToasterEvent.RemovedByHost
        }
        sendEvent(event)
    }

    override fun onTabClicked(tab: LiveRoomTab) {
        trackClick(
            view = analyticsView,
            roomId = params.liveRoomId,
            element = when (tab) {
                LiveRoomTab.Stage -> "liveroom_mainstage"
                LiveRoomTab.Chat -> "liveroom_chat"
            }
        )
        updateState { copy(selectedTab = tab) }
        trackView(
            view = analyticsView,
            roomId = params.liveRoomId,
            isLive = if (state.liveAudioRoom?.isLive == true) "true" else "false",
        )
    }

    override fun onChatPreviewClicked() {
        state.chatRoom?.messages?.lastOrNull()?.id?.let { messageId ->
            trackClick(
                roomId = params.liveRoomId,
                element = "liveroom_chat",
                objectType = "message_id",
                objectId = messageId,
            )
        }
        updateState { copy(selectedTab = LiveRoomTab.Chat) }
    }

    override fun onLeaveRoomClicked() {
        viewModelScope.launch {
            trackClick(
                view = analyticsView,
                roomId = params.liveRoomId,
                element = "leave_room"
            )
            if (shouldShowHostLeaveWarning()) {
                sendEvent(LiveAudioRoomContract.Event.ShowHostLeaveWarning)
            } else {
                liveAudioEventProducer.emit(LiveAudioEvent.LeaveRoom())
            }
        }
    }

    private fun shouldShowHostLeaveWarning(): Boolean {
        val hostIds = state.liveAudioRoom?.hosts?.map { it.id }?.toSet() ?: emptySet()
        val othersOnStage = state.usersOnStage.map { it.id }.toSet()

        val otherHostsOnStage = hostIds.intersect(othersOnStage) - state.currentUserId

        return currentUserIsHost && state.isOnStage && otherHostsOnStage.isEmpty()
    }

    override fun onMuteControlClicked(mute: Boolean) {
        viewModelScope.launch {
            trackClick(
                view = analyticsView,
                roomId = params.liveRoomId,
                element = if (mute) "mute" else "speak"
            )
            liveAudioEventProducer.emit(LiveAudioEvent.ChangeMute(mute))
        }
    }

    override fun onRequestToSpeakClicked() {
        if (state.liveAudioRoom?.isUserLocked(state.currentUserId) == true) {
            sendEvent(LiveRoomToasterEvent.NotifyUserLocked)
        } else if (!userManager.isUserSubscribed()) {
            screenNavigator.startPlansActivity(
                source = ClickSource.LIVE_ROOM,
                liveRoomId = params.liveRoomId,
                liveRoomAction = "request_to_speak",
            )
        } else {
            viewModelScope.launch {
                trackClick(
                    view = analyticsView,
                    roomId = params.liveRoomId,
                    element = "request_to_speak"
                )
                sendEvent(LiveAudioRoomContract.Event.RequestMicPermissions)
            }
        }
    }

    override fun onCancelRequestClicked() {
        trackClick(
            view = analyticsView,
            roomId = params.liveRoomId,
            element = "cancel_request"
        )
        roomsRepository.deleteSpeakingRequest(state.currentUserId, params.liveRoomId)
    }

    override fun onPermissionsAccepted() {
        viewModelScope.launch {
            when {
                currentUserIsHost -> {
                    liveAudioEventProducer.emit(LiveAudioEvent.SwapStageStatus(onStage = true))
                    if (state.liveAudioRoom?.startedAt == null) {
                        roomsRepository.startRoom(params.liveRoomId)
                    }
                }
                !state.recordingWarningApproved && state.liveAudioRoom?.isRecording == true -> {
                    sendEvent(LiveAudioRoomContract.Event.ShowRecordingWarningDialog)
                }
                else -> sendingSpeakingRequest()
            }
        }
    }

    override fun onRecordingWarningApproved() {
        viewModelScope.launch {
            sendingSpeakingRequest()
            updateState { copy(recordingWarningApproved = true) }
        }
    }

    override fun onViewCodeOfConduct() {
        screenNavigator.showCodeOfConduct()
    }

    override fun onCodeOfConductApproved() {
        userRepository.acceptChatCodeOfConduct()
        sendCurrentChatMessage()
    }

    private suspend fun sendingSpeakingRequest() {
        sendEvent(LiveRoomToasterEvent.PendingRequest)
        roomsRepository.createSpeakingRequest(params.liveRoomId).join()
    }

    override fun onLeaveStageClicked() {
        viewModelScope.launch {
            trackClick(
                view = analyticsView,
                roomId = params.liveRoomId,
                element = "leave_stage"
            )
            liveAudioEventProducer.emit(LiveAudioEvent.SwapStageStatus(onStage = false))
        }
    }

    override fun onAudienceButtonClicked() {
        sendEvent(LiveAudioRoomContract.Event.ShowHostControls(params.liveRoomId))
    }

    override fun onBackButtonClicked() {
        trackClick(
            view = analyticsView,
            roomId = params.liveRoomId,
            element = "minimize_room"
        )
        screenNavigator.finishActivity()
    }

    override fun onShareClicked() {
        trackClick(
            view = analyticsView,
            roomId = params.liveRoomId,
            element = "share"
        )
        state.liveAudioRoom?.let { screenNavigator.startShareTextActivity(it.permalink) }
    }

    override fun onTagClicked(id: String, deeplink: String) {
        trackClick(
            roomId = params.liveRoomId,
            element = "title_tags",
            objectType = "tag_id",
            objectId = id
        )
        viewModelScope.launch {
            deeplinkEventProducer.emit(deeplink)
        }
    }

    override fun onUserClicked(id: String) {
        if (state.liveAudioRoom?.isUserHost(id) == true) {
            trackClick(
                element = "host_image",
                roomId = params.liveRoomId,
                objectType = "user_id",
                objectId = id
            )
            sendEvent(LiveAudioRoomContract.Event.ShowHostProfile(id, params.liveRoomId))
        } else {
            showModal(
                LiveAudioRoomContract.ModalSheetType.UserProfile(
                    userId = id,
                    roomId = params.liveRoomId,
                )
            )
        }
    }

    override fun onUserLongClicked(id: String) {
        if (state.isStaff) {
            showModal(
                LiveAudioRoomContract.ModalSheetType.StaffModeration(
                    userId = id,
                    showDemoteOption = state.usersOnStage.any { it.id == id },
                    showMuteOption = state.usersOnStage.any { it.id == id && !it.isMuted },
                    isUserLocked = state.liveAudioRoom?.isUserLocked(id) ?: false,
                )
            )
        }
    }

    override fun onBottomSheetModalDismissed() {
        showModal(null)
    }

    override fun onDeleteMessageClicked(messageId: String) {
        state.liveAudioRoom?.chatRoomId?.let { chatRoomId ->
            chatRepository.deleteMessage(
                chatRoomId = chatRoomId,
                messageId = messageId,
            )
        }
        showModal(null)
    }

    override fun onMessageReported(messageId: String, reason: ChatMessageReportReason) {
        state.liveAudioRoom?.chatRoomId?.let { chatRoomId ->
            chatRepository.reportMessage(
                chatRoomId = chatRoomId,
                messageId = messageId,
                reason = reason,
            )
        }
    }

    override fun onLockUserClicked(userId: String) {
        roomsRepository.lockUser(params.liveRoomId, userId)
        showModal(null)
    }

    override fun onUnlockUserClicked(userId: String) {
        roomsRepository.unlockUser(params.liveRoomId, userId)
        showModal(null)
    }

    override fun onDemoteUserClicked(userId: String) {
        trackClick(
            roomId = params.liveRoomId,
            element = "remove_user",
            objectType = "user_id",
            objectId = userId
        )
        roomsRepository.createDemotionRequest(userId, params.liveRoomId)
        showModal(null)
    }

    override fun onMuteUserClicked(userId: String) {
        roomsRepository.createMuteRequest(userId, params.liveRoomId)
        showModal(null)
    }

    private fun showModal(modal: LiveAudioRoomContract.ModalSheetType?) {
        updateState { copy(currentBottomSheetModal = modal) }
    }

    override fun onHostMenuClicked() {
        sendEvent(LiveAudioRoomContract.Event.ShowHostToolbarMenu)
    }

    private val currentUserIsHost
        get() = state.liveAudioRoom?.isUserHost(state.currentUserId) == true

    override fun onEndRoomSelected() {
        viewModelScope.launch {
            roomsRepository.endRoom(params.liveRoomId).join()
            liveAudioEventProducer.emit(LiveAudioEvent.LeaveRoom())
        }
    }

    override fun onEditRoomSelected() {
        viewModelScope.launch {
            screenNavigator.startCreateLiveRoomActivity(roomToEditId = state.liveAudioRoom?.id)
        }
    }

    override fun onRecordingIndicatorClicked() {
        sendEvent(LiveAudioRoomContract.Event.ShowRecordingIndicatorDialog)
    }

    override fun onChatInputChanged(value: String) {
        updateState { copy(chatInput = value) }
    }

    override fun onLockedChatInputClicked() {
        sendEvent(LiveRoomToasterEvent.NotifyUserLocked)
    }

    override fun onSendChatClicked() {
        if (state.chatInput.isEmpty()) return

        when {
            !userManager.isUserSubscribed() -> {
                screenNavigator.startPlansActivity(
                    source = ClickSource.LIVE_ROOM,
                    liveRoomId = params.liveRoomId,
                    liveRoomAction = "send_message",
                )
            }
            userManager.isCodeOfConductAccepted().not() -> {
                sendEvent(LiveAudioRoomContract.Event.ShowCodeOfConductDialog)
            }
            else -> sendCurrentChatMessage()
        }
    }

    private fun sendCurrentChatMessage() {
        trackClick(
            roomId = params.liveRoomId,
            element = "send_message",
        )

        state.liveAudioRoom?.chatRoomId?.let { chatRoomId ->
            chatRepository.sendMessage(
                chatRoomId = chatRoomId,
                message = state.chatInput,
            )
        }

        updateState { copy(chatInput = "") }
    }

    override fun onMessageClicked(messageId: String) {
        val message = state.chatRoom?.messages?.find { it.id == messageId } ?: return

        when {
            state.liveAudioRoom?.isUserModerator(message.authorId) == true -> { /* Do nothing */ }
            state.liveAudioRoom?.isUserHost(message.authorId) == true -> sendEvent(
                LiveAudioRoomContract.Event.ShowHostProfile(message.authorId, params.liveRoomId)
            )
            else -> showModal(
                LiveAudioRoomContract.ModalSheetType.UserProfile(
                    userId = message.authorId,
                    roomId = params.liveRoomId,
                    messageId = messageId,
                )
            )
        }
    }

    override fun onMessageLongClicked(messageId: String) {
        val message = state.chatRoom?.messages?.find { it.id == messageId } ?: return

        if (!state.isStaff && state.liveAudioRoom?.isUserModerator(message.authorId) == true) {
            // Don't let subscribers report moderator messages
            return
        }

        showModal(
            if (state.isStaff) {
                LiveAudioRoomContract.ModalSheetType.StaffModeration(
                    userId = message.authorId,
                    messageId = messageId,
                    isUserLocked = state.liveAudioRoom?.isUserLocked(message.authorId) ?: false,
                )
            } else {
                LiveAudioRoomContract.ModalSheetType.UserChatModeration(
                    userId = message.authorId,
                    messageId = messageId,
                )
            }
        )
    }

    override fun trackSlideDetailsSheet(slideUp: Boolean) {
        trackCustom(
            verb = if (slideUp) "slide_up" else "slide_down",
            element = "liveroom_details",
            roomId = params.liveRoomId,
        )
    }

    private val analyticsView get() = when (state.selectedTab) {
        LiveRoomTab.Stage -> "liveroom_mainstage"
        LiveRoomTab.Chat -> "liveroom_chat"
    }
}

data class LiveAudioRoomState(
    val loadingState: LoadingState = LoadingState.INITIAL_LOADING,
    val token: String? = null,
    val isStaff: Boolean = false,
    val currentUserId: String,
    val currentUser: UserEntity? = null,
    val selectedTab: LiveRoomTab = LiveRoomTab.Stage,
    val liveAudioRoom: LiveAudioRoomEntity? = null,
    val chatRoom: ChatRoomEntity? = null,
    val usersOnStage: Set<LiveAudioStageUser> = emptySet(),
    val userInRoomDetails: Map<String, LiveAudioRoomUserDetails> = emptyMap(),
    val userIsMuted: Boolean = false,
    val isOnStage: Boolean = false,
    val recordingWarningApproved: Boolean = false,
    val chatInput: String = "",
    val currentBottomSheetModal: LiveAudioRoomContract.ModalSheetType? = null,
) : DataState