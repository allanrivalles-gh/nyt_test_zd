package com.theathletic.scores.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.followable.Followable
import com.theathletic.main.ui.FollowableNavigationBar
import com.theathletic.main.ui.NavigationItem
import com.theathletic.scores.ui.search.SearchTextField
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme

@Composable
fun ScoresPageHeader(
    modifier: Modifier = Modifier,
    navItems: List<NavigationItem>,
    onLeagueItemClick: (Followable.Id, Int) -> Unit
) {
    Column(modifier = Modifier.then(modifier)) {
        FollowableNavigationBar(
            navItems = navItems,
            showEdit = false,
            bottomPadding = 0.dp,
            onEditClick = { /* Not Supported */ },
            onFollowableClick = { id, index -> onLeagueItemClick(id, index) }
        )
    }
}

@Composable
fun ScoreSearchBar(
    onSearchBarClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = AthTheme.colors.dark200
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .then(modifier)
    ) {
        SearchTextField(
            searchText = "",
            isEnable = false,
            onClick = { onSearchBarClick() },
            onUpdateSearchText = {}
        )
    }
}

@Composable
@Preview
private fun ScoresPageHeaderPreview_Dark() {
    ScoresPageHeader(
        navItems = ScorePreviewData.scoreHeaderLeagueData(),
        onLeagueItemClick = { _, _ -> }
    )
}

@Composable
@Preview
private fun ScoresPageHeaderPreview_Light() {
    AthleticTheme(lightMode = true) {
        ScoresPageHeader(
            navItems = ScorePreviewData.scoreHeaderLeagueData(),
            onLeagueItemClick = { _, _ -> }
        )
    }
}