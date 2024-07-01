package com.theathletic.rooms.ui

import androidx.compose.runtime.Stable
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.chat.data.ChatRepository
import com.theathletic.entity.room.LiveAudioRoomEntity
import com.theathletic.followable.Followable
import com.theathletic.followable.analyticsType
import com.theathletic.followables.FollowItemUseCase
import com.theathletic.followables.ObserveUserFollowingUseCase
import com.theathletic.followables.UnfollowItemUseCase
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.rooms.RoomsRepository
import com.theathletic.rooms.local.LiveAudioRoomUserDetails
import com.theathletic.rooms.ui.LiveRoomUserProfileSheetContract.ViewState
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.DataState
import com.theathletic.ui.utility.initializedName
import com.theathletic.ui.utility.initials
import com.theathletic.user.IUserManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class LiveRoomUserProfileSheetViewModel @AutoKoin constructor(
    @Assisted val params: Params,
    @Assisted val navigator: ScreenNavigator,
    analytics: LiveAudioRoomAnalytics,
    userManager: IUserManager,
    private val roomsRepository: RoomsRepository,
    private val observeUserFollowingUseCase: ObserveUserFollowingUseCase,
    private val followItemUseCase: FollowItemUseCase,
    private val unfollowItemUseCase: UnfollowItemUseCase,
    private val chatRepository: ChatRepository,
) : AthleticViewModel<LiveRoomUserProfileSheetState, ViewState>(),
    DefaultLifecycleObserver,
    LiveAudioRoomAnalytics by analytics,
    LiveRoomUserProfileSheetContract.Presenter {

    @Stable
    data class Params(
        val userId: String,
        val roomId: String,
    )

    override val initialState by lazy {
        LiveRoomUserProfileSheetState(
            currentUserIsStaff = userManager.isStaff,
        )
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        initialize()
    }

    fun initialize() {
        roomsRepository.getLiveAudioRoomFlow(params.roomId).onEach { room ->
            updateState { copy(liveRoom = room) }
        }.launchIn(viewModelScope)

        roomsRepository.getUserDetailsForRoom(params.roomId).onEach { users ->
            val user = users?.find { it.id == params.userId } ?: return@onEach
            updateState { copy(userDetails = user) }
        }.launchIn(viewModelScope)

        observeUserFollowingUseCase().onEach {
            updateState { copy(currentUserFollowedIds = it.map { it.id }.toSet()) }
        }.launchIn(viewModelScope)

        loadFollowedItems()

        trackView(
            roomId = params.roomId,
            element = "user_profile",
            objectType = "user_id",
            objectId = params.userId
        )
    }

    private fun loadFollowedItems() = viewModelScope.launch {
        val followedItems = roomsRepository.getUserFollowingDetails(
            params.roomId,
            params.userId,
        ) ?: emptyList()
        updateState { copy(followedItems = followedItems) }
    }

    override fun onFollowClicked(
        id: Followable.Id,
        follow: Boolean
    ) {
        trackClick(
            view = "user_profile",
            element = if (follow) "follow" else "unfollow",
            roomId = params.roomId,
            objectType = id.analyticsType,
            objectId = id.id,
        )
        viewModelScope.launch {
            if (follow) {
                followItemUseCase(id)
            } else {
                unfollowItemUseCase(id)
            }
        }
    }

    override fun onLockUserClicked() {
        roomsRepository.lockUser(params.roomId, params.userId)
    }

    override fun onUnlockUserClicked() {
        roomsRepository.unlockUser(params.roomId, params.userId)
    }

    override fun onRemoveMessageClicked(messageId: String) {
        state.liveRoom?.chatRoomId?.let { chatRoomId ->
            chatRepository.deleteMessage(
                chatRoomId = chatRoomId,
                messageId = messageId,
            )
        }
    }

    override fun transform(data: LiveRoomUserProfileSheetState): ViewState {
        val user = data.userDetails
        return ViewState(
            showSpinner = data.followedItems == null,
            name = user?.let { initializedName(it.firstname, it.lastname) },
            initials = user?.let { initials(it.firstname, it.lastname) },
            isLocked = user?.let { state.liveRoom?.isUserLocked(it.id) } ?: false,
            showStaffControls = state.currentUserIsStaff,
            currentUserFollowedIds = data.currentUserFollowedIds,
            followedItems = data.followedItems?.map {
                LiveRoomUserProfileUi.FollowedItem(
                    id = it.followableId,
                    name = it.name,
                    imageUrl = it.imageUrl,
                )
            } ?: emptyList()
        )
    }
}

data class LiveRoomUserProfileSheetState(
    val currentUserIsStaff: Boolean,
    val liveRoom: LiveAudioRoomEntity? = null,
    val userDetails: LiveAudioRoomUserDetails? = null,
    val currentUserFollowedIds: Set<Followable.Id> = emptySet(),
    val followedItems: List<LiveAudioRoomUserDetails.FollowableItem>? = null,
) : DataState