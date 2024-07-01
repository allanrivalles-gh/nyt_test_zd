package com.theathletic.scores

import com.theathletic.core.R
import com.theathletic.ui.ResourceString

object GameUtil {
    fun buildGameTitle(
        firstTeamDisplayString: String,
        secondTeamDisplayString: String,
        firstTeamTbd: Boolean,
        secondTeamTbd: Boolean,
        isSoccer: Boolean
    ) = when {
        firstTeamTbd && secondTeamTbd -> ResourceString.StringWithParams(
            if (isSoccer) {
                R.string.game_detail_toolbar_soccer_with_both_team_tbc_label
            } else {
                R.string.game_details_toolbar_non_soccer_both_teams_tbc_label
            }
        )
        firstTeamTbd -> ResourceString.StringWithParams(
            if (isSoccer) {
                R.string.game_detail_toolbar_soccer_with_first_team_tbc_label
            } else {
                R.string.game_details_toolbar_non_soccer_first_team_tbc_label
            },
            secondTeamDisplayString
        )
        secondTeamTbd -> ResourceString.StringWithParams(
            if (isSoccer) {
                R.string.game_detail_toolbar_soccer_with_second_team_tbc_label
            } else {
                R.string.game_details_toolbar_non_soccer_second_team_tbc_label
            },
            firstTeamDisplayString
        )
        else -> ResourceString.StringWithParams(
            if (isSoccer) {
                R.string.game_detail_toolbar_soccer_label
            } else {
                R.string.game_details_toolbar_american_football_label
            },
            firstTeamDisplayString,
            secondTeamDisplayString
        )
    }
}