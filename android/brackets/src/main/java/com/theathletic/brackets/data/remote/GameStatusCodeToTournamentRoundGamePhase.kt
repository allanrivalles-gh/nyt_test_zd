package com.theathletic.brackets.data.remote

import com.theathletic.brackets.data.local.TournamentRoundGame
import com.theathletic.type.GameStatusCode

fun GameStatusCode.toTournamentRoundGamePhase(scheduledAt: Long?): TournamentRoundGame.Phase? {
    val status = if (this == GameStatusCode.delayed) {
        if (scheduledAt == null) {
            GameStatusCode.scheduled
        } else {
            GameStatusCode.in_progress
        }
    } else {
        this
    }
    return when (status) {
        GameStatusCode.final -> TournamentRoundGame.Phase.PostGame
        GameStatusCode.in_progress, GameStatusCode.suspended -> TournamentRoundGame.Phase.InGame
        // pre game
        GameStatusCode.scheduled, GameStatusCode.if_necessary -> TournamentRoundGame.Phase.PreGame
        // non starter (also considered pre game in this context)
        GameStatusCode.unnecessary, GameStatusCode.postponed, GameStatusCode.cancelled -> TournamentRoundGame.Phase.PreGame
        else -> null
    }
}