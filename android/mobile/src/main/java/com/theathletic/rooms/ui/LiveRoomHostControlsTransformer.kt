package com.theathletic.rooms.ui

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.rooms.local.LiveAudioRoomUserDetails
import com.theathletic.ui.Transformer
import com.theathletic.ui.UiModel
import com.theathletic.ui.asParameterizedString
import com.theathletic.ui.binding.ParameterizedString
import com.theathletic.ui.binding.orEmpty
import com.theathletic.ui.utility.displayName

class LiveRoomHostControlsTransformer @AutoKoin constructor() :
    Transformer<LiveRoomHostControlsState, LiveRoomHostControlsContract.ViewState> {

    override fun transform(state: LiveRoomHostControlsState): LiveRoomHostControlsContract.ViewState {
        val uiModels = mutableListOf<UiModel>(LiveRoomControlsOnStageTitle)

        val currentUserDetails = state.userDetails[state.currentUserId]
        if (state.isOnStage) {
            uiModels.add(
                LiveRoomControlsOnStageHost(
                    id = "0",
                    name = ParameterizedString(R.string.global_you),
                    initials = "",
                    imageUrl = currentUserDetails?.staffInfo?.imageUrl,
                )
            )
        }

        val pendingDemotions = state.liveAudioRoom?.demotionRequests ?: emptyList()

        uiModels.addAll(
            state.usersOnStage
                .sortedByDescending { state.liveAudioRoom?.isUserHost(it.id) }
                .map { user ->
                    val details = state.userDetails[user.id] ?: createAnonymousStageUser(user.id)
                    if (state.liveAudioRoom?.isUserHost(user.id) == true) {
                        LiveRoomControlsOnStageHost(
                            id = user.id,
                            name = details.displayName.asParameterizedString().orEmpty(),
                            initials = details.initials,
                            imageUrl = details.staffInfo?.imageUrl,
                        )
                    } else {
                        LiveRoomControlsOnStageUser(
                            id = user.id,
                            name = details.displayName.asParameterizedString().orEmpty(),
                            initials = details.initials,
                            showSpinner = pendingDemotions.any { it.userId == user.id },
                            imageUrl = details.staffInfo?.imageUrl,
                            showVerifiedCheck = details.staffInfo?.verified == true,
                        )
                    }
                }
        )

        val requestModels = getRequestModels(state)
        if (requestModels.isNotEmpty()) {
            uiModels.add(LiveRoomControlsRequestsTitle(requestModels.size.toString()))
            uiModels.addAll(requestModels)
        }

        return LiveRoomHostControlsContract.ViewState(
            uiModels = uiModels
        )
    }

    private fun getRequestModels(state: LiveRoomHostControlsState): List<UiModel> {
        val requests = state.liveAudioRoom?.promotionRequests
            ?.filterNot { it.approved }
            ?.sortedWith(
                compareBy(
                    // Put staff requests at the beginning, then sort by order of creation
                    { if (state.userDetails[it.userId]?.staffInfo?.verified == true) 0 else 1 },
                    { it.createdAt },
                )
            ) ?: emptyList()

        return requests.mapNotNull { request ->
            val details = state.userDetails[request.userId] ?: return@mapNotNull null
            LiveRoomControlsSpeakingRequest(
                id = request.userId,
                name = details.displayName.asParameterizedString().orEmpty(),
                initials = details.initials,
                imageUrl = details.staffInfo?.imageUrl,
                showVerifiedCheck = details.staffInfo?.verified == true,
            )
        }
    }

    private val LiveAudioRoomUserDetails.initials get() = "${firstname.firstOrNull()}${lastname.firstOrNull()}"
}