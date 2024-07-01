package com.theathletic.ads.ui

import android.view.ViewGroup
import android.webkit.WebView
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class AdUiExtensionsTest {
    @Mock lateinit var mockViewGroup: ViewGroup
    @Mock lateinit var mockFirstWebview: WebView
    @Mock lateinit var mockNestedViewGroup: ViewGroup
    @Mock lateinit var mockSecondWebView: WebView

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `find webveiw within a viewgroup`() {
        whenever(mockViewGroup.getChildAt(0)).thenReturn(mockFirstWebview)
        whenever(mockViewGroup.childCount).thenReturn(1)
        assertThat(mockViewGroup.findInternalWebView()).isEqualTo(mockFirstWebview)
    }

    @Test
    fun `find webveiw within a nested viewgroup`() {
        whenever(mockViewGroup.getChildAt(0)).thenReturn(mockNestedViewGroup)
        whenever(mockViewGroup.childCount).thenReturn(1)
        whenever(mockNestedViewGroup.getChildAt(0)).thenReturn(mockFirstWebview)
        whenever(mockNestedViewGroup.childCount).thenReturn(1)
        assertThat(mockViewGroup.findInternalWebView()).isEqualTo(mockFirstWebview)
    }

    @Test
    fun `verify webview within the first nested viewgroup returned`() {
        whenever(mockViewGroup.getChildAt(0)).thenReturn(mockNestedViewGroup)
        whenever(mockViewGroup.getChildAt(1)).thenReturn(mockFirstWebview)
        whenever(mockViewGroup.childCount).thenReturn(2)
        whenever(mockNestedViewGroup.getChildAt(0)).thenReturn(mockSecondWebView)
        whenever(mockNestedViewGroup.childCount).thenReturn(1)
        assertThat(mockViewGroup.findInternalWebView()).isEqualTo(mockSecondWebView)
    }

    @Test
    fun `verify first webview within first nested viewgroup returned`() {
        whenever(mockViewGroup.getChildAt(0)).thenReturn(mockNestedViewGroup)
        whenever(mockViewGroup.childCount).thenReturn(1)
        whenever(mockNestedViewGroup.getChildAt(0)).thenReturn(mockFirstWebview)
        whenever(mockNestedViewGroup.getChildAt(1)).thenReturn(mockSecondWebView)
        whenever(mockNestedViewGroup.childCount).thenReturn(2)
        assertThat(mockViewGroup.findInternalWebView()).isEqualTo(mockFirstWebview)
    }
}