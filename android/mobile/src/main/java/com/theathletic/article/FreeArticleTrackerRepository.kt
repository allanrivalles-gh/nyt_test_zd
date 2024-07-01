package com.theathletic.article

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.datetime.TimeProvider

class FreeArticleTrackerRepository @AutoKoin(Scope.SINGLE) constructor(
    private val dataSource: FreeArticleTrackerDataSource,
    private val timeProvider: TimeProvider,
) {
    private var tracker: FreeArticleTracker? = null

    fun getTracker(): FreeArticleTracker {
        tracker?.let { return it }
        return FreeArticleTracker(timeProvider, initialState = dataSource.load()).also {
            it.onSnapshotChanged = dataSource::save
            tracker = it
        }
    }
}