package com.theathletic.profile.following

import android.content.Context
import android.content.Intent
import com.theathletic.activity.SingleFragmentActivity
import com.theathletic.profile.manage.UserTopicId

class ManageFollowingActivity : SingleFragmentActivity() {

    companion object {
        private const val EXTRA_FOLLOW_ID = "follow_id"

        fun newIntent(
            context: Context,
            autoFollowId: UserTopicId? = null
        ) = Intent(context, ManageFollowingActivity::class.java).apply {
            putExtra(EXTRA_FOLLOW_ID, autoFollowId)
        }
    }

    override fun getFragment() = ManageFollowingFragment.newInstance(
        autoFollowId = intent.getSerializableExtra(EXTRA_FOLLOW_ID) as? UserTopicId?
    )
}