package com.theathletic.injection

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.theathletic.analytics.KochavaWrapper
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.AnalyticsEventConsumer
import com.theathletic.analytics.newarch.AnalyticsEventProducer
import com.theathletic.analytics.newarch.LifecycleTracker
import com.theathletic.analytics.newarch.collectors.php.PhpCallQueue
import org.koin.core.qualifier.named
import org.koin.dsl.module

val newAnalyticsModule = module {
    single {
        LifecycleTracker(
            get<Analytics>()
        )
    }
    single { PhpCallQueue() }
    single { FirebaseAnalytics.getInstance(get<Context>(named("application-context"))) }
    single {
        AnalyticsEventConsumer(get())
    }
    single {
        AnalyticsEventProducer()
    }

    single {
        KochavaWrapper(
            get(),
            get(named("application-context"))
        )
    }
}