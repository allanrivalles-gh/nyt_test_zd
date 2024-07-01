package com.theathletic.announcement

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.announcement.remote.AnnouncementClickFetcher
import com.theathletic.announcement.remote.AnnouncementDismissFetcher
import com.theathletic.entity.local.EntityDataSource
import com.theathletic.feed.data.local.AnnouncementEntity
import com.theathletic.repository.CoroutineRepository
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class AnnouncementsRepository @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val announcementClickFetcher: AnnouncementClickFetcher,
    private val announcementDismissFetcher: AnnouncementDismissFetcher,
    private val entityDataSource: EntityDataSource
) : CoroutineRepository {
    override val repositoryScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)

    suspend fun getAnnouncement(id: String) = entityDataSource.get<AnnouncementEntity>(id)

    suspend fun markAnnouncementClicked(id: String) {
        announcementClickFetcher.fetchRemote(AnnouncementClickFetcher.Params(id))
    }

    suspend fun markAnnouncementDismissed(id: String) {
        announcementDismissFetcher.fetchRemote(AnnouncementDismissFetcher.Params(id))
    }

    suspend fun isAnnouncementDismissed(id: String): Boolean {
        return entityDataSource.get<AnnouncementEntity>(id)?.isDismissed ?: false
    }
}