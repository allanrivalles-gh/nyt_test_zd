package com.theathletic

import android.annotation.SuppressLint
import android.app.Application
import android.content.IntentFilter
import android.os.StrictMode
import android.webkit.WebView
import androidx.annotation.VisibleForTesting
import androidx.core.content.ContextCompat
import androidx.lifecycle.ProcessLifecycleOwner
import com.akaita.java.rxjava2debug.RxJava2Debug
import com.iterable.iterableapi.IterableApi
import com.iterable.iterableapi.IterableConfig
import com.mlykotom.valifi.ValiFi
import com.theathletic.activity.article.ReferredArticleIdManager
import com.theathletic.ads.AdsManager
import com.theathletic.analytics.AnalyticsManager
import com.theathletic.analytics.AnalyticsTracker
import com.theathletic.analytics.ComscoreWrapper
import com.theathletic.analytics.DatadogWrapper
import com.theathletic.analytics.KochavaWrapper
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.AnalyticsEventConsumer
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.LifecycleTracker
import com.theathletic.analytics.newarch.LiveScoresSubscriptionLifecycleTracker
import com.theathletic.analytics.newarch.track
import com.theathletic.compass.CompassClient
import com.theathletic.datetime.TimeProvider
import com.theathletic.debugtools.logs.AnalyticsLogHelper
import com.theathletic.di.autoKoinModules
import com.theathletic.entity.local.EntityCleanupScheduler
import com.theathletic.extension.extLogError
import com.theathletic.featureswitch.Features
import com.theathletic.injection.analyticsModule
import com.theathletic.injection.apiModule
import com.theathletic.injection.baseModule
import com.theathletic.injection.billingModule
import com.theathletic.injection.databaseModule
import com.theathletic.injection.feedModule
import com.theathletic.injection.gameDetailModule
import com.theathletic.injection.gameHubModule
import com.theathletic.injection.ioModule
import com.theathletic.injection.liveAudioModule
import com.theathletic.injection.networkModule
import com.theathletic.injection.newAnalyticsModule
import com.theathletic.injection.newsModule
import com.theathletic.injection.podcastModule
import com.theathletic.injection.profileModule
import com.theathletic.injection.referralsModule
import com.theathletic.injection.scoresModule
import com.theathletic.injection.teamHubModule
import com.theathletic.notifications.AthleticIterableHandler
import com.theathletic.notifications.NotificationStatusScheduler
import com.theathletic.performance.Performance
import com.theathletic.profile.data.remote.TranscendConsentWrapper
import com.theathletic.remoteconfig.RemoteConfigRepository
import com.theathletic.service.PodcastService
import com.theathletic.share.ShareBroadcastReceiver
import com.theathletic.ui.DisplayPreferences
import com.theathletic.user.UserManager
import com.theathletic.utility.NetworkManager
import com.theathletic.utility.Preferences
import com.theathletic.utility.flipper.FlipperClientUtility
import com.theathletic.utility.logging.BreadcrumbTree
import com.theathletic.utility.logging.EmbraceWrapper
import com.theathletic.utility.logging.ICrashLogHandler
import com.theathletic.viewmodel.main.SLEEP_TIMER_EPISODE_END
import com.theathletic.worker.SavedStoriesScheduler
import com.theathletic.worker.UserUpdateScheduler
import dagger.hilt.android.HiltAndroidApp
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import java.util.Date
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

@HiltAndroidApp
class AthleticApplication : Application() {

    companion object {
        private lateinit var instance: AthleticApplication

        @JvmStatic
        @Deprecated("This should not exist at all, if you need the context, receive it in the constructor or as a method parameter")
        fun getContext(): AthleticApplication = instance

        /**
         * This should only ever be called to configure the Singleton for test purposes
         */
        @JvmStatic
        @VisibleForTesting
        @Deprecated("This should not exist at all, if you need the context, receive it in the constructor or as a method parameter")
        fun setInstance(athleticApplication: AthleticApplication) {
            instance = athleticApplication
        }
    }

    private val analyticsTracker by inject<AnalyticsTracker>()
    private val lifecycleTracker by inject<LifecycleTracker>()
    private val liveScoresSubscriptionLifecycleTracker by inject<LiveScoresSubscriptionLifecycleTracker>()
    private val analyticsEventConsumer by inject<AnalyticsEventConsumer>()
    private val referredArticleManager by inject<ReferredArticleIdManager>()
    private val timeProvider by inject<TimeProvider>()
    private val compassClient by inject<CompassClient>()
    private val analyticsHistoryLogDbHelper by inject<AnalyticsLogHelper>()
    private val notificationStatusScheduler by inject<NotificationStatusScheduler>()
    private val userUpdateScheduler by inject<UserUpdateScheduler>()
    private val crashLogHandler by inject<ICrashLogHandler>()
    private val kochavaWrapper by inject<KochavaWrapper>()
    private val comscoreWrapper by inject<ComscoreWrapper>()
    private val datadogWrapper by inject<DatadogWrapper>()
    private val analytics by inject<Analytics>()
    private val iterableHandler by inject<AthleticIterableHandler>()
    private val entityCleanupScheduler by inject<EntityCleanupScheduler>()
    private val applicationProcessListener by inject<ApplicationProcessListener>()
    private val displayPreferences by inject<DisplayPreferences>()
    private val flipperClientUtility by inject<FlipperClientUtility>()
    private val embraceWrapper by inject<EmbraceWrapper>()
    private val feature by inject<Features>()
    private val adManager by inject<AdsManager>()
    private val savedStoriesScheduler by inject<SavedStoriesScheduler>()
    private val remoteConfigRepository by inject<RemoteConfigRepository>()
    private val transcendConsentWrapper by inject<TranscendConsentWrapper>()

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        // Check the last App version
        checkTheLastAppVersion()

        val onCreateTrace = Performance.newTrace("app_on_create").start()
        var trace = Performance.newTrace("koin_injection").start()
        // Initialize DI graph
        startKoin {
            androidLogger()
            androidContext(this@AthleticApplication)

            val allModules = mutableListOf(
                baseModule, analyticsModule, networkModule,
                databaseModule, podcastModule, apiModule,
                newAnalyticsModule, scoresModule, gameDetailModule, referralsModule,
                newsModule, ioModule, feedModule, liveAudioModule, profileModule,
                teamHubModule, gameHubModule, billingModule
            )
            allModules.addAll(autoKoinModules)
            modules(allModules)
        }
        trace.stop()

        if (feature.isTcfConsentEnabled) {
            transcendConsentWrapper.initialize()
        }

        registerReceivers()
        remoteConfigRepository.fetchRemoteConfig()

        // ValiFi
        runWithTracing("valifi_install") {
            ValiFi.install(this)
        }

        val user = UserManager.getCurrentUser()

        // start flipper
        flipperClientUtility.initializeAndStartFlipper(applicationContext)

        // CrashLogHandler init
        if (AthleticConfig.IS_EXCEPTION_TRACKING_ENABLED) {
            runWithTracing("crashlytics_init") {
                crashLogHandler.setUserInformation(user)
            }
        }

        // Setup Logging
        if (BuildConfig.DEBUG || AthleticConfig.LOGS || BuildConfig.DEV_ENVIRONMENT) {
            Timber.plant(Timber.DebugTree())
        }
        Timber.plant(BreadcrumbTree(crashLogHandler))

        displayPreferences.initializeDayNightPreferences()

        // Initialize NetworkManager to connection state is populated
        runWithTracing("network_manager_init") {
            NetworkManager.getInstance()
        }

        // Init Iterable
        trace = Performance.newTrace("iterable_init").start()
        val apiKey: String
        val pushName: String
        if (AthleticConfig.DEBUG || !AthleticConfig.IS_EXCEPTION_TRACKING_ENABLED) {
            apiKey = "57514ec9946b45d7917eaa5c435ca2f1"
            pushName = "android_push_staging"
        } else {
            apiKey = "ce5b7ffb2d95426b8bef802e052b11fc"
            pushName = "google_push_production"
        }
        val iterableConfig = IterableConfig.Builder()
            .setPushIntegrationName(pushName)
            .setCustomActionHandler(iterableHandler)
            .setUrlHandler(iterableHandler)
            .setAllowedProtocols(arrayOf("http", "https", "theathletic"))
            .build()
        IterableApi.initialize(applicationContext, apiKey, iterableConfig)

        IterableApi.getInstance().apply {
            setNotificationIcon("ic_notification_small")
            inAppManager.setAutoDisplayPaused(true)
        }
        trace.stop()

        // Init Kochava
        runWithTracing("kochava_init") {
            kochavaWrapper.initialize(this, timeProvider.currentTimeMs, referredArticleManager)
        }

        runWithTracing("comscore_init") {
            comscoreWrapper.initializeAndStart(this)
        }

        // Init Embrace
        runWithTracing("embrace_init") {
            embraceWrapper.initialize(this)
        }

        // Analytics init - do it first so we can send logs!
        trace = Performance.newTrace("analytics_init").start()
        AnalyticsManager.init()
        // update analytics user and track app open event
        AnalyticsManager.updateAnalyticsUser()
        when {
            UserManager.getCurrentUser() == null -> {
                analytics.track(Event.AppLifecycle.OpenNewUser)
            }
            UserManager.isAnonymous -> {
                analytics.track(Event.AppLifecycle.OpenAnonymousUser)
            }
            UserManager.isUserSubscribed() -> {
                analytics.track(Event.AppLifecycle.OpenSubscriber)
            }
            else -> analytics.track(Event.AppLifecycle.OpenNonSubscriber)
        }
        analyticsTracker.startAnalyticsUploadWork()
        trace.stop()

        // Enable strict mode for debug
        if (AthleticConfig.DEBUG) setupStrictMode()

        // Enable debug for WebView
        if (AthleticConfig.DEBUG) WebView.setWebContentsDebuggingEnabled(true)

        // Enable RxJava assembly stack collection, to make RxJava crash reports clear and unique
        // Make sure this is called AFTER setting up any Crash reporting mechanism as Crashlytics
        RxJava2Debug.enableRxJava2AssemblyTracking(arrayOf("com.theathletic"))

        // Set global RxJava error handler
        RxJavaPlugins.setErrorHandler { e ->
            when (e) {
                is UndeliverableException -> {
                    // "Irrelevant network problem or API that throws on cancellation"
                    if (AthleticConfig.IS_EXCEPTION_TRACKING_ENABLED) {
                        crashLogHandler.logException(e.cause ?: e)
                    } else {
                        e.extLogError()
                    }
                }
                is InterruptedException -> {
                    // Blocking code was interrupted by a dispose call
                    if (AthleticConfig.IS_EXCEPTION_TRACKING_ENABLED) {
                        crashLogHandler.logException(e)
                    } else {
                        e.extLogError()
                    }
                }
                else -> {
                    // Keep other exception types flowing
                    Thread.currentThread().uncaughtExceptionHandler?.uncaughtException(Thread.currentThread(), e)
                }
            }
        }

        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleTracker)
        ProcessLifecycleOwner.get().lifecycle.addObserver(liveScoresSubscriptionLifecycleTracker)

        notificationStatusScheduler.schedule(this)

        runWithTracing("analytics_history_log_init") {
            analyticsHistoryLogDbHelper.initialize(analyticsEventConsumer)
        }

        userUpdateScheduler.schedule(this)

        runWithTracing("refresh_user_init") {
            UserManager.refreshUserIfSubscriptionIsAboutToExpire()
        }

        runWithTracing("compass_init") {
            initializeCompass()
        }

        // Initialize ads sdk
        if (feature.isAdsEnabled) {
            adManager.initAds(this)
        }

        clearPodcastTimerEpisodeEndFlag()

        clearExternalLinkSourceInfo()

        entityCleanupScheduler.schedule(this)
        savedStoriesScheduler.schedule()
        onCreateTrace.stop()

        applicationProcessListener.attach()
    }

    @SuppressLint("WrongConstant")
    private fun registerReceivers() {
        ContextCompat.registerReceiver(
            applicationContext,
            ShareBroadcastReceiver(),
            IntentFilter(),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    private fun runWithTracing(traceName: String, block: () -> Unit) {
        val trace = Performance.newTrace(traceName).start()
        block()
        trace.stop()
    }

    @SuppressLint("CheckResult")
    private fun initializeCompass() {
        runBlocking {
            compassClient.loadConfig(
                isUserLoggedIn = UserManager.isUserLoggedIn()
            )
        }
    }

    private fun clearPodcastTimerEpisodeEndFlag() {
        // clear "on episode end" when is still set, but nothing is playing - it would pause next track without intention
        if (Preferences.podcastSleepTimestampMillis == SLEEP_TIMER_EPISODE_END &&
            !PodcastService().isInForegroundServiceState()
        ) {
            Preferences.podcastSleepTimestampMillis = -1
        }
    }

    private fun clearExternalLinkSourceInfo() {
        // clear our deeplink/push info related to source on cold start of the app
        analytics.clearDeeplinkParams()
    }

    /**
     * The logic that should be executed when user updates his app should be putted here.
     *
     * We should always reset all FetchDate data, so user will refresh fresh data when he get back
     * into the app after an update.
     */
    private fun checkTheLastAppVersion() {
        if (Preferences.lastAppVersionInstalled != AthleticConfig.VERSION_NAME) {
            Preferences.lastAppVersionInstalled = AthleticConfig.VERSION_NAME
            Preferences.communityLastFetchDate = Date().apply { time = 0 }
            Preferences.userDataLastFetchDate = Date().apply { time = 0 }
            Preferences.podcastLastCheckDate = Date().apply { time = 0 }
            Preferences.clearFeedRefreshData()
        }
    }

    private fun setupStrictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectNetwork()
                .penaltyDeath()
                .build()
        )

        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .build()
        )
    }
}