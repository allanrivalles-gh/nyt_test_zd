package com.theathletic.podcast.data.remote

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.SingleRemoteRequest
import com.theathletic.extension.toInt
import com.theathletic.user.IUserManager
import com.theathletic.utility.coroutines.DispatcherProvider
import com.theathletic.utility.datetime.DateUtilityImpl

class UpdatePodcastListenedStateRequest @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val podcastRestApi: PodcastRestApi,
    private val userManager: IUserManager
) : SingleRemoteRequest<
    UpdatePodcastListenedStateRequest.Params,
    Unit
    >(dispatcherProvider) {

    data class Params(
        val podcastEpisodeId: Long,
        val progressMs: Long,
        val finished: Boolean
    )

    override suspend fun makeRemoteRequest(params: Params) {
        podcastRestApi.sendLogPodcastListen(
            podcastEpisodeId = params.podcastEpisodeId,
            timeElapsedInSeconds = (params.progressMs / 1000).toInt(),
            finished = params.finished.toInt(),
            date = DateUtilityImpl.getCurrentTimeInGMT(),
            isSubscriber = userManager.isUserSubscribed().toInt()
        )
    }
}