package com.theathletic.analytics.data.remote

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.theathletic.analytics.data.local.FlexibleAnalyticsEvent
import com.theathletic.ui.Transformer

abstract class AnalyticsSchemaTransformer<
    out Remote : RemoteAnalyticsRecord,
    SchemaImpl
    >(private val gson: Gson) : Transformer<FlexibleAnalyticsEvent, Remote> {

    override fun transform(data: FlexibleAnalyticsEvent): Remote {
        // Create Map<String, String> from json blob
        val extraFields = gson.fromJson(
            data.extrasJsonBlob,
            object : TypeToken<Map<String, String>>() {}.type
        ) ?: mapOf<String, String>()

        val schemaImpl = getSchemaImpl(gson, data.schemaJsonBlob)

        return mapToRemoteModel(data, schemaImpl, extraFields)
    }

    abstract fun getSchemaImpl(gson: Gson, schemaJsonBlob: String): SchemaImpl

    abstract fun mapToRemoteModel(
        event: FlexibleAnalyticsEvent,
        schemaImpl: SchemaImpl,
        eventFields: Map<String, String>
    ): Remote
}