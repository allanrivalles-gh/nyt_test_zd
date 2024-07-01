package com.theathletic.fragment.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.theathletic.R
import com.theathletic.activity.BaseActivity

class PodcastDetailActivity : BaseActivity() {

    companion object {
        private const val EXTRA_PODCAST_ID = "podcast_id"

        fun newIntent(context: Context, podcastId: Long): Intent {
            return Intent(context, PodcastDetailActivity::class.java).apply {
                putExtra(EXTRA_PODCAST_ID, podcastId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_base)

        val podcastId = intent.extras?.getLong(EXTRA_PODCAST_ID, -1L)!!

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, PodcastDetailFragment.newInstance(podcastId))
            .commit()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}