package com.theathletic.injection

import com.theathletic.analytics.AnalyticsEndpointConfig
import com.theathletic.analytics.DatadogLoggerImpl
import com.theathletic.analytics.DatadogWrapper
import com.theathletic.analytics.IAnalytics
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.context.ContextInfoPreferences
import com.theathletic.analytics.newarch.context.ContextInfoProvider
import com.theathletic.analytics.newarch.context.ContextInfoProviderImpl
import com.theathletic.analytics.repository.AnalyticsApi
import com.theathletic.analytics.repository.AnalyticsDatabase
import com.theathletic.debugtools.DebugPreferences
import com.theathletic.debugtools.logs.AnalyticsLogHelper
import com.theathletic.debugtools.logs.db.AnalyticsLogDao
import com.theathletic.debugtools.logs.db.AnalyticsLogDatabase
import com.theathletic.links.AnalyticsContextUpdater
import com.theathletic.user.IUserManager
import okhttp3.OkHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.binds
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val analyticsModule = module {
    single { AnalyticsDatabase.newInstance(get(named("application-context"))) }
    single { get<AnalyticsDatabase>().analyticsEventDao() }
    single { get<AnalyticsDatabase>().flexibleAnalyticsEventDao() }

    single {
        val endpoint = get<AnalyticsEndpointConfig>().endpoint
        Retrofit.Builder()
            .baseUrl(endpoint)
            .client(get<OkHttpClient>(named("analytics-token-http-client")))
            .addConverterFactory(GsonConverterFactory.create(get()))
            .build()
            .create(AnalyticsApi::class.java)
    }
    single {
        ContextInfoProviderImpl(
            get(named("user-agent")),
            get<ContextInfoPreferences>(),
            get<IUserManager>()
        ) as ContextInfoProvider
    }

    // TT Analytics History Log
    single { AnalyticsLogDatabase.newInstance(get(named("application-context"))) }
    single { get<AnalyticsLogDatabase>().analyticsHistoryDao() }
    single {
        AnalyticsLogHelper(
            get<AnalyticsLogDao>(),
            get<DebugPreferences>()
        )
    }
    single { DatadogWrapper(DatadogLoggerImpl(get(named("application-context")))) }
    single {
        Analytics(get(), get(), get(), get(), get(), get(), get(), get(), get(), get())
    } binds arrayOf(Analytics::class, IAnalytics::class, AnalyticsContextUpdater::class)
}