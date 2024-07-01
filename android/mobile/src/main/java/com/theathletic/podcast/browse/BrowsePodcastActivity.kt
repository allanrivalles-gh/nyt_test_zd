package com.theathletic.podcast.browse

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.theathletic.R
import com.theathletic.activity.BaseActivity
import com.theathletic.entity.main.PodcastTopicEntryType

class BrowsePodcastActivity : BaseActivity() {
    companion object {
        const val EXTRA_TOPIC_ID = "topic_id"
        const val EXTRA_TOPIC_NAME = "topic_name"
        const val EXTRA_TOPIC_ENTRY_TYPE = "entry_type"

        fun newIntent(
            context: Context,
            topicId: Long,
            topicName: String,
            entryType: PodcastTopicEntryType
        ): Intent {
            return Intent(context, BrowsePodcastActivity::class.java).apply {
                putExtra(EXTRA_TOPIC_ID, topicId)
                putExtra(EXTRA_TOPIC_NAME, topicName)
                putExtra(EXTRA_TOPIC_ENTRY_TYPE, entryType.toString())
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse_podcast)
        setupActionBar(
            intent.getStringExtra(EXTRA_TOPIC_NAME),
            findViewById(R.id.toolbar)
        )
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}