package com.theathletic.subscriptionplans

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.theathletic.R
import com.theathletic.activity.BaseActivity
import com.theathletic.analytics.data.ClickSource
import com.theathletic.billing.SpecialOffer

class SubscriptionPlansActivity : BaseActivity() {

    companion object {
        const val EXTRA_SOURCE = "source"
        const val EXTRA_ARTICLE_ID = "article_id"
        const val EXTRA_SPECIAL_OFFER = "special_offer"
        const val EXTRA_ROOM_ID = "room_id"
        const val EXTRA_ROOM_ACTION = "room_action"

        @Suppress("LongParameterList")
        fun newIntent(
            context: Context,
            source: ClickSource,
            articleId: Long,
            specialOffer: SpecialOffer?,
            liveRoomId: String? = null,
            liveRoomAction: String? = null,
        ) = Intent(context, SubscriptionPlansActivity::class.java).apply {
            putExtra(EXTRA_SOURCE, source)
            putExtra(EXTRA_ARTICLE_ID, articleId)
            putExtra(EXTRA_SPECIAL_OFFER, specialOffer)
            putExtra(EXTRA_ROOM_ID, liveRoomId)
            putExtra(EXTRA_ROOM_ACTION, liveRoomAction)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription_plans)
    }

    override fun getStatusBarColor() = R.color.ath_grey_65
}