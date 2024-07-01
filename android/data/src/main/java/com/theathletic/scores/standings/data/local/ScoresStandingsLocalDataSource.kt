package com.theathletic.scores.standings.data.local

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.local.InMemoryLocalDataSource
import com.theathletic.entity.main.League

class ScoresStandingsLocalDataSource @AutoKoin(Scope.SINGLE) constructor() :
    InMemoryLocalDataSource<League, ScoresStandingsLocalModel>()