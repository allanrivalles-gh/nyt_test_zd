package com.theathletic.analytics.data.remote

import com.google.gson.Gson
import com.theathletic.AthleticConfig
import com.theathletic.analytics.data.local.AnalyticsEvent
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Named
import com.theathletic.ui.Transformer
import com.theathletic.user.IUserManager
import java.util.Locale

class AnalyticsToRemoteTransformer @AutoKoin constructor(
    @Named("user-agent") private val userAgent: String,
    val userManager: IUserManager,
    val gson: Gson
) : Transformer<AnalyticsEvent, AnalyticsEventRemote> {

    override fun transform(data: AnalyticsEvent): AnalyticsEventRemote {
        return AnalyticsEventRemote(
            event_timestamp = data.timestampMs,
            user_id = userManager.getCurrentUserId(),
            device_id = userManager.getDeviceId(),
            is_subscriber = userManager.isUserSubscribed(),
            platform = "android",
            browser = "com.theathletic",
            browser_version = AthleticConfig.VERSION_NAME,
            locale = Locale.getDefault().country,
            user_agent = userAgent,
            session_id = userManager.getDeviceId(),
            ip_address = null,

            verb = data.verb,
            view = data.view ?: "",
            element = data.element,
            object_type = data.objectType,
            object_id = data.objectId,
            meta_blob = gson.toJson(data.metaBlob).toString(),
            source = data.source,
            previous_view = data.previousView
        )
    }
}