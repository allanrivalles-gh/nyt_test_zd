package com.theathletic.boxscore.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.boxscore.ui.modules.SoccerPreviewData.mockTimelineSummaryModule
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asString
import com.theathletic.ui.widgets.ResourceIcon

sealed class TimelineSummaryModel {
    data class ExpectedGoals(
        val firstTeamValue: String = "",
        val secondTeamValue: String = "",
        val showExpectedGoals: Boolean = false
    )

    data class SummaryItem(
        val icon: Int,
        val firstTeam: List<DisplayStrings>,
        val secondTeam: List<DisplayStrings>
    )

    data class DisplayStrings(
        val strings: List<ResourceString>
    )
}

@Composable
fun TimelineSummary(
    expectedGoals: TimelineSummaryModel.ExpectedGoals,
    timelineSummaryItems: List<TimelineSummaryModel.SummaryItem>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        if (expectedGoals.showExpectedGoals) {
            ExpectedGoalsRow(expectedGoals.firstTeamValue, expectedGoals.secondTeamValue)
        }
        timelineSummaryItems.forEach {
            TimelineRow(icon = it.icon, firstTeam = it.firstTeam, secondTeam = it.secondTeam)
        }

        BoxScoreFooterDivider(false)
    }
}

@Composable
private fun TimelineRow(
    icon: Int,
    firstTeam: List<TimelineSummaryModel.DisplayStrings>,
    secondTeam: List<TimelineSummaryModel.DisplayStrings>
) {
    Spacer(modifier = Modifier.height(16.dp))

    EventRowSlot(
        firstValue = {
            Column() {
                firstTeam.forEach {
                    Text(
                        text = it.strings.asString(ignoreFirstSeparator = true),
                        style = AthTextStyle.Calibre.Utility.Regular.Small,
                        color = AthTheme.colors.dark800,
                        textAlign = TextAlign.End,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        event = {
            ResourceIcon(
                resourceId = icon,
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .padding(top = 4.dp)
                    .size(18.dp)
            )
        },
        secondValue = {
            Column() {
                secondTeam.forEach {
                    Text(
                        text = it.strings.asString(ignoreFirstSeparator = true),
                        style = AthTextStyle.Calibre.Utility.Regular.Small,
                        color = AthTheme.colors.dark800,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        verticalAlignment = Alignment.Top
    )
}

@Composable
private fun ExpectedGoalsRow(
    firstTeamGoals: String,
    secondTeamGoals: String
) {
    EventRowSlot(
        firstValue = {
            Text(
                text = firstTeamGoals,
                style = AthTextStyle.Calibre.Utility.Regular.Small,
                color = AthTheme.colors.dark800,
            )
        },
        event = {
            Text(
                text = stringResource(id = R.string.box_score_expected_goals_label),
                style = AthTextStyle.Calibre.Utility.Regular.Small,
                color = AthTheme.colors.dark800,
                modifier = Modifier
                    .clip(RoundedCornerShape(2.dp))
                    .background(color = AthTheme.colors.dark300)
                    .padding(vertical = 2.dp, horizontal = 6.dp)
            )
        },
        secondValue = {
            Text(
                text = secondTeamGoals,
                style = AthTextStyle.Calibre.Utility.Regular.Small,
                color = AthTheme.colors.dark800,
            )
        },
        verticalAlignment = Alignment.CenterVertically
    )
}

@Composable
private fun EventRowSlot(
    firstValue: @Composable BoxScope.() -> Unit,
    event: @Composable BoxScope.() -> Unit,
    secondValue: @Composable BoxScope.() -> Unit,
    verticalAlignment: Alignment.Vertical
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = verticalAlignment,
        horizontalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier.weight(0.4f),
            contentAlignment = Alignment.CenterEnd
        ) { firstValue() }
        Box(
            modifier = Modifier.weight(0.2f),
            contentAlignment = Alignment.Center
        ) { event() }
        Box(
            modifier = Modifier.weight(0.4f),
            contentAlignment = Alignment.CenterStart
        ) { secondValue() }
    }
}

@Preview
@Composable
private fun TimelineSummary_Preview() {
    TimelineSummary(
        expectedGoals = mockTimelineSummaryModule.expectedGoals,
        timelineSummaryItems = mockTimelineSummaryModule.timelineSummary
    )
}

@Preview(device = Devices.PIXEL)
@Composable
private fun TimelineSummary_PreviewSmallDevice() {
    TimelineSummary(
        expectedGoals = mockTimelineSummaryModule.expectedGoals,
        timelineSummaryItems = mockTimelineSummaryModule.timelineSummary
    )
}

@Preview(device = Devices.PIXEL_4_XL)
@Composable
private fun TimelineSummary_PreviewLargeDevice() {
    TimelineSummary(
        expectedGoals = mockTimelineSummaryModule.expectedGoals,
        timelineSummaryItems = mockTimelineSummaryModule.timelineSummary
    )
}

@Preview
@Composable
private fun TimelineSummary_PreviewLight() {
    AthleticTheme(lightMode = true) {
        TimelineSummary(
            expectedGoals = mockTimelineSummaryModule.expectedGoals,
            timelineSummaryItems = mockTimelineSummaryModule.timelineSummary
        )
    }
}