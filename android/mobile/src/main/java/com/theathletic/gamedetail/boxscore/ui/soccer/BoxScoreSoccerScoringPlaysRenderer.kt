package com.theathletic.gamedetail.boxscore.ui.soccer

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.SoccerMomentsUi
import com.theathletic.boxscore.ui.modules.KeyMomentsModule
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.SoccerPlayType

class BoxScoreSoccerScoringPlaysRenderer @AutoKoin constructor() {
    fun createKeyMomentsModule(game: GameDetailLocalModel): FeedModuleV2? {
        val moments =
            (game.sportExtras as? GameDetailLocalModel.SoccerExtras)?.keyMoments ?: return null
        if (moments.isEmpty()) return null
        return KeyMomentsModule(
            id = game.id,
            keyMoments = moments.filterNot { it is GameDetailLocalModel.SoccerShootoutPlay }.toKeyMoments(game),
            soccerPenaltyShootoutModule = moments.toSoccerPenaltyShootoutModule(
                game.id,
                game.homeTeam?.team,
                game.awayTeam?.team
            )
        )
    }

    private fun List<GameDetailLocalModel.SoccerPlay>.toKeyMoments(game: GameDetailLocalModel): List<SoccerMomentsUi> {
        val keyMoments = mutableListOf<SoccerMomentsUi>()
        this.forEach { moment ->
            when {
                moment.playType.isGoalPlay -> {
                    keyMoments.add(scoringSoccerMomentBuilder(moment, game))
                }
                moment.playType.isCardEvent -> {
                    moment.playType.toEventIcon()?.let { icon ->
                        keyMoments.add(eventSoccerMomentBuilder(moment, icon))
                    }
                }
                else -> {
                    keyMoments.add(standardSoccerMomentBuilder(moment))
                }
            }
        }
        return keyMoments
    }

    private val SoccerPlayType.isCardEvent
        get() = when (this) {
            SoccerPlayType.RED_CARD,
            SoccerPlayType.SECOND_YELLOW_CARD,
            SoccerPlayType.SUBSTITUTION,
            SoccerPlayType.INJURY_SUBSTITUTION,
            SoccerPlayType.YELLOW_CARD -> true
            else -> false
        }

    private val SoccerPlayType.isGoalPlay
        get() = when (this) {
            SoccerPlayType.GOAL,
            SoccerPlayType.OWN_GOAL,
            SoccerPlayType.PENALTY_GOAL -> true
            else -> false
        }
}