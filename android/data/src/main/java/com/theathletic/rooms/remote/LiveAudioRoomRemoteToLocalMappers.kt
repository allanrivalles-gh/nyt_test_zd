package com.theathletic.rooms.remote

import com.theathletic.chat.remote.toEntity
import com.theathletic.datetime.Datetime
import com.theathletic.datetime.asDatetimeOrNull
import com.theathletic.entity.chat.ChatRoomEntity
import com.theathletic.entity.room.LiveAudioRoomEntity
import com.theathletic.entity.room.LiveRoomCategory
import com.theathletic.entity.room.SpeakingRequest
import com.theathletic.fragment.ChatMessageFragment
import com.theathletic.fragment.LiveRoomFragment
import com.theathletic.fragment.LiveRoomUserFragment
import com.theathletic.rooms.create.ui.LiveRoomTagType
import com.theathletic.rooms.local.LiveAudioRoomUserDetails
import com.theathletic.type.LiveRoomType
import com.theathletic.type.SpeakingRequestType
import com.theathletic.type.UserRole

internal fun LiveRoomFragment.toEntity() = LiveAudioRoomEntity(
    id = id,
    title = title,
    subtitle = subtitle,
    description = description,
    chatDisabled = disable_chat,
    audienceSize = audience_total,
    maxCapacity = room_limit,
    permalink = permalink,
    chatRoomId = chat.fragments.chatRoomFragment.id,
    createdAt = created_at.asDatetimeOrNull(),
    startedAt = started_at.asDatetimeOrNull(),
    endedAt = ended_at.asDatetimeOrNull(),
    isRecording = is_recorded,
    autoPushEnabled = auto_push_enabled,
    autoPushSent = auto_push_sent,
    tags = tags.mapNotNull { it?.toEntityTag() },
    categories = live_room_types.mapNotNull { it.asLocal },
    topicImages = images.map { it.image_uri },
    hosts = hosts.map {
        val staff = it.fragments.liveRoomUserFragment.asStaff
        LiveAudioRoomEntity.Host(
            id = it.fragments.liveRoomUserFragment.id,
            name = it.fragments.liveRoomUserFragment.name,
            imageUrl = staff?.avatar_uri.orEmpty(),
            tagImageUrl = staff?.team_avatar_uri ?: staff?.league_avatar_uri.orEmpty(),
        )
    },
    moderatorIds = moderators.map { it.id },
    lockedUserIds = locked_users.map { it.id },
    promotionRequests = requests
        .filter { it.type == SpeakingRequestType.promotion }
        .map { remoteRequest ->
            SpeakingRequest(
                userId = remoteRequest.from.id,
                approved = remoteRequest.approved,
                createdAt = Datetime(remoteRequest.created_at),
            )
        },
    demotionRequests = requests
        .filter { it.type == SpeakingRequestType.demotion }
        .map { remoteRequest ->
            SpeakingRequest(
                userId = remoteRequest.from.id,
                approved = remoteRequest.approved,
                createdAt = Datetime(remoteRequest.created_at),
            )
        },
    muteRequests = requests
        .filter { it.type == SpeakingRequestType.mute }
        .map { remoteRequest ->
            SpeakingRequest(
                userId = remoteRequest.from.id,
                approved = remoteRequest.approved,
                createdAt = Datetime(remoteRequest.created_at),
            )
        },
    usersInRoom = usersInRoom,
)

internal fun LiveRoomFragment.toChatRoomEntity(): ChatRoomEntity {
    val remote = chat.fragments.chatRoomFragment
    return ChatRoomEntity(
        id = remote.id,
        messages = remote.messages.map { it.fragments.chatMessageFragment.toEntity() },
    )
}

private val LiveRoomFragment.usersInRoom: List<String> get() {
    val hosts = hosts.map { it.fragments.liveRoomUserFragment.id }
    val broadcasters = broadcasters.map { it.fragments.liveRoomUserFragment.id }
    val audience = audiences.map { it.fragments.liveRoomUserFragment.id }

    return hosts.union(broadcasters).union(audience).toList()
}

internal fun LiveRoomFragment.toParticipantList(): List<LiveAudioRoomUserDetails> {
    val hosts = hosts.map { it.fragments.liveRoomUserFragment.toParticipant() }
    val broadcasters = broadcasters.map { it.fragments.liveRoomUserFragment.toParticipant() }
    val audience = audiences.map { it.fragments.liveRoomUserFragment.toParticipant() }
    val chatUsers = chat.fragments.chatRoomFragment.messages.map {
        it.fragments.chatMessageFragment.created_by.toParticipant()
    }
    val requests = requests.map { it.from.fragments.liveRoomUserFragment.toParticipant() }

    return (hosts + broadcasters + audience + chatUsers + requests).distinctBy { it.id }
}

private fun LiveRoomUserFragment.toParticipant() = LiveAudioRoomUserDetails(
    id = id,
    name = name,
    firstname = first_name,
    lastname = last_name,
    staffInfo = asStaff?.let {
        LiveAudioRoomUserDetails.StaffInfo(
            bio = it.bio.orEmpty(),
            imageUrl = it.avatar_uri,
            twitterHandle = it.twitter.orEmpty().removePrefix("@"),
            description = it.description.orEmpty(),
            verified = it.role.showAsLiveRoomVerified,
        )
    },
)

private fun ChatMessageFragment.Created_by.toParticipant() = LiveAudioRoomUserDetails(
    id = id,
    name = name,
    firstname = first_name,
    lastname = last_name,
    staffInfo = asStaff?.let {
        LiveAudioRoomUserDetails.StaffInfo(
            bio = it.bio.orEmpty(),
            imageUrl = it.avatar_uri,
            twitterHandle = it.twitter.orEmpty().removePrefix("@"),
            description = it.description.orEmpty(),
            verified = it.role.showAsLiveRoomVerified,
        )
    },
)

private fun LiveRoomFragment.Tag.toEntityTag(): LiveAudioRoomEntity.Tag? {
    return LiveAudioRoomEntity.Tag(
        id = this.id,
        title = this.title,
        name = this.name.orEmpty(),
        shortname = this.shortname,
        deeplink = this.deeplink_url.orEmpty(),
        color = this.asTeamTag?.teamRef?.color_primary,
        type = when (this.type) {
            "team" -> LiveRoomTagType.TEAM
            "league" -> LiveRoomTagType.LEAGUE
            else -> return null
        }
    )
}

private val LiveRoomType.asLocal get() = when (this) {
    LiveRoomType.breaking_news -> LiveRoomCategory.BREAKING_NEWS
    LiveRoomType.game_preview_1_team -> LiveRoomCategory.GAME_PREVIEW_1_TEAM
    LiveRoomType.game_preview_2_team -> LiveRoomCategory.GAME_PREVIEW_2_TEAM
    LiveRoomType.game_recap -> LiveRoomCategory.GAME_RECAP
    LiveRoomType.live_podcast -> LiveRoomCategory.LIVE_PODCAST
    LiveRoomType.question_and_answer -> LiveRoomCategory.QUESTION_AND_ANSWER
    LiveRoomType.recurring -> LiveRoomCategory.RECURRING
    else -> null
}

internal val UserRole.showAsLiveRoomVerified get() = when (this) {
    UserRole.administrator,
    UserRole.author,
    UserRole.editor,
    UserRole.podcast_host,
    UserRole.podcast_producer -> true
    else -> false
}