package com.theathletic.subscriptionplans

import android.os.Bundle
import com.theathletic.analytics.data.ClickSource
import com.theathletic.billing.SpecialOffer

data class SubscriptionPlansInitialData(
    val specialOffer: SpecialOffer?,
    val articleId: Long,
    val liveRoomId: String?,
    val liveRoomAction: String?,
    val source: ClickSource
) {
    constructor(bundle: Bundle?) : this(
        specialOffer = bundle?.get(SubscriptionPlansActivity.EXTRA_SPECIAL_OFFER) as SpecialOffer?,
        articleId = bundle?.getLong(SubscriptionPlansActivity.EXTRA_ARTICLE_ID, -1) ?: -1,
        liveRoomId = bundle?.getString(SubscriptionPlansActivity.EXTRA_ROOM_ID, null),
        liveRoomAction = bundle?.getString(SubscriptionPlansActivity.EXTRA_ROOM_ACTION, null),
        source = bundle?.get(
            SubscriptionPlansActivity.EXTRA_SOURCE
        ) as? ClickSource ?: ClickSource.UNKNOWN
    )
}