package com.theathletic.gamedetail.boxscore.ui.injuryreport

import android.content.Context
import android.content.Intent
import com.theathletic.activity.SingleFragmentActivity

class BoxScoreInjuryReportActivity : SingleFragmentActivity() {

    companion object {
        private const val EXTRA_GAME_ID = "extra_game_id"
        private const val EXTRA_FIRST_TEAM_SELECTED = "extra_first_team_selected"

        fun newIntent(
            context: Context,
            gameId: String,
            isFirstTeamSelected: Boolean
        ) = Intent(context, BoxScoreInjuryReportActivity::class.java).apply {
            putExtra(EXTRA_GAME_ID, gameId)
            putExtra(EXTRA_FIRST_TEAM_SELECTED, isFirstTeamSelected)
        }
    }

    override fun getFragment() = BoxScoreInjuryReportFragment.newInstance(
        gameId = intent.getStringExtra(EXTRA_GAME_ID),
        isFirstTeamSelected = intent.getBooleanExtra(EXTRA_FIRST_TEAM_SELECTED, true)
    )
}