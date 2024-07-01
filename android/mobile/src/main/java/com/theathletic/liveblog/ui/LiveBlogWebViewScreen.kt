package com.theathletic.liveblog.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.theathletic.R
import com.theathletic.ads.bridge.AdBridge
import com.theathletic.ads.bridge.data.local.AdEvent
import com.theathletic.featureswitch.Features
import com.theathletic.liveblog.data.local.LiveBlogLinks
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.DisplayPreferences
import com.theathletic.ui.utility.rememberKoin
import com.theathletic.ui.widgets.buttons.PrimaryButtonSmall
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Composable
fun LiveBlogWebViewScreen(
    viewModel: LiveBlogWebViewViewModel,
    refreshable: Boolean = false,
    showToolbar: Boolean = true
) {
    val displayPreferences = rememberKoin<DisplayPreferences>()
    val context = LocalContext.current
    val state by viewModel.viewState.collectAsState()
    val liveBlogLinks = state.liveBlogLinks

    AthleticTheme(lightMode = displayPreferences.shouldDisplayDayMode(context)) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if (showToolbar) {
                LiveBlogToolbar(
                    onBackClick = viewModel::onBackClick,
                    onShareClick = viewModel::onShareClick,
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AthTheme.colors.dark200)
            ) {
                if (refreshable) {
                    RefreshableContent(
                        liveBlogLinks = liveBlogLinks,
                        postId = viewModel.params.initialPostId,
                        isLoading = state.isLoading,
                        adConfig = state.adConfig,
                        handleAdEvents = viewModel::handleAdEvents,
                        handleLink = viewModel::handleLink,
                        onWebViewReloaded = viewModel::onWebViewReloaded,
                        onWebViewContentLoaded = viewModel::onWebViewContentLoaded,
                    )
                } else {
                    NonRefreshableContent(
                        liveBlogLinks = liveBlogLinks,
                        postId = viewModel.params.initialPostId,
                        adConfig = state.adConfig,
                        handleAdEvents = viewModel::handleAdEvents,
                        handleLink = viewModel::handleLink,
                        onWebViewContentLoaded = viewModel::onWebViewContentLoaded,
                    )
                }
                if (state.isLoading && !refreshable) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = AthTheme.colors.dark600,
                    )
                }
                if (state.hasError) {
                    ErrorView(retry = viewModel::retry)
                }
            }
        }
    }
}

private fun LiveBlogLinks.createWebView(
    context: Context,
    postId: String?,
    adConfig: String?,
    handleAdEvents: (StateFlow<AdEvent>) -> Unit,
    handleLink: (Uri) -> Unit,
    onWebViewContentLoaded: () -> Unit,
): AndroidWebView {
    return AndroidWebView(
        context,
        linkForEmbed = linkForEmbed,
        postId = postId,
        adConfig = adConfig,
        handleAdEvents = handleAdEvents,
        handleLink = handleLink,
        onContentLoaded = onWebViewContentLoaded,
    )
}

@Composable
private fun RefreshableContent(
    liveBlogLinks: LiveBlogLinks?,
    postId: String?,
    isLoading: Boolean,
    adConfig: String?,
    handleAdEvents: (StateFlow<AdEvent>) -> Unit,
    handleLink: (Uri) -> Unit,
    onWebViewReloaded: () -> Unit,
    onWebViewContentLoaded: () -> Unit,
) {
    fun AndroidSwipeRefresh.updateStateIfNeeded() {
        if (webView == null && liveBlogLinks != null) {
            setWebView(
                liveBlogLinks.createWebView(
                    context,
                    postId = postId,
                    adConfig = adConfig,
                    handleAdEvents = handleAdEvents,
                    handleLink = handleLink,
                    onWebViewContentLoaded = onWebViewContentLoaded
                )
            )
        }
        if (isRefreshing != isLoading) {
            isRefreshing = isLoading
        }
    }
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            AndroidSwipeRefresh(
                context,
                onReloadWebView = onWebViewReloaded,
            ).apply {
                updateStateIfNeeded()
            }
        },
        update = { swipeRefresh: AndroidSwipeRefresh ->
            swipeRefresh.updateStateIfNeeded()
        }
    )
}

@Composable
private fun NonRefreshableContent(
    liveBlogLinks: LiveBlogLinks?,
    postId: String?,
    adConfig: String?,
    handleAdEvents: (StateFlow<AdEvent>) -> Unit,
    handleLink: (Uri) -> Unit,
    onWebViewContentLoaded: () -> Unit,
) {
    if (liveBlogLinks != null) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                liveBlogLinks.createWebView(
                    context,
                    postId = postId,
                    adConfig = adConfig,
                    handleAdEvents = handleAdEvents,
                    handleLink = handleLink,
                    onWebViewContentLoaded = onWebViewContentLoaded
                )
            }
        )
    }
}

@Composable
private fun ErrorView(retry: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            stringResource(R.string.global_error),
            style = AthTextStyle.Calibre.Utility.Regular.Small
                .copy(color = AthTheme.colors.dark800),
        )
        PrimaryButtonSmall(
            text = stringResource(R.string.global_retry),
            onClick = retry,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

@SuppressLint("SetJavaScriptEnabled", "ViewConstructor")
private class AndroidWebView(
    context: Context,
    private val linkForEmbed: String,
    private val postId: String?,
    adConfig: String?,
    handleAdEvents: (StateFlow<AdEvent>) -> Unit,
    handleLink: (Uri) -> Unit,
    onContentLoaded: () -> Unit,
) : WebView(context), KoinComponent {
    private val cookieManager by inject<LiveBlogCookieManager>()
    private val displayPreferences by inject<DisplayPreferences>()
    private val features by inject<Features>()
    private val adBridge = AdBridge()

    init {
        setBackgroundColor(Color.TRANSPARENT)
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.domStorageEnabled = true
        settings.javaScriptEnabled = true
        settings.useWideViewPort = true
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        settings.mediaPlaybackRequiresUserGesture = false
        isFocusable = false
        isFocusableInTouchMode = false
        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                handleLink(request.url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                onContentLoaded()
            }
        }
        cookieManager.setCookie(this)
        if (features.isLiveBlogAdsEnabled && adConfig?.isNotEmpty() == true) {
            adBridge.attach(this, adConfig, linkForEmbed)
            handleAdEvents(adBridge.observeAdEvents())
        }
        reload()
    }

    override fun reload() {
        val lightMode = displayPreferences.shouldDisplayDayMode(context)
        val url = createLinkForWebViewLiveBlog(linkForEmbed, lightMode, postId)
        loadUrl(url)
    }
}

@SuppressLint("ViewConstructor")
private class AndroidSwipeRefresh(
    context: Context,
    private val onReloadWebView: () -> Unit
) : SwipeRefreshLayout(context) {
    private var _webView: WebView? = null
    val webView: WebView?
        get() = _webView

    init {
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
    }

    fun setWebView(webView: WebView) {
        assert(_webView == null)
        if (_webView != null) return

        _webView = webView
        addView(webView)
        setOnRefreshListener {
            webView.reload()
            onReloadWebView()
        }
    }
}