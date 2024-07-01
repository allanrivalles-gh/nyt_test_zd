package com.theathletic.article

class WebViewRestorationManager(
    private val maximumForegroundCrashCount: Int = 3,
    private val restore: () -> Unit,
) {
    private var crashedInForegroundCount = 0
    // when creating this class we assume the app is in the foreground
    private var isAppInForeground = true
    private var pendingRestoration = false

    fun onAppInForeground() {
        isAppInForeground = true
        if (pendingRestoration) {
            pendingRestoration = false
            restore()
        }
    }

    fun onAppInBackground() {
        isAppInForeground = false
    }

    fun onWebViewCrashed() {
        if (isAppInForeground) {
            crashedInForegroundCount += 1

            // we don't want to keep recreating the view if it crashes in the foreground
            // https://developer.android.com/develop/ui/views/layout/webapps/managing-webview#termination-handle
            //   """
            //   Be aware that, if a renderer crashes while loading a particular web page,
            //   attempting to load that same page again could cause a new WebView object to exhibit
            //   the same rendering crash behavior.
            //   """
            if (crashedInForegroundCount <= maximumForegroundCrashCount) {
                restore()
            }
        } else {
            pendingRestoration = true
        }
    }
}