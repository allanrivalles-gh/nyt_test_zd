package com.theathletic.rooms.ui

import com.theathletic.followable.Followable
import com.theathletic.ui.ResourceString.StringWrapper
import com.theathletic.ui.asResourceString
import com.theathletic.ui.binding.LinkableTag
import com.theathletic.utility.LogoUtility

object LiveRoomPreviewData {

    const val RoomTitle = "Knicks' Noel sues agent Rich Paul, alleges \$58m in lost earnings"
    const val RoomDescription = "The Yankees handed him a 3-0 lead before he even set foot on the mound in Baltimore, and German kept the O's off the board until his final inning of work. The right-hander fired 53 of 80 pitches for strikes in his second straight quality start and fourth of the year, and he'll take a 3.62 ERA and 37:8 K:BB through 37.1 innings into his next outing."

    val Speakers = listOf(
        LiveRoomUi.Speaker(
            id = "3",
            initials = "SC".asResourceString(),
            name = "Shams C.".asResourceString(),
            subtitle = "NBA Insider".asResourceString(),
            imageUrl = null,
            isMuted = false,
            isVerified = true,
        ),
        LiveRoomUi.Speaker(
            id = "4",
            initials = "RS".asResourceString(),
            name = "Ron S.".asResourceString(),
            subtitle = "Podcast Host".asResourceString(),
            imageUrl = null,
            isMuted = true,
            isVerified = true,
        ),
        LiveRoomUi.Speaker(
            id = "3",
            initials = "SC".asResourceString(),
            name = "SuperLongName ToTestStuff".asResourceString(),
            subtitle = "NBA Insider".asResourceString(),
            imageUrl = null,
            isMuted = true,
            isVerified = true,
        ),
        LiveRoomUi.Speaker(
            id = "4",
            initials = "RS".asResourceString(),
            name = "Ron S.".asResourceString(),
            subtitle = "Podcast Host".asResourceString(),
            imageUrl = null,
            isMuted = false,
            isVerified = false,
        ),
        LiveRoomUi.Speaker(
            id = "3",
            initials = "SC".asResourceString(),
            name = "Shams C.".asResourceString(),
            subtitle = "NBA Insider".asResourceString(),
            imageUrl = null,
            isMuted = false,
            isVerified = false,
        ),
    )
    val Audience = listOf(
        LiveRoomUi.AudienceCell.User(
            id = "1",
            initials = "MK".asResourceString(),
            name = "Matt K.".asResourceString(),
            imageUrl = null,
            locked = false,
        ),
        LiveRoomUi.AudienceCell.User(
            id = "2",
            initials = "DD".asResourceString(),
            name = "Donald D.".asResourceString(),
            imageUrl = null,
            locked = false,
        ),
        LiveRoomUi.AudienceCell.User(
            id = "3",
            initials = "GG".asResourceString(),
            name = "Goofy G.".asResourceString(),
            imageUrl = null,
            locked = true,
        ),
        LiveRoomUi.AudienceCell.OverflowIndicator(overflowCount = 99),
    )
    val Tags = listOf(
        LinkableTag("1", "Browns", ""),
        LinkableTag("2", "Cavaliers", ""),
    )

    val Messages = listOf(
        LiveRoomUi.ChatMessage(
            id = "a",
            authorId = "1",
            authorInitials = StringWrapper("DD"),
            authorInitializedName = StringWrapper("Donald D."),
            authorAvatarUrl = null,
            authorIsHost = true,
            authorIsStaff = false,
            authorIsModerator = false,
            showAsLocked = false,
            content = "I can't believe I lost in Fantasy Football this week. My team is loaded with talent.",
        ),
        LiveRoomUi.ChatMessage(
            id = "b",
            authorId = "2",
            authorInitials = StringWrapper("MM"),
            authorInitializedName = StringWrapper("Micky M."),
            authorAvatarUrl = null,
            authorIsHost = false,
            authorIsStaff = true,
            authorIsModerator = false,
            showAsLocked = false,
            content = "Maybe you should have drafted better. Ben Roethlisberger was a terrible selection.",
        ),
        LiveRoomUi.ChatMessage(
            id = "c",
            authorId = "3",
            authorInitials = StringWrapper("GG"),
            authorInitializedName = StringWrapper("Goofy G."),
            authorAvatarUrl = null,
            authorIsHost = false,
            authorIsStaff = false,
            authorIsModerator = false,
            showAsLocked = true,
            content = "Wanna trade? Ah-hyuck!",
        ),
        LiveRoomUi.ChatMessage(
            id = "d",
            authorId = "4",
            authorInitials = StringWrapper("GG"),
            authorInitializedName = StringWrapper("Goofy G."),
            authorAvatarUrl = null,
            authorIsHost = false,
            authorIsStaff = false,
            authorIsModerator = true,
            showAsLocked = false,
            content = "That is a violation of the rules.",
        ),
    )

    val FollowedItems = listOf(
        LiveRoomUserProfileUi.FollowedItem(
            id = Followable.Id("1", Followable.Type.TEAM),
            name = "Browns",
            imageUrl = LogoUtility.getTeamLogoPath(1),
        ),
        LiveRoomUserProfileUi.FollowedItem(
            id = Followable.Id("2", Followable.Type.TEAM),
            name = "Cavaliers",
            imageUrl = LogoUtility.getTeamLogoPath(2),
        ),
        LiveRoomUserProfileUi.FollowedItem(
            id = Followable.Id("3", Followable.Type.TEAM),
            name = "Indians",
            imageUrl = LogoUtility.getTeamLogoPath(3),
        ),
        LiveRoomUserProfileUi.FollowedItem(
            id = Followable.Id("4", Followable.Type.TEAM),
            name = "Buckeyes",
            imageUrl = LogoUtility.getTeamLogoPath(4),
        ),
    )

    val Interactor = object : LiveRoomUi.Interactor {
        override fun onLeaveRoomClicked() { }
        override fun onRequestToSpeakClicked() { }
        override fun onCancelRequestClicked() { }
        override fun onLeaveStageClicked() { }
        override fun onMuteControlClicked(mute: Boolean) { }
        override fun onAudienceButtonClicked() { }
        override fun onBackButtonClicked() { }
        override fun onHostMenuClicked() { }
        override fun onShareClicked() { }
        override fun onUserClicked(id: String) { }
        override fun onUserLongClicked(id: String) { }
        override fun onTagClicked(id: String, deeplink: String) { }
        override fun onSendChatClicked() { }
        override fun onChatInputChanged(value: String) { }
        override fun onTabClicked(tab: LiveRoomTab) { }
        override fun onMessageClicked(messageId: String) { }
        override fun onMessageLongClicked(messageId: String) { }
        override fun onChatPreviewClicked() { }
        override fun onLockedChatInputClicked() { }
    }

    val UserProfileInteractor = object : LiveRoomUserProfileUi.Interactor {
        override fun onFollowClicked(id: Followable.Id, follow: Boolean) {}
        override fun onLockUserClicked() { }
        override fun onUnlockUserClicked() { }
        override fun onRemoveMessageClicked(messageId: String) { }
    }

    val HostInfo = listOf(
        LiveRoomUi.HostInfo(
            id = "1",
            name = "Matt Kula",
            subtitle = "Android Engineer",
            imageUrl = "",
        ),
        LiveRoomUi.HostInfo(
            id = "2",
            name = "Barry Bonds",
            subtitle = null,
            imageUrl = "",
        ),
    )

    val TagInfo = listOf(
        LiveRoomUi.TagInfo(
            id = "01",
            name = "Cleveland Browns",
            imageUrl = "",
            deeplink = "",
        ),
        LiveRoomUi.TagInfo(
            id = "02",
            name = "NFL",
            imageUrl = "",
            deeplink = "",
        ),
    )
}