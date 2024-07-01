package com.theathletic.rooms.create.data.local

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.local.InMemorySingleLocalDataSource
import com.theathletic.rooms.create.ui.LiveRoomTagType
import com.theathletic.utility.LogoUtility

data class LiveRoomTagOption(
    val id: String,
    val type: LiveRoomTagType,
    val title: String,
    val name: String,
    val shortname: String,
) {
    val logoUrl: String = when (type) {
        LiveRoomTagType.TEAM -> LogoUtility.getTeamSmallLogoPath(id.toInt())
        LiveRoomTagType.LEAGUE -> LogoUtility.getColoredLeagueLogoPath(id.toInt())
        else -> ""
    }
}

data class LiveRoomHostOption(
    val id: String,
    val name: String,
    val avatarUrl: String,
)

class LiveRoomTagOptionsLocalDataSource @AutoKoin(Scope.SINGLE) constructor() :
    InMemorySingleLocalDataSource<List<LiveRoomTagOption>>()

class LiveRoomHostOptionsLocalDataSource @AutoKoin(Scope.SINGLE) constructor() :
    InMemorySingleLocalDataSource<List<LiveRoomHostOption>>()