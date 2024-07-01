package com.theathletic.datetime

import androidx.annotation.VisibleForTesting
import com.theathletic.annotation.autokoin.AutoKoin
import java.util.Calendar
import kotlin.math.absoluteValue

class Chronos @AutoKoin constructor(
    private val timeProvider: TimeProvider = TimeProviderImpl()
) : TimeProvider by timeProvider {
    fun timeDiff(
        from: Datetime = currentDatetime,
        to: Datetime = currentDatetime
    ) = TimeDiff((to.timeMillis - from.timeMillis).absoluteValue)

    fun isThisYear(milliseconds: Long): Boolean {
        val currentYear = Calendar.getInstance().apply { timeInMillis = currentTimeMs }.get(Calendar.YEAR)
        val dateYear = Calendar.getInstance().apply { timeInMillis = milliseconds }.get(Calendar.YEAR)

        return dateYear == currentYear
    }

    fun isWithinWeek(milliseconds: Long): Boolean {
        val dateInOneWeek = Calendar.getInstance().apply {
            timeInMillis = currentTimeMs
            add(Calendar.WEEK_OF_YEAR, 1)
        }.timeInMillis
        return dateInOneWeek > milliseconds
    }

    fun isToday(milliseconds: Long): Boolean {
        val reference = Calendar.getInstance().apply { timeInMillis = milliseconds }
        val now = Calendar.getInstance().apply { timeInMillis = currentTimeMs }

        return reference.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
            reference.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
            reference.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH)
    }
}

data class TimeDiff @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE) constructor(
    private val diffMs: Long
) {

    companion object {
        @Deprecated("This static method makes our formatters untestable", replaceWith = ReplaceWith("Chronos.kt"))
        fun fromNow(
            datetime: Datetime,
            timeProvider: TimeProvider
        ) = TimeDiff(timeProvider.currentTimeMs - datetime.timeMillis)

        @Deprecated("This static method makes our formatters untestable", replaceWith = ReplaceWith("Chronos.kt"))
        fun fromNow(
            datetime: Datetime,
        ) = TimeDiff(System.currentTimeMillis() - datetime.timeMillis)

        @Deprecated("This static method makes our formatters untestable", replaceWith = ReplaceWith("Chronos.kt"))
        fun toNow(
            datetime: Datetime,
        ) = TimeDiff(datetime.timeMillis - System.currentTimeMillis())
    }

    val inSeconds: Long get() = diffMs / 1000
    val inMinutes: Long get() = inSeconds / 60
    val inHours: Long get() = inMinutes / 60
    val inDays: Long get() = inHours / 24
}