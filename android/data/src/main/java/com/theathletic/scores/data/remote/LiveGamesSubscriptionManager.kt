package com.theathletic.scores.data.remote

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.other.UniqueSubscriptionsManager
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

class LiveGamesSubscriptionManager @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val liveGamesSubscriber: LiveGamesSubscriber,
) {
    private val manager = UniqueSubscriptionsManager(::subscribe)
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, t -> Timber.e(t) }
    private val coroutineScope = CoroutineScope(dispatcherProvider.io + coroutineExceptionHandler)

    fun subscribeToGames(gameIds: List<String>) {
        if (!manager.add(gameIds.toSet())) {
            Timber.v("Already subscribed to all games provided")
        }
    }

    fun pause() {
        Timber.v("Pausing live games subscription.")
        manager.pause()
    }

    fun resume() {
        Timber.v("Resuming live games subscription")
        manager.resume()
    }

    private fun subscribe(ids: Set<String>): () -> Unit {
        Timber.v("Subscribing to live games: $ids")
        val job = coroutineScope.launch {
            liveGamesSubscriber.subscribe(LiveGamesSubscriber.Params(gameIds = ids))
        }
        return { job.cancel() }
    }
}