package com.theathletic.repository.resource

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import com.theathletic.extension.extLogError
import com.theathletic.utility.NetworkManager
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.CompletableSubject
import timber.log.Timber

open class NetworkBoundResource<T> {
    interface Callback<T> {
        // Called to save the resourceLiveData of the API response into the database
        @WorkerThread
        @Throws(Exception::class)
        fun saveCallResult(response: T)

        // Called to get the cached data from the database
        @MainThread
        fun loadFromDb(): Maybe<T>

        // Called to create the API call.
        @MainThread
        fun createNetworkCall(): Maybe<T>

        @MainThread
        fun mapData(data: T?): T? {
            return data
        }
    }

    var isDataLoading = false
    protected var callback: Callback<T>? = null
    private val resourceSubject: BehaviorSubject<Resource<T?>> = BehaviorSubject.create()
    private var dbDisposable: Disposable? = null
    private var networkDisposable: Disposable? = null
    private var networkCompletable: Completable? = null
    private var isNetworkCallRunning = false

    init {
        logNetworkResourceEvent("init()")
        resourceSubject.doOnSubscribe { resourceSubject.onNext(Resource.loading(null)) }
    }

    open fun load() {
        logNetworkResourceEvent("load()")
        dispose()
        isDataLoading = true

        val isOffline = NetworkManager.getInstance().isOffline()
        dbDisposable = callback?.loadFromDb()
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(Schedulers.io())
            ?.subscribe(
                {
                    logNetworkResourceEvent("load() - onNext")
                    if (isOffline) {
                        resourceSubject.onNext(Resource.success(callback?.mapData(it), true))
                        isDataLoading = false
                    } else {
                        resourceSubject.onNext(Resource.loading(callback?.mapData(it), true))
                        fetchNetwork()
                    }
                    dbDisposable?.dispose()
                },
                {
                    logNetworkResourceEvent("load() - onError")
                    it.extLogError()
                    resourceSubject.onNext(Resource.error<T>(it, true))
                    if (!isOffline)
                        fetchNetwork()
                    else
                        isDataLoading = false
                    dbDisposable?.dispose()
                },
                {
                    logNetworkResourceEvent("load() - onComplete")
                    if (isOffline) {
                        resourceSubject.onNext(Resource.success(callback?.mapData(null), true))
                        isDataLoading = false
                    } else {
                        resourceSubject.onNext(Resource.loading(callback?.mapData(null), true))
                        fetchNetwork()
                    }
                    dbDisposable?.dispose()
                }
            )
    }

    /**
     *	This method don't have a valve(isObserving).
     *	This means that the call will be also called when the fragment, etc. is in background.
     */
    fun loadOnlyCache() {
        logNetworkResourceEvent("loadOnlyCache()")
        dbDisposable?.dispose()
        isDataLoading = true

        dbDisposable = callback?.loadFromDb()
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(Schedulers.io())
            ?.subscribe(
                {
                    logNetworkResourceEvent("loadOnlyCache() - onNext")
                    resourceSubject.onNext(Resource.success(callback?.mapData(it), true))
                    dbDisposable?.dispose()
                    isDataLoading = false
                },
                {
                    logNetworkResourceEvent("loadOnlyCache() - onError")
                    it.extLogError()
                    resourceSubject.onNext(Resource.error<T>(it, true))
                    dbDisposable?.dispose()
                    isDataLoading = false
                },
                {
                    logNetworkResourceEvent("loadOnlyCache() - onComplete")
                    resourceSubject.onNext(Resource.success(callback?.mapData(null), true))
                    dbDisposable?.dispose()
                    isDataLoading = false
                }
            )
    }

    /**
     * Returns a completable that completes when the network request finishes. This doesn't give
     * the data from the request itself, subscribe to the data observable for that.
     */
    @Suppress("LongMethod")
    fun fetchNetwork(force: Boolean = false): Completable? {
        logNetworkResourceEvent("fetchNetwork()")
        isDataLoading = true

        if (!isNetworkCallRunning || force) {
            isNetworkCallRunning = true

            val completable = CompletableSubject.create()
            networkCompletable = completable

            networkDisposable?.dispose()
            networkDisposable = callback?.createNetworkCall()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(Schedulers.io())
                ?.subscribe(
                    { networkResult ->
                        logNetworkResourceEvent("fetchNetwork() - onNext")
                        // Tt Dispose DB before save
                        dbDisposable?.dispose()
                        callback?.saveCallResult(networkResult)

                        completable.onComplete()
                        networkCompletable = null

                        dbDisposable = callback?.loadFromDb()
                            ?.subscribeOn(Schedulers.io())
                            ?.observeOn(Schedulers.io())
                            ?.subscribe(
                                {
                                    logNetworkResourceEvent("fetchNetwork() - loadCache - onNext")
                                    resourceSubject.onNext(Resource.success(callback?.mapData(it), false))

                                    networkDisposable?.dispose()
                                    isNetworkCallRunning = false
                                    isDataLoading = false
                                },
                                {
                                    logNetworkResourceEvent("fetchNetwork() - loadCache - onError")
                                    it.extLogError()
                                    resourceSubject.onNext(Resource.error<T>(it, false))

                                    networkDisposable?.dispose()
                                    isNetworkCallRunning = false
                                    isDataLoading = false
                                },
                                {
                                    logNetworkResourceEvent("fetchNetwork() - loadCache - onComplete")
                                    resourceSubject.onNext(Resource.success(callback?.mapData(null), false))
                                    dbDisposable?.dispose()

                                    networkDisposable?.dispose()
                                    isNetworkCallRunning = false
                                    isDataLoading = false
                                }
                            )
                    },
                    {
                        logNetworkResourceEvent("fetchNetwork() - onError")
                        it.extLogError()
                        resourceSubject.onNext(Resource.error<T>(it, false))
                        completable.onError(it)
                        networkCompletable = null
                        networkDisposable?.dispose()
                        isNetworkCallRunning = false
                        isDataLoading = false
                    }
                )
        }
        return networkCompletable
    }

    fun getDataObservable(): Observable<Resource<T?>> {
        return resourceSubject
    }

    fun dispose() {
        logNetworkResourceEvent("dispose()")
        dbDisposable?.dispose()
        networkDisposable?.dispose()
        isDataLoading = false
    }

    private fun logNetworkResourceEvent(event: String) =
        Timber.v("[NetworkBoundResource-${this.javaClass.simpleName}] $event")
}