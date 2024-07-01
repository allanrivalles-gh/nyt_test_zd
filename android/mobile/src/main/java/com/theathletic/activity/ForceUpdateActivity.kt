package com.theathletic.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.theathletic.R

class ForceUpdateActivity : BaseActivity() {
    companion object {
        fun newIntent(context: Context): Intent = Intent(context, ForceUpdateActivity::class.java)
    }

    private lateinit var appUpdateManager: AppUpdateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_force_update)

        appUpdateManager = AppUpdateManagerFactory.create(this)
    }

    override fun onResume() {
        super.onResume()

        appUpdateManager.appUpdateInfo.addOnSuccessListener { updateInfo ->
            if (updateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                appUpdateManager.startUpdateFlow(
                    updateInfo,
                    this,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                )
            }
        }
    }

    fun onGetNewVersionClick(view: View) {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { updateInfo ->
            if (updateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                updateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlow(
                    updateInfo,
                    this,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                )
            }
        }
    }
}