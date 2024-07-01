package com.theathletic.boxscore.ui.playbyplay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.theathletic.data.SizedImages
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.R
import com.theathletic.ui.widgets.Headshot

@Composable
internal fun HockeyShootoutPlay(
    headshots: SizedImages,
    teamLogos: SizedImages,
    teamColor: Color,
    playerName: String,
    teamAlias: String,
    description: String,
    isGoal: Boolean,
    showDivider: Boolean
) {
    Column(
        modifier = Modifier
            .background(AthTheme.colors.dark200)
            .fillMaxWidth()
            .padding(
                top = 12.dp,
                start = 16.dp,
                end = 16.dp
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Headshot(
                headshotsUrls = headshots,
                teamUrls = teamLogos,
                teamColor = teamColor,
                preferredSize = 24.dp,
                modifier = Modifier.size(24.dp)
            )
            Row {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    PlayerDetails(playerName, teamAlias, description)
                }
                Text(
                    text = stringResource(
                        id = if (isGoal) {
                            R.string.box_score_hockey_shootout_result_goal
                        } else {
                            R.string.box_score_hockey_shootout_result_save
                        }
                    ).uppercase(),
                    style = AthTextStyle.Calibre.Utility.Medium.ExtraSmall,
                    color = if (isGoal) {
                        AthTheme.colors.green
                    } else {
                        AthTheme.colors.dark700
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
        ShowDivider(showDivider)
    }
}

@Composable
private fun ColumnScope.PlayerDetails(playerName: String, teamAlias: String, description: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = playerName,
            style = AthTextStyle.Calibre.Utility.Medium.Large,
            color = AthTheme.colors.dark700
        )
        Text(
            text = teamAlias,
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            color = AthTheme.colors.dark500,
            modifier = Modifier.padding(start = 6.dp)
        )
    }
    Text(
        text = description,
        style = AthTextStyle.Calibre.Utility.Regular.Small,
        color = AthTheme.colors.dark500,
    )
}

@Composable
private fun ShowDivider(showDivider: Boolean) {
    if (showDivider) {
        Divider(
            color = AthTheme.colors.dark300,
            thickness = 1.dp
        )
    }
}