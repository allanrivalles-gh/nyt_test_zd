package com.theathletic.preferences.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.theathletic.R
import com.theathletic.activity.BaseActivity

class NotificationPreferenceActivity : BaseActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, NotificationPreferenceActivity::class.java)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_preference)
        setupActionBar(
            title,
            findViewById(R.id.toolbar)
        )
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}