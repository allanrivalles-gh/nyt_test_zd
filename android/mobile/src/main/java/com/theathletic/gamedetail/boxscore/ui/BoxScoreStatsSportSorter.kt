package com.theathletic.gamedetail.boxscore.ui

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.entity.main.Sport
import com.theathletic.gamedetail.boxscore.ui.baseball.stats.BoxScoreStatsBaseballSorter
import com.theathletic.gamedetail.boxscore.ui.basketball.stats.BoxScoreStatsBasketballSorter
import com.theathletic.gamedetail.boxscore.ui.football.stats.BoxScoreStatsFootballSorter
import com.theathletic.gamedetail.boxscore.ui.hockey.stats.BoxScoreStatsHockeySorter

class BoxScoreStatsSportSorter @AutoKoin constructor(
    private val footballStatsSorter: BoxScoreStatsFootballSorter,
    private val basketballStatsSorter: BoxScoreStatsBasketballSorter,
    private val hockeyStatsSorter: BoxScoreStatsHockeySorter,
    private val baseballStatsSorter: BoxScoreStatsBaseballSorter
) {
    fun get(sport: Sport) = when (sport) {
        Sport.FOOTBALL -> footballStatsSorter
        Sport.BASKETBALL -> basketballStatsSorter
        Sport.HOCKEY -> hockeyStatsSorter
        Sport.BASEBALL -> baseballStatsSorter
        else -> null
    }
}