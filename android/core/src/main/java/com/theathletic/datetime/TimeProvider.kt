package com.theathletic.datetime

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Exposes
import com.theathletic.annotation.autokoin.Scope
import java.util.Date

interface TimeProvider {
    val currentTimeMs: Long
    val currentDatetime: Datetime
    val currentDate: Date
}

@Exposes(TimeProvider::class)
class TimeProviderImpl @AutoKoin(Scope.SINGLE) constructor() : TimeProvider {

    override val currentTimeMs get() = System.currentTimeMillis()
    override val currentDatetime get() = Datetime(currentTimeMs)
    override val currentDate get() = Date()
}