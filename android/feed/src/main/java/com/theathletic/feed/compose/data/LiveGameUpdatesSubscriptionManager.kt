package com.theathletic.feed.compose.data

import android.text.format.DateUtils
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.datetime.Datetime
import com.theathletic.datetime.TimeProvider
import com.theathletic.other.UniqueSubscriptionsManager
import com.theathletic.scores.data.local.GameState
import com.theathletic.utility.Throttler
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

private const val THROTTLER_INTERVAL = DateUtils.MINUTE_IN_MILLIS * 1

internal class LiveGameUpdatesSubscriptionManager @AutoKoin constructor(
    dispatcherProvider: DispatcherProvider,
    timeProvider: TimeProvider,
    private val feedRepository: FeedRepository,
    private val gameSubscriptionHelper: GameSubscriptionHelper
) {

    private val subscriptionManager = UniqueSubscriptionsManager(::subscribe)
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, t -> Timber.e(t) }
    private val coroutineScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io + coroutineExceptionHandler)

    private var checkThrottler = Throttler<String>(timeProvider, THROTTLER_INTERVAL)
    private lateinit var feedKey: String

    fun subscribeToLiveGameUpdates(key: String, feed: Feed) {
        feedKey = key
        if (checkThrottler.willRun(key)) {
            val subscribeTo = extractLiveGameIds(feed.layouts)
            subscriptionManager.set(subscribeTo.distinct().toSet())
        }
    }

    private fun extractLiveGameIds(layouts: List<Layout>): List<String> {
        return mutableListOf<String>().apply {
            layouts
                .filter { it is FeaturedGameLayout || it is ScoresCarouselLayout }
                .forEach { layout ->
                    when (layout) {
                        is FeaturedGameLayout -> addAll(
                            layout.items.mapNotNull {
                                subscribeToGameIfLive(it.id, it.game.state, it.game.scheduledAt)
                            }
                        )
                        is ScoresCarouselLayout -> addAll(
                            layout.items.mapNotNull {
                                subscribeToGameIfLive(it.id, it.game.state, it.game.scheduledAt)
                            }
                        )
                    }
                }
        }
    }

    private fun subscribeToGameIfLive(id: String, state: GameState, scheduledAt: Datetime?) =
        if (gameSubscriptionHelper.isGameLiveOrAboutToStart(state, scheduledAt)) id else null

    private fun subscribe(gameIds: Set<String>): () -> Unit {
        val subscribeJob = coroutineScope.launch {
            try {
                feedRepository.subscribeToLiveGameUpdates(feedKey, gameIds.toList())
            } catch (exception: Exception) {
                Timber.e(exception)
            }
        }
        return { subscribeJob.cancel() }
    }

    fun pause() {
        subscriptionManager.pause()
    }

    fun resume() {
        subscriptionManager.resume()
    }
}