package com.theathletic.comments.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.theathletic.R
import com.theathletic.activity.BaseActivity
import com.theathletic.analytics.data.ClickSource
import com.theathletic.comments.analytics.CommentsAnalyticsPayload
import com.theathletic.comments.analytics.CommentsLaunchAction
import com.theathletic.comments.v2.data.local.CommentsSourceType
import com.theathletic.data.ContentDescriptor

class CommentsActivity : BaseActivity() {

    companion object {
        const val EXTRA_SOURCE_DESCRIPTOR = "extra_source_descriptor"
        const val EXTRA_SOURCE_TYPE = "extra_source_type"
        const val EXTRA_ENTRY_ACTIVE = "extra_entry_active"
        const val EXTRA_ANALYTICS_PAYLOAD = "extra_analytics_payload"
        const val EXTRA_LAUNCH_ACTION = "extra_launch_action"
        const val EXTRA_CLICK_SOURCE = "extra_click_source"

        @Suppress("LongParameterList")
        fun newIntent(
            context: Context,
            contentDescriptor: ContentDescriptor,
            type: CommentsSourceType,
            isEntryActive: Boolean,
            launchAction: CommentsLaunchAction?,
            analyticsPayload: CommentsAnalyticsPayload?,
            clickSource: ClickSource?
        ): Intent {
            return Intent(context, CommentsActivity::class.java).apply {
                putExtra(EXTRA_SOURCE_DESCRIPTOR, contentDescriptor)
                putExtra(EXTRA_SOURCE_TYPE, type)
                putExtra(EXTRA_ENTRY_ACTIVE, isEntryActive)
                putExtra(EXTRA_ANALYTICS_PAYLOAD, analyticsPayload)
                putExtra(EXTRA_LAUNCH_ACTION, launchAction)
                putExtra(EXTRA_CLICK_SOURCE, clickSource)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        val contentDescriptor = intent.extras?.getParcelable<ContentDescriptor>(EXTRA_SOURCE_DESCRIPTOR)
        val type = intent.extras?.getSerializable(EXTRA_SOURCE_TYPE) as CommentsSourceType
        val isEntryActive = intent.extras?.getBoolean(EXTRA_ENTRY_ACTIVE) == true
        val analyticsPayload = intent.extras?.getSerializable(EXTRA_ANALYTICS_PAYLOAD) as? CommentsAnalyticsPayload
        val launchAction = intent.extras?.getSerializable(EXTRA_LAUNCH_ACTION) as? CommentsLaunchAction
        val clickSource = intent.extras?.getSerializable(EXTRA_CLICK_SOURCE) as? ClickSource

        val fragment = CommentsFragment.newInstance(
            contentDescriptor,
            type,
            isEntryActive,
            launchAction,
            analyticsPayload,
            clickSource
        )

        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun getStatusBarColor() = R.color.ath_grey_65
}