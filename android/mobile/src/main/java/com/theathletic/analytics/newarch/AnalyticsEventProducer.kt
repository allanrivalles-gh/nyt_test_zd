package com.theathletic.analytics.newarch

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class AnalyticsEventProducer(
    private val mutableSharedFlow: MutableSharedFlow<Analytics.DebugToolEvent> = MutableSharedFlow()
) :
    MutableSharedFlow<Analytics.DebugToolEvent> by mutableSharedFlow

class AnalyticsEventConsumer(private val producer: AnalyticsEventProducer) :
    Flow<Analytics.DebugToolEvent> by producer