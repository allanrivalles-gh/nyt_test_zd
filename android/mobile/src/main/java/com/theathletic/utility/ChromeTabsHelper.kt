package com.theathletic.utility

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsService

class ChromeTabsHelper {
    companion object {
        const val STABLE_PACKAGE = "com.android.chrome"
        const val BETA_PACKAGE = "com.chrome.beta"
        const val DEV_PACKAGE = "com.chrome.dev"
        const val LOCAL_PACKAGE = "com.google.android.apps.chrome"
        private var mPackageNameToUse: String? = null
    }

    fun isChromeTabsAvailable(context: Context) = getPackageName(context) != null

    private fun getPackageName(context: Context): String? {
        if (mPackageNameToUse != null) {
            return mPackageNameToUse
        }

        // Get default VIEW intent handler that can view a web url.
        val activityIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.test-url.com"))

        // Get all apps that can handle VIEW intents.
        val pm = context.packageManager
        val resolvedActivityList = pm.queryIntentActivities(activityIntent, 0)
        val packagesSupportingCustomTabs = arrayListOf<String>()
        for (info in resolvedActivityList) {
            val serviceIntent = Intent()
            serviceIntent.action = CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION
            serviceIntent.setPackage(info.activityInfo.packageName)
            if (pm.resolveService(serviceIntent, 0) != null) {
                packagesSupportingCustomTabs.add(info.activityInfo.packageName)
            }
        }

        // Now packagesSupportingCustomTabs contains all apps that can handle both VIEW intents
        // and service calls.
        when {
            packagesSupportingCustomTabs.isEmpty() -> mPackageNameToUse = null
            packagesSupportingCustomTabs.size == 1 -> mPackageNameToUse = packagesSupportingCustomTabs[0]
            packagesSupportingCustomTabs.contains(STABLE_PACKAGE) -> mPackageNameToUse = STABLE_PACKAGE
            packagesSupportingCustomTabs.contains(BETA_PACKAGE) -> mPackageNameToUse = BETA_PACKAGE
            packagesSupportingCustomTabs.contains(DEV_PACKAGE) -> mPackageNameToUse = DEV_PACKAGE
            packagesSupportingCustomTabs.contains(LOCAL_PACKAGE) -> mPackageNameToUse = LOCAL_PACKAGE
        }
        return mPackageNameToUse
    }
}