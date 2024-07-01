package com.theathletic.article.ui

import android.content.Context
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.theathletic.AthleticConfig
import com.theathletic.R
import com.theathletic.ads.AdsManager
import com.theathletic.ads.bridge.AdBridge
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.featureswitch.Features
import com.theathletic.ui.ContentTextSize
import com.theathletic.ui.DisplayPreferences
import com.theathletic.widget.webview.VideoEnabledWebView
import java.util.regex.Pattern

class WebViewHtmlBinder @AutoKoin constructor(
    private val adsManager: AdsManager,
    private val adBridge: AdBridge,
    private val displayPreferences: DisplayPreferences,
    private val features: Features
) {
    fun setHtmlContent(
        webView: VideoEnabledWebView,
        content: String?,
        url: String?,
        forceRefresh: Boolean = false,
        adConfig: String?
    ) {
        // Let's use webView tag to store last content value. Let's not update webView if the content is the same.
        if ((content.isNullOrEmpty() || webView.tag == content) && !forceRefresh)
            return

        val context = webView.context ?: return
        val isDayMode = displayPreferences.shouldDisplayDayMode(context)
        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            WebSettingsCompat.setForceDark(
                webView.settings,
                if (isDayMode) WebSettingsCompat.FORCE_DARK_OFF else WebSettingsCompat.FORCE_DARK_ON
            )
        }

        webView.tag = content
        if (features.isArticleAdsEnabled && adConfig?.isNotEmpty() == true) {
            adBridge.attach(webView, adConfig, url)
        }

        var htmlString = formatBaseHtmlContent(context, content, isDayMode)
        htmlString = formatLazyLoadingImages(htmlString)
        htmlString = formatTextSize(htmlString, displayPreferences.contentTextSize)
        htmlString = formatTheme(htmlString, isDayMode)
        htmlString = formatTwitterEmbeds(htmlString, isDayMode)

        // Remove Video download option
        htmlString = htmlString.replace("<video ", "<video controlsList=\"nodownload\" ")

        webView.loadDataWithBaseURL(
            "https://${AthleticConfig.BASE_URL_US}",
            htmlString,
            "text/html",
            null,
            null
        )
    }

    private fun formatBaseHtmlContent(
        context: Context,
        htmlContent: String?,
        isDayMode: Boolean
    ): String {
        val bodyTags = context.getString(
            if (isDayMode) {
                R.string.webview_mvp_bodyclass_day
            } else {
                R.string.webview_mvp_bodyclass_night
            }
        )

        val htmlString = when {
            features.isTcfConsentEnabled && features.isArticleAdsEnabled -> {
                context.getString(
                    R.string.webview_mvp_base_html_with_ads_and_consent,
                    adsManager.adCacheUniqueIdentifier,
                    AthleticConfig.TRANSCEND_CONSENT_URL,
                    bodyTags,
                    htmlContent
                )
            }
            features.isArticleAdsEnabled -> {
                context.getString(
                    R.string.webview_mvp_base_html_with_ads,
                    adsManager.adCacheUniqueIdentifier,
                    bodyTags,
                    htmlContent
                )
            }
            features.isTcfConsentEnabled -> {
                context.getString(
                    R.string.webview_mvp_base_html_with_consent,
                    AthleticConfig.TRANSCEND_CONSENT_URL,
                    bodyTags,
                    htmlContent
                )
            }
            else -> {
                context.getString(R.string.webview_mvp_base_html, bodyTags, htmlContent)
            }
        }

        return htmlString.replace("<img", "<img onclick=\"imageClicked(this)\"")
    }

    private fun formatLazyLoadingImages(htmlContent: String): String {
        var resultString = htmlContent
        val pattern = Pattern.compile("<img[^>]*>")
        val matcher = pattern.matcher(resultString)
        while (matcher.find()) {
            val original = matcher.group()
            if (original.contains("srcset=") && original.contains("src=")) {
                var new = original.replace(" src=", " data-src=")
                new = new.replace(" srcset=", " data-srcset=")
                new = if (new.contains(" class=\"")) {
                    new.replace(" class=\"", " class=\"lazyload ")
                } else {
                    new.replace("<img ", "<img class=\"lazyload\" ")
                }
                resultString = resultString.replace(original, new)
            }
        }
        return resultString
    }

    private fun formatTextSize(
        htmlContent: String,
        contentTextSize: ContentTextSize
    ): String {
        return when (contentTextSize) {
            ContentTextSize.MEDIUM -> htmlContent.replace("article_text_default.css", "article_text_medium.css")
            ContentTextSize.LARGE -> htmlContent.replace("article_text_default.css", "article_text_large.css")
            ContentTextSize.EXTRA_LARGE -> htmlContent.replace(
                "article_text_default.css",
                "article_text_extra_large.css"
            )
            else -> htmlContent
        }
    }

    private fun formatTheme(
        htmlContent: String,
        isDayMode: Boolean
    ): String {
        return if (isDayMode) {
            htmlContent
        } else {
            htmlContent.replace("webview_mvp_theme_day.css", "webview_mvp_theme_night.css")
        }
    }

    private fun formatTwitterEmbeds(
        htmlContent: String,
        isDayMode: Boolean
    ): String {
        return if (!isDayMode) {
            htmlContent.replace("class=\"twitter-tweet\"", "class=\"twitter-tweet\" data-theme=\"dark\"")
        } else {
            htmlContent
        }
    }
}