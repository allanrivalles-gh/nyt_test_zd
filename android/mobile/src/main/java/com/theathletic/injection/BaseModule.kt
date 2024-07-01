package com.theathletic.injection

import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Vibrator
import androidx.work.WorkManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.gson.Gson
import com.theathletic.AthleticConfig
import com.theathletic.activity.article.ReferredArticleIdManager
import com.theathletic.ads.AdConfig
import com.theathletic.ads.AdConfigClient
import com.theathletic.ads.AdConfigClientImpl
import com.theathletic.analytics.DatadogLoggerImpl
import com.theathletic.analytics.DatadogWrapper
import com.theathletic.analytics.newarch.context.ContextInfoPreferences
import com.theathletic.article.FreeArticleTrackerDataSource
import com.theathletic.auth.data.AuthenticationRepository
import com.theathletic.auth.loginoptions.AuthorizationUrlCreator
import com.theathletic.compass.CompassApi
import com.theathletic.compass.CompassClient
import com.theathletic.compass.CompassPreferences
import com.theathletic.compass.codegen.CompassExperiment
import com.theathletic.datetime.DateUtility
import com.theathletic.datetime.TimeProvider
import com.theathletic.debugtools.DebugPreferences
import com.theathletic.device.IsTabletProvider
import com.theathletic.links.NavigationLinkParser
import com.theathletic.links.deep.DeeplinkEventConsumer
import com.theathletic.links.deep.DeeplinkEventProducer
import com.theathletic.main.MainEventConsumer
import com.theathletic.main.MainEventProducer
import com.theathletic.main.ui.MainNavigationEventConsumer
import com.theathletic.main.ui.MainNavigationEventProducer
import com.theathletic.manager.UserTopicsManager
import com.theathletic.network.rest.RetrofitClient.retrofit
import com.theathletic.notifications.FirebaseMessagingHelper
import com.theathletic.notifications.NotificationStatusScheduler
import com.theathletic.remoteconfig.local.FirebaseRemoteConfigDataSource
import com.theathletic.remoteconfig.local.RemoteConfigDataSource
import com.theathletic.repository.user.IUserDataRepository
import com.theathletic.repository.user.UserDataRepository
import com.theathletic.share.ShareEventConsumer
import com.theathletic.share.ShareEventProducer
import com.theathletic.topics.LegacyUserTopicsManager
import com.theathletic.user.IUserManager
import com.theathletic.user.UserManager
import com.theathletic.utility.ActivityProvider
import com.theathletic.utility.ActivityUtility
import com.theathletic.utility.AdPreferences
import com.theathletic.utility.ArticlePreferences
import com.theathletic.utility.AttributionPreferences
import com.theathletic.utility.BackoffState
import com.theathletic.utility.BillingPreferences
import com.theathletic.utility.ExponentialBackoff
import com.theathletic.utility.FeatureIntroductionPreferences
import com.theathletic.utility.FeedPreferences
import com.theathletic.utility.IActivityUtility
import com.theathletic.utility.IPreferences
import com.theathletic.utility.LiveBlogPreferences
import com.theathletic.utility.LocaleUtility
import com.theathletic.utility.LocaleUtilityImpl
import com.theathletic.utility.LogoUtility
import com.theathletic.utility.OnboardingPreferences
import com.theathletic.utility.Preferences
import com.theathletic.utility.PrivacyPreferences
import com.theathletic.utility.ProfileBadgingPreferences
import com.theathletic.utility.coroutines.DispatcherProvider
import com.theathletic.utility.datetime.DateUtilityImpl
import com.theathletic.utility.device.DeviceInfo
import com.theathletic.utility.logging.AggregateCrashLogHandler
import com.theathletic.utility.logging.CrashlyticsLogHandler
import com.theathletic.utility.logging.EmbraceWrapper
import com.theathletic.utility.logging.ICrashLogHandler
import com.theathletic.utility.logging.LocalCrashLogHandler
import com.theathletic.worker.UserUpdateScheduler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val baseModule = module {
    single {
        if (AthleticConfig.IS_EXCEPTION_TRACKING_ENABLED) {
            AggregateCrashLogHandler(
                listOf(
                    CrashlyticsLogHandler(),
                    EmbraceWrapper(get()),
                    DatadogWrapper(DatadogLoggerImpl(get(named("application-context"))))
                )
            )
        } else {
            LocalCrashLogHandler()
        } as ICrashLogHandler
    }

    single<DispatcherProvider> {
        object : DispatcherProvider {
            override val default: CoroutineDispatcher = Dispatchers.Default
            override val io: CoroutineDispatcher = Dispatchers.IO
            override val main: CoroutineDispatcher = Dispatchers.Main
            override val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
        }
    }

    single {
        ActivityProvider(androidApplication())
    }

    single(named("application-context")) {
        androidApplication().applicationContext
    }

    single<AdConfigClient> {
        AdConfigClientImpl(get<IsTabletProvider>())
    }

    single {
        retrofit.newBuilder()
            .baseUrl(AthleticConfig.REST_BASE_URL)
            .build()
            .create(CompassApi::class.java)
    }

    single { LogoUtility }

    single {
        CompassClient(
            compassExperiment = CompassExperiment,
            compassPreferences = get<CompassPreferences>(),
            crashLogHandler = get<ICrashLogHandler>(),
            gson = get<Gson>(),
            deviceInfo = get<DeviceInfo>(),
            compassApi = get<CompassApi>(),
            dispatcherProvider = get<DispatcherProvider>(),
            userManager = get<IUserManager>(),
            localeUtility = get<LocaleUtility>(),
            timeProvider = get<TimeProvider>(),
            preferences = get<IPreferences>()
        )
    }

    single {
        get<Context>(named("application-context")).getSystemService(LocationManager::class.java)
    }

    single {
        DeviceInfo.buildFromContext(
            get<Context>(named("application-context"))
        )
    }

    single { LocaleUtilityImpl as LocaleUtility }

    single { UserUpdateScheduler() }
    single { NotificationStatusScheduler() }

    single { FirebaseMessagingHelper() }

    // Tt Preferences
    single { Preferences as IPreferences }
    single { Preferences as BillingPreferences }
    single { Preferences as AttributionPreferences }
    single { Preferences as OnboardingPreferences }
    single { Preferences as FeedPreferences }
    single { Preferences as LiveBlogPreferences }
    single { Preferences as ProfileBadgingPreferences }
    single { Preferences as ArticlePreferences }
    single { Preferences as PrivacyPreferences }
    single { Preferences as FeatureIntroductionPreferences }
    single { Preferences as AdPreferences }

    factory { AdConfig.Builder(get(), get()) }

    single { ShareEventProducer() }
    single { ShareEventConsumer(get()) }

    single { MainNavigationEventProducer() }
    single { MainNavigationEventConsumer(get()) }

    single { CompassPreferences(get<Context>(named("application-context"))) }
    single { DebugPreferences(get<Context>(named("application-context"))) }
    single {
        ContextInfoPreferences(
            get<Context>(named("application-context")),
            get<Gson>()
        )
    }

    single { ReferredArticleIdManager(ActivityUtility, get<IPreferences>(), get<TimeProvider>(), get<AuthenticationRepository>()) }

    single { FreeArticleTrackerDataSource(androidContext(), get<Gson>()) }

    single<IUserManager> { UserManager }
    single { ActivityUtility as IActivityUtility }

    single { NavigationLinkParser() }

    factory {
        AuthorizationUrlCreator(
            get<DispatcherProvider>()
        )
    }

    factory { WorkManager.getInstance(get<Context>(named("application-context"))) }

    single<IUserDataRepository> { UserDataRepository }

    single { DateUtilityImpl as DateUtility }

    single { DeeplinkEventProducer() }
    single { DeeplinkEventConsumer(get()) }

    single { MainEventProducer() }
    single { MainEventConsumer(get()) }

    factory {
        val context = get<Context>(named("application-context"))
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    factory<BackoffState> { ExponentialBackoff(dispatcherProvider = get()) }

    single<LegacyUserTopicsManager> { UserTopicsManager }

    single { AppUpdateManagerFactory.create(get(named("application-context"))) }

    factory {
        val context = get<Context>(named("application-context"))
        context.getSystemService(ConnectivityManager::class.java)
    }

    single<RemoteConfigDataSource> { FirebaseRemoteConfigDataSource() }
}