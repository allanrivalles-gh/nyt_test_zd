package com.theathletic.rooms.create.ui

enum class LiveRoomTagType {
    TEAM,
    LEAGUE,

    // Since tags show up in the same search UI as other non-tag items, this helps us reuse some
    // functions with the other search modes without needing to duplicate code.
    NONE,
}