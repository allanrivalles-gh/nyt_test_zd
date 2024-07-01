package com.theathletic.datetime

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Exposes
import com.theathletic.annotation.autokoin.Scope

interface TimeCalculator {
    fun timeDiffFromNow(datetime: Datetime): TimeDiff
}

@Exposes(TimeCalculator::class)
class TimeCalculatorImpl @AutoKoin(Scope.SINGLE) constructor(
    private val timeProvider: TimeProvider
) : TimeCalculator {

    override fun timeDiffFromNow(datetime: Datetime): TimeDiff {
        return TimeDiff.fromNow(datetime, timeProvider)
    }
}