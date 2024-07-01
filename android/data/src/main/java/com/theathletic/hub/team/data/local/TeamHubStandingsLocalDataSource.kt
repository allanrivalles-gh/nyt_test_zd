package com.theathletic.hub.team.data.local

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.local.InMemoryLocalDataSource
import com.theathletic.scores.standings.data.local.TeamStandingsLocalModel

class TeamHubStandingsLocalDataSource @AutoKoin(Scope.SINGLE) constructor() :
    InMemoryLocalDataSource<String, TeamStandingsLocalModel>()