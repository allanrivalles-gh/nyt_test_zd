package com.theathletic.liveblog.data.remote

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.utility.coroutines.DispatcherProvider
import com.theathletic.utility.coroutines.tickerFlow
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.time.Duration.Companion.minutes

class LiveBlogRibbonSubscriptionManager @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val liveBlogRibbonSubscriber: LiveBlogRibbonSubscriber
) {
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, t -> Timber.e(t) }
    private val coroutineScope = CoroutineScope(dispatcherProvider.io + coroutineExceptionHandler)

    private val currentSubscribedBlogs = mutableSetOf<String>()
    private var currentSubscription: Job? = null
    private var liveBlogTicker: Job? = null

    fun subscribeToLiveBlogs(liveBlogIds: List<String>, tickerUpdate: () -> Unit = {}) {

        if (liveBlogIds.isEmpty()) {
            cancel()
            return
        }

        val liveBlogsToAdd = liveBlogIds - currentSubscribedBlogs
        if (currentSubscription?.isActive == true && liveBlogsToAdd.isEmpty()) {
            Timber.v("Already subscribed to all live blogs provided")
            return
        }

        currentSubscribedBlogs += liveBlogsToAdd
        resetSubscription(tickerUpdate)
    }

    private fun resetSubscription(tickerUpdate: () -> Unit = {}) {
        currentSubscription?.cancel()
        if (currentSubscribedBlogs.isEmpty()) {
            return
        }

        Timber.v("Subscribing to live blogs: $currentSubscribedBlogs")
        currentSubscription = coroutineScope.launch {
            liveBlogRibbonSubscriber.subscribe(
                LiveBlogRibbonSubscriber.Params(liveBlogIds = currentSubscribedBlogs)
            )
        }

        if (liveBlogTicker == null || liveBlogTicker?.isActive == false) {
            liveBlogTicker = tickerFlow(period = 1.1.minutes, initialDelay = 1.1.minutes)
                .onEach { tickerUpdate() }
                .launchIn(coroutineScope)
        }
    }

    fun cancel() {
        Timber.v("Pausing live blogs subscription.")
        currentSubscription?.cancel()
        currentSubscription = null

        liveBlogTicker?.cancel()
        liveBlogTicker = null
    }

    fun resume() {
        Timber.v("Resuming live blog subscription. Blogs = $currentSubscribedBlogs")
        resetSubscription()
    }
}