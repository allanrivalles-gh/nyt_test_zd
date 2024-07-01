package com.theathletic.hub.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentManager
import com.theathletic.hub.HubTabType
import com.theathletic.liveblog.ui.LiveBlogPreviewData.listState
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.asResourceString

val previewDataTeamHubHeader = HubUi.Team(
    teamHeader = HubUi.Team.TeamHeader(
        teamLogos = emptyList(),
        teamName = "Oilers",
        currentStanding = "53-22-7, 2nd in Central",
        backgroundColor = "053d88",
        isFollowed = false
    ),
    showLoadingSpinner = false,
    tabs = previewDataTeamHubTabs,
    tabIndexes = previewDataTeamHubTabIndexes,
    currentTab = HubTabType.Standings
)

val previewDataTeamHubHeader_loadingData = HubUi.Team(
    teamHeader = HubUi.Team.TeamHeader(
        teamLogos = emptyList(),
        teamName = "-",
        currentStanding = "",
        backgroundColor = null,
        isFollowed = false
    ),
    showLoadingSpinner = true,
    tabs = previewDataTeamHubTabs,
    tabIndexes = previewDataTeamHubTabIndexes,
    currentTab = HubTabType.Home
)

private val previewDataTeamHubTabs: List<HubUi.HubTab>
    get() = listOf(
        HubUi.HubTab(
            type = HubTabType.Home,
            label = "Home".asResourceString(),
            module = PreviewTeamHubTabModule
        ),
        HubUi.HubTab(
            type = HubTabType.Schedule,
            label = "Schedule".asResourceString(),
            module = PreviewTeamHubTabModule
        ),
        HubUi.HubTab(
            type = HubTabType.Standings,
            label = "Standings".asResourceString(),
            module = PreviewTeamHubTabModule
        ),
        HubUi.HubTab(
            type = HubTabType.Stats,
            label = "Stats".asResourceString(),
            module = PreviewTeamHubTabModule
        ),
        HubUi.HubTab(
            type = HubTabType.Roster,
            label = "Roster".asResourceString(),
            module = PreviewTeamHubTabModule
        ),
    )

private val previewDataTeamHubTabIndexes: Map<HubTabType, Int> get() = previewDataTeamHubTabs
    .withIndex()
    .associate { it.value.type to it.index }

private object PreviewTeamHubTabModule : HubUi.HubTabModule {
    @Composable
    override fun Render(isActive: Boolean, fragmentManager: () -> FragmentManager) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(AthTheme.colors.dark100)
        ) {
            item {
                for (i in 1..30) {
                    DummyItem("Dummy item - $i")
                }
            }
        }
    }
}

@Composable
private fun DummyItem(label: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = AthTheme.colors.dark800,
            style = AthTextStyle.Calibre.Utility.Medium.Large,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

object PreviewDataTeamHubInteractor : HubUi.Interactor {
    override fun onBackButtonClicked() {}
    override fun onManageFollowClicked() {}
    override fun onManageNotificationsClicked() {}
    override fun onTabClicked(fromTab: HubTabType, toTab: HubTabType) {}
}

object PreviewDataTeamHubFragManager : FragmentManager()