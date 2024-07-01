package com.theathletic.onboarding.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.theathletic.themes.AthleticTheme

@Preview
@Composable
private fun Preview_OnboardingUi_DarkMode() {
    AthleticTheme(lightMode = false) {
        OnboardingScreen(
            isLoading = false,
            onboardingStep = OnboardingUi.OnboardingStep.Teams,
            selectedTeamGroupIndex = 1,
            teamsGroups = List(3) { getTeamTab(it, it == 1) },
            searchItems = List(8) { getTopicItem(it) },
            followedItems = emptyList(),
            searchText = "",
            interactor = previewInteractor
        )
    }
}

@Preview
@Composable
private fun Preview_OnboardingUi_LightMode() {
    AthleticTheme(lightMode = true) {
        OnboardingScreen(
            isLoading = false,
            onboardingStep = OnboardingUi.OnboardingStep.Teams,
            selectedTeamGroupIndex = 0,
            teamsGroups = emptyList(),
            searchItems = List(8) { getTopicItem(it) },
            followedItems = emptyList(),
            searchText = "",
            interactor = previewInteractor
        )
    }
}

@Preview
@Composable
private fun Preview_OnboardingUi_Podcasts_DarkMode() {
    AthleticTheme(lightMode = false) {
        OnboardingScreen(
            isLoading = false,
            onboardingStep = OnboardingUi.OnboardingStep.Podcasts,
            selectedTeamGroupIndex = 0,
            teamsGroups = emptyList(),
            searchItems = List(8) { getPodcastItem(it) },
            followedItems = List(2) { getTopicItem(it) } + List(1) { getPodcastItem(it) },
            searchText = "",
            interactor = previewInteractor
        )
    }
}

@Preview
@Composable
private fun Preview_OnboardingUi_Podcasts_Error_DarkMode() {
    AthleticTheme(lightMode = false) {
        OnboardingScreen(
            isLoading = false,
            onboardingStep = OnboardingUi.OnboardingStep.Podcasts,
            selectedTeamGroupIndex = 0,
            teamsGroups = emptyList(),
            searchItems = emptyList(),
            followedItems = List(1) { getTopicItem(it) },
            searchText = "",
            errorState = OnboardingUi.ErrorState.NetworkErrorLoadingData,
            interactor = previewInteractor
        )
    }
}

private fun getTopicItem(index: Int) = OnboardingUi.OnboardingItem.FollowableItemUi(
    id = "item $index",
    name = "Team $index",
    imageUrl = "",
    isChosen = index % 2 == 0
)

private fun getPodcastItem(index: Int) = OnboardingUi.OnboardingItem.OnboardingPodcastItem(
    id = "item $index",
    name = "Podcast $index",
    imageUrl = "",
    isFollowing = index % 2 == 0,
    topicLabel = "NFL",
    isLoading = false
)

private fun getTeamTab(
    index: Int,
    isSelected: Boolean
) = OnboardingUi.OnboardingTeamsGroup(
    title = "Tab $index",
    isSelected = isSelected
)

private val previewInteractor = object : OnboardingUi.Interactor {
    override fun onFollowableClick(id: String) {}
    override fun onPodcastClick(id: String) {}
    override fun onSearchUpdated(searchText: String) {}
    override fun onTeamGroupSelected(index: Int) {}
    override fun onNextClick() {}
    override fun onBackClick() {}
}