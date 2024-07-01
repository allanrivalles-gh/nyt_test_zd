package com.theathletic.onboarding.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.followables.data.domain.getImageUrl
import com.theathletic.ui.Transformer
import com.theathletic.utility.LogoUtility

class OnboardingTransformer @AutoKoin constructor() :
    Transformer<OnboardingDataState, OnboardingContract.OnboardingViewState> {

    override fun transform(data: OnboardingDataState): OnboardingContract.OnboardingViewState {
        return OnboardingContract.OnboardingViewState(
            isLoading = data.isLoading,
            onboardingStep = data.onboardingStep,
            selectedTeamsGroupIndex = data.teamGroupIndex,
            teamsGroups = emptyList(),
            searchItems = data.getSearchItems(),
            chosenItems = data.getChosenItems(),
            searchText = data.searchText,
            errorState = data.errorState
        )
    }

    private fun OnboardingDataState.getSearchItems(): List<OnboardingUi.OnboardingItem> {
        return if (onboardingStep == OnboardingUi.OnboardingStep.Podcasts) getPodcasts() else getFollowableItems()
    }

    private fun OnboardingDataState.getFollowableItems(): List<OnboardingUi.OnboardingItem> {
        return if (
            searchText.isEmpty() &&
            onboardingStep == OnboardingUi.OnboardingStep.Teams
        ) recommendedTeams.map { team ->
            OnboardingUi.OnboardingItem.FollowableItemUi(
                id = team.id.toString(),
                name = team.name,
                imageUrl = team.getImageUrl(LogoUtility),
                isChosen = chosenFollowables.any { it.followableId == team.id }
            )
        }
        else {
            followableItems.map { followableItem ->
                OnboardingUi.OnboardingItem.FollowableItemUi(
                    id = followableItem.followableId.toString(),
                    name = followableItem.name,
                    imageUrl = followableItem.imageUrl,
                    isChosen = chosenFollowables.any { it.followableId == followableItem.followableId }
                )
            }
        }
    }

    private fun OnboardingDataState.getPodcasts(): List<OnboardingUi.OnboardingItem.OnboardingPodcastItem> {
        return podcasts.map { podcast ->
            OnboardingUi.OnboardingItem.OnboardingPodcastItem(
                id = podcast.id,
                name = podcast.title,
                imageUrl = podcast.imageUrl.orEmpty(),
                isFollowing = chosenPodcasts.any { it.id == podcast.id },
                topicLabel = podcast.metadataString.orEmpty(),
                isLoading = loadingPodcastIds.any { it == podcast.id }
            )
        }
    }

    private fun OnboardingDataState.getChosenItems(): List<OnboardingUi.OnboardingItem> {
        val teamsAndLeagues = chosenFollowables.map { item ->
            OnboardingUi.OnboardingItem.FollowableItemUi(
                id = item.followableId.toString(),
                name = item.name,
                imageUrl = item.imageUrl,
                isChosen = true
            )
        }

        val podcasts = chosenPodcasts.map { podcast ->
            OnboardingUi.OnboardingItem.OnboardingPodcastItem(
                id = podcast.id,
                name = podcast.title,
                imageUrl = podcast.imageUrl.orEmpty(),
                topicLabel = podcast.metadataString.orEmpty(),
                isFollowing = true,
                isLoading = false
            )
        }
        return teamsAndLeagues + podcasts
    }
}