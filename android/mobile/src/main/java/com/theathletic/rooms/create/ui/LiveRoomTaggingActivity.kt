package com.theathletic.rooms.create.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.theathletic.R
import com.theathletic.activity.BaseActivity
import com.theathletic.rooms.create.data.local.LiveRoomCreationSearchMode

class LiveRoomTaggingActivity : BaseActivity() {

    companion object {
        private const val EXTRA_SEARCH_MODE = "extra_search_mode"

        fun newIntent(
            context: Context,
            searchMode: LiveRoomCreationSearchMode,
        ) = Intent(context, LiveRoomTaggingActivity::class.java).apply {
            putExtra(EXTRA_SEARCH_MODE, searchMode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_base_toolbar)

        val searchMode = intent.getSerializableExtra(EXTRA_SEARCH_MODE) as LiveRoomCreationSearchMode

        val title = when (searchMode) {
            LiveRoomCreationSearchMode.TAGS -> R.string.rooms_create_add_tags_title
            LiveRoomCreationSearchMode.HOSTS -> R.string.rooms_create_add_hosts_title
        }

        setupActionBar(
            getString(title),
            findViewById(R.id.toolbar)
        )

        val newFragment = LiveRoomTaggingFragment.newInstance(searchMode)

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, newFragment)
            .commit()
    }
}