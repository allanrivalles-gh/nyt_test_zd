package com.theathletic.ads.articles

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.webkit.WebView
import com.theathletic.ads.R
import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.runTest
import kotlinx.coroutines.delay
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import java.lang.ref.WeakReference

class AdScrollBehaviorTest {
    @get:Rule val coroutineTestRule = CoroutineTestRule()

    @Mock lateinit var mockContext: Context
    @Mock lateinit var mockResources: Resources
    @Mock lateinit var mockDisplayMetrics: DisplayMetrics
    @Mock lateinit var webView: WebView

    private val adScrollBehavior = AdScrollBehaviorImpl(coroutineTestRule.dispatcherProvider)

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        whenever(mockResources.getString(R.string.initialize_ad_event))
            .thenReturn("javascript:window.dispatch(\'scroll\')")
        mockDisplayMetrics.heightPixels = 800
        mockDisplayMetrics.density = 1f
        whenever(mockResources.displayMetrics).thenReturn(mockDisplayMetrics)
        whenever(mockContext.resources).thenReturn(mockResources)
        whenever(webView.context).thenReturn(mockContext)
        adScrollBehavior.webView = WeakReference(webView)
    }

    @Test
    fun `trigger a scroll event on first scroll after delay`() = runTest {
        adScrollBehavior.requestInitialAd()

        adScrollBehavior.onScrolled(0)
        verify(webView, times(2)).loadUrl(anyString())
    }

    @Test
    fun `trigger a scroll event only after delay`() = runTest {
        adScrollBehavior.requestInitialAd()
        verify(webView, times(1)).loadUrl(anyString())
        adScrollBehavior.onScrolled(-400)
        verify(webView, times(2)).loadUrl(anyString())
        adScrollBehavior.onScrolled(0)
        verify(webView, times(2)).loadUrl(anyString())
        coroutineTestRule.advanceTimeBy(600)
        adScrollBehavior.onScrolled(300)
        verify(webView, times(3)).loadUrl(anyString())
        adScrollBehavior.onScrolled(640)
        verify(webView, times(3)).loadUrl(anyString())
        delay(600)
        adScrollBehavior.onScrolled(1280)
        verify(webView, times(4)).loadUrl(anyString())
    }

    @Test
    fun `verify ad scroll event when scrolling up`() = runTest {
        adScrollBehavior.onScrolled(0)
        verify(webView, times(1)).loadUrl(anyString())
        coroutineTestRule.advanceTimeBy(600)
        adScrollBehavior.onScrolled(640)
        verify(webView, times(2)).loadUrl(anyString())
        coroutineTestRule.advanceTimeBy(600)
        adScrollBehavior.onScrolled(500)
        verify(webView, times(3)).loadUrl(anyString())
    }

    @Test
    fun `verify cannot divide by zero density`() = runTest {
        val mockDisplayMetrics = mock(DisplayMetrics::class.java)
        mockDisplayMetrics.heightPixels = 800
        mockDisplayMetrics.density = 0f
        whenever(mockResources.displayMetrics).thenReturn(mockDisplayMetrics)
        adScrollBehavior.webView = WeakReference(webView)

        coroutineTestRule.advanceTimeBy(600)
        adScrollBehavior.onScrolled(-200)
        verify(webView, times(1)).loadUrl(anyString())
    }
}