package com.theathletic.podcast.data.local

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.local.InMemoryLocalDataSource
import com.theathletic.entity.local.AthleticEntity

class PodcastNewEpisodesDataSource @AutoKoin(Scope.SINGLE) constructor() : InMemoryLocalDataSource<Any, List<AthleticEntity.Id>>() {

    private object Key

    fun update(data: List<AthleticEntity.Id>) {
        update(Key, data)
    }

    fun getItem() = observeItem(Key)
}