package com.theathletic.feed.search.ui

import androidx.annotation.DrawableRes
import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.followable.Followable
import com.theathletic.followable.FollowableId
import com.theathletic.ui.Transformer
import com.theathletic.ui.UiModel
import com.theathletic.ui.list.ListLoadingItem
import com.theathletic.ui.list.list

class UserTopicSearchStateTransformer @AutoKoin constructor() :
    Transformer<UserTopicSearchState, UserTopicSearch.ViewState> {

    override fun transform(data: UserTopicSearchState): UserTopicSearch.ViewState {
        return UserTopicSearch.ViewState(
            uiModels = generateList(data),
            showSearchClearButton = data.queryText.isNotBlank(),
            showClearSelected = data.selectedFollowable != null,
            searchEntryHint = if (data.applyScoresFiltering) {
                R.string.user_topic_search_for_scores_hint
            } else {
                R.string.user_topic_search_hint
            }
        )
    }

    private fun generateList(data: UserTopicSearchState) = when {
        data.isLoading -> listOf(ListLoadingItem)
        data.queryText.isEmpty() -> generateDefaultList(data)
        else -> data.filteredFollowables.map { followableItem ->
            followableItem.toSearchFollowableItem(followableItem.followableId.listDrawable(data))
        }
    }

    private fun generateDefaultList(data: UserTopicSearchState): List<UiModel> {
        // If a topic is selected, we want to move it to the top
        val selectedTopic = getSelectedTopicModel(data)

        val leagues = data.filteredFollowables.filter { it.followableId.type == Followable.Type.LEAGUE }.map {
            it.toSearchFollowableItem(it.followableId.listDrawable(data))
        }
        val teamLogos = data.filteredFollowables.filter { it.followableId.type == Followable.Type.TEAM }.associate {
            it.followableId to it.imageUrl
        }
        val teams = data.recommendedTeams
            .filterNot { data.selectedFollowable == it.id }
            .map { team ->
                UserSearchFollowableItem(
                    id = team.id,
                    name = team.displayName,
                    logoUri = teamLogos[team.id],
                    selectedIcon = team.id.listDrawable(data),
                    showDivider = true
                )
            }

        val followingItem = UserSearchFollowingGrid(
            carouselItemModels = getFollowingItems(data)
        )

        return list {
            single {
                if (followingItem.carouselItemModels.isNotEmpty()) {
                    followingItem
                } else {
                    null
                }
            }

            section(UserTopicSearchListSection.Browse) {
                val selectedTopicList = if (selectedTopic == null) emptyList() else listOf(selectedTopic)
                selectedTopicList + teams + leagues
            }
        }
    }

    private fun getSelectedTopicModel(data: UserTopicSearchState): UiModel? {
        val selectedFollowable = data.filteredFollowables.firstOrNull {
            it.followableId == data.selectedFollowable
        }
        return selectedFollowable?.toSearchFollowableItem(
            selectedFollowable.followableId.listDrawable(data)
        )
    }

    private fun getFollowingItems(data: UserTopicSearchState): List<UiModel> {
        val leaguesAndTeams = data.following
            .filterNot { it.followableId.type == Followable.Type.AUTHOR }
            .map { topic ->
                topic.toSearchFollowableItem(topic.followableId.listDrawable(data))
            }

        val authors = data.following
            .filter { it.followableId.type == Followable.Type.AUTHOR }
            .map { topic ->
                topic.toSearchFollowableItem(topic.followableId.listDrawable(data))
            }

        return leaguesAndTeams + authors
    }

    @DrawableRes
    private fun FollowableId.listDrawable(data: UserTopicSearchState): Int? {
        return if (data.selectedFollowable == this) {
            R.drawable.ic_check_2_padded
        } else {
            null
        }
    }
}