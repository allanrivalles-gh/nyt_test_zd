package com.theathletic.podcast.downloaded.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.theathletic.R
import com.theathletic.activity.BaseActivity

class PodcastDownloadedActivity : BaseActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_podcast_downloaded)
        setupActionBar(
            title,
            findViewById(R.id.toolbar)
        )
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, PodcastDownloadedActivity::class.java)
    }
}