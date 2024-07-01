package com.theathletic.activity

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.theathletic.ApplicationProcessListener
import com.theathletic.analytics.KochavaWrapper
import com.theathletic.auth.AuthenticationActivity
import com.theathletic.extension.extLogError
import com.theathletic.featureswitch.Features
import com.theathletic.links.LinkParser
import com.theathletic.links.deep.DeeplinkDestination
import com.theathletic.links.deep.DeeplinkEventProducer
import com.theathletic.main.DeeplinkThrottle
import com.theathletic.main.ui.MainActivity
import com.theathletic.utility.ArticlePreferences
import com.theathletic.utility.logging.ICrashLogHandler
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import timber.log.Timber

class DeepLinkDispatcherActivity : AppCompatActivity() {
    companion object {
        const val EXTRAS_DEEPLINK_URL = "extras_deeplink_url"
        const val KOCHAVA_SMARTLINK_HOST = "theathletic.smart.link"
        const val KOCHAVA_SMARTLINK_TEST_HOST = "theathletic.testing.smart.link"

        fun newIntent(context: Context, deepLinkUri: Uri): Intent =
            Intent(context, DeepLinkDispatcherActivity::class.java).apply {
                action = ACTION_VIEW
                data = deepLinkUri
            }
    }

    private val deeplinkDispatcher by inject<LinkParser>()
    private val deeplinkThrottle by inject<DeeplinkThrottle>()
    private val deeplinkEventProducer by inject<DeeplinkEventProducer>()
    private val applicationProcessListener by inject<ApplicationProcessListener>()
    private val preferences by inject<ArticlePreferences>()
    private val crashLogHandler by inject<ICrashLogHandler>()
    private val features by inject<Features>()
    private val kochavaWrapper by inject<KochavaWrapper>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val deepLink = intent?.data ?: return finish()
        Timber.i("Attempting to follow universal link: $deepLink")
        Timber.v("[DISPATCHER] START")
        preferences.referrerURI = intent?.extras?.get(Intent.EXTRA_REFERRER).toString()

        // TT Check Action and data
        if (intent.action == ACTION_VIEW && deepLink.toString().isNotBlank()) {
            try {
                when {
                    // Tt First we are going to check for Article Sharing experiment
                    handleArticleSharingExperimentDeeplink() -> {
                        Timber.v("[DISPATCHER] Deeplink IS ARTICLE")
                        finish()
                    }
                    // Tt We have to check if the deeplink is Kochava SmartLink, and handle it
                    handleKochavaDeeplink() -> {
                        Timber.v("[DISPATCHER] Deeplink is KOCHAVA")
                    }
                    handleOAuthCallback() -> {
                        Timber.v("[DISPATCHER] Deeplink is OAUTH")
                        finish()
                    }
                    // Tt In case of normal deeplink, just route to correct Activity immediately.
                    else -> {
                        handleDeeplink(deepLink)
                    }
                }
            } catch (e: Exception) {
                e.extLogError()
                crashLogHandler.trackException(e)
                finish()
                Timber.v("[DISPATCHER] ERROR")
            }
        }
    }

    private fun handleDeeplink(deepLink: Uri) {
        deeplinkThrottle.startThrottle()
        routeToActivity(deepLink) { startingRoute, deeplinkUrl ->
            Timber.v("[DISPATCHER] onActivityRouted")
            when (startingRoute) {
                StartingRoute.SPLASH -> startViaSplashActivity(deeplinkUrl)
                StartingRoute.MAIN_DISPATCHER -> sendToMainDispatcher(deeplinkUrl)
                StartingRoute.BROWSER -> startViaBrowser(deeplinkUrl)
            }
        }
    }

    private fun routeToActivity(
        deepLink: Uri,
        onActivityRouted: (StartingRoute, String) -> Unit = { _, _ -> }
    ) {
        lifecycleScope.launch {
            val deeplinkDestination = deeplinkDispatcher.parseDeepLink(deepLink)
            Timber.v("[DISPATCHER] DeeplinkDestination: $deeplinkDestination")
            val startWith = if (deeplinkDestination is DeeplinkDestination.Universal) {
                Timber.v("[DISPATCHER] Destination is universal")
                if (packageManager.queryIntentActivities(intent, 0).isNotEmpty()) {
                    verifyStartWithBrowser()
                } else {
                    verifyStartWithMain()
                }
            } else {
                Timber.v("[DISPATCHER] Destination not universal")
                verifyStartWithMain()
            }
            onActivityRouted(startWith, deepLink.toString())
        }
    }

    private fun verifyStartWithBrowser() = if (MainActivity.activityExists) {
        StartingRoute.MAIN_DISPATCHER
    } else {
        StartingRoute.BROWSER
    }

    private fun verifyStartWithMain(): StartingRoute {
        return if (isMainActivityOpened) {
            StartingRoute.MAIN_DISPATCHER
        } else {
            StartingRoute.SPLASH
        }
    }

    private fun startViaSplashActivity(deepLink: String) {
        Timber.v("[DISPATCHER] Starting via Splash Screen extras")
        val intent = Intent(this@DeepLinkDispatcherActivity, SplashActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(EXTRAS_DEEPLINK_URL, deepLink)
        }
        startActivity(intent)
        finish()
    }

    private fun startViaBrowser(deepLink: String) {
        Timber.v("[DISPATCHER] Starting via Browser")
        val targetIntent = getStartViaBrowserIntent(deepLink)
        if (isMainActivityOpened) {
            sendToMainDispatcher(deepLink)
        } else {
            startActivity(targetIntent)
            finishAndRemoveTask()
        }
    }

    private fun getStartViaBrowserIntent(deepLink: String): Intent {
        val emptyBrowserIntent = Intent().apply {
            action = ACTION_VIEW
            addCategory(Intent.CATEGORY_BROWSABLE)
            data = Uri.fromParts("http", "", null)
        }
        val targetIntent = Intent().apply {
            action = ACTION_VIEW
            addCategory(Intent.CATEGORY_BROWSABLE)
            data = Uri.parse(deepLink)
            selector = emptyBrowserIntent
        }
        return targetIntent
    }

    private fun sendToMainDispatcher(deepLink: String) {
        Timber.v("[DISPATCHER] Sending deeplink via Main Dispatcher")
        lifecycleScope.launch {
            deeplinkEventProducer.emit(deepLink)
            DeepLinkDispatcherActivity@ finish()
        }
    }

    private fun handleArticleSharingExperimentDeeplink(): Boolean {
        // Tt Handle Article Sharing experiment Deeplink.
        //  Redirect to mobile web if the app has a browser application
        return isArticleSharingExperimentDeepLink(intent.data) && openUrlIntentWithAnyAppButCurrent(intent)
    }

    /**
     * Handles Kochava SmartLink. Returns [true] in case the deeplink is Kochava SmartLink. We need to call
     * [Tracker.processDeeplink] here and wait for it callback before routing to the correct Activity.
     */
    private fun handleKochavaDeeplink(): Boolean {
        val deepLink = intent?.data ?: return false
        if (deepLink.host == KOCHAVA_SMARTLINK_HOST || deepLink.host == KOCHAVA_SMARTLINK_TEST_HOST) {
            kochavaWrapper.processDeeplink(deepLink.toString(), 10.0) { kochavaProcessedDeeplink ->
                handleDeeplink(Uri.parse(kochavaProcessedDeeplink.destination))
            }
            return true
        }
        return false
    }

    private fun handleOAuthCallback(): Boolean {
        val deepLink = intent?.data ?: return false
        if (deepLink.encodedAuthority == "oauth-callback") {
            val intent = AuthenticationActivity.newIntent(
                this,
                AuthenticationActivity.FragmentType.AUTHENTICATION
            ).apply { data = deepLink }
            startActivity(intent)
            return true
        }
        return false
    }

    /**
     * Returns true if and only if another application was found to handle the intent
     */
    private fun openUrlIntentWithAnyAppButCurrent(intent: Intent): Boolean {
        val browserIntent = Intent(ACTION_VIEW).also { it.data = Uri.parse("https://www.theathletic.com") }
        val externalIntentList = packageManager.queryIntentActivities(browserIntent, 0)
            .asSequence()
            .filter { resolveInfo ->
                !resolveInfo.activityInfo.packageName.startsWith("com.theathletic")
            }.map { resolveInfo ->
                Intent(ACTION_VIEW).also {
                    it.`package` = resolveInfo.activityInfo.packageName
                    it.data = intent.data
                }
            }.toMutableList()
        if (externalIntentList.isEmpty()) return false
        val chooserIntent = Intent.createChooser(externalIntentList.removeAt(0), "Open link with")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, externalIntentList.toTypedArray())
        startActivity(chooserIntent)
        return true
    }

    private fun isArticleSharingExperimentDeepLink(deepLink: Uri?): Boolean {
        deepLink?.getQueryParameter("share_token") ?: return false
        if (deepLink.getQueryParameter("source") != "shared_article") return false
        // For a 'deep link' such as "theathletic://gift", host will return 'gift'
        // For an 'app link' such as "https://theathletic.com", host will return 'theathletic.com'
        return deepLink.host == "theathletic.com"
    }

    private val isMainActivityOpened
        get() = (features.isDeeplinkForegroundCheckDisabled || applicationProcessListener.isInForeground) &&
            MainActivity.activityExists

    private enum class StartingRoute {
        SPLASH,
        MAIN_DISPATCHER,
        BROWSER
    }
}