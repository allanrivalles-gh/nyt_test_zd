package com.theathletic.preferences.ui

import com.theathletic.R
import com.theathletic.entity.settings.EmailSettingsItem
import com.theathletic.ui.UiModel
import com.theathletic.ui.binding.ParameterizedString

sealed class PreferenceSwitchItem(
    val title: ParameterizedString,
    val description: ParameterizedString?,
    val isSwitchOn: Boolean,
    val showDivider: Boolean = true,
    val imageUrl: String? = null
) : UiModel {

    override val stableId = "$title$description"

    override fun equals(other: Any?): Boolean {
        val otherItem = (other as? PreferenceSwitchItem) ?: return false

        return otherItem.stableId == stableId && otherItem.isSwitchOn == isSwitchOn
    }

    override fun hashCode() = stableId.hashCode() * isSwitchOn.hashCode()
}

sealed class PushNotificationSwitchItem(
    title: ParameterizedString,
    description: ParameterizedString? = null,
    isSwitchOn: Boolean,
    showDivider: Boolean = false,
    imageUrl: String? = null
) : PreferenceSwitchItem(title, description, isSwitchOn, showDivider, imageUrl) {
    class CommentReplies(isSwitchOn: Boolean) : PushNotificationSwitchItem(
        title = ParameterizedString(R.string.preferences_comment_replies),
        isSwitchOn = isSwitchOn
    )

    class TopSportsNews(isSwitchOn: Boolean) : PushNotificationSwitchItem(
        title = ParameterizedString(R.string.preferences_top_sports_news),
        description = ParameterizedString(R.string.preferences_top_sports_news_description),
        isSwitchOn = isSwitchOn
    )

    class Podcast(
        val id: String,
        isSwitchOn: Boolean,
        title: String,
        imageUrl: String?,
        showDivider: Boolean = true
    ) : PushNotificationSwitchItem(
        title = ParameterizedString(title),
        isSwitchOn = isSwitchOn,
        showDivider = showDivider,
        imageUrl = imageUrl
    )

    class Stories(
        isSwitchOn: Boolean,
        showDivider: Boolean = false
    ) : PushNotificationSwitchItem(
        title = ParameterizedString(R.string.preferences_stories),
        isSwitchOn = isSwitchOn,
        showDivider = showDivider
    )

    class GameResults(isSwitchOn: Boolean) : PushNotificationSwitchItem(
        title = ParameterizedString(R.string.preferences_game_results),
        isSwitchOn = isSwitchOn
    )

    class GameStart(isSwitchOn: Boolean) : PushNotificationSwitchItem(
        title = ParameterizedString(R.string.preferences_game_start),
        isSwitchOn = isSwitchOn
    )
}

data class NewsletterSwitchItem(
    val title: String,
    val description: String,
    val isSwitchOn: Boolean,
    val showDivider: Boolean = true
) : UiModel {
    companion object {
        fun fromEmailSettingsItem(
            item: EmailSettingsItem,
            isLastItem: Boolean = false
        ): NewsletterSwitchItem {
            return NewsletterSwitchItem(
                title = item.title,
                description = item.description,
                isSwitchOn = item.value,
                showDivider = !isLastItem
            )
        }
    }

    override val stableId = title

    override fun hashCode() = stableId.hashCode() * isSwitchOn.hashCode()

    interface Interactor {
        fun onNewsletterToggled(item: NewsletterSwitchItem, isOn: Boolean)
    }
}