package com.theathletic.rooms.local

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.local.InMemoryStaticLocalDataSource

class RoomsTokenLocalStorage @AutoKoin(Scope.SINGLE) constructor() :
    InMemoryStaticLocalDataSource<String, String>()