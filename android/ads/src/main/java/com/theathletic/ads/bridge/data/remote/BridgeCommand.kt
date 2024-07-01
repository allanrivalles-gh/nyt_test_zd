package com.theathletic.ads.bridge.data.remote

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.EnumJsonAdapter

@JsonClass(generateAdapter = true)
data class BridgeCommand(
    val eventName: AdSlotEvent = AdSlotEvent.UnsupportedEvent,
    val eventData: Map<String, Any>?
) {
    companion object {
        private val moshiAdapter = Moshi
            .Builder()
            .add(
                AdSlotEvent::class.java,
                EnumJsonAdapter.create(AdSlotEvent::class.java)
                    .withUnknownFallback(AdSlotEvent.UnsupportedEvent)
            )
            .build()
            .adapter(BridgeCommand::class.java)

        fun fromJson(json: String) = moshiAdapter.fromJson(json)
    }
}