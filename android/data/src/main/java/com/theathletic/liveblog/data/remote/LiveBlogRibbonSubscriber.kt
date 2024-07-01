package com.theathletic.liveblog.data.remote

import com.theathletic.LiveBlogBannerSubscription
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.RemoteToLocalSubscriber
import com.theathletic.datetime.Datetime
import com.theathletic.entity.local.EntityDataSource
import com.theathletic.liveblog.data.local.LiveBlogEntity
import com.theathletic.type.LiveStatus
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge

class LiveBlogRibbonSubscriber @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val entityDataSource: EntityDataSource,
    private val liveBlogApi: LiveBlogApi,
) : RemoteToLocalSubscriber<
    LiveBlogRibbonSubscriber.Params,
    LiveBlogBannerSubscription.Data,
    LiveBlogEntity?
    >(dispatcherProvider) {

    data class Params(val liveBlogIds: Set<String>)

    override suspend fun makeRemoteRequest(params: Params): Flow<LiveBlogBannerSubscription.Data> {
        val subscriptions = params.liveBlogIds.map {
            liveBlogApi.subscribeToLiveBlogBannerUpdates(it)
        }

        return merge(*subscriptions.toTypedArray())
    }

    override fun mapToLocalModel(
        params: Params,
        remoteModel: LiveBlogBannerSubscription.Data
    ): LiveBlogEntity {
        return LiveBlogEntity(
            id = remoteModel.updatedLiveBlog.id,
            title = remoteModel.updatedLiveBlog.title,
            lastActivityAt = Datetime(remoteModel.updatedLiveBlog.lastActivityAt),
            isLive = remoteModel.updatedLiveBlog.liveStatus == LiveStatus.live
        )
    }

    override suspend fun saveLocally(params: Params, dbModel: LiveBlogEntity?) {
        dbModel ?: return
        entityDataSource.insertOrUpdate(dbModel)
    }
}