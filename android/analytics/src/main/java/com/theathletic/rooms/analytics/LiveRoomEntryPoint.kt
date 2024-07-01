package com.theathletic.rooms.analytics

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope

enum class LiveRoomEntryPoint(val value: String) {
    UNIVERSAL_LINK("universal_link"),
    DEEPLINK("push"),
    FEED("feed"),
    LISTEN_TAB("listen"),
}

class LiveRoomAnalyticsContext @AutoKoin(Scope.SINGLE) constructor() {
    var roomIdToEntryPoint: MutableMap<String, LiveRoomEntryPoint?> = mutableMapOf()
}