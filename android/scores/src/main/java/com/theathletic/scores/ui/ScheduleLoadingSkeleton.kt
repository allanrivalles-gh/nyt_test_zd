package com.theathletic.scores.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.utility.athPlaceholder

@Composable
fun ScheduleLoadingSkeleton(includeNavBar: Boolean = false) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AthTheme.colors.dark100)
    ) {
        if (includeNavBar) ScheduleNavBarSkeleton()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AthTheme.colors.dark200),
        ) {
            repeat(times = 12) { ScheduleGameSkeleton() }
        }
    }
}

@Composable
private fun ScheduleNavBarSkeleton() {
    Column {
        Spacer(modifier = Modifier.height(4.dp))
        LazyRow(modifier = Modifier.fillMaxWidth()) {
            items(count = 10) {
                Column(
                    modifier = Modifier
                        .height(44.dp)
                        .background(AthTheme.colors.dark200)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(
                        modifier = Modifier
                            .width(24.dp)
                            .height(14.dp)
                            .athPlaceholder(visible = true)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Spacer(
                        modifier = Modifier
                            .width(36.dp)
                            .height(10.dp)
                            .athPlaceholder(visible = true)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun ScheduleGameSkeleton() {
    Box(
        modifier = Modifier
            .height(82.dp)
            .padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(0.6f)) {
                ScheduleTeamSkeleton()
                Spacer(modifier = Modifier.height(8.dp))
                ScheduleTeamSkeleton()
            }
            ScheduleGameInfoSkeleton(modifier = Modifier.weight(0.4f))
        }
        Spacer(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(AthTheme.colors.dark300)
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun ScheduleTeamSkeleton() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(
            modifier = Modifier
                .width(24.dp)
                .height(24.dp)
                .athPlaceholder(visible = true)
        )
        Spacer(
            modifier = Modifier
                .width(100.dp)
                .height(16.dp)
                .padding(start = 8.dp)
                .athPlaceholder(visible = true)
        )
    }
}

@Composable
private fun ScheduleGameInfoSkeleton(modifier: Modifier) {
    Box(
        modifier = Modifier
            .height(82.dp)
            .then(modifier)
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
                .padding(vertical = 12.dp)
                .background(AthTheme.colors.dark300)
                .align(Alignment.CenterStart)
        )
        Column(
            modifier = Modifier
                .padding(start = 12.dp)
                .align(Alignment.CenterStart),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(
                modifier = Modifier
                    .width(30.dp)
                    .height(12.dp)
                    .athPlaceholder(visible = true)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Spacer(
                modifier = Modifier
                    .width(40.dp)
                    .height(12.dp)
                    .athPlaceholder(visible = true)
            )
        }
    }
}

@Preview
@Composable
private fun ScheduleLoadingSkeletonPreview_WithNavBar() {
    ScheduleLoadingSkeleton(includeNavBar = true)
}

@Preview
@Composable
private fun ScheduleLoadingSkeletonPreview_WithNavBar_Light() {
    AthleticTheme(lightMode = true) {
        ScheduleLoadingSkeleton(includeNavBar = true)
    }
}