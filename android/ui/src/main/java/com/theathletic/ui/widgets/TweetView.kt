package com.theathletic.ui.widgets

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.theathletic.themes.AthTheme
import com.theathletic.ui.R
import timber.log.Timber

@Composable
fun TweetView(
    tweetHtml: String?,
    tweetKey: String,
    tweetMap: MutableMap<String, WebView>,
    modifier: Modifier
) {
    var showSpinner by remember { mutableStateOf(tweetMap[tweetKey] == null) }

    if (showSpinner) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = AthTheme.colors.dark600,
            )
        }
    }
    AndroidView(
        modifier = modifier,
        factory = { context ->
            tweetMap[tweetKey]?.let {
                (it.parent as? ViewGroup)?.removeView(it)
            }
            tweetMap[tweetKey] ?: WebView(context).apply {
                webChromeClient = WebChromeClient()
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    loadsImagesAutomatically = true
                    cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                    defaultTextEncodingName = "UTF-8"
                    layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
                    useWideViewPort = false
                }
                isHorizontalScrollBarEnabled = false
                isVerticalScrollBarEnabled = false
                isScrollContainer = false
                setOnTouchListener { _: View?, event: MotionEvent -> event.action == MotionEvent.ACTION_MOVE }
                webViewClient = CustomWebViewClient {
                    showSpinner = false
                    tweetMap[tweetKey] = it
                }
                setBackgroundColor(ContextCompat.getColor(context, R.color.ath_grey_65))
            }
        },
        update = {
            if (tweetMap[tweetKey] == null) {
                try {
                    if (tweetHtml != null) {
                        val cleansedHtml = tweetHtml
                            .replace("\\\"", "\"")
                            .replace("\\n", "\n")
                        it.loadDataWithBaseURL(
                            "https://twitter.com",
                            cleansedHtml,
                            "text/html",
                            "utf-8",
                            null
                        )
                    }
                    it.visibility = if (showSpinner) View.GONE else View.VISIBLE
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
        }
    )
}

class CustomWebViewClient(private val onLoaded: (view: WebView) -> Unit) : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        view.context.startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse(url))
        )
        return true
    }

    override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        view.visibility = View.INVISIBLE
    }

    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        // Wait for WebView to render with proper size, hacky but necessary
        Handler().postDelayed(
            {
                view.visibility = View.VISIBLE
                onLoaded(view)
            },
            500
        )
    }
}