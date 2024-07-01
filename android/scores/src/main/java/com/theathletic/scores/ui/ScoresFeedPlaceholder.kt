package com.theathletic.scores.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.main.ui.FollowableNavigationBar
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.utility.athPlaceholder

@Composable
fun ScoresFeedPlaceholder() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = AthTheme.colors.dark200),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        FollowableNavigationBar(
            navItems = ScoresPlaceholderData.placeholderNavigationItems,
            onEditClick = { },
            showEdit = false,
            onFollowableClick = { _, _ -> },
            showPlaceholder = true
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp)
                .athPlaceholder(visible = true)
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = AthTheme.colors.dark100)
                .height(4.dp)
                .padding(top = 8.dp)
                .athPlaceholder(visible = false)
        )

        LazyRow(
            horizontalArrangement = Arrangement.SpaceEvenly,
            userScrollEnabled = false,
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    bottom = 16.dp
                )
                .fillMaxWidth()
        ) {
            items(10) {
                DayTabPlaceholder()
            }
        }

        FeedListPlaceholder()
    }
}

@Composable
fun FeedListPlaceholder() {
    Column(modifier = Modifier.background(color = AthTheme.colors.dark200)) {
        repeat(4) {
            HeaderPlaceholder()
            repeat(2) {
                GameCellPlaceholder()
            }
        }
    }
}

@Composable
private fun DayTabPlaceholder() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Spacer(
            modifier = Modifier
                .width(16.dp)
                .height(10.dp)
                .athPlaceholder(visible = true)
        )
        Spacer(
            modifier = Modifier
                .width(14.dp)
                .height(10.dp)
                .athPlaceholder(visible = true)
        )
    }
}

@Composable
private fun HeaderPlaceholder() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = AthTheme.colors.dark100)
            .height(4.dp)
            .padding(vertical = 4.dp)
            .athPlaceholder(visible = false)
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp)
            .padding(top = 8.dp)
    ) {
        Spacer(
            modifier = Modifier
                .width(60.dp)
                .height(10.dp)
                .athPlaceholder(visible = true)
        )

        Spacer(
            modifier = Modifier
                .width(40.dp)
                .height(10.dp)
                .athPlaceholder(visible = true)
        )
    }
}

@Composable
private fun GameCellPlaceholder() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(modifier = Modifier.weight(0.7f)) {
            TeamCellPlaceholder()
            TeamCellPlaceholder()
        }

        Divider(
            modifier = Modifier
                .width(1.dp)
                .height(20.dp)
                .padding(horizontal = 8.dp)
                .background(AthTheme.colors.dark300)
        )

        Spacer(
            modifier = Modifier
                .width(60.dp)
                .height(60.dp)
                .athPlaceholder(visible = true)
        )
    }
}

@Composable
private fun TeamCellPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)

    ) {
        Spacer(
            modifier = Modifier
                .width(24.dp)
                .height(24.dp)
                .align(Alignment.CenterStart)
                .athPlaceholder(visible = true)
        )

        Spacer(
            modifier = Modifier
                .width(150.dp)
                .height(18.dp)
                .align(Alignment.CenterStart)
                .padding(start = 28.dp)
                .athPlaceholder(visible = true)
        )

        Spacer(
            modifier = Modifier
                .width(70.dp)
                .height(20.dp)
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .athPlaceholder(visible = true)
        )
    }
}

@Composable
@Preview
fun ScoresFeedPlaceholder_Preview() {
    ScoresFeedPlaceholder()
}

@Composable
@Preview
fun ScoresFeedPlaceholder_PreviewLight() {
    AthleticTheme(lightMode = true) {
        ScoresFeedPlaceholder()
    }
}