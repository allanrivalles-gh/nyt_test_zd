package com.theathletic.data

import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * This is used to set up long-running GraphQL subscriptions. [subscribe] is a `suspend` function
 * so it must be called from separate scope. Internally, we set up a collector to listen for the
 * updates on the IO dispatcher, but as a child of that scope. When the scope that called
 * [subscribe] is cancelled, then the subscription finishes. This means we have to be careful
 * which scopes we use to call [subscribe].
 *
 * For a subscription that we want to live while a specific screen is active, we can use the
 * [AthleticPresenter.presenterScope]. When we leave the screen and [presenterScope] gets cancelled,
 * then the subscription will end.
 */
abstract class RemoteToLocalSubscriber<Params, RemoteModel, LocalModel>(
    val dispatcherProvider: DispatcherProvider
) {
    private val exceptionHandler = CoroutineExceptionHandler { _, e -> logRemoteException(e) }

    suspend fun subscribe(params: Params) {
        val collector = FlowCollector<RemoteModel> { value ->
            val localModel = mapToLocalModel(params, value)
            saveLocally(params, localModel)
        }

        coroutineScope {
            launch(dispatcherProvider.io + exceptionHandler) {
                try {
                    makeRemoteRequest(params).collect(collector)
                } catch (e: Exception) {
                    exceptionHandler.handleException(coroutineContext, e)
                }
            }
        }
    }

    open fun logRemoteException(t: Throwable) = Timber.e(t)

    protected abstract suspend fun makeRemoteRequest(params: Params): Flow<RemoteModel>

    protected abstract fun mapToLocalModel(params: Params, remoteModel: RemoteModel): LocalModel

    protected abstract suspend fun saveLocally(params: Params, dbModel: LocalModel)
}