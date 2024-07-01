package com.theathletic.rooms.ui

import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Exposes
import com.theathletic.rooms.analytics.LiveRoomEntryPoint

@Suppress("LongParameterList")
interface LiveAudioRoomAnalytics {
    fun trackView(
        roomId: String,
        element: String = "",
        view: String = "liveroom_mainstage",
        objectType: String = "room_id",
        objectId: String = roomId,
        entryPoint: LiveRoomEntryPoint? = null,
        isLive: String = "",
    )
    fun trackClick(
        roomId: String,
        element: String,
        view: String = "liveroom_mainstage",
        objectType: String = "room_id",
        objectId: String = roomId,
    )
    fun trackCustom(
        verb: String,
        roomId: String,
        element: String,
        view: String = "liveroom_mainstage",
        objectType: String = "room_id",
        objectId: String = roomId,
    )
}

@Exposes(LiveAudioRoomAnalytics::class)
class LiveAudioRoomAnalyticsHandler @AutoKoin constructor(
    val analytics: Analytics
) : LiveAudioRoomAnalytics {

    override fun trackView(
        roomId: String,
        element: String,
        view: String,
        objectType: String,
        objectId: String,
        entryPoint: LiveRoomEntryPoint?,
        isLive: String,
    ) {
        analytics.track(
            Event.LiveRoom.View(
                view = view,
                object_type = objectType,
                object_id = objectId,
                element = element,
                room_id = roomId,
                entry_point = entryPoint?.value.orEmpty(),
                is_live = isLive,
            )
        )
    }

    override fun trackClick(
        roomId: String,
        element: String,
        view: String,
        objectType: String,
        objectId: String
    ) {
        analytics.track(
            Event.LiveRoom.Click(
                view = view,
                element = element,
                object_type = objectType,
                object_id = objectId,
                room_id = roomId,
            )
        )
    }

    override fun trackCustom(
        verb: String,
        roomId: String,
        element: String,
        view: String,
        objectType: String,
        objectId: String
    ) {
        analytics.track(
            Event.LiveRoom.Custom(
                verb = verb,
                view = view,
                element = element,
                object_type = objectType,
                object_id = objectId,
                room_id = roomId,
            )
        )
    }
}