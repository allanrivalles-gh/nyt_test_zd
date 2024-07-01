package com.theathletic.rooms.ui

import android.net.Uri
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.followable.Followable
import com.theathletic.followables.FollowItemUseCase
import com.theathletic.followables.ObserveUserFollowingUseCase
import com.theathletic.followables.UnfollowItemUseCase
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.rooms.RoomsRepository
import com.theathletic.rooms.local.LiveAudioRoomUserDetails
import com.theathletic.rooms.ui.LiveRoomHostProfileSheetContract.ViewState
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.DataState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class LiveRoomHostProfileSheetViewModel @AutoKoin constructor(
    @Assisted val params: Params,
    @Assisted val navigator: ScreenNavigator,
    analytics: LiveAudioRoomAnalytics,
    private val observeUserFollowingUseCase: ObserveUserFollowingUseCase,
    private val followItemUseCase: FollowItemUseCase,
    private val unfollowItemUseCase: UnfollowItemUseCase,
    private val roomsRepository: RoomsRepository,
) :
    AthleticViewModel<LiveRoomHostProfileSheetState, ViewState>(),
    DefaultLifecycleObserver,
    LiveAudioRoomAnalytics by analytics,
    LiveRoomHostProfileSheetContract.Presenter {

    data class Params(
        val authorId: String,
        val roomId: String,
    )

    override val initialState by lazy {
        LiveRoomHostProfileSheetState()
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        initialize()
    }
    fun initialize() {
        val authorId = Followable.Id(params.authorId, Followable.Type.AUTHOR)
        observeUserFollowingUseCase().onEach {
            updateState { copy(isFollowingHost = it.any { it.id == authorId })}
        }.launchIn(viewModelScope)

        roomsRepository.getUserDetailsForRoom(params.roomId).onEach { users ->
            val host = users?.find { it.id == params.authorId } ?: return@onEach
            updateState { copy(hostDetails = host) }
        }.launchIn(viewModelScope)

        trackView(
            roomId = params.roomId,
            element = "host_profile",
            objectType = "user_id",
            objectId = params.authorId
        )
    }

    override fun onFollowClicked(shouldFollow: Boolean) {
        val id = Followable.Id(params.authorId, Followable.Type.AUTHOR)
        viewModelScope.launch {
            if (shouldFollow) {
                trackClick(
                    view = "host_profile",
                    element = "follow",
                    roomId = params.roomId,
                    objectType = "user_id",
                    objectId = params.authorId,
                )
                followItemUseCase(id)
            } else {
                trackClick(
                    view = "host_profile",
                    element = "unfollow",
                    roomId = params.roomId,
                    objectType = "user_id",
                    objectId = params.authorId,
                )
                unfollowItemUseCase(id)
            }
        }
    }

    override fun onTwitterHandleClicked(twitterHandle: String?) {
        twitterHandle ?: return
        trackClick(
            view = "host_profile",
            element = "twitter",
            roomId = params.roomId,
            objectType = "user_id",
            objectId = params.authorId,
        )
        navigator.startOpenExternalLink(
            Uri.parse("https://www.twitter.com/$twitterHandle")
        )
    }

    override fun transform(data: LiveRoomHostProfileSheetState): ViewState {
        return ViewState(
            name = data.hostDetails?.name.orEmpty(),
            avatarUrl = data.hostDetails?.staffInfo?.imageUrl,
            twitterHandle = data.hostDetails?.staffInfo?.twitterHandle,
            bio = data.hostDetails?.staffInfo?.bio.orEmpty(),
            isFollowing = data.isFollowingHost
        )
    }
}

data class LiveRoomHostProfileSheetState(
    val isFollowingHost: Boolean = false,
    val hostDetails: LiveAudioRoomUserDetails? = null
) : DataState