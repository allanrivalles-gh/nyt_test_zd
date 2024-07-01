package com.theathletic.gamedetail.boxscore.ui.common

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.entity.main.Sport
import com.theathletic.gamedetail.data.local.FractionSeparator
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.GameStatus
import com.theathletic.gamedetail.data.local.Period
import com.theathletic.ui.ResourceString
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.utility.orShortDash

class BoxScoreCommonRenderers @AutoKoin constructor() {

    enum class CommonRenderedComponent {
        COMPONENT_SCORING,
        COMPONENT_LAST_GAMES,
        COMPONENT_PLAYER_STATS,
        COMPONENT_RELATED_STORIES,
        COMPONENT_FOOTBALL_LAST_PLAY,
        COMPONENT_BASKETBALL_LAST_PLAYS,
        COMPONENT_HOCKEY_LAST_PLAY,
        COMPONENT_BASEBALL_STARTING_PITCHERS,
        COMPONENT_BASEBALL_INNING_PLAY,
        COMPONENT_LEADERS
    }

    fun formatStatisticValue(stat: GameDetailLocalModel.Statistic): String? {
        return when (stat) {
            is GameDetailLocalModel.DecimalStatistic -> stat.stringValue ?: "0.0"
            is GameDetailLocalModel.IntegerStatistic -> stat.intValue.toString()
            is GameDetailLocalModel.PercentageStatistic -> stat.stringValue ?: "0.0%"
            is GameDetailLocalModel.StringStatistic -> stat.value?.orShortDash()
            is GameDetailLocalModel.FractionStatistic -> stat.format()
            is GameDetailLocalModel.TimeStatistic -> stat.stringValue?.orShortDash()
            else -> null
        }
    }

    private fun GameDetailLocalModel.FractionStatistic.format(): String {
        val sepChar = if (separator == FractionSeparator.DASH) "-" else "/"
        return "${numeratorValue ?: "-"}$sepChar${denominatorValue ?: "-"}"
    }

    fun compareStatValuesForLargest(
        statPair: Pair<GameDetailLocalModel.Statistic, GameDetailLocalModel.Statistic>
    ): Pair<Boolean, Boolean> {
        val highLowPair = when (statPair.first) {
            is GameDetailLocalModel.DecimalStatistic -> {
                val firstTeam = (statPair.first as GameDetailLocalModel.DecimalStatistic)
                val secondTeam = (statPair.second as GameDetailLocalModel.DecimalStatistic)
                Pair(firstTeam >= secondTeam, secondTeam >= firstTeam)
            }
            is GameDetailLocalModel.IntegerStatistic -> {
                val firstTeam = (statPair.first as GameDetailLocalModel.IntegerStatistic)
                val secondTeam = (statPair.second as GameDetailLocalModel.IntegerStatistic)
                Pair(firstTeam >= secondTeam, secondTeam >= firstTeam)
            }
            is GameDetailLocalModel.PercentageStatistic -> {
                val firstTeam = (statPair.first as GameDetailLocalModel.PercentageStatistic)
                val secondTeam = (statPair.second as GameDetailLocalModel.PercentageStatistic)
                Pair(firstTeam >= secondTeam, secondTeam >= firstTeam)
            }
            is GameDetailLocalModel.StringStatistic -> Pair(first = true, second = true)
            is GameDetailLocalModel.FractionStatistic -> {
                val firstTeam = (statPair.first as GameDetailLocalModel.FractionStatistic)
                val secondTeam = (statPair.second as GameDetailLocalModel.FractionStatistic)
                Pair(firstTeam >= secondTeam, secondTeam >= firstTeam)
            }
            is GameDetailLocalModel.TimeStatistic -> {
                val firstTeam = (statPair.first as GameDetailLocalModel.TimeStatistic)
                val secondTeam = (statPair.second as GameDetailLocalModel.TimeStatistic)
                Pair(firstTeam >= secondTeam, secondTeam >= firstTeam)
            }
            else -> Pair(first = true, second = true)
        }
        return if (statPair.first.lessIsBest && highLowPair.first != highLowPair.second) {
            Pair(!highLowPair.first, !highLowPair.second)
        } else {
            highLowPair
        }
    }
}

val Period.toPeriodLabel: ResourceString
    get() = when (this) {
        Period.FIRST_QUARTER -> StringWithParams(R.string.box_score_first_quarter)
        Period.FIRST_PERIOD -> StringWithParams(R.string.box_score_first_period)
        Period.SECOND_QUARTER -> StringWithParams(R.string.box_score_second_quarter)
        Period.SECOND_PERIOD -> StringWithParams(R.string.box_score_second_period)
        Period.THIRD_QUARTER -> StringWithParams(R.string.box_score_third_quarter)
        Period.THIRD_PERIOD -> StringWithParams(R.string.box_score_third_period)
        Period.FOURTH_QUARTER -> StringWithParams(R.string.box_score_forth_quarter)
        Period.KICK_OFF -> StringWithParams(R.string.plays_soccer_plays_kick_off_title)
        Period.FIRST_HALF -> StringWithParams(R.string.plays_soccer_plays_first_half_title)
        Period.SECOND_HALF -> StringWithParams(R.string.plays_soccer_plays_second_half_title)
        Period.EXTRA_TIME_FIRST_HALF -> StringWithParams(R.string.plays_soccer_plays_first_half_extra_time_title)
        Period.EXTRA_TIME_SECOND_HALF -> StringWithParams(R.string.plays_soccer_plays_second_half_extra_time_title)
        Period.PENALTY_SHOOTOUT -> StringWithParams(R.string.plays_soccer_plays_penalty_shootout_title)
        Period.HALF_TIME -> StringWithParams(R.string.plays_soccer_plays_half_time_title)
        Period.FULL_TIME -> StringWithParams(R.string.plays_soccer_plays_full_time_title)
        Period.OVER_TIME -> StringWithParams(R.string.game_detail_post_game_overtime_label)
        Period.OVER_TIME_2 -> StringWithParams(R.string.game_detail_post_game_overtime_formatter, 2)
        Period.OVER_TIME_3 -> StringWithParams(R.string.game_detail_post_game_overtime_formatter, 3)
        Period.OVER_TIME_4 -> StringWithParams(R.string.game_detail_post_game_overtime_formatter, 4)
        Period.OVER_TIME_5 -> StringWithParams(R.string.game_detail_post_game_overtime_formatter, 5)
        Period.OVER_TIME_6 -> StringWithParams(R.string.game_detail_post_game_overtime_formatter, 6)
        Period.OVER_TIME_7 -> StringWithParams(R.string.game_detail_post_game_overtime_formatter, 7)
        Period.OVER_TIME_8 -> StringWithParams(R.string.game_detail_post_game_overtime_formatter, 8)
        Period.OVER_TIME_9 -> StringWithParams(R.string.game_detail_post_game_overtime_formatter, 9)
        Period.OVER_TIME_10 -> StringWithParams(R.string.game_detail_post_game_overtime_formatter, 10)
        Period.SHOOTOUT -> StringWithParams(R.string.box_score_shootout)
        else -> StringWithParams(R.string.box_score_unknown)
    }

fun Period.toHeaderPeriodLabel(sport: Sport, status: GameStatus): ResourceString? {
    if (
        (status == GameStatus.IN_PROGRESS || status == GameStatus.FINAL).not()
    ) return null
    return when (this) {
        Period.FIRST_QUARTER,
        Period.FIRST_HALF,
        Period.FIRST_PERIOD -> StringWithParams(R.string.game_detail_post_game_1st_quarter_label)
        Period.SECOND_QUARTER,
        Period.SECOND_HALF,
        Period.SECOND_PERIOD -> StringWithParams(R.string.game_detail_post_game_2nd_quarter_label)
        Period.THIRD_QUARTER,
        Period.THIRD_PERIOD -> StringWithParams(R.string.game_detail_post_game_3rd_quarter_label)
        Period.FOURTH_QUARTER -> StringWithParams(R.string.game_detail_post_game_4th_quarter_label)
        Period.HALF_TIME -> StringWithParams(R.string.game_detail_post_game_halftime_label)
        Period.OVER_TIME -> StringWithParams(R.string.game_detail_post_game_overtime_label)
        Period.OVER_TIME_2 -> StringWithParams(R.string.game_detail_post_game_overtime_formatter, 2)
        Period.OVER_TIME_3 -> StringWithParams(R.string.game_detail_post_game_overtime_formatter, 3)
        Period.OVER_TIME_4 -> StringWithParams(R.string.game_detail_post_game_overtime_formatter, 4)
        Period.OVER_TIME_5 -> StringWithParams(R.string.game_detail_post_game_overtime_formatter, 5)
        Period.OVER_TIME_6 -> StringWithParams(R.string.game_detail_post_game_overtime_formatter, 6)
        Period.OVER_TIME_7 -> StringWithParams(R.string.game_detail_post_game_overtime_formatter, 7)
        Period.OVER_TIME_8 -> StringWithParams(R.string.game_detail_post_game_overtime_formatter, 8)
        Period.OVER_TIME_9 -> StringWithParams(R.string.game_detail_post_game_overtime_formatter, 9)
        Period.OVER_TIME_10 -> StringWithParams(R.string.game_detail_post_game_overtime_formatter, 10)
        Period.FULL_TIME -> fullTimeLabel(sport)
        Period.FULL_TIME_OT -> StringWithParams(R.string.game_detail_post_game_final_overtime_label)
        Period.FULL_TIME_OT_2 -> StringWithParams(R.string.game_detail_post_game_final_overtime_formatter, 2)
        Period.FULL_TIME_OT_3 -> StringWithParams(R.string.game_detail_post_game_final_overtime_formatter, 3)
        Period.FULL_TIME_OT_4 -> StringWithParams(R.string.game_detail_post_game_final_overtime_formatter, 4)
        Period.FULL_TIME_OT_5 -> StringWithParams(R.string.game_detail_post_game_final_overtime_formatter, 5)
        Period.FULL_TIME_OT_6 -> StringWithParams(R.string.game_detail_post_game_final_overtime_formatter, 6)
        Period.FULL_TIME_OT_7 -> StringWithParams(R.string.game_detail_post_game_final_overtime_formatter, 7)
        Period.FULL_TIME_OT_8 -> StringWithParams(R.string.game_detail_post_game_final_overtime_formatter, 8)
        Period.FULL_TIME_OT_9 -> StringWithParams(R.string.game_detail_post_game_final_overtime_formatter, 9)
        Period.FULL_TIME_OT_10 -> StringWithParams(R.string.game_detail_post_game_final_overtime_formatter, 10)
        Period.SHOOTOUT -> StringWithParams(R.string.game_detail_post_game_shootout_label)
        Period.FULL_TIME_SO -> StringWithParams(R.string.game_detail_post_game_final_shootout_label)
        else -> null
    }
}

private fun fullTimeLabel(sport: Sport): ResourceString =
    if (sport == Sport.SOCCER) StringWithParams(R.string.box_score_soccer_timeline_full)
    else StringWithParams(
        R.string.game_detail_post_game_final_label
    )