package com.theathletic.injection

import com.theathletic.feed.FeedNavItemEventConsumer
import com.theathletic.feed.FeedNavItemEventProducer
import com.theathletic.feed.UserFeedStateObserver
import com.theathletic.feed.UserFeedStateProducer
import org.koin.dsl.module

val feedModule = module {
    single { FeedNavItemEventProducer() }
    single { FeedNavItemEventConsumer(get()) }
    single { UserFeedStateProducer() }
    single { UserFeedStateObserver(get()) }
}