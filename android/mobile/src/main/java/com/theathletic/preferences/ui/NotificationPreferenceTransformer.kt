package com.theathletic.preferences.ui

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.featureswitch.Features
import com.theathletic.feed.search.ui.UserTopicListItem
import com.theathletic.followable.FollowableType
import com.theathletic.followables.data.domain.UserFollowing
import com.theathletic.preferences.ui.models.PodcastNotificationsEmptyItem
import com.theathletic.profile.manage.UserTopicId
import com.theathletic.profile.manage.UserTopicType
import com.theathletic.ui.LoadingState
import com.theathletic.ui.Transformer
import com.theathletic.ui.UiModel
import com.theathletic.ui.list.ListSectionTitleItem
import com.theathletic.ui.list.ListVerticalPadding
import com.theathletic.ui.list.SimpleListViewState

class NotificationPreferenceTransformer @AutoKoin constructor(
    val features: Features
) : Transformer<NotificationPreferenceState, SimpleListViewState> {

    override fun transform(data: NotificationPreferenceState): SimpleListViewState {
        return SimpleListViewState(
            showSpinner = data.loadingState.isFreshLoadingState,
            uiModels = when (data.loadingState) {
                LoadingState.INITIAL_LOADING -> emptyList()
                else -> getNotificationPreferences(data) + getFollowedTopics(data) + getFollowedPodcasts(data)
            },
            backgroundColorRes = R.color.ath_grey_70
        )
    }

    private fun getNotificationPreferences(state: NotificationPreferenceState): List<UiModel> {
        return listOfNotNull(
            PushNotificationSwitchItem.CommentReplies(state.commentRepliesEnabled),
            PushNotificationSwitchItem.TopSportsNews(state.topSportsNewsOptIn).takeIf { features.isTopSportsNewsNotificationEnabled },
            ListSectionTitleItem(R.string.profile_following)
        )
    }

    private fun getFollowedTopics(
        state: NotificationPreferenceState
    ): List<UiModel> = state.followedItems.mapIndexedNotNull { index, item ->
        item.toUserTopicListItem(index != state.followedItems.lastIndex)
    }

    private fun UserFollowing.toUserTopicListItem(showDivider: Boolean): UserTopicListItem? {
        // TODO(Todd): map to a different model with String ids when we migrate the UI to Compose
        val longId = id.id.toLongOrNull() ?: return null
        val (userTopicId, userTopicType) = when (id.type) {
            FollowableType.TEAM -> Pair(UserTopicId.Team(longId), UserTopicType.TEAM)
            FollowableType.LEAGUE -> Pair(UserTopicId.League(longId), UserTopicType.LEAGUE)
            FollowableType.AUTHOR -> Pair(UserTopicId.Author(longId), UserTopicType.AUTHOR)
        }

        return UserTopicListItem(
            id = longId,
            topicType = userTopicType,
            topicId = userTopicId,
            name = name,
            logoUri = imageUrl,
            selectedIcon = R.drawable.ic_chevron_right,
            showDivider = showDivider
        )
    }

    private fun getFollowedPodcasts(state: NotificationPreferenceState): List<UiModel> {
        val title = listOf(
            ListSectionTitleItem(R.string.podcast_push_settings_title)
        )

        val items = state.followedPodcasts.mapIndexed { index, entity ->
            PushNotificationSwitchItem.Podcast(
                id = entity.id,
                isSwitchOn = entity.notifyEpisodes,
                title = entity.title,
                imageUrl = entity.imageUrl,
                showDivider = index != state.followedPodcasts.lastIndex
            )
        }

        return when {
            items.isEmpty() -> listOf(PodcastNotificationsEmptyItem)
            else -> title + items
        } + ListVerticalPadding(R.dimen.global_spacing_64)
    }
}