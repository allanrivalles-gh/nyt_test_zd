package com.theathletic.profile.ui

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.theathletic.AthleticConfig
import com.theathletic.R
import com.theathletic.followable.Followable
import com.theathletic.ui.CarouselUiModel
import com.theathletic.ui.DayNightMode
import com.theathletic.ui.UiModel
import com.theathletic.ui.binding.ParameterizedString

class ProfileHeaderItem(
    val name: String,
    val isSubscriber: Boolean
) : UiModel {
    override val stableId = "PROFILE_HEADER"

    interface Interactor {
        fun onProfileSettingsClicked()
    }
}

object ProfileAnonymousHeaderItem : UiModel {
    override val stableId = "ANONYMOUS_HEADER"

    interface Interactor {
        fun onAnonymousHeaderClicked()
    }
}

object ProfileLoginItem : UiModel {
    override val stableId = "LOGIN_HEADER"

    interface Interactor {
        fun onLoginClicked()
    }
}

class ProfileFooterItem(
    val versionName: String = AthleticConfig.VERSION_NAME
) : UiModel {
    override val stableId = "PROFILE_FOOTER"

    interface Interactor {
        fun onPrivacyPolicyClick()
        fun onTermsOfServiceClick()
    }
}

@Suppress("LongParameterList")
sealed class ProfileListItem(
    @StringRes val text: Int,
    val subtext: ParameterizedString? = null,
    @DrawableRes val icon: Int,
    val primaryItemShowsIcon: Boolean = false,
    val analyticsElement: String,
    @ColorRes val tintColor: Int = R.color.ath_grey_20,
    val badgeText: ParameterizedString? = null,
    val primaryItem: Boolean
) : UiModel {

    object CreateLiveRoom : ProfileListItem(
        text = R.string.rooms_create_title,
        icon = R.drawable.ic_edit_white,
        analyticsElement = "create_live_room",
        primaryItem = true,
        primaryItemShowsIcon = true
    )

    object ScheduledLiveRooms : ProfileListItem(
        text = R.string.rooms_scheduled_title,
        icon = R.drawable.ic_discuss,
        analyticsElement = "scheduled_live_rooms",
        primaryItem = true,
        primaryItemShowsIcon = true
    )

    class SavedStory(
        badgeText: ParameterizedString?
    ) : ProfileListItem(
        text = R.string.profile_saved_stories,
        icon = R.drawable.ic_article_bookmark_selected,
        analyticsElement = "saved_stories",
        primaryItem = true,
        badgeText = badgeText,
        primaryItemShowsIcon = true
    )

    class Podcasts(
        badgeText: ParameterizedString?
    ) : ProfileListItem(
        text = R.string.profile_podcasts,
        icon = R.drawable.ic_podcasts,
        analyticsElement = "podcast",
        primaryItem = true,
        badgeText = badgeText,
        primaryItemShowsIcon = true
    )

    object NewsletterPreferences : ProfileListItem(
        text = R.string.profile_email_preferences,
        icon = R.drawable.ic_preference,
        analyticsElement = "email_preference",
        primaryItem = true
    )

    object NotificationPreferences : ProfileListItem(
        text = R.string.profile_notification_preferences,
        icon = R.drawable.ic_preference,
        analyticsElement = "notifications",
        primaryItem = true
    )

    object RegionSelection : ProfileListItem(
        text = R.string.profile_region_preferences,
        icon = R.drawable.ic_preference,
        analyticsElement = "region",
        primaryItem = true
    )

    class GuestPasses(subtext: ParameterizedString) : ProfileListItem(
        text = R.string.profile_guest_pass,
        icon = R.drawable.ic_gift,
        tintColor = R.color.ath_grey_10,
        analyticsElement = "guest_pass",
        primaryItem = true,
        subtext = subtext
    )

    object GiveGift : ProfileListItem(
        text = R.string.profile_give_gift,
        icon = R.drawable.ic_gift,
        tintColor = R.color.ath_grey_10,
        analyticsElement = "gift",
        primaryItem = false
    )

    object RateApp : ProfileListItem(
        text = R.string.profile_rate_app,
        icon = R.drawable.ic_star,
        tintColor = R.color.ath_grey_10,
        analyticsElement = "rate",
        primaryItem = false
    )

    object FAQ : ProfileListItem(
        text = R.string.profile_faq,
        icon = R.drawable.ic_faq_questionmark,
        tintColor = R.color.ath_grey_10,
        analyticsElement = "faq",
        primaryItem = false
    )

    object EmailSupport : ProfileListItem(
        text = R.string.profile_email_support,
        icon = R.drawable.ic_mail,
        tintColor = R.color.ath_grey_10,
        analyticsElement = "email_support",
        primaryItem = false
    )

    object LogOut : ProfileListItem(
        text = R.string.profile_log_out,
        icon = R.drawable.ic_log_out,
        tintColor = R.color.ath_grey_10,
        analyticsElement = "log_out",
        primaryItem = false
    )

    object DebugTools : ProfileListItem(
        text = R.string.profile_debug_tools,
        icon = R.drawable.ic_gear,
        tintColor = R.color.ath_red,
        analyticsElement = "debug_tools",
        primaryItem = false
    )

    override val stableId: String get() = "$text"

    interface Interactor {
        fun onProfileListItemClick(item: ProfileListItem)
    }
}

class ProfileFollowingListItem(
    override val carouselItemModels: List<UiModel>
) : CarouselUiModel {
    override val stableId = "PROFILE_FOLLOWING"

    interface Interactor {
        fun onEditClicked()
    }
}

@Suppress("LongParameterList")
class ProfileFollowingCarouselItem(
    val id: Followable.Id,
    val itemAbbreviation: String,
    val iconUri: String,
    val hasContrastColor: Boolean = true,
    val contrastColorHex: String = "",
    @DrawableRes val errorImage: Int = R.drawable.ic_athletic_logo
) : UiModel {
    override val stableId = id.toString()

    interface Interactor {
        fun onFollowingItemClicked(id: Followable.Id)
    }
}

object ProfileFollowingCarouselAddMoreItem : UiModel {
    override val stableId = "ADD_MORE"

    interface Interactor {
        fun onAddMoreClicked()
    }
}

data class ProfileSubscribeItem(
    @StringRes val text: Int
) : UiModel {
    override val stableId = "PROFILE_SUBSCRIBE"

    interface Interactor {
        fun onSubscribeClicked()
    }
}

data class DayNightToggleItem(
    @StringRes val text: Int,
    val displaySystemThemeButton: Boolean = false,
    val selectedMode: DayNightMode
) : UiModel {
    override val stableId = "DAY_NIGHT"

    interface Interactor {
        fun onDayNightToggle(displayTheme: DayNightMode)
    }
}