package com.theathletic.widget.twitter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewStub
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.theathletic.R
import com.theathletic.extension.extLogError
import com.theathletic.repository.safeApiRequest
import com.theathletic.twitter.data.TwitterRepository
import com.theathletic.ui.DisplayPreferences
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TweetView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyle, defStyleRes), KoinComponent {

    companion object {
        const val THEME_DARK = "dark"
        const val THEME_LIGHT = "light"
    }

    private val twitterRepository by inject<TwitterRepository>()
    private val displayPreferences by inject<DisplayPreferences>()
    private val dispatcherProvider by inject<DispatcherProvider>()

    private var webView: WebView? = null
    private val viewStub: ViewStub
    private val tweetFrame: FrameLayout

    private var loadedTweetUrl: String? = null
    private var loadedTweetHtml: String? = null

    private var tweetTheme = THEME_DARK
    private var tweetBg: Int

    private val coroutineScope = CoroutineScope(dispatcherProvider.io)

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.tweet_widget, this, true)
        tweetFrame = view.findViewById(R.id.tweet_frame)
        viewStub = view.findViewById(R.id.tweet_view_stub)

        tweetBg = ContextCompat.getColor(context, R.color.ath_grey_80)
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.TweetView, 0, 0)
            tweetBg = ContextCompat.getColor(
                context,
                typedArray.getResourceId(R.styleable.TweetView_tweetBackgroundColor, R.color.ath_grey_80)
            )
            tweetTheme = if (displayPreferences.shouldDisplayDayMode(context)) {
                THEME_LIGHT
            } else {
                THEME_DARK
            }
            typedArray.recycle()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(webView: WebView) {
        webView.apply {
            settings.javaScriptEnabled = true
            settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            settings.domStorageEnabled = true
            setBackgroundColor(tweetBg)
            scrollBarSize = 0
            settings.loadWithOverviewMode = true
            webChromeClient = WebChromeClient()
            webViewClient = CustomWebViewClient(tweetFrame)
        }
    }

    @SuppressLint("CheckResult", "SetJavaScriptEnabled")
    fun showTweet(tweetUrl: String?, inflateWebview: Boolean) {
        if (webView == null && inflateWebview) {
            val tweetWebView = viewStub.inflate().findViewById<WebView>(R.id.tweet_view)
            webView = tweetWebView
            setupWebView(tweetWebView)

            if (!loadedTweetHtml.isNullOrEmpty()) {
                displayDataIfInflated()
            }
        }

        if (loadedTweetUrl != tweetUrl && tweetUrl != null) {
            loadTwitterHtml(tweetUrl)
        }
    }

    private fun loadTwitterHtml(url: String) {
        if (url != loadedTweetUrl) {
            loadedTweetUrl = url
            coroutineScope.launch {
                safeApiRequest { twitterRepository.getTwitterUrl(url, tweetTheme == THEME_LIGHT) }
                    .onSuccess {
                        loadedTweetHtml = it.html
                            .replace("\\\"", "\"")
                            .replace("\\n", "\n")
                        displayDataIfInflated()
                    }
                    .onError {
                        it.extLogError()
                        loadedTweetUrl = null

                        tweetFrame.visibility = View.GONE
                        webView?.visibility = View.GONE
                    }
            }
        }
    }

    private fun displayDataIfInflated() {
        webView?.let { webView ->
            loadedTweetHtml?.let { html ->
                tweetFrame.visibility = View.VISIBLE
                webView.visibility = View.GONE

                webView.loadDataWithBaseURL("", html, "text/html", "UTF-8", null)
            }
        }
    }
}

class CustomWebViewClient(private val tweetFrame: FrameLayout) : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        tweetFrame.context.startActivity(
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
                tweetFrame.visibility = View.GONE
                view.visibility = View.VISIBLE
            },
            500
        )
    }
}