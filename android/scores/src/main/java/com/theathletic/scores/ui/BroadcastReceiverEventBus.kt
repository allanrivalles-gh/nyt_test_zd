package com.theathletic.scores.ui

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class DateChangeEventProducer(
    private val mutableSharedFlow: MutableSharedFlow<DateChangeEvents> = MutableSharedFlow()
) : MutableSharedFlow<DateChangeEvents> by mutableSharedFlow

class DateChangeEventConsumer(
    private val producer: DateChangeEventProducer
) : Flow<DateChangeEvents> by producer

sealed class DateChangeEvents {
    object OnDateChanged : DateChangeEvents()
}