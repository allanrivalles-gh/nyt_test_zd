package com.theathletic.injection

import com.theathletic.rooms.ui.LiveAudioEventConsumer
import com.theathletic.rooms.ui.LiveAudioEventProducer
import org.koin.dsl.module

val liveAudioModule = module {
    single { LiveAudioEventProducer() }
    single { LiveAudioEventConsumer(get()) }
}