package com.theathletic.boxscore.ui.formatters

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.R
import com.theathletic.gamedetail.data.local.InningHalf
import com.theathletic.ui.ResourceString
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.asResourceString

class BoxScoreBaseballInningFormatter @AutoKoin constructor(
    private val ordinalFormatter: OrdinalFormatter
) {

    fun format(inning: Int, inningHalf: InningHalf?): ResourceString {
        val ordinal = ordinalFormatter.format(inning)
        return when (inningHalf) {
            null -> inning.toString().asResourceString()
            InningHalf.BOTTOM -> StringWithParams(
                R.string.box_score_baseball_play_title_bottom,
                ordinal
            )
            InningHalf.TOP -> StringWithParams(
                R.string.box_score_baseball_play_title_top,
                ordinal
            )
            InningHalf.MIDDLE -> StringWithParams(
                R.string.box_score_baseball_play_title_middle,
                ordinal
            )
            InningHalf.OVER -> StringWithParams(
                R.string.box_score_baseball_play_title_end,
                ordinal
            )
            else -> StringWithParams(R.string.box_score_unknown)
        }
    }

    fun longFormat(inning: String, inningHalf: InningHalf?): ResourceString {
        return when (inningHalf) {
            null -> inning.asResourceString()
            InningHalf.BOTTOM -> StringWithParams(
                R.string.box_score_baseball_play_title_bottom_long,
                inning
            )
            InningHalf.TOP -> StringWithParams(
                R.string.box_score_baseball_play_title_top_long,
                inning
            )
            InningHalf.MIDDLE -> StringWithParams(
                R.string.box_score_baseball_play_title_middle_long,
                inning
            )
            InningHalf.OVER -> StringWithParams(
                R.string.box_score_baseball_play_title_end_long,
                inning
            )
            else -> StringWithParams(R.string.box_score_unknown)
        }
    }
}