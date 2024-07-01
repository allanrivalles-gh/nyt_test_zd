package com.theathletic.article

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class WebViewRestorationManagerTest {
    @Test
    fun `calls restore when app is in foreground and has not crashed maximum times`() {
        var restoreCount = 0
        val manager = WebViewRestorationManager(3, restore = { restoreCount += 1 })

        manager.onAppInForeground()
        manager.onWebViewCrashed()

        assertThat(restoreCount).isEqualTo(1)
    }

    @Test
    fun `doesn't call restore when app is in foreground but has crashed maximum times`() {
        var restoreCount = 0
        val manager = WebViewRestorationManager(1, restore = { restoreCount += 1 })

        manager.onAppInForeground()
        manager.onWebViewCrashed()

        // we reset to test the second time we notified about the crash it was not incremented
        restoreCount = 0

        manager.onWebViewCrashed()

        assertThat(restoreCount).isEqualTo(0)
    }

    @Test
    fun `doesn't call restore when app is in background`() {
        var restoreCount = 0
        val manager = WebViewRestorationManager(restore = { restoreCount += 1 })

        manager.onAppInBackground()
        manager.onWebViewCrashed()

        assertThat(restoreCount).isEqualTo(0)
    }

    @Test
    fun `calls restore when app returns to foreground after a webview crash`() {
        var restoreCount = 0
        val manager = WebViewRestorationManager(restore = { restoreCount += 1 })

        manager.onAppInBackground()
        manager.onWebViewCrashed()

        // should not have restored by this point
        assertThat(restoreCount).isEqualTo(0)

        manager.onAppInForeground()

        assertThat(restoreCount).isEqualTo(1)
    }
}