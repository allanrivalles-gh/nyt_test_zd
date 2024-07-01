package com.theathletic.boxscore.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.boxscore.ui.modules.SoccerPreviewData.mockKeyMoments
import com.theathletic.boxscore.ui.playbyplay.SoccerEventPlay
import com.theathletic.boxscore.ui.playbyplay.SoccerGoalPlay
import com.theathletic.boxscore.ui.playbyplay.SoccerStandardPlay
import com.theathletic.data.SizedImages
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.FeedModule
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R

sealed class SoccerMomentsUi {

    abstract val id: String
    abstract val description: String
    abstract val headerLabel: String
    abstract val clock: String
    abstract val teamLogos: SizedImages

    data class StandardSoccerMoment(
        override val clock: String,
        override val description: String,
        override val headerLabel: String,
        override val id: String,
        override val teamLogos: SizedImages,
    ) : SoccerMomentsUi()

    data class ScoringSoccerMoment(
        override val clock: String,
        override val description: String,
        override val headerLabel: String,
        override val id: String,
        override val teamLogos: SizedImages,
        val awayTeamScore: String,
        val homeTeamScore: String,
        val awayTeamName: String,
        val homeTeamName: String,
        val teamColor: String?,
    ) : SoccerMomentsUi()

    data class EventSoccerMoment(
        override val clock: String,
        override val description: String,
        override val headerLabel: String,
        override val id: String,
        override val teamLogos: SizedImages,
        @DrawableRes val iconRes: Int,
    ) : SoccerMomentsUi()

    interface Interaction {
        object OnFullTimeLineClick : FeedInteraction
    }
}

@Composable
fun KeyMoments(
    keyMoments: List<SoccerMomentsUi>,
    soccerPenaltyShootoutModule: FeedModule? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = AthTheme.colors.dark200)
    ) {
        BoxScoreHeaderTitle(R.string.box_score_key_moments_title)

        soccerPenaltyShootoutModule?.Render()

        keyMoments.forEachIndexed { index, moment ->
            when (moment) {
                is SoccerMomentsUi.EventSoccerMoment -> SoccerEventPlay(
                    teamLogos = moment.teamLogos,
                    title = moment.headerLabel,
                    description = moment.description,
                    clock = moment.clock,
                    eventIcon = moment.iconRes,
                    showDivider = index < keyMoments.lastIndex
                )
                is SoccerMomentsUi.ScoringSoccerMoment -> SoccerGoalPlay(
                    homeTeamScore = moment.homeTeamScore,
                    awayTeamScore = moment.awayTeamScore,
                    clock = moment.clock,
                    description = moment.description,
                    title = moment.headerLabel,
                    teamLogos = moment.teamLogos,
                    teamColor = moment.teamColor,
                    awayTeamAlias = moment.awayTeamName,
                    homeTeamAlias = moment.homeTeamName,
                    showDivider = index < keyMoments.lastIndex
                )
                is SoccerMomentsUi.StandardSoccerMoment -> SoccerStandardPlay(
                    teamLogos = moment.teamLogos,
                    title = moment.headerLabel,
                    description = moment.description,
                    clock = moment.clock,
                    showDivider = index < keyMoments.lastIndex
                )
            }
        }

        BoxScoreFooterDivider(includeBottomBar = false)
    }
}

@Composable
fun RecentMoments(
    recentMoments: List<SoccerMomentsUi>,
    soccerPenaltyShootoutModule: FeedModule? = null,
    onFullTimeLineClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = AthTheme.colors.dark200)
    ) {
        BoxScoreHeaderTitle(R.string.box_score_recent_moments_title)

        soccerPenaltyShootoutModule?.Render()

        recentMoments.forEachIndexed { index, moment ->
            when (moment) {
                is SoccerMomentsUi.EventSoccerMoment -> SoccerEventPlay(
                    teamLogos = moment.teamLogos,
                    title = moment.headerLabel,
                    description = moment.description,
                    clock = moment.clock,
                    eventIcon = moment.iconRes,
                    showDivider = true
                )
                is SoccerMomentsUi.ScoringSoccerMoment -> SoccerGoalPlay(
                    homeTeamScore = moment.homeTeamScore,
                    awayTeamScore = moment.awayTeamScore,
                    clock = moment.clock,
                    description = moment.description,
                    title = moment.headerLabel,
                    teamLogos = moment.teamLogos,
                    teamColor = moment.teamColor,
                    awayTeamAlias = moment.awayTeamName,
                    homeTeamAlias = moment.homeTeamName,
                    showDivider = true
                )
                is SoccerMomentsUi.StandardSoccerMoment -> SoccerStandardPlay(
                    teamLogos = moment.teamLogos,
                    title = moment.headerLabel,
                    description = moment.description,
                    clock = moment.clock,
                    showDivider = true
                )
            }
        }
        RecentMomentsFooter(onFullTimeLineClick)
        BoxScoreFooterDivider(includeBottomBar = false)
    }
}

@Composable
private fun RecentMomentsFooter(onFullTimeLineClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
            .padding(top = 15.dp)
            .clickable {
                onFullTimeLineClick()
            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(id = R.string.box_score_recent_moments_full_timeline_title),
            style = AthTextStyle.Calibre.Utility.Regular.ExtraLarge,
            color = AthTheme.colors.dark500,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 10.dp)
        )
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = AthTheme.colors.dark500,
            modifier = Modifier.padding(vertical = 10.dp)
        )
    }
}

@Preview
@Composable
private fun SeasonStats_Preview() {
    KeyMoments(mockKeyMoments)
}

@Preview(device = Devices.PIXEL)
@Composable
private fun SeasonStats_PreviewSmallDevice() {
    KeyMoments(mockKeyMoments)
}

@Preview(device = Devices.PIXEL_4_XL)
@Composable
private fun SeasonStats_PreviewLargeDevice() {
    KeyMoments(mockKeyMoments)
}

@Preview
@Composable
private fun SeasonStats_PreviewLight() {
    AthleticTheme(lightMode = true) {
        KeyMoments(mockKeyMoments)
    }
}