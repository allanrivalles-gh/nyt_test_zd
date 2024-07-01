package com.theathletic.scores.ui.gamecells

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.scores.R
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.widgets.ResourceIcon

@Composable
fun SectionHeader(
    id: String,
    title: String?,
    subTitle: String?,
    canNavigate: Boolean,
    leagueId: Long?,
    index: Int,
    onClick: (Long, Int) -> Unit
) {
    Box(
        modifier = Modifier
            .background(AthTheme.colors.dark200)
            .clickable(enabled = leagueId != null) { leagueId?.let { onClick(it, index) } }
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .background(AthTheme.colors.dark200)
                .align(Alignment.CenterStart)
        ) {
            Text(
                text = title.orEmpty(),
                style = AthTextStyle.Slab.Bold.Small,
                color = AthTheme.colors.dark800,
            )
            if (subTitle.isNullOrBlank().not()) {
                Text(
                    text = subTitle.orEmpty(),
                    style = AthTextStyle.Calibre.Utility.Regular.Small,
                    color = AthTheme.colors.dark500,
                )
            }
        }
        if (canNavigate) {
            ResourceIcon(
                resourceId = R.drawable.ic_chalk_chevron_right,
                tint = AthTheme.colors.dark800,
                modifier = Modifier
                    .size(16.dp)
                    .padding(end = 6.dp)
                    .align(Alignment.CenterEnd)
            )
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(AthTheme.colors.dark300)
                .align(Alignment.BottomCenter)
        )
    }
}

@Preview
@Composable
private fun SectionHeader_FollowingPreview() {
    SectionHeader(
        id = "uniqueId",
        title = "Following",
        subTitle = null,
        canNavigate = false,
        leagueId = null,
        index = 0,
        onClick = { _, _ -> }
    )
}

@Preview
@Composable
private fun SectionHeader_LeaguePreview() {
    SectionHeader(
        id = "uniqueId",
        title = "NFL",
        subTitle = "10 games today",
        canNavigate = true,
        leagueId = 2,
        index = 0,
        onClick = { _, _ -> }
    )
}

@Preview
@Composable
private fun SectionHeader_LeagueNoSubTextPreview() {
    SectionHeader(
        id = "uniqueId",
        title = "NFL",
        subTitle = null,
        canNavigate = true,
        leagueId = 2,
        index = 0,
        onClick = { _, _ -> }
    )
}

@Preview
@Composable
private fun SectionHeader_Light_FollowingPreview() {
    AthleticTheme(lightMode = true) {
        SectionHeader(
            id = "uniqueId",
            title = "Following",
            subTitle = null,
            canNavigate = false,
            leagueId = null,
            index = 0,
            onClick = { _, _ -> }
        )
    }
}

@Preview
@Composable
private fun SectionHeader_Light_LeaguePreview() {
    AthleticTheme(lightMode = true) {
        SectionHeader(
            id = "uniqueId",
            title = "NFL",
            subTitle = "10 games today",
            canNavigate = true,
            leagueId = 2,
            index = 0,
            onClick = { _, _ -> }
        )
    }
}

@Preview
@Composable
private fun SectionHeader_Light_LeagueNoSubTextPreview() {
    AthleticTheme(lightMode = true) {
        SectionHeader(
            id = "uniqueId",
            title = "NFL",
            subTitle = null,
            canNavigate = true,
            leagueId = 2,
            index = 0,
            onClick = { _, _ -> }
        )
    }
}