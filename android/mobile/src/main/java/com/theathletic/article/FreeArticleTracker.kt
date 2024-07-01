package com.theathletic.article

import com.google.gson.annotations.SerializedName
import com.theathletic.datetime.TimeProvider
import java.util.Calendar
import java.util.Date

class FreeArticleTracker(
    private val timeProvider: TimeProvider,
    initialState: Snapshot?,
) {
    data class Moment(val year: Int, val month: Int) {
        companion object {
            fun fromDate(date: Date): Moment {
                val calendar = Calendar.getInstance().apply { time = date }
                return Moment(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH))
            }
        }
    }

    data class Snapshot(
        @SerializedName("lastMoment") val lastMoment: Moment,
        @SerializedName("readIds") val readIds: HashSet<Long>,
    )

    private var lastMoment = initialState?.lastMoment ?: Moment.now()
    private val readIds = initialState?.readIds ?: HashSet()
    var onSnapshotChanged: ((Snapshot) -> Unit)? = null

    fun isArticleFree(articleId: Long, limit: Int): Boolean {
        val now = Moment.now()
        if (lastMoment != now) {
            lastMoment = now
            readIds.clear()
        }

        if (readIds.contains(articleId)) return true
        if (readIds.count() >= limit) return false

        readIds.add(articleId)
        onSnapshotChanged?.invoke(snapshot())

        return true
    }

    private fun snapshot(): Snapshot = Snapshot(lastMoment, readIds)

    private fun Moment.Companion.now(): Moment = fromDate(timeProvider.currentDate)
}