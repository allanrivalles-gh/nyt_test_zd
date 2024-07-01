package com.theathletic.announcement.remote

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.announcement.AnnouncementApi
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.entity.local.EntityDataSource
import com.theathletic.feed.data.local.AnnouncementEntity
import com.theathletic.utility.coroutines.DispatcherProvider

class AnnouncementClickFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val announcementApi: AnnouncementApi,
    private val entityDataSource: EntityDataSource,
) : RemoteToLocalFetcher<
    AnnouncementClickFetcher.Params,
    Unit,
    Unit
    >(dispatcherProvider) {

    data class Params(
        val announcementId: String
    )

    override suspend fun makeRemoteRequest(params: Params) {
        announcementApi.announcementClicked(params.announcementId)
    }

    override fun mapToLocalModel(params: Params, remoteModel: Unit) {
        // No op
    }

    override suspend fun saveLocally(params: Params, dbModel: Unit) {
        entityDataSource.update<AnnouncementEntity>(params.announcementId) {
            copy(isDismissed = true)
        }
    }
}

class AnnouncementDismissFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val announcementApi: AnnouncementApi,
    private val entityDataSource: EntityDataSource,
) : RemoteToLocalFetcher<
    AnnouncementDismissFetcher.Params,
    Unit,
    Unit
    >(dispatcherProvider) {

    data class Params(
        val announcementId: String
    )

    override suspend fun makeRemoteRequest(params: Params) {
        announcementApi.announcementHidden(params.announcementId)
    }

    override fun mapToLocalModel(params: Params, remoteModel: Unit) {
        // No op
    }

    override suspend fun saveLocally(params: Params, dbModel: Unit) {
        entityDataSource.update<AnnouncementEntity>(params.announcementId) {
            copy(isDismissed = true)
        }
    }
}