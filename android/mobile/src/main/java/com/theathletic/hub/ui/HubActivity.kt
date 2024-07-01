package com.theathletic.hub.ui

import android.content.Context
import android.content.Intent
import com.theathletic.activity.SingleFragmentActivity
import com.theathletic.feed.FeedType
import com.theathletic.hub.HubTabType
import com.theathletic.hub.league.ui.LeagueHubFragment
import com.theathletic.hub.team.ui.TeamHubFragment
import com.theathletic.utility.getSerializableExtraCompat
import com.theathletic.utility.safeLet

private const val ARG_FEED_TYPE = "extra_feed_type"
private const val ARG_INITIAL_TAB = "extra_initial_tab"

class HubActivity : SingleFragmentActivity() {

    companion object {
        fun newIntent(
            context: Context,
            feedType: FeedType,
            initialTab: HubTabType = HubTabType.Home
        ): Intent {
            return Intent(context, HubActivity::class.java).apply {
                putExtra(ARG_FEED_TYPE, feedType)
                putExtra(ARG_INITIAL_TAB, initialTab)
            }
        }
    }

    override fun getFragment() =
        safeLet(
            intent?.getSerializableExtraCompat<FeedType>(ARG_FEED_TYPE),
            intent?.getSerializableExtraCompat<HubTabType>(ARG_INITIAL_TAB)
        ) { feedType, initialTab ->
            when (feedType) {
                is FeedType.Team -> TeamHubFragment.newInstance(
                    feedType = feedType,
                    initialTab = initialTab,
                )
                is FeedType.League -> LeagueHubFragment.newInstance(
                    feedType = feedType,
                    initialTab = initialTab,
                )
                else -> null
            }
        }
}