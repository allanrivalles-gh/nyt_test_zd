package com.theathletic.utility.logging

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import androidx.webkit.WebViewCompat
import com.theathletic.R
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.featureswitches.FeatureSwitch
import com.theathletic.featureswitches.FeatureSwitches
import com.theathletic.utility.IPreferences
import timber.log.Timber

class WebviewVersionValidator @AutoKoin constructor(
    private val analytics: Analytics,
    private val preferences: IPreferences,
    private val featureSwitches: FeatureSwitches
) {
    companion object {
        const val MINIMUM_SUPPORTED_VERSION = 84
    }

    fun isOnOldWebView(context: Context): Boolean {
        if (!featureSwitches.isFeatureEnabled(FeatureSwitch.WEBVIEW_VERSION_VALIDATOR_ENABLED)) return false
        val packageInfo = WebViewCompat.getCurrentWebViewPackage(context)
        return if (packageInfo != null) {
            try {
                val majorVersion = packageInfo.versionName.split(".").firstOrNull() ?: return false
                Integer.parseInt(majorVersion) < MINIMUM_SUPPORTED_VERSION
            } catch (e: Exception) {
                Timber.e("Failed to parse WebviewVersion")
                false
            }
        } else {
            Timber.e("Failed to find WebView package info")
            false
        }
    }

    private fun getAppName(context: Context, packageInfo: PackageInfo): String {
        return try {
            val appInfo = context.packageManager.getApplicationInfo(packageInfo.packageName, 0)
            context.packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            "System Webview"
        }
    }

    fun launchWebviewVersionDialog(context: Context) {
        val packageInfo = WebViewCompat.getCurrentWebViewPackage(context) ?: return
        val appName = getAppName(context, packageInfo)
        val message = context.getString(R.string.webview_update_dialogue_message, appName)
        analytics.track(Event.Diagnostics.WebviewVersionDialogueView("$appName:${packageInfo.versionName}"))
        AlertDialog.Builder(context)
            .setTitle(R.string.webview_update_dialogue_title)
            .setIcon(R.drawable.ic_alert_red)
            .setMessage(message)
            .setPositiveButton(R.string.webview_update_dialogue_cta_update) { _, _ ->
                launchGooglePlayForPackage(context, packageInfo.packageName)
                preferences.hasSeenWebViewVersionNotice = true
                analytics.track(Event.Diagnostics.WebviewVersionDialogueUpgrade)
            }
            .setNegativeButton(R.string.webview_update_dialogue_cta_dismiss) { _, _ ->
                preferences.hasSeenWebViewVersionNotice = true
                analytics.track(Event.Diagnostics.WebviewVersionDialogueDismiss)
            }
            .create()
            .show()
    }

    private fun launchGooglePlayForPackage(context: Context, packageName: String) {
        val adjustedPackageName =
            if (packageName == "com.android.webview") "com.google.android.webview" else packageName
        try {
            Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$adjustedPackageName")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(this)
            }
        } catch (exception: ActivityNotFoundException) {
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$adjustedPackageName")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(this)
            }
        } catch (exception: Exception) {
            Timber.e(exception, "Cannot open play store for webkit upgrade")
        }
    }
}