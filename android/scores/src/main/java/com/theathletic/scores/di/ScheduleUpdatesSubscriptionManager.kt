package com.theathletic.scores.di

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.datetime.TimeProvider
import com.theathletic.other.UniqueSubscriptionsManager
import com.theathletic.scores.data.ScheduleRepository
import com.theathletic.scores.data.ScoresFeedRepository
import com.theathletic.scores.data.local.Schedule
import com.theathletic.utility.Throttler
import com.theathletic.utility.coroutines.DispatcherProvider
import com.theathletic.utility.safeLet
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

class ScheduleUpdatesSubscriptionManager @AutoKoin constructor(
    dispatcherProvider: DispatcherProvider,
    private val timeProvider: TimeProvider,
    private val scheduleRepository: ScheduleRepository,
) {

    private val subscriptionManager = UniqueSubscriptionsManager(::subscribe)
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, t -> Timber.e(t) }
    private val coroutineScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io + coroutineExceptionHandler)

    private var currentKey: String? = null
    private var currentGroupId: String = ""
    private var currentFilterId: String? = null

    private var checkThrottler = Throttler<String>(timeProvider)
    private var lastCheckPerformedAt: Long = 0L

    fun subscribeForUpdates(
        entityKey: String,
        groupId: String,
        filterId: String?,
        forceSubscriptionCheck: Boolean
    ) {
        coroutineScope.launch {
            if (checkThrottler.willRun(groupId) || forceSubscriptionCheck) {
                lastCheckPerformedAt = timeProvider.currentTimeMs
                currentKey = entityKey
                currentGroupId = groupId
                currentFilterId = filterId
                val gameIds = gamesRequiringUpdates(
                    scheduleRepository.getScheduleFeedGroup(
                        entityKey,
                        groupId,
                        filterId
                    )
                )
                if (gameIds.isNotEmpty()) {
                    if (subscriptionManager.set(gameIds.toSet()).not()) {
                        Timber.v("Already subscribed to all games provided")
                    }
                }
            }
        }
    }

    private fun subscribe(ids: Set<String>): () -> Unit {
        val subscribeJob = coroutineScope.launch {
            safeLet(currentKey, currentGroupId) { safeKey, safeGroupId ->
                try {
                    scheduleRepository.subscribeToScheduleUpdates(
                        key = safeKey,
                        groupId = safeGroupId,
                        blockIds = ids.toList()
                    )
                } catch (exception: ScoresFeedRepository.ScoresFeedException) {
                    // As its a subscription log error and carry on. VM does not need to handle error
                    Timber.e(exception)
                }
            }
        }
        return { subscribeJob.cancel() }
    }

    private fun gamesRequiringUpdates(group: Schedule.Group?): List<String> {
        if (group == null) return emptyList()
        return mutableListOf<String>().apply {
            group.sections.forEach { section ->
                addAll(section.games.filter { it.willUpdate }.map { it.gameId })
            }
        }
    }

    fun pause() {
        subscriptionManager.pause()
    }

    fun resume() {
        subscriptionManager.resume()
    }
}