package com.theathletic.analytics.data.remote

import com.theathletic.AthleticConfig
import com.theathletic.analytics.data.local.FlexibleAnalyticsEvent
import com.theathletic.analytics.newarch.schemas.AnalyticsSchema
import com.theathletic.analytics.newarch.schemas.KafkaTopic
import com.theathletic.annotation.autokoin.AutoKoin

class AnalyticsBatchBuilder @AutoKoin constructor(
    private val impressionTransformer: ImpressionTransformer
) {
    fun buildBatch(
        topic: KafkaTopic,
        events: List<FlexibleAnalyticsEvent>
    ): AnalyticsEventBatch {
        val transformer = getTransformerForSchema(topic.schema)

        return AnalyticsEventBatch(
            version = AthleticConfig.VERSION_NAME,
            topic = topic.topic,
            schemaId = topic.schema.schemaId,
            records = events.map(transformer::transform)
        )
    }

    private fun getTransformerForSchema(
        schema: AnalyticsSchema.Type
    ): AnalyticsSchemaTransformer<RemoteAnalyticsRecord, *> = when (schema) {
        AnalyticsSchema.Type.IMPRESSION -> impressionTransformer
    }
}