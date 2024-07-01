package com.theathletic.ads.articles

import android.webkit.WebView
import com.theathletic.ads.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.utility.coroutines.DispatcherProvider
import com.theathletic.utility.coroutines.collectIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean

class AdScrollBehaviorImpl @AutoKoin constructor(
    dispatcherProvider: DispatcherProvider
) : AdsScrollBehavior {
    override var webView: WeakReference<WebView>? = null
        set(value) {
            val resources = value?.get()?.context?.resources
            val deviceDensity = resources?.displayMetrics?.density?.toInt() ?: 1
            density = if (deviceDensity <= 0) 1 else deviceDensity
            deviceScreenHeight = resources?.displayMetrics?.heightPixels
            adsInitializeJS = resources?.getString(R.string.initialize_ad_event)
            field = value
        }

    private val timerJob = Job()
    private val timerScope = CoroutineScope(dispatcherProvider.io + timerJob)

    private var shouldSendScrollEvent = AtomicBoolean(true)
    private var density = 1
    private var deviceScreenHeight: Int? = null
    private var adsInitializeJS: String? = null

    private val timer = flow {
        delay(THROTTLE_IN_MILLI)
        emit(true)
    }

    override fun requestInitialAd() {
        adsInitializeJS?.let {
            webView?.get()?.loadUrl(String.format(it, deviceScreenHeight ?: 0))
        }
    }

    override fun onScrolled(scrollY: Int) {
        if (shouldSendScrollEvent.get()) {
            shouldSendScrollEvent = AtomicBoolean(false)
            adsInitializeJS?.let {
                webView?.get()?.loadUrl(String.format(it, (scrollY / density)))
            }
            timer.collectIn(timerScope) {
                shouldSendScrollEvent = AtomicBoolean(it)
            }
        }
    }

    companion object {
        private const val THROTTLE_IN_MILLI = 500L
    }
}