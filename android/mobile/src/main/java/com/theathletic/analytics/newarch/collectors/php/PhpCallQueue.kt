package com.theathletic.analytics.newarch.collectors.php

import com.theathletic.extension.applySchedulers
import com.theathletic.extension.delayEach
import com.theathletic.extension.doAsync
import com.theathletic.extension.extLogError
import com.theathletic.user.IUserManager
import com.theathletic.user.UserManager
import com.theathletic.utility.NetworkManager
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.TimeUnit
import timber.log.Timber

class PhpCallQueue {
    private val analyticsCallQueue: MutableList<Completable> = ArrayList()
    private var analyticsCallQueueDisposable: Disposable? = null

    fun addAPICall(sendLogAnalytics: Completable) {
        if (UserManager.getCurrentUserId() == IUserManager.NO_USER)
            return

        analyticsCallQueue.add(sendLogAnalytics)
        checkAnalyticsCallQueue()
    }

    private fun checkAnalyticsCallQueue() {
        val networkManager = NetworkManager.getInstance()
        if (networkManager.isOffline() || analyticsCallQueueDisposable?.isDisposed == false || analyticsCallQueue.isEmpty())
            return

        Timber.v("[ANALYTICS] Let's clear all cached calls! Size: ${analyticsCallQueue.size}")
        analyticsCallQueueDisposable = Observable.fromIterable(analyticsCallQueue)
            .takeWhile { NetworkManager.connected.get() }
            .delayEach(2, TimeUnit.SECONDS)
            .flatMap { call ->
                if (networkManager.isOnline()) {
                    call.applySchedulers()
                        .doOnComplete { analyticsCallQueue.remove(call) }
                        .toObservable()
                } else {
                    Observable.empty<Any>()
                }
            }
            .subscribeBy(
                onNext = {},
                onError = { throwable ->
                    throwable.extLogError()
                    val first = analyticsCallQueue.firstOrNull()
                    analyticsCallQueue.remove(first)
                    first?.let { analyticsCallQueue.add(it) }
                },
                onComplete = {
                    if (analyticsCallQueue.size > 0) {
                        doAsync { checkAnalyticsCallQueue() }
                    }
                }
            )
    }
}