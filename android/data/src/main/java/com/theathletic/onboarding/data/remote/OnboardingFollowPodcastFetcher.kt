package com.theathletic.onboarding.data.remote

import com.theathletic.FollowPodcastMutation
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.audio.data.remote.AudioApi
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.onboarding.data.local.OnboardingPodcastsDataSource
import com.theathletic.podcast.data.local.PodcastDao
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.flow.firstOrNull

class OnboardingFollowPodcastFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val audioApi: AudioApi,
    private val onboardingPodcastsDataSource: OnboardingPodcastsDataSource,
    private val podcastDao: PodcastDao
) : RemoteToLocalFetcher<
    OnboardingFollowPodcastFetcher.Params,
    FollowPodcastMutation.Data,
    OnboardingFollowPodcastFetcher.Params?
    >(dispatcherProvider) {

    data class Params(val podcastId: String)

    override suspend fun makeRemoteRequest(params: Params) = audioApi.followPodcast(params.podcastId).data

    override fun mapToLocalModel(
        params: Params,
        remoteModel: FollowPodcastMutation.Data
    ) = if (remoteModel.followPodcast.success) params else null

    override suspend fun saveLocally(params: Params, dbModel: Params?) {
        dbModel?.let { model ->
            val current = onboardingPodcastsDataSource.item.firstOrNull() ?: return
            current.firstOrNull { it.id.toString() == model.podcastId }?.let { podcast ->
                val index = current.indexOf(podcast)
                val new = current.toMutableList().apply {
                    removeAt(index)
                    add(index, podcast.copy(selected = true))
                }
                onboardingPodcastsDataSource.update(new)
                podcastDao.insertOrUpdatePodcastFollowing(
                    podcast.toPodcastEntity().apply { isFollowing = true }
                )
            }
        }
    }
}