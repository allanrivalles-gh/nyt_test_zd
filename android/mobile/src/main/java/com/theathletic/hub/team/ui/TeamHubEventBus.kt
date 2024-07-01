package com.theathletic.hub.team.ui

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class TeamHubEventProducer(
    private val mutableSharedFlow: MutableSharedFlow<Event> = MutableSharedFlow()
) : MutableSharedFlow<Event> by mutableSharedFlow

class TeamHubEventConsumer(
    private val producer: TeamHubEventProducer
) : Flow<Event> by producer

sealed class Event : com.theathletic.utility.Event() {
    object AutoScrollCompleted : Event()
}