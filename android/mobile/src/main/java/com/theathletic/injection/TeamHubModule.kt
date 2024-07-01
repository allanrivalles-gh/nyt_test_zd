package com.theathletic.injection

import com.theathletic.hub.team.ui.TeamHubEventConsumer
import com.theathletic.hub.team.ui.TeamHubEventProducer
import org.koin.dsl.module

val teamHubModule = module {
    single { TeamHubEventProducer() }
    single { TeamHubEventConsumer(get()) }
}