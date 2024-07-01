package com.theathletic.gamedetail.data

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.entity.main.Sport
import com.theathletic.other.UniqueSubscriptionsManager
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

class PlayerGradesSubscriptionManager @AutoKoin constructor(
    dispatcherProvider: DispatcherProvider,
    private val playerGradesRepository: PlayerGradesRepository
) {
    private val subscriptionManager = UniqueSubscriptionsManager(::subscribe)
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, t -> Timber.e(t) }
    private val coroutineScope = CoroutineScope(dispatcherProvider.io + coroutineExceptionHandler)

    private var pgSport: Sport = Sport.UNKNOWN

    fun subscribeForUpdates(
        gameId: String,
        sport: Sport
    ) {
        pgSport = sport
        if (subscriptionManager.add(setOf(gameId)).not()) {
            Timber.v("Already subscribed to player grade updates for provided games")
        }
    }

    private fun subscribe(ids: Set<String>): () -> Unit {
        val job = coroutineScope.launch {
            try {
                ids.firstOrNull()?.let { gameId ->
                    playerGradesRepository.subscribeForPlayerGradesUpdates(gameId, pgSport)
                }
            } catch (exception: PlayerGradesRepository.PlayerGradesException) {
                // As its a subscription log error and carry on. VM does not need to handle error
                Timber.e(exception)
            }
        }
        return { job.cancel() }
    }

    fun pause() {
        subscriptionManager.pause()
    }

    fun resume() {
        subscriptionManager.resume()
    }
}