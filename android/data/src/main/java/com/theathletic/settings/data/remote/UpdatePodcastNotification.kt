package com.theathletic.settings.data.remote

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.entity.local.EntityDataSource
import com.theathletic.podcast.data.local.PodcastSeriesEntity
import com.theathletic.utility.coroutines.DispatcherProvider

class UpdatePodcastNotification @AutoKoin constructor(
    dispatcherProvider: DispatcherProvider,
    private val settingsRestApi: SettingsRestApi,
    private val entityDataSource: EntityDataSource
) : RemoteToLocalFetcher<
    UpdatePodcastNotification.Params,
    Unit,
    Unit
    >(dispatcherProvider) {

    data class Params(val podcastSeriesId: String, val notifyEpisodes: Boolean)

    override suspend fun makeRemoteRequest(params: Params) {
        if (params.notifyEpisodes) {
            settingsRestApi.addPushSettings(
                notifType = "podcast",
                notifName = "podcast",
                notifValue = params.podcastSeriesId.toLong()
            )
        } else {
            settingsRestApi.removePushSettings(
                notifType = "podcast",
                notifName = "podcast",
                notifValue = params.podcastSeriesId.toLong()
            )
        }
    }

    override fun mapToLocalModel(
        params: Params,
        remoteModel: Unit
    ) {
        // Do nothing
    }

    override suspend fun saveLocally(
        params: Params,
        dbModel: Unit
    ) {
        entityDataSource.update<PodcastSeriesEntity>(params.podcastSeriesId) {
            copy(notifyEpisodes = params.notifyEpisodes)
        }
    }
}