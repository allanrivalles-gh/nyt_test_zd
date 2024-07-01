package com.theathletic.settings.data

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.preferences.data.remote.SettingsApi
import com.theathletic.repository.CoroutineRepository
import com.theathletic.settings.data.remote.UpdateCommentNotifications
import com.theathletic.settings.data.remote.UpdatePodcastNotification
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SettingsRepository @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val updatePodcastNotification: UpdatePodcastNotification,
    private val updateCommentNotifications: UpdateCommentNotifications,
    private val settingsApi: SettingsApi
) : CoroutineRepository {

    override val repositoryScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)

    fun updatePodcastNotification(id: String, notifyEpisodes: Boolean) = repositoryScope.launch {
        updatePodcastNotification.fetchRemote(
            UpdatePodcastNotification.Params(podcastSeriesId = id, notifyEpisodes = notifyEpisodes)
        )
    }

    fun updateCommentNotification(notifyReplies: Boolean) = repositoryScope.launch {
        updateCommentNotifications.fetchRemote(
            UpdateCommentNotifications.Params(notifyReplies = notifyReplies)
        )
    }

    suspend fun updateTopSportsNewsNotification(optIn: Boolean) = settingsApi.setTopSportsNewsNotificationOpt(optIn)
}