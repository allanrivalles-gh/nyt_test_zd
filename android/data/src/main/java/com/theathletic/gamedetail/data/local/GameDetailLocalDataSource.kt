package com.theathletic.gamedetail.data.local

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.local.InMemoryLocalDataSource

class GameDetailLocalDataSource @AutoKoin(Scope.SINGLE) constructor() :
    InMemoryLocalDataSource<String, GameDetailLocalModel>()