package com.theathletic.feed.compose.data

import com.theathletic.datetime.Datetime
import com.theathletic.feed.R
import com.theathletic.scores.data.local.GameState
import com.theathletic.ui.ResourceString

internal data class ScoresCarouselItem(
    override val id: String,
    val scrollIndex: Int,
    val game: Game
) : Layout.Item {

    data class Game(
        val id: String,
        val permalink: String?,
        val firstTeam: Team?,
        val secondTeam: Team?,
        val scheduledAt: Datetime?,
        val timeTBD: Boolean,
        val statusDisplay: StatusDisplay,
        val hasLiveDiscussion: Boolean,
        val state: GameState
    )

    data class StatusDisplay(
        val main: String?,
        val extra: String?,
    )

    data class Team(
        val logoUrl: String?,
        val alias: String?,
        val score: Int?,
        val penaltyScore: Int?,
        val lost: Boolean,
    )

    val shouldHideScores: Boolean
        get() = when (game.state) {
            GameState.LIVE, GameState.FINAL -> false
            else -> true
        }

    val isFirstStatusTextHighlighted: Boolean
        get() = game.state == GameState.LIVE
}

internal val ScoresCarouselItem.Team?.identifier: ResourceString
    get() = this?.alias?.let { ResourceString.StringWrapper(it) }
        ?: ResourceString.StringWithParams(R.string.global_tbd)

internal val ScoresCarouselItem.Team?.displayScore: String?
    get() {
        var score = this?.score?.toString() ?: return null
        penaltyScore?.also { score = "$score ($it)" }
        return score
    }

internal val ScoresCarouselItem.Team?.isTextDimmed: Boolean
    get() = this?.lost ?: false