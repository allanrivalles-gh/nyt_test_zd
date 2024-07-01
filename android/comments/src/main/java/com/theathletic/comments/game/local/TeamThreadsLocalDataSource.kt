package com.theathletic.comments.game.local

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.comments.game.TeamThreads
import com.theathletic.data.local.InMemoryLocalDataSource

class TeamThreadsLocalDataSource @AutoKoin(Scope.SINGLE) constructor() : InMemoryLocalDataSource<String, TeamThreads>()