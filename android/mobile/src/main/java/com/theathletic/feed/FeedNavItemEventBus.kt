package com.theathletic.feed

import com.theathletic.entity.settings.UserTopicsBaseItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch

sealed class FeedNavItemEvent {
    class TopicFollowed(val topic: UserTopicsBaseItem) : FeedNavItemEvent()
    class ScrollToTopOfFeed(val feedType: FeedType) : FeedNavItemEvent()
    object ScrollToTopHeadlines : FeedNavItemEvent()
}

class FeedNavItemEventProducer(
    private val mutableSharedFlow: MutableSharedFlow<FeedNavItemEvent> = MutableSharedFlow()
) : MutableSharedFlow<FeedNavItemEvent> by mutableSharedFlow

class FeedNavItemEventConsumer(
    private val producer: FeedNavItemEventProducer
) : Flow<FeedNavItemEvent> by producer {

    inline fun <reified T : FeedNavItemEvent> observe(
        coroutineScope: CoroutineScope,
        crossinline collector: suspend (value: T) -> Unit
    ) {
        coroutineScope.launch {
            filterIsInstance<T>().collect { collector(it) }
        }
    }
}