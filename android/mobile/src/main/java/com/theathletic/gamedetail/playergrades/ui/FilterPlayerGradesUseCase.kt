package com.theathletic.gamedetail.playergrades.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.gamedetail.data.local.PlayerGradesLocalModel

class FilterPlayerGradesUseCase @AutoKoin constructor() {

    // Only show players with statistics
    operator fun invoke(players: List<PlayerGradesLocalModel.Player>?) =
        players?.mapNotNull { it.filter() }
            ?.sortedBy { it.grading?.order } ?: emptyList()

    // Must have a supported position and contains stats
    private fun PlayerGradesLocalModel.Player.filter(): PlayerGradesLocalModel.Player? {
        if (grading == null || defaultStatistics.isEmpty()) return null
        return this
    }
}