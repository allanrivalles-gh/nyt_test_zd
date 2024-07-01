package com.theathletic.feed.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.theathletic.main.ui.FollowableNavigationBar
import com.theathletic.main.ui.NavigationItems
import com.theathletic.themes.AthTheme
import com.theathletic.ui.preview.AthleticThemeProvider
import com.theathletic.ui.preview.PreviewContent
import com.theathletic.ui.utility.athPlaceholder

@Composable
fun FeedListPlaceholder() {
    Column {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            userScrollEnabled = false,
            modifier = Modifier
                .padding(top = 4.dp, bottom = 4.dp)
        ) {
            items(3) {
                FeedScoresPlaceholder()
            }
        }
        LazyColumn(
            userScrollEnabled = false,
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        ) {
            item {
                FeedHeroPlaceholder()
            }
            items(3) {
                FeedItemPlaceholder()
            }
        }
    }
}

@Composable
private fun FeedScoresPlaceholder() {
    LazyColumn(
        userScrollEnabled = false,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .background(color = AthTheme.colors.dark200)
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        items(2) {
            Row {
                Spacer(
                    modifier = Modifier
                        .size(16.dp)
                        .athPlaceholder(
                            visible = true,
                            shape = CircleShape
                        )
                )
                Spacer(
                    modifier = Modifier
                        .size(width = 30.dp, height = 16.dp)
                        .padding(start = 8.dp)
                        .athPlaceholder(visible = true)
                )
                Spacer(
                    modifier = Modifier
                        .size(width = 30.dp, height = 16.dp)
                        .padding(start = 16.dp)
                        .athPlaceholder(visible = true)
                )
                Spacer(
                    modifier = Modifier
                        .size(width = 50.dp, height = 16.dp)
                        .padding(start = 16.dp)
                        .athPlaceholder(visible = true)
                )
            }
        }
    }
}

@Composable
private fun FeedHeroPlaceholder() {
    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .background(color = AthTheme.colors.dark200)
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .athPlaceholder(visible = true, shape = RectangleShape)
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .padding(horizontal = 16.dp)
                .athPlaceholder(visible = true)
        )
        Spacer(
            modifier = Modifier.height(16.dp)
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(30.dp)
                .padding(horizontal = 16.dp)
                .athPlaceholder(visible = true)
        )
        Spacer(
            modifier = Modifier.height(16.dp)
        )
    }
}

@Composable
private fun FeedItemPlaceholder() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(color = AthTheme.colors.dark200)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(0.7f)
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(24.dp)
                    .padding(horizontal = 16.dp)
                    .athPlaceholder(visible = true)
            )
            Spacer(
                modifier = Modifier.height(16.dp)
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(20.dp)
                    .padding(horizontal = 16.dp)
                    .athPlaceholder(visible = true)
            )
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .weight(0.4f)
        ) {
            Spacer(
                modifier = Modifier
                    .size(width = 150.dp, height = 100.dp)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .athPlaceholder(visible = true, shape = RectangleShape)
            )
        }
    }
}

@Preview
@Composable
private fun ArticlesPlaceholderPreview(
    @PreviewParameter(AthleticThemeProvider::class)
    athleticTheme: PreviewContent,
) {
    athleticTheme {
        Column {
            FollowableNavigationBar(
                navItems = NavigationItems.placeholderNavigationItems,
                onEditClick = { },
                onFollowableClick = { _, _ -> },
                showPlaceholder = true
            )
            FeedListPlaceholder()
        }
    }
}