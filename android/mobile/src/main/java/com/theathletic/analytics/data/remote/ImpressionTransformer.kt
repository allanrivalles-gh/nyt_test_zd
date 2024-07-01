package com.theathletic.analytics.data.remote

import com.google.gson.Gson
import com.theathletic.AthleticConfig
import com.theathletic.analytics.data.local.FlexibleAnalyticsEvent
import com.theathletic.analytics.newarch.schemas.AnalyticsSchema
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Named
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.user.IUserManager
import java.util.Locale

class ImpressionTransformer @AutoKoin(Scope.SINGLE) constructor(
    @Named("user-agent") private val userAgent: String,
    val userManager: IUserManager,
    val gson: Gson
) : AnalyticsSchemaTransformer<
    ImpressionEventRecordRemote,
    AnalyticsSchema.Local.Impression
    >(gson) {

    override fun getSchemaImpl(
        gson: Gson,
        schemaJsonBlob: String
    ): AnalyticsSchema.Local.Impression = gson.fromJson(
        schemaJsonBlob,
        AnalyticsSchema.Local.Impression::class.java
    )

    override fun mapToRemoteModel(
        event: FlexibleAnalyticsEvent,
        schemaImpl: AnalyticsSchema.Local.Impression,
        eventFields: Map<String, String>
    ): ImpressionEventRecordRemote {
        return ImpressionEventRecordRemote(
            event_timestamp = event.timestampMs,
            user_id = userManager.getCurrentUserId(),
            device_id = userManager.getDeviceId(),
            platform = "android",
            browser = "com.theathletic",
            browser_version = AthleticConfig.VERSION_NAME,
            locale = Locale.getDefault().toLanguageTag(),
            user_agent = userAgent,
            session_id = userManager.getDeviceId(),

            verb = schemaImpl.verb,
            view = schemaImpl.view,
            object_type = schemaImpl.object_type,
            object_id = schemaImpl.object_id,
            impress_start_time = schemaImpl.impress_start_time,
            impress_end_time = schemaImpl.impress_end_time,
            filter_type = schemaImpl.filter_type,
            filter_id = schemaImpl.filter_id,
            v_index = schemaImpl.v_index,
            h_index = schemaImpl.h_index,
            element = schemaImpl.element,
            container = schemaImpl.container,
            page_order = schemaImpl.page_order,
            parent_object_type = schemaImpl.parent_object_type,
            parent_object_id = schemaImpl.parent_object_id
        )
    }
}