package com.theathletic.data

import com.theathletic.repository.safeApiRequest
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Types the parameters as RemoteModel and LocalModel
 */
abstract class RemoteToLocalSingleFetcher<
    Params,
    RemoteModel : com.theathletic.data.RemoteModel,
    LocalModel : com.theathletic.data.LocalModel>(
    dispatcherProvider: DispatcherProvider
) : RemoteToLocalFetcher<Params, RemoteModel, LocalModel>(dispatcherProvider)

/**
 * Types the parameters as a list of RemoteModel and DbModel
 */
abstract class RemoteToLocalListFetcher<
    Params,
    RemoteModel : com.theathletic.data.RemoteModel,
    LocalModel : com.theathletic.data.LocalModel>(
    dispatcherProvider: DispatcherProvider
) : RemoteToLocalFetcher<Params, List<RemoteModel>, List<LocalModel>>(dispatcherProvider)

// lint error here if not using braces - https://issuetracker.google.com/issues/216418684#comment3
object EmptyParams { /* comment */ }

/**
 * Do not use directly, use either [RemoteToLocalSingleFetcher] or [RemoteToLocalListFetcher]
 */
abstract class RemoteToLocalFetcher<Params, RemoteModel, LocalModel>(
    val dispatcherProvider: DispatcherProvider
) {

    private val fetcherScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)

    private val inflightCache = mutableMapOf<Params, Job?>()

    suspend fun fetchRemote(params: Params) {
        fetchRemoteInternal(params).join()
    }

    private fun fetchRemoteInternal(params: Params): Job {
        val job = inflightCache[params]
        if (job?.isActive == true) return job

        val fetchJob = fetcherScope.launch {
            try {
                safeApiRequest(coroutineContext) { makeRemoteRequest(params) }
                    .onSuccess { remoteModel ->
                        val localModel = mapToLocalModel(params, remoteModel)
                        saveLocally(params, localModel)
                    }
                    .onError {
                        Timber.e(it)
                    }
            } catch (e: Exception) {
                logFetchRemoteException(e)
            } finally {
                inflightCache[params] = null
            }
        }

        inflightCache[params] = fetchJob

        return fetchJob
    }

    open fun logFetchRemoteException(t: Throwable) = Timber.e(t)

    protected abstract suspend fun makeRemoteRequest(params: Params): RemoteModel?

    protected abstract fun mapToLocalModel(params: Params, remoteModel: RemoteModel): LocalModel

    protected abstract suspend fun saveLocally(params: Params, dbModel: LocalModel)
}

/**
 * Similar to [RemoteToLocalFetcher] but only for remote requests where we do not really care about
 * the response. Use [RemoteToLocalFetcher] when making requests that have response data that we will
 * display or otherwise care about. Use [SingleRemoteRequest] only when needing to notify the
 * server of something and we can ignore its response.
 */
abstract class SingleRemoteRequest<Params, Response>(
    val dispatcherProvider: DispatcherProvider
) {
    private val fetcherScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)

    suspend fun fetchRemote(params: Params) {
        fetchRemoteInternal(params).join()
    }

    private fun fetchRemoteInternal(params: Params): Job {
        return fetcherScope.launch {
            try {
                safeApiRequest(coroutineContext) { makeRemoteRequest(params) }
                    .onSuccess { Timber.v("${this.javaClass} request success") }
                    .onError { Timber.e(it) }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    protected abstract suspend fun makeRemoteRequest(params: Params): Response
}