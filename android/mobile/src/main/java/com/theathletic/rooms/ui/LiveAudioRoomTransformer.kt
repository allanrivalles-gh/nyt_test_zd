package com.theathletic.rooms.ui

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.audio.getAnonymousAnimal
import com.theathletic.audio.getAnonymousColor
import com.theathletic.rooms.create.ui.LiveRoomTagType
import com.theathletic.rooms.local.LiveAudioRoomUserDetails
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.ResourceString.StringWrapper
import com.theathletic.ui.Transformer
import com.theathletic.ui.asResourceString
import com.theathletic.ui.binding.LinkableTag
import com.theathletic.ui.orEmpty
import com.theathletic.ui.utility.displayName
import com.theathletic.ui.utility.initializedName
import com.theathletic.ui.utility.initials
import com.theathletic.utility.LogoUtility

class LiveAudioRoomTransformer @AutoKoin constructor() :
    Transformer<LiveAudioRoomState, LiveAudioRoomContract.ViewState> {

    companion object {
        private const val MAX_USERS_IN_AUDIENCE = 39
    }

    override fun transform(data: LiveAudioRoomState): LiveAudioRoomContract.ViewState {
        val pendingRequests = data.liveAudioRoom?.promotionRequests?.filter { !it.approved } ?: emptyList()

        val room = data.liveAudioRoom

        return LiveAudioRoomContract.ViewState(
            selectedTab = data.selectedTab,
            showSpinner = data.loadingState.isFreshLoadingState,
            roomTitle = room?.title.orEmpty(),
            roomDescription = room?.description.orEmpty(),
            hosts = room?.hosts?.map { host ->
                LiveRoomUi.HostInfo(
                    id = host.id,
                    name = host.name,
                    subtitle = null,
                    imageUrl = host.imageUrl,
                )
            }.orEmpty(),
            tags = room?.tags?.map { tag ->
                LiveRoomUi.TagInfo(
                    id = tag.id,
                    name = tag.name,
                    imageUrl = when (tag.type) {
                        LiveRoomTagType.LEAGUE -> LogoUtility.getColoredLeagueLogoPath(tag.id)
                        LiveRoomTagType.TEAM -> LogoUtility.getTeamLogoPath(tag.id)
                        else -> ""
                    },
                    deeplink = tag.deeplink,
                )
            }.orEmpty(),
            chatEnabled = room?.chatDisabled != true,
            recording = room?.isRecording == true,
            linkableTags = createLinkableTags(data),
            isMuted = data.userIsMuted,
            isOnStage = data.isOnStage,
            isLocked = data.liveAudioRoom?.isUserLocked(data.currentUserId) ?: false,
            hasPendingRequest = pendingRequests.any { it.userId == data.currentUserId },
            isHost = room?.isUserHost(data.currentUserId) ?: false,
            isModerator = room?.moderatorIds?.contains(data.currentUserId) ?: false,
            audienceControlsBadgeCount = pendingRequests.size,
            chatInput = data.chatInput,
            messages = getChatRoomMessages(data),
            currentBottomSheetModal = data.currentBottomSheetModal,
            speakers = getStageUsers(data),
            audience = getAudienceUiModels(data),
            totalAudienceSize = data.liveAudioRoom?.audienceSize ?: 0,
        )
    }

    private fun getStageUsers(state: LiveAudioRoomState): List<LiveRoomUi.Speaker> {
        val currentUserDetails = state.userInRoomDetails[state.currentUserId]
        val you = LiveRoomUi.Speaker(
            id = state.currentUserId,
            name = StringWithParams(R.string.global_you),
            subtitle = currentUserDetails?.staffInfo?.description?.asResourceString().orEmpty(),
            initials = StringWrapper(""),
            isMuted = state.userIsMuted,
            imageUrl = currentUserDetails?.staffInfo?.imageUrl,
            isVerified = currentUserDetails?.staffInfo?.verified == true,
        )

        val others = state.usersOnStage.sortedBy { it.id }.map { user ->
            val details = state.userInRoomDetails[user.id] ?: createAnonymousStageUser(user.id)
            LiveRoomUi.Speaker(
                id = user.id,
                name = details.displayName,
                subtitle = details.staffInfo?.description?.asResourceString()
                    ?: StringWithParams(R.string.rooms_subscriber),
                initials = initials(details.firstname, details.lastname),
                isMuted = user.isMuted,
                imageUrl = details.staffInfo?.imageUrl,
                isVerified = details.staffInfo?.verified == true,
            )
        }

        return if (state.isOnStage) {
            listOf(you) + others
        } else {
            others
        }
    }

    private fun getAudienceUiModels(state: LiveAudioRoomState): List<LiveRoomUi.AudienceCell> {
        val usersInAudience = state.getFullAudience()

        val filteredAudience = usersInAudience.take(MAX_USERS_IN_AUDIENCE).toMutableList()

        val audienceSize = state.liveAudioRoom?.audienceSize ?: 0
        val overflowCount = audienceSize - filteredAudience.size
        if (overflowCount > 0) {
            filteredAudience.add(
                LiveRoomUi.AudienceCell.OverflowIndicator(overflowCount = overflowCount)
            )
        }

        return filteredAudience
    }

    private fun LiveAudioRoomState.getFullAudience(): List<LiveRoomUi.AudienceCell> {
        liveAudioRoom ?: return emptyList()

        val currentUserDetails = userInRoomDetails[currentUserId]
        val you = LiveRoomUi.AudienceCell.User(
            id = currentUserId,
            name = StringWithParams(R.string.global_you),
            initials = currentUserDetails?.let {
                initials(it.firstname, it.lastname)
            }.orEmpty(),
            imageUrl = currentUserDetails?.staffInfo?.imageUrl,
            locked = false,
        )

        val onStageIds = usersOnStage.map { it.id }.toMutableSet()
        val hostIds = liveAudioRoom.hosts.map { it.id }.toMutableSet()

        val audience = liveAudioRoom.usersInRoom.asSequence()
            .filterNot { it == currentUserId }
            .filterNot { it in onStageIds }
            .filterNot { it in hostIds }
            .sortedBy { it }
            .mapNotNull { userId ->
                val user = userInRoomDetails[userId] ?: return@mapNotNull null
                val userLocked = liveAudioRoom.isUserLocked(userId)

                LiveRoomUi.AudienceCell.User(
                    id = user.id,
                    name = initializedName(user.firstname, user.lastname),
                    initials = initials(user.firstname, user.lastname),
                    imageUrl = user.staffInfo?.imageUrl,
                    locked = userLocked && isStaff
                )
            }
            .toList()

        return if (isOnStage) {
            audience
        } else {
            listOf(you) + audience
        }
    }

    private fun getChatRoomMessages(state: LiveAudioRoomState): List<LiveRoomUi.ChatMessage> {
        return state.chatRoom?.messages
            ?.filter { !it.authorIsShadowbanned || it.authorId == state.currentUserId }
            ?.sortedByDescending { it.createdAt }
            ?.map {
                val userLocked = state.liveAudioRoom?.isUserLocked(it.authorId) ?: false

                LiveRoomUi.ChatMessage(
                    id = it.id,
                    authorId = it.authorId,
                    authorInitials = initials(it.authorFirstname, it.authorLastname),
                    authorInitializedName = when (it.authorId) {
                        state.currentUserId -> StringWithParams(R.string.global_you)
                        else -> initializedName(it.authorFirstname, it.authorLastname)
                    },
                    authorAvatarUrl = it.authorAvatarUrl,
                    authorIsHost = state.liveAudioRoom?.isUserHost(it.authorId) ?: false,
                    authorIsStaff = it.authorIsStaff,
                    authorIsModerator = state.liveAudioRoom?.isUserModerator(it.authorId) ?: false,
                    showAsLocked = userLocked && state.isStaff,
                    content = it.content,
                )
            } ?: emptyList()
    }

    private fun createLinkableTags(state: LiveAudioRoomState): List<LinkableTag> {
        return state.liveAudioRoom?.tags?.map {
            LinkableTag(it.id, it.title, it.deeplink)
        } ?: emptyList()
    }
}

fun createAnonymousStageUser(id: String): LiveAudioRoomUserDetails {
    val firstname = getAnonymousColor(id)
    val lastname = getAnonymousAnimal(id)
    return LiveAudioRoomUserDetails(
        id = id,
        firstname = firstname,
        lastname = lastname,
        name = "$firstname $lastname",
        staffInfo = null
    )
}