package com.theathletic.activity.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.theathletic.R
import com.theathletic.activity.BaseActivity
import com.theathletic.feed.FeedType
import com.theathletic.feed.ui.FeedFragment

class StandaloneFeedActivity : BaseActivity() {

    companion object {
        private const val ARG_FEED_TYPE = "feed_type"
        private const val ARG_DISPLAY_TITLE = "display_title"

        fun newIntent(
            context: Context,
            feedType: FeedType,
            displayTitle: String? = null
        ): Intent {
            return Intent(context, StandaloneFeedActivity::class.java).apply {
                putExtra(ARG_FEED_TYPE, feedType)
                displayTitle?.let { putExtra(ARG_DISPLAY_TITLE, it) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_standalone_feed)

        if (savedInstanceState == null) {
            if (intent.hasExtra(ARG_FEED_TYPE)) {
                val feedType = (intent?.getSerializableExtra(ARG_FEED_TYPE) as? FeedType) ?: FeedType.User
                val newFragment = FeedFragment.newInstance(
                    feedType = feedType,
                    title = intent?.getStringExtra(ARG_DISPLAY_TITLE).orEmpty(),
                    isStandaloneFeed = true
                )

                supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, newFragment)
                    .commit()
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}