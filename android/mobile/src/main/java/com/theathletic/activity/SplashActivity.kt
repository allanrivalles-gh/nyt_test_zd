package com.theathletic.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.os.Bundle
import android.text.format.DateFormat
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.TextDelegate
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.ScaleXY
import com.airbnb.lottie.value.SimpleLottieValueCallback
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.iterable.iterableapi.IterableApi
import com.theathletic.AthleticConfig
import com.theathletic.R
import com.theathletic.activity.article.ReferredArticleIdManager
import com.theathletic.billing.BillingStartupHelper
import com.theathletic.datetime.TimeProvider
import com.theathletic.datetime.formatter.DisplayFormat
import com.theathletic.extension.applySchedulers
import com.theathletic.extension.extLogError
import com.theathletic.featureswitches.FeatureSwitch
import com.theathletic.featureswitches.FeatureSwitches
import com.theathletic.feed.FeedType
import com.theathletic.feed.data.FeedRefreshJob
import com.theathletic.followables.data.FollowableRepository
import com.theathletic.manager.UserDataManager
import com.theathletic.manager.UserTopicsManager
import com.theathletic.onboarding.data.OnboardingRepository
import com.theathletic.performance.Performance
import com.theathletic.performance.Trace
import com.theathletic.remoteconfig.RemoteConfigRepository
import com.theathletic.repository.savedstories.SavedStoriesRepository
import com.theathletic.user.UserManager
import com.theathletic.utility.ActivityUtility
import com.theathletic.utility.NetworkManager
import com.theathletic.utility.datetime.DateUtilityImpl
import com.theathletic.utility.logging.ICrashLogHandler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import timber.log.Timber

class SplashActivity : AppCompatActivity() {
    companion object {
        /**
         * Timeout to be used for any blocking operations that occur while the splash screen is
         * displayed
         */
        const val SPLASH_TIMEOUT_MS = 2500L
    }

    private lateinit var animationView: LottieAnimationView

    private var trace: Trace? = null
    private val compositeDisposable = CompositeDisposable()
    private val referredArticleManager by inject<ReferredArticleIdManager>()
    private var referralLinkDisposable: Disposable? = null
    private val crashLogHandler by inject<ICrashLogHandler>()
    private val featureSwitches by inject<FeatureSwitches>()
    private val timeProvider by inject<TimeProvider>()
    private val billingStartupHelper by inject<BillingStartupHelper>()
    private val feedRefreshJob by inject<FeedRefreshJob>()
    private val followableRepository by inject<FollowableRepository>()
    private val remoteConfigRepository by inject<RemoteConfigRepository>()
    private val onboardingRepository by inject<OnboardingRepository>()

    private var navigationLambda: (() -> Unit)? = null
        set(value) {
            synchronized(this) {
                field = value
                runNavigationIfReady()
            }
        }

    private var isAnimationRunning = false
        set(value) {
            synchronized(this) {
                field = value
                runNavigationIfReady()
            }
        }

    private fun runNavigationIfReady() {
        navigationLambda?.let { if (!isAnimationRunning) it.invoke() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        animationView = findViewById(R.id.animation_view)
        trace = Performance.newTrace("splash_activity_complete").start()
        startSplashRequests()
        window.statusBarColor = getColor(R.color.ath_grey_65)
    }

    override fun onDestroy() {
        trace?.stop()
        compositeDisposable.clear()
        referralLinkDisposable?.dispose()
        navigationLambda = null
        super.onDestroy()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        this.intent = intent
    }

    private fun startSplashRequests() {
        if (verifyGooglePlayServices()) {
            runSplashAnimation()
            updateRemoteConfigAndRoute()
        }
    }

    private fun verifyGooglePlayServices(): Boolean {
        val status = GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(applicationContext, AthleticConfig.GOOGLE_SERVICES_MIN_VERSION)
        return if (status != ConnectionResult.SUCCESS) {
            ActivityUtility.startGoogleServicesUnavailableActivity(this)
            crashLogHandler.trackException(
                ICrashLogHandler.PlayServicesException(),
                "Google Services not available",
                "Status code: $status. Client version is: ${
                GoogleApiAvailability.getInstance().getClientVersion(applicationContext)
                }"
            )
            finish()
            false
        } else {
            true
        }
    }

    @SuppressLint("CheckResult")
    private fun updateRemoteConfigAndRoute() {
        if (UserManager.isUserLoggedIn()) {
            checkAndForceUpdate()
        } else {
            requestStartupInfoAndRouteSync()
        }
    }

    private fun requestStartupInfoAndRouteSync() {
        val referredArticleSingle = referredArticleManager.fetchAndUpdateArticleIdFromServer()
            .applySchedulers()
            .timeout(SPLASH_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            .onErrorReturnItem(-1)

        billingStartupHelper.updateBillingInfo(null)

        compositeDisposable.add(
            referredArticleSingle.subscribe(
                { result ->
                    Timber.i("Starting application with article referral id: $result")
                    checkAndForceUpdate()
                },
                { error ->
                    Exception("Synchronous startup info failed to fetch in time", error).extLogError()
                    routeToAppropriateActivity()
                }
            )
        )
    }

    private fun prefetchFollowingFeed() {
        if (featureSwitches.isFeatureEnabled(FeatureSwitch.PREFETCH_FEED_DURING_SPLASH)) {
            lifecycleScope.launch {
                if (feedRefreshJob.shouldRefreshFeed(FeedType.User)) {
                    feedRefreshJob.prefetchFeed(
                        FeedType.User,
                        SPLASH_TIMEOUT_MS,
                    )
                }
            }
        }
    }

    private fun checkAndForceUpdate() {
        lifecycleScope.launch {
            val updateVersions = remoteConfigRepository.androidForceUpdateVersions.firstOrNull()
            if (updateVersions?.contains(AthleticConfig.VERSION_NAME) == true) {
                val appUpdateManager = AppUpdateManagerFactory.create(this@SplashActivity)
                appUpdateManager.appUpdateInfo.addOnSuccessListener { updateInfo ->
                    if (updateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                        updateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                    ) {
                        ActivityUtility.startForceUpdateActivity(this@SplashActivity)
                        crashLogHandler.trackException(
                            ICrashLogHandler.ForceUpdateException(),
                            "Version of the app is marked for force update!",
                            "Version code: ${AthleticConfig.VERSION_NAME}"
                        )
                        finish()
                    }
                }
            }
            routeToAppropriateActivity()
            prefetchFollowingFeed()
        }
    }

    private fun routeToAppropriateActivity() {
        fetchFollowableItems()
        checkUserStatusAndRouteAccordingly()
    }

    private fun checkUserStatusAndRouteAccordingly() {
        updateDataInCache()
        validateUserAuthenticationStatus()
        navigationLambda = {
            when {
                !UserManager.isUserLoggedIn() -> {
                    // Even if the user was onboarding, we take them to the auth activity if they restart the app
                    ActivityUtility.startAuthenticationActivity(this, false)
                }
                else -> {
                    IterableApi.getInstance().setEmail(UserManager.getCurrentUser()?.email)
                    IterableApi.getInstance().registerForPush()

                    ActivityUtility.startMainActivity(this, intent)
                }
            }
            overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left)
            finish()
        }
    }

    private fun updateDataInCache() {
        UserDataManager.loadUserData()
        SavedStoriesRepository.cacheData()
    }

    private fun validateUserAuthenticationStatus() {
        if (!UserManager.isUserLoggedIn() || NetworkManager.getInstance().isOffline())
            return

        UserManager.validateUserAuthenticationStatus(UserManager.getCurrentUserId(), lifecycleScope)
    }

    private fun runSplashAnimation() {
        isAnimationRunning = true
        val dateAnimationParams = calculateDateAnimationParams()

        animationView.apply {
            setAnimation(R.raw.splash_line_animation_lottie)
            setTextDelegate(
                TextDelegate(animationView).apply {
                    setText("#left_text", dateAnimationParams.dateString)
                    setText("#right_text", dateAnimationParams.yearString)
                }
            )
            addValueCallback(
                KeyPath("**", "#mask_day_layer", "Rectangle 1"),
                LottieProperty.TRANSFORM_SCALE
            ) { dateAnimationParams.scale }

            addValueCallback(
                KeyPath("**", "#mask_day_layer", "Rectangle 1"),
                LottieProperty.TRANSFORM_POSITION
            ) { dateAnimationParams.translationPx }

            applyThemeColorsToAnimation(this)

            addAnimatorListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    isAnimationRunning = false
                }

                override fun onAnimationCancel(animation: Animator) {
                    isAnimationRunning = false
                }
            })
            playAnimation()
        }
    }

    private fun applyThemeColorsToAnimation(animationView: LottieAnimationView) {
        val foregroundColorProvider = SimpleLottieValueCallback<Int> {
            ContextCompat.getColor(this@SplashActivity, R.color.ath_grey_10)
        }
        val backgroundColorProvider = SimpleLottieValueCallback<Int> {
            ContextCompat.getColor(this@SplashActivity, R.color.ath_grey_65)
        }
        val dateColorProvider = SimpleLottieValueCallback<Int> {
            ContextCompat.getColor(this@SplashActivity, R.color.ath_grey_50)
        }

        animationView.apply {
            addValueCallback(
                KeyPath("**", "screen_background", "Rectangle", "#background_color"),
                LottieProperty.COLOR,
                backgroundColorProvider
            )

            addValueCallback(
                KeyPath("**", "FADE", "Rectangle 1", "Fill 1"),
                LottieProperty.COLOR,
                backgroundColorProvider
            )

            addValueCallback(
                KeyPath("**", "#mask_day_layer", "Rectangle 1", "#background_color"),
                LottieProperty.COLOR,
                backgroundColorProvider
            )

            addValueCallback(
                KeyPath("**", "#mask_year_layer", "Rectangle 1", "#background_color"),
                LottieProperty.COLOR,
                backgroundColorProvider
            )

            addValueCallback(
                KeyPath("**", "#txt_year"),
                LottieProperty.COLOR,
                dateColorProvider
            )

            addValueCallback(
                KeyPath("**", "#txt_day"),
                LottieProperty.COLOR,
                dateColorProvider
            )

            addValueCallback(
                KeyPath("**", "Rectangle 1288", "**", "Fill 1"),
                LottieProperty.COLOR,
                foregroundColorProvider
            )

            addValueCallback(
                KeyPath("**", "Rectangle 1289", "**", "Fill 1"),
                LottieProperty.COLOR,
                foregroundColorProvider
            )

            addValueCallback(
                KeyPath("**", "TA_logo_wordmark", "**", "Fill 1"),
                LottieProperty.COLOR,
                foregroundColorProvider
            )
        }
    }

    data class DateAnimationParams(
        val dateString: String = "",
        val yearString: String = "",
        val scale: ScaleXY = ScaleXY(1f, 1f),
        val translationPx: PointF = PointF(0f, 0f)
    )

    private fun calculateDateAnimationParams(): DateAnimationParams {
        val referenceText = "JULY 14" // the text used in the original lottie file
        val referenceFontSizeDp = 24 // the font size that seems to scale best
        val (monthText, yearText) = formatDateTextValues()

        val sizingPaint = Paint().apply {
            typeface = ResourcesCompat.getFont(this@SplashActivity, R.font.calibre_regular)
            textSize = referenceFontSizeDp * resources.displayMetrics.density
        }
        val bounds = Rect()
        sizingPaint.getTextBounds(referenceText, 0, referenceText.length, bounds)
        val referenceWidth = bounds.width()
        sizingPaint.getTextBounds(monthText, 0, monthText.length, bounds)

        return DateAnimationParams(
            monthText,
            yearText,
            ScaleXY(bounds.width().toFloat() / referenceWidth, 1f),
            PointF(bounds.width().toFloat() - referenceWidth, 0f)
        )
    }

    private fun formatDateTextValues(): Pair<String, String> {
        val today = timeProvider.currentDate
        val monthText = DateUtilityImpl.formatGMTDate(
            today,
            DisplayFormat.MONTH_DATE_LONG
        ).toUpperCase(Locale.getDefault())
        val yearText = DateFormat.format(
            "yyyy",
            today
        ).toString().toUpperCase(Locale.getDefault())
        return Pair(monthText, yearText)
    }

    private fun fetchFollowableItems() {
        // We are going to populate both tables for followable items
        followableRepository.fetchFollowableItems()
        UserTopicsManager.loadUserTopics()
    }
}