package com.theathletic.onboarding.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthColor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.R
import com.theathletic.ui.widgets.RemoteImage
import com.theathletic.ui.widgets.ResourceIcon

@Composable
fun OnboardingListItem(
    item: OnboardingUi.OnboardingItem,
    interactor: OnboardingUi.Interactor
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable {
                when (item) {
                    is OnboardingUi.OnboardingItem.FollowableItemUi -> interactor.onFollowableClick(item.id)
                    is OnboardingUi.OnboardingItem.OnboardingPodcastItem -> interactor.onPodcastClick(item.id)
                }
            }
            .padding(vertical = 8.dp, horizontal = 6.dp)
    ) {
        if (item is OnboardingUi.OnboardingItem.OnboardingPodcastItem) {
            PodcastImage(item = item)
        } else {
            TeamLeagueImage(item = item)
        }

        Text(
            text = item.name,
            style = AthTextStyle.Calibre.Utility.Medium.Large,
            color = if (item.isFollowing) AthTheme.colors.dark800 else AthTheme.colors.dark500,
            maxLines = when (item) {
                is OnboardingUi.OnboardingItem.OnboardingPodcastItem -> 1
                is OnboardingUi.OnboardingItem.FollowableItemUi -> Int.MAX_VALUE
            },
            overflow = TextOverflow.Ellipsis,
            textAlign = when (item) {
                is OnboardingUi.OnboardingItem.OnboardingPodcastItem -> TextAlign.Start
                is OnboardingUi.OnboardingItem.FollowableItemUi -> TextAlign.Center
            },
            modifier = Modifier.padding(top = 16.dp)
        )

        if (item is OnboardingUi.OnboardingItem.OnboardingPodcastItem) {
            Text(
                text = item.topicLabel,
                style = AthTextStyle.Calibre.Utility.Medium.Large,
                color = AthTheme.colors.dark500,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun TeamLeagueImage(
    item: OnboardingUi.OnboardingItem
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(AthTheme.colors.dark300)
            .border(
                width = 2.dp,
                color = if (item.isFollowing) AthTheme.colors.dark800 else AthTheme.colors.dark300,
                shape = CircleShape
            )
    ) {
        RemoteImage(
            url = item.imageUrl,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .align(Alignment.Center)
        )
    }
}

@Composable
private fun PodcastImage(
    item: OnboardingUi.OnboardingItem.OnboardingPodcastItem
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(4.dp))
    ) {
        RemoteImage(
            url = item.imageUrl,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
        )

        if (item.isFollowing || item.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xCC000000))
            )

            if (item.isLoading) {
                CircularProgressIndicator(
                    color = AthTheme.colors.dark700,
                    strokeWidth = 2.dp,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                ResourceIcon(
                    resourceId = R.drawable.ic_check,
                    tint = AthColor.Gray800,
                    modifier = Modifier
                        .size(28.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun OnboardingFollowedListItem(
    item: OnboardingUi.OnboardingItem,
    interactor: OnboardingUi.Interactor
) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clickable {
                when (item) {
                    is OnboardingUi.OnboardingItem.FollowableItemUi -> interactor.onFollowableClick(item.id)
                    is OnboardingUi.OnboardingItem.OnboardingPodcastItem -> interactor.onPodcastClick(item.id)
                }
            }
    ) {
        if (item is OnboardingUi.OnboardingItem.OnboardingPodcastItem) {
            RemoteImage(
                url = item.imageUrl,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(AthTheme.colors.dark300)
            )
            RemoteImage(
                url = item.imageUrl,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Center)
            )
        }

        ResourceIcon(
            resourceId = R.drawable.ic_circle_minus_red,
            modifier = Modifier
                .size(20.dp)
                .align(Alignment.TopEnd)
        )
    }
}