package com.theathletic.gamedetail.playergrades.ui

import android.content.Context
import android.content.Intent
import com.theathletic.activity.SingleFragmentActivity
import com.theathletic.entity.main.Sport
import com.theathletic.utility.getSerializableExtraCompat

private const val EXTRA_GAME_ID = "extra_game_id"
private const val EXTRA_PLAYER_ID = "extra_player_id"
private const val EXTRA_SPORT = "extra_sport"
private const val EXTRA_LEAGUE = "extra_league"
private const val EXTRA_GRADES_TAB = "extra_from_grades_tab"

class PlayerGradesDetailActivity : SingleFragmentActivity() {

    companion object {
        @SuppressWarnings("LongParameterList")
        fun newIntent(
            context: Context,
            gameId: String,
            playerId: String,
            sport: Sport,
            leagueId: String,
            launchedFromGradesTab: Boolean,
        ) = Intent(context, PlayerGradesDetailActivity::class.java).apply {
            putExtra(EXTRA_GAME_ID, gameId)
            putExtra(EXTRA_PLAYER_ID, playerId)
            putExtra(EXTRA_SPORT, sport)
            putExtra(EXTRA_LEAGUE, leagueId)
            putExtra(EXTRA_GRADES_TAB, launchedFromGradesTab)
        }
    }

    override fun getFragment() = PlayerGradesDetailFragment.newInstance(
        gameId = intent.getStringExtra(EXTRA_GAME_ID).orEmpty(),
        playerId = intent.getStringExtra(EXTRA_PLAYER_ID).orEmpty(),
        sport = intent.getSerializableExtraCompat(EXTRA_SPORT) ?: Sport.UNKNOWN,
        leagueId = intent.getStringExtra(EXTRA_LEAGUE).orEmpty(),
        launchedFromGradesTab = intent.getBooleanExtra(EXTRA_GRADES_TAB, false)
    )
}