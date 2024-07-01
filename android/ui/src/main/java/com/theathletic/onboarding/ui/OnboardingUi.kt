package com.theathletic.onboarding.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.R
import com.theathletic.ui.widgets.ResourceIcon
import com.theathletic.ui.widgets.SearchTextField

class OnboardingUi {
    sealed class OnboardingItem(
        val id: String,
        val name: String,
        val imageUrl: String,
        val isFollowing: Boolean
    ) {
        class FollowableItemUi(
            id: String,
            name: String,
            imageUrl: String,
            isChosen: Boolean
        ) : OnboardingItem(id, name, imageUrl, isChosen)

        class OnboardingPodcastItem(
            id: String,
            name: String,
            imageUrl: String,
            isFollowing: Boolean,
            val topicLabel: String,
            val isLoading: Boolean
        ) : OnboardingItem(id, name, imageUrl, isFollowing)
    }

    data class OnboardingTeamsGroup(
        val title: String,
        val isSelected: Boolean
    )

    sealed class OnboardingStep {
        object Teams : OnboardingStep()
        object Leagues : OnboardingStep()
        object Podcasts : OnboardingStep()
    }

    sealed interface ErrorState {
        object NetworkErrorLoadingData : ErrorState
    }

    interface Interactor {
        fun onFollowableClick(id: String)
        fun onPodcastClick(id: String)
        fun onSearchUpdated(searchText: String)
        fun onTeamGroupSelected(index: Int)
        fun onNextClick()
        fun onBackClick()
    }
}

@Composable
fun OnboardingScreen(
    isLoading: Boolean,
    onboardingStep: OnboardingUi.OnboardingStep,
    selectedTeamGroupIndex: Int,
    teamsGroups: List<OnboardingUi.OnboardingTeamsGroup>,
    searchItems: List<OnboardingUi.OnboardingItem>,
    followedItems: List<OnboardingUi.OnboardingItem>,
    searchText: String,
    errorState: OnboardingUi.ErrorState? = null,
    interactor: OnboardingUi.Interactor
) {
    Column(modifier = Modifier.background(AthTheme.colors.dark200)) {
        OnboardingToolbar(
            onboardingStep = onboardingStep,
            isNextEnabled = followedItems.isNotEmpty() && !isLoading,
            interactor = interactor
        )

        if (onboardingStep != OnboardingUi.OnboardingStep.Podcasts) {
            SearchBar(
                onboardingStep = onboardingStep,
                searchText = searchText,
                interactor = interactor
            )
        }

        if (errorState != null) {
            ErrorBar(
                errorState = errorState,
                modifier = Modifier.align(CenterHorizontally)
            )
        }

        if (onboardingStep == OnboardingUi.OnboardingStep.Teams && searchText.isEmpty()) {
            RecommendedTeamsTabs(
                selectedTeamGroupIndex = selectedTeamGroupIndex,
                teamsGroups = teamsGroups,
                interactor = interactor
            )
        }

        ListTopper(onboardingStep = onboardingStep)

        if (isLoading) {
            LoadingIndicator()
        } else {
            OnboardingTopicsList(items = searchItems, interactor = interactor)
        }

        FollowTray(followedItems = followedItems, interactor = interactor)
    }
}

@Composable
private fun OnboardingToolbar(
    onboardingStep: OnboardingUi.OnboardingStep,
    isNextEnabled: Boolean,
    interactor: OnboardingUi.Interactor
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp)
    ) {
        IconButton(onClick = interactor::onBackClick) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = null,
                tint = AthTheme.colors.dark800
            )
        }

        Text(
            text = when (onboardingStep) {
                is OnboardingUi.OnboardingStep.Teams -> stringResource(id = R.string.onboarding_team_title)
                is OnboardingUi.OnboardingStep.Leagues -> stringResource(id = R.string.onboarding_league_title)
                is OnboardingUi.OnboardingStep.Podcasts -> stringResource(id = R.string.onboarding_podcast_title)
            },
            style = AthTextStyle.Slab.Bold.Small,
            color = AthTheme.colors.dark800
        )

        TextButton(
            onClick = interactor::onNextClick,
            enabled = isNextEnabled
        ) {
            Text(
                text = stringResource(id = R.string.onboarding_toolbar_next),
                style = AthTextStyle.Navigation,
                color = if (isNextEnabled) AthTheme.colors.dark800 else AthTheme.colors.dark500
            )
        }
    }
}

@Composable
private fun SearchBar(
    onboardingStep: OnboardingUi.OnboardingStep,
    searchText: String,
    interactor: OnboardingUi.Interactor
) {
    SearchTextField(
        searchText = searchText,
        placeholderRes = when (onboardingStep) {
            is OnboardingUi.OnboardingStep.Teams -> R.string.onboarding_search_teams_hint
            else -> R.string.onboarding_search_leagues_hint
        },
        leadingIconRes = R.drawable.ic_nav2_search,
        trailingIconRes = R.drawable.ic_x,
        onUpdateSearchText = { interactor.onSearchUpdated(it) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun ErrorBar(errorState: OnboardingUi.ErrorState, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(color = AthTheme.colors.dark300)
    ) {
        ResourceIcon(
            modifier = Modifier.padding(8.dp),
            resourceId = R.drawable.ic_alert_red
        )
        val errorString = when (errorState) {
            is OnboardingUi.ErrorState.NetworkErrorLoadingData -> stringResource(id = R.string.onboarding_error_loading_data)
        }
        Text(
            text = errorString,
            color = AthTheme.colors.dark800,
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.width(8.dp))
    }
}

@Composable
private fun RecommendedTeamsTabs(
    selectedTeamGroupIndex: Int,
    teamsGroups: List<OnboardingUi.OnboardingTeamsGroup>,
    interactor: OnboardingUi.Interactor
) {
    if (teamsGroups.size <= 2) return
    TabRow(
        selectedTabIndex = selectedTeamGroupIndex,
        backgroundColor = AthTheme.colors.dark200,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                color = AthTheme.colors.dark500,
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTeamGroupIndex])
            )
        }
    ) {
        teamsGroups.forEachIndexed { index, group ->
            Tab(selected = group.isSelected, onClick = { interactor.onTeamGroupSelected(index) }) {
                Text(
                    text = group.title,
                    style = AthTextStyle.Calibre.Utility.Regular.Large,
                    color = if (group.isSelected) AthTheme.colors.dark800 else AthTheme.colors.dark500,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.LoadingIndicator() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
    ) {
        CircularProgressIndicator(
            color = AthTheme.colors.dark700,
            strokeWidth = 2.dp
        )
    }
}

@Composable
private fun ListTopper(onboardingStep: OnboardingUi.OnboardingStep) {
    if (onboardingStep != OnboardingUi.OnboardingStep.Teams) {
        Text(
            text = when (onboardingStep) {
                is OnboardingUi.OnboardingStep.Leagues -> stringResource(id = R.string.onboarding_title_league_follow)
                else -> stringResource(id = R.string.onboarding_title_podcast_follow)
            },
            style = AthTextStyle.Calibre.Utility.Regular.Large,
            color = AthTheme.colors.dark600,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
    }
}

@Composable
private fun ColumnScope.OnboardingTopicsList(
    items: List<OnboardingUi.OnboardingItem>,
    interactor: OnboardingUi.Interactor
) {
    if (items.isEmpty()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                text = stringResource(id = R.string.onboarding_team_search_no_results),
                style = AthTextStyle.Calibre.Utility.Regular.Large,
                color = AthTheme.colors.dark500
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(integerResource(id = R.integer.onboarding_pick_user_topics_column_count)),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 6.dp)
        ) {
            items(count = items.size) { index ->
                OnboardingListItem(item = items[index], interactor = interactor)
            }
        }
    }
}

@Composable
private fun FollowTray(
    followedItems: List<OnboardingUi.OnboardingItem>,
    interactor: OnboardingUi.Interactor
) {
    Column {
        Divider(
            color = AthTheme.colors.dark300,
            thickness = 1.dp
        )

        Text(
            text = stringResource(id = R.string.onboarding_following_bar_title),
            style = AthTextStyle.Calibre.Utility.Regular.Large,
            color = AthTheme.colors.dark500,
            modifier = Modifier.padding(top = 8.dp, start = 16.dp, bottom = 16.dp)
        )

        if (followedItems.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(followedItems) { item ->
                    OnboardingFollowedListItem(item = item, interactor = interactor)
                }
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ResourceIcon(
                    resourceId = R.drawable.shape_onboarding_topic_background_empty,
                    modifier = Modifier
                        .size(64.dp)
                        .padding(start = 16.dp)
                )

                Text(
                    text = stringResource(id = R.string.onboarding_following_bar_empty),
                    style = AthTextStyle.Calibre.Utility.Regular.Large,
                    color = AthTheme.colors.dark500,
                    modifier = Modifier.padding(start = 20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}