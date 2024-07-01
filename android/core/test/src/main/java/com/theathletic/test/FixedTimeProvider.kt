package com.theathletic.test

import com.theathletic.datetime.Datetime
import com.theathletic.datetime.TimeProvider
import java.util.Date

/**
 * This class provides a way to work with a fixed or predetermined time in testing scenarios.
 * It implements the TimeProvider interface.
 *
 * At the moment of object creation, it uses the current system time as a default, but allows
 * the time to be manually set to a specific value at any point in the lifecycle of the object.
 * This flexibility enables control over time-dependent behavior in tests.
 *
 * The class has three main functions:
 *
 * - `currentTimeMs` allows setting/getting the time in milliseconds since the Unix Epoch.
 * - `currentDatetime` returns a Datetime object representing the set time.
 * - `currentDate` returns a Date object representing the set time.
 *
 * Usage:
 *
 * ```kotlin
 * // create the class and set a specific time
 * val testTimeProvider = FixedTimeProvider()
 * testTimeProvider.currentTimeMs = Calendar.getInstance().apply {
 *     set(Calendar.YEAR, 2023)
 *     set(Calendar.MONTH, Calendar.MARCH)
 *     set(Calendar.DAY_OF_MONTH, 27)
 * }.timeInMillis
 * // all the methods will return that specific time
 * val fixedTimeInMs = testTimeProvider.currentTimeMs
 * val fixedDatetime = testTimeProvider.currentDatetime
 * val fixedDate = testTimeProvider.currentDate
 * ```
 */
class FixedTimeProvider : TimeProvider {
    override var currentTimeMs: Long = System.currentTimeMillis()
    override val currentDatetime: Datetime
        get() = Datetime(currentTimeMs)
    override val currentDate: Date
        get() = Date(currentTimeMs)
}