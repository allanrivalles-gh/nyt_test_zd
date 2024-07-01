package com.theathletic.feed.ui

/**
 * A marker interface for a class which represents some sort of user interaction with a [FeedModule]
 * This can include clicking, long pressing, etc. Each [FeedModule] should define its own
 * subclasses to represent the ways that a user can interact with its content.
 */
interface FeedInteraction

interface FeedInteractionWithPayload<T : FeedAnalyticsPayload> : FeedInteraction {
    val analyticsPayload: T
}

interface FeedAnalyticsPayload

/**
 * Interface that should be implemented by Presenters which need to respond to [FeedInteraction]
 * events. [FeedModule] can send events through this pipeline by using [LocalFeedInteractor].
 */
interface FeedInteractor {
    fun send(interaction: FeedInteraction)
}

val EmptyInteractor = object : FeedInteractor {
    override fun send(interaction: FeedInteraction) {
        // Do nothing
    }
}