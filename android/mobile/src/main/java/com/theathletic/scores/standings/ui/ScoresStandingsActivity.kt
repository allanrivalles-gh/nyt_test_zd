package com.theathletic.scores.standings.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.theathletic.R
import com.theathletic.activity.BaseActivity
import com.theathletic.entity.main.League

class ScoresStandingsActivity : BaseActivity() {

    companion object {
        private const val EXTRA_LEAGUE = "extra_league"
        private const val EXTRA_TEAM_ID = "extra_team_id"

        fun newIntent(
            context: Context,
            league: League,
            teamId: String?
        ) = Intent(context, ScoresStandingsActivity::class.java).apply {
            putExtra(EXTRA_LEAGUE, league)
            putExtra(EXTRA_TEAM_ID, teamId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_base)

        intent.extras?.let { extras ->
            (extras.get(EXTRA_LEAGUE) as League?)?.let { league ->
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.container,
                        ScoresStandingsFragment.newInstance(
                            league,
                            extras.getString(EXTRA_TEAM_ID),
                        )
                    ).commit()
            } ?: finish()
        }
    }
}