package com.theathletic.rooms.create.ui

import com.theathletic.R
import com.theathletic.entity.room.LiveRoomCategory
import com.theathletic.ui.ResourceString
import com.theathletic.ui.ResourceString.StringWithParams

val LiveRoomCategory.displayString get(): ResourceString =
    StringWithParams(
        when (this) {
            LiveRoomCategory.QUESTION_AND_ANSWER -> R.string.rooms_category_q_and_a
            LiveRoomCategory.BREAKING_NEWS -> R.string.rooms_category_breaking_news
            LiveRoomCategory.GAME_PREVIEW_1_TEAM -> R.string.rooms_category_game_preview_1
            LiveRoomCategory.GAME_PREVIEW_2_TEAM -> R.string.rooms_category_game_preview_2
            LiveRoomCategory.GAME_RECAP -> R.string.rooms_category_game_recap
            LiveRoomCategory.RECURRING -> R.string.rooms_category_recurring
            LiveRoomCategory.LIVE_PODCAST -> R.string.rooms_category_live_podcast
        }
    )