package com.theathletic.analytics.repository

import com.theathletic.AthleticConfig
import com.theathletic.analytics.AnalyticsEndpointConfig
import com.theathletic.analytics.data.local.AnalyticsEvent
import com.theathletic.analytics.data.local.FlexibleAnalyticsEvent
import com.theathletic.analytics.data.remote.AnalyticsBatchBuilder
import com.theathletic.analytics.data.remote.AnalyticsEventBatch
import com.theathletic.analytics.data.remote.AnalyticsToRemoteTransformer
import com.theathletic.analytics.newarch.schemas.KafkaTopic
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import timber.log.Timber

class AnalyticsRepository @AutoKoin(Scope.SINGLE) constructor(
    private val eventDao: AnalyticsEventDao,
    private val flexibleAnalyticsEventDao: FlexibleAnalyticsEventDao,
    private val analyticsApi: AnalyticsApi,
    private val analyticsTransformer: AnalyticsToRemoteTransformer,
    private val endpointConfig: AnalyticsEndpointConfig,
    private val analyticsBatchBuilder: AnalyticsBatchBuilder
) {

    suspend fun clearEventsOlderThanXDays(days: Int) {
        Timber.i("clearEventsOlderThanXDays(days: $days)")
        eventDao.deleteEventsBefore("-$days day")
    }

    suspend fun getAnalyticsEventsToPost(batchSize: Int): List<AnalyticsEvent> {
        Timber.i("getAnalyticsEventsToPost(batchSize: $batchSize)")
        return eventDao.getEvents(batchSize)
    }

    suspend fun hasAnalyticsEventsToPost(): Boolean {
        Timber.i("hasAnalyticsEventsToPost()")
        return eventDao.getEventCount() > 0
    }

    suspend fun saveAnalyticsEvents(events: List<AnalyticsEvent>) {
        Timber.i("saveAnalyticsEvents()")
        return eventDao.insertEvents(events)
    }

    suspend fun uploadAnalyticsEvents(events: List<AnalyticsEvent>) {
        Timber.i("uploadAnalyticsEvents()")

        // Errors are caught at uploadAnalyticsEvents callsite
        analyticsApi.postAnalytics(
            AnalyticsEventBatch(
                version = AthleticConfig.VERSION_NAME,
                topic = endpointConfig.topic,
                schemaId = endpointConfig.schemaId,
                records = events.map { analyticsTransformer.transform(it) }
            )
        )
    }

    suspend fun deleteAnalyticsEvents(events: List<AnalyticsEvent>) {
        Timber.i("deleteAnalyticsEvents()")
        eventDao.deleteEvents(events)
    }

    // Flexible Schemas
    suspend fun saveFlexibleAnalyticsEvents(events: List<FlexibleAnalyticsEvent>) {
        return flexibleAnalyticsEventDao.insertEvents(events)
    }

    suspend fun hasFlexibleAnalyticsEventsToPost(topic: KafkaTopic): Boolean {
        return flexibleAnalyticsEventDao.getEventCount(topic.topic) > 0
    }

    suspend fun getFlexibleAnalyticsEventsToPost(
        topic: KafkaTopic,
        batchSize: Int
    ): List<FlexibleAnalyticsEvent> {
        Timber.i("getAnalyticsEventsToPost(batchSize: $batchSize)")
        return flexibleAnalyticsEventDao.getEvents(topic.topic, batchSize)
    }

    suspend fun deleteFlexibleAnalyticsEvents(events: List<FlexibleAnalyticsEvent>) {
        flexibleAnalyticsEventDao.deleteEvents(events)
    }

    suspend fun deleteAllFlexibleAnalyticsEvents() {
        flexibleAnalyticsEventDao.deleteAllEvents()
    }

    suspend fun uploadFlexibleAnalyticsEvents(
        topic: KafkaTopic,
        events: List<FlexibleAnalyticsEvent>
    ) {
        Timber.i("uploadAnalyticsEvents()")

        val batchModel = analyticsBatchBuilder.buildBatch(topic, events)

        // Errors are caught at uploadAnalyticsEvents callsite
        analyticsApi.postAnalytics(batchModel)
    }

    suspend fun clearFlexibleEventsOlderThanXDays(days: Int) {
        Timber.i("clearFlexibleEventsOlderThanXDays(days: $days)")
        flexibleAnalyticsEventDao.deleteEventsBefore("-$days day")
    }
}