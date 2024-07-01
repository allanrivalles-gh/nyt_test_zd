package com.theathletic.preferences.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.theathletic.R
import com.theathletic.activity.BaseActivity
import com.theathletic.profile.manage.UserTopicType

class UserTopicNotificationsActivity : BaseActivity() {

    companion object {
        const val EXTRA_ID = "id"
        const val EXTRA_TYPE = "type"
        const val EXTRA_TITLE = "title"

        fun newIntent(
            context: Context,
            entityId: Long,
            type: UserTopicType,
            title: String
        ): Intent {
            return Intent(context, UserTopicNotificationsActivity::class.java).apply {
                putExtra(EXTRA_ID, entityId)
                putExtra(EXTRA_TYPE, type.name)
                putExtra(EXTRA_TITLE, title)
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_topic_notifications)
        setupActionBar(
            title = intent.extras?.getString(EXTRA_TITLE),
            findViewById(R.id.toolbar)
        )
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}