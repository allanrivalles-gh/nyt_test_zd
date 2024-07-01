package com.theathletic.scores.ui

import android.text.format.DateUtils
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.datetime.TimeProvider
import com.theathletic.other.UniqueSubscriptionsManager
import com.theathletic.scores.data.ScoresFeedRepository
import com.theathletic.scores.data.local.ScoresFeedDay
import com.theathletic.scores.data.local.ScoresFeedLocalModel
import com.theathletic.utility.coroutines.DispatcherProvider
import com.theathletic.utility.safeLet
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

private const val FIVE_MINS = DateUtils.MINUTE_IN_MILLIS * 5

class ScoresFeedUpdatesSubscriptionManager @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val timeProvider: TimeProvider,
    private val scoresFeedRepository: ScoresFeedRepository
) {
    private val subscriptionManager = UniqueSubscriptionsManager(::subscribe)
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, t -> Timber.e(t) }
    private val coroutineScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io + coroutineExceptionHandler)

    private var currentFeedId: String? = null
    private var dayGroupId: String? = null

    private var lastCheckPerformedAt: Long = 0L

    fun subscribeForUpdates(
        currentFeedIdentifier: String,
        scoresFeed: ScoresFeedLocalModel
    ) {
        if (isTimeToCheck()) {
            scoresFeed.days.find { it.isTopGames }?.let { topGamesDay ->
                lastCheckPerformedAt = timeProvider.currentTimeMs
                currentFeedId = currentFeedIdentifier
                dayGroupId = topGamesDay.day
                val blockIds = getGamesThatCanProvideUpdates(topGamesDay)
                if (subscriptionManager.add(blockIds.toSet()).not()) {
                    Timber.v("Already subscribed to all games provided")
                }
            }
        }
    }

    private fun subscribe(ids: Set<String>): () -> Unit {
        Timber.v("Subscribing to live games: $ids")
        val subscribeJob = coroutineScope.launch {
            safeLet(currentFeedId, dayGroupId) { safeFeedId, safeDayId ->
                try {
                    scoresFeedRepository.subscribeToScoresFeedUpdates(
                        currentFeedIdentifier = safeFeedId,
                        dayGroupIdentifier = safeDayId,
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

    private fun isTimeToCheck(): Boolean =
        lastCheckPerformedAt + FIVE_MINS < timeProvider.currentTimeMs

    private fun getGamesThatCanProvideUpdates(dayFeed: ScoresFeedDay): List<String> {
        return mutableListOf<String>().apply {
            dayFeed.groups.forEach { group ->
                addAll(group.blocks.filter { it.willUpdate }.map { it.id })
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