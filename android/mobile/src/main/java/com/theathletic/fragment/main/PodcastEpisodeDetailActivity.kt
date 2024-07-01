package com.theathletic.fragment.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.theathletic.R
import com.theathletic.activity.BaseActivity

class PodcastEpisodeDetailActivity : BaseActivity() {

    companion object {
        private const val EXTRA_PODCAST_EPISODE_ID = "podcast_episode_id"

        fun newIntent(context: Context, podcastEpisodeId: Long): Intent {
            return Intent(context, PodcastEpisodeDetailActivity::class.java).apply {
                putExtra(EXTRA_PODCAST_EPISODE_ID, podcastEpisodeId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_base)

        val episodeId = intent.extras?.getLong(EXTRA_PODCAST_EPISODE_ID, -1L) ?: -1L

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, PodcastEpisodeDetailFragment.newInstance(episodeId))
            .commit()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}