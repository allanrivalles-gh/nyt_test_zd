package com.theathletic.profile.ui

import com.theathletic.AthleticConfig
import com.theathletic.BuildConfig
import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.extension.addAll
import com.theathletic.extension.addIf
import com.theathletic.followable.Followable.Type.AUTHOR
import com.theathletic.followable.FollowableType
import com.theathletic.profile.ui.ProfileContract.ProfileViewState
import com.theathletic.ui.DisplayPreferences
import com.theathletic.ui.Transformer
import com.theathletic.ui.UiModel
import com.theathletic.ui.binding.ParameterizedString
import com.theathletic.ui.list.Divider
import com.theathletic.ui.list.ListVerticalPadding

class ProfileTransformer @AutoKoin constructor(
    private val displayPreferences: DisplayPreferences,
) :
    Transformer<ProfileState, ProfileViewState> {

    override fun transform(data: ProfileState) = ProfileViewState(
        listModels = getListItems(data),
        displayLoginMenuItem = data.user?.isAnonymous != false
    )

    @Suppress("LongMethod")
    private fun getListItems(data: ProfileState) = mutableListOf<UiModel>().apply {
        var dividerSeed = 0

        add(getHeaderItem(data))
        getProfileSubscribeListItem(data)?.let {
            add(it)
        }
        getLoginItem(data)?.let {
            add(it)
        }
        add(ListVerticalPadding(R.dimen.global_spacing_8))

        addAll(
            ProfileFollowingListItem(carouselItemModels = getFollowingItems(data)),
            ListVerticalPadding(R.dimen.global_spacing_8)
        )

        data.getCreateLiveRoomItem()?.let {
            add(it)
            add(Divider(++dividerSeed))
        }
        data.getScheduledLiveRoomsItem()?.let {
            add(it)
            add(Divider(++dividerSeed))
        }

        addAll(
            getPodcastItem(data),
            Divider(++dividerSeed),
            getSavedStoriesItem(data),
            ListVerticalPadding(R.dimen.global_spacing_8)
        )

        add(
            DayNightToggleItem(
                text = R.string.profile_display_theme,
                displaySystemThemeButton = displayPreferences.supportsSystemDayNightMode,
                selectedMode = data.displayTheme
            )
        )
        addAll(
            ProfileListItem.NewsletterPreferences,
            Divider(++dividerSeed),
            ProfileListItem.NotificationPreferences,
            Divider(++dividerSeed),
            ProfileListItem.RegionSelection,
        )

        // Tt Add Referral item
        getProfileGuestPassListItem(data)?.let { profileGuestPassListItem ->
            add(Divider(++dividerSeed))
            add(profileGuestPassListItem)
        }
        add(ListVerticalPadding(R.dimen.global_spacing_8))

        addAll(
            ProfileListItem.GiveGift,
            ProfileListItem.RateApp,
            ProfileListItem.FAQ,
            ProfileListItem.EmailSupport,
            ProfileListItem.LogOut,
        )

        addIf(ProfileListItem.DebugTools) { data.isDebugMode }

        addAll(
            ListVerticalPadding(R.dimen.global_spacing_8),
            ProfileFooterItem(AthleticConfig.VERSION_NAME)
        )
    }

    private fun ProfileState.getCreateLiveRoomItem() = when {
        user?.canHostLiveRoom == true -> ProfileListItem.CreateLiveRoom
        BuildConfig.DEV_ENVIRONMENT -> ProfileListItem.CreateLiveRoom
        else -> null
    }

    private fun ProfileState.getScheduledLiveRoomsItem() = when {
        isStaff -> ProfileListItem.ScheduledLiveRooms
        else -> null
    }

    private fun getPodcastItem(data: ProfileState): ProfileListItem.Podcasts {
        if (data.showDiscoverPodcastBadge) {
            return ProfileListItem.Podcasts(ParameterizedString(R.string.profile_podcast_discover))
        }

        return ProfileListItem.Podcasts(
            badgeText = when (val episodes = data.newPodcastEpisodes.size) {
                0 -> null
                1 -> ParameterizedString(R.string.profile_podcast_episodes_badge_single, episodes)
                else -> ParameterizedString(
                    R.string.profile_podcast_episodes_badge_plural,
                    episodes
                )
            }
        )
    }

    private fun getSavedStoriesItem(data: ProfileState): ProfileListItem.SavedStory {
        return ProfileListItem.SavedStory(
            badgeText = when (val unreadCount = data.unreadSavedStoryCount) {
                0 -> null
                else -> ParameterizedString(R.string.profile_saved_stories_badge, unreadCount)
            }
        )
    }

    /**
     * In case the feature is enabled and we have valid data, we are going to return the item.
     * We return null in case of item should not be shown.
     */
    private fun getProfileGuestPassListItem(data: ProfileState): ProfileListItem? {
        val referralsRedeemed = data.user?.referralsRedeemed ?: return null
        val referralsTotal = data.user.referralsTotal

        if (data.isUserSubscribed) {
            return ProfileListItem.GuestPasses(
                subtext = ParameterizedString(
                    R.string.profile_guest_pass_note,
                    referralsRedeemed,
                    referralsTotal
                )
            )
        }

        return null
    }

    private fun getProfileSubscribeListItem(data: ProfileState): ProfileSubscribeItem? {
        return when {
            data.isUserSubscribed -> null
            data.isUserFreeTrialEligible -> ProfileSubscribeItem(R.string.profile_start_free_trial)
            !data.isUserSubscribed -> ProfileSubscribeItem(R.string.profile_subscribe)
            else -> null
        }
    }

    private fun getHeaderItem(data: ProfileState): UiModel {
        return if (data.user?.isAnonymous != false) {
            return ProfileAnonymousHeaderItem
        } else {
            ProfileHeaderItem(
                name = data.user.getUserNickName(),
                isSubscriber = data.isUserSubscribed
            )
        }
    }

    private fun getLoginItem(data: ProfileState) = when {
        data.user?.isAnonymous != false -> ProfileLoginItem
        else -> null
    }

    private fun getFollowingItems(data: ProfileState): List<UiModel> {
        if (data.followableLoaded.not()) return emptyList()

        return data.followingItems.map { item ->
            val hasBackground = item.id.type != FollowableType.AUTHOR
            val errorImage = if (item.id.type == AUTHOR) {
                R.drawable.ic_headshot_placeholder
            } else {
                R.drawable.ic_athletic_logo
            }

            ProfileFollowingCarouselItem(
                id = item.id,
                itemAbbreviation = item.shortName,
                iconUri = item.imageUrl,
                contrastColorHex = item.color,
                hasContrastColor = hasBackground,
                errorImage = errorImage
            )
        }
    }
}