package com.theathletic.utility

import android.app.Activity
import android.app.Activity.RESULT_OK
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Exposes

interface InAppUpdateHelper {
    fun checkForAppStoreUpdate(activity: Activity, requestCode: Int, onDownloadComplete: () -> Unit)
    fun checkForFinishedUpdate(onDownloadComplete: () -> Unit)
    fun handleActivityResult(resultCode: Int)
    fun completeUpdate()
}

@Exposes(InAppUpdateHelper::class)
class InAppUpdateHelperImpl @AutoKoin constructor(
    private val appUpdateManager: AppUpdateManager,
    private val preferences: IPreferences,
    private val analytics: Analytics
) : InAppUpdateHelper {

    companion object {
        private const val DAYS_FOR_FLEXIBLE_UPDATE = 30
    }

    override fun handleActivityResult(resultCode: Int) {
        if (resultCode != RESULT_OK) {
            appUpdateManager.appUpdateInfo.addOnSuccessListener { updateInfo ->
                preferences.lastDeclinedUpdateVersionCode = updateInfo.availableVersionCode()
                analytics.track(Event.InAppUpdates.FlexibleUpdateSkip)
            }
        }
    }

    override fun checkForAppStoreUpdate(
        activity: Activity,
        requestCode: Int,
        onDownloadComplete: () -> Unit
    ) {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { updateInfo ->
            if (shouldStartUpdateFlow(updateInfo)) {
                appUpdateManager.registerListener { state ->
                    when (state.installStatus()) {
                        InstallStatus.DOWNLOADED -> {
                            onDownloadComplete()
                            analytics.track(Event.InAppUpdates.FlexibleUpdateComplete)
                        }
                        InstallStatus.CANCELED -> {
                            preferences.lastDeclinedUpdateVersionCode = updateInfo.availableVersionCode()
                            analytics.track(Event.InAppUpdates.FlexibleUpdateSkip)
                        }
                        else -> { /* Do nothing */ }
                    }
                }

                appUpdateManager.startUpdateFlowForResult(
                    updateInfo,
                    activity,
                    AppUpdateOptions.defaultOptions(AppUpdateType.FLEXIBLE),
                    requestCode
                )
                analytics.track(Event.InAppUpdates.FlexibleUpdateShown)
            }
        }
    }

    override fun checkForFinishedUpdate(onDownloadComplete: () -> Unit) {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { updateInfo ->
            if (updateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                onDownloadComplete()
                analytics.track(Event.InAppUpdates.FlexibleUpdateComplete)
            }
        }
    }

    override fun completeUpdate() {
        appUpdateManager.completeUpdate()
    }

    private fun shouldStartUpdateFlow(updateInfo: AppUpdateInfo): Boolean {
        return false
        /*
        TODO(Todd): see ATH-16607
        return updateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
            updateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) &&
            (updateInfo.clientVersionStalenessDays() ?: 0) >= DAYS_FOR_FLEXIBLE_UPDATE &&
            updateInfo.availableVersionCode() != preferences.lastDeclinedUpdateVersionCode
         */
    }
}