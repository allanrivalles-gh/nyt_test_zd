package com.theathletic.boxscore.ui.modules

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import com.theathletic.boxscore.ui.playbyplay.NoKeyMoments
import com.theathletic.boxscore.ui.playbyplay.SoccerEventPlay
import com.theathletic.boxscore.ui.playbyplay.SoccerGoalPlay
import com.theathletic.boxscore.ui.playbyplay.SoccerStandardPlay
import com.theathletic.data.SizedImages
import com.theathletic.feed.ui.FeedModule

data class StandardSoccerMomentModule(
    val clock: String,
    val description: String,
    val headerLabel: String,
    val id: String,
    val teamLogos: SizedImages,
    val showDivider: Boolean,
) : FeedModule {

    @Composable
    override fun Render() {
        SoccerStandardPlay(
            teamLogos = teamLogos,
            title = headerLabel,
            description = description,
            clock = clock,
            showDivider = true
        )
    }
}

data class ScoringSoccerMomentModule(
    val clock: String,
    val description: String,
    val headerLabel: String,
    val id: String,
    val teamLogos: SizedImages,
    val teamColor: String?,
    val awayTeamScore: String,
    val homeTeamScore: String,
    val awayTeamName: String,
    val homeTeamName: String,
    val showDivider: Boolean,
) : FeedModule {

    @Composable
    override fun Render() {
        SoccerGoalPlay(
            homeTeamScore = homeTeamScore,
            awayTeamScore = awayTeamScore,
            clock = clock,
            description = description,
            title = headerLabel,
            teamLogos = teamLogos,
            teamColor = teamColor,
            awayTeamAlias = awayTeamName,
            homeTeamAlias = homeTeamName,
            showDivider = showDivider
        )
    }
}

data class EventSoccerMomentModule(
    val clock: String,
    val description: String,
    val headerLabel: String,
    val id: String,
    val teamLogos: SizedImages,
    @DrawableRes val iconRes: Int,
    val showDivider: Boolean,
) : FeedModule {

    @Composable
    override fun Render() {
        SoccerEventPlay(
            teamLogos = teamLogos,
            title = headerLabel,
            description = description,
            clock = clock,
            eventIcon = iconRes,
            showDivider = showDivider
        )
    }
}

data class NoKeyMomentModule(
    val id: String,
) : FeedModule {

    @Composable
    override fun Render() {
        NoKeyMoments()
    }
}