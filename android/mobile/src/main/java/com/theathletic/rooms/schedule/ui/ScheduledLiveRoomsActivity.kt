package com.theathletic.rooms.schedule.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.theathletic.R
import com.theathletic.activity.BaseActivity

class ScheduledLiveRoomsActivity : BaseActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, ScheduledLiveRoomsActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduled_live_rooms)

        setupActionBar(getString(R.string.rooms_scheduled_title), findViewById(R.id.toolbar))

        val newFragment = ScheduledLiveRoomsFragment.newInstance()

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, newFragment)
            .commit()
    }
}