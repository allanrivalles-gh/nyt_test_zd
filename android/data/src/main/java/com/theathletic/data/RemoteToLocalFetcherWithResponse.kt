package com.theathletic.data

import com.theathletic.network.ResponseStatus
import com.theathletic.repository.safeApiRequest
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.Exception

/**
 * Similar to [RemoteToLocalFetcher] but only to be used when there is a request where the response
 * needs to be immediately read in the [Presenter] and isn't used anywhere else.
 *
 * For example, when a host creates live room, we make a request to create the room then immediately
 * need the ID from that created room to start the Live Room activity with. This scenario does not
 * fit in to our typical uni-directional data flow model so we should use this fetcher instead of
 * [RemoteToLocalFetcher].
 */
abstract class RemoteToLocalFetcherWithResponse<Params, RemoteModel, LocalModel>(
    val dispatcherProvider: DispatcherProvider
) {

    suspend fun fetchRemote(
        params: Params
    ): LocalModel? = withContext(dispatcherProvider.io) {
        try {
            val response = safeApiRequest(coroutineContext) { makeRemoteRequest(params) }
            when (response) {
                is ResponseStatus.Success -> {
                    val localModel = mapToLocalModel(params, response.body)
                    saveLocally(params, localModel)
                    return@withContext localModel
                }
                is ResponseStatus.Error -> {
                    Timber.e(response.throwable)
                }
            }
        } catch (e: Exception) {
            logFetchRemoteException(e)
        }
        null
    }

    open fun logFetchRemoteException(t: Throwable) = Timber.e(t)

    protected abstract suspend fun makeRemoteRequest(params: Params): RemoteModel?

    protected abstract fun mapToLocalModel(params: Params, remoteModel: RemoteModel): LocalModel

    protected abstract suspend fun saveLocally(params: Params, dbModel: LocalModel)
}