package com.theathletic.utility

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.theathletic.R
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.compass.codegen.AndroidPushPrePrompt
import com.theathletic.compass.codegen.CompassExperiment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val ONE_SECOND_IN_MILLIS = 1000L

class NotificationPermissionRequest(
    private val fragment: Fragment,
    private val analytics: Analytics
) {

    private val prePromptExperiment = PrePromptExperiment()
    private val permissionRequest =
        fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            onPermissionRequestResult(it)
        }

    fun requestPermission() {

        if (Build.VERSION.SDK_INT >= 33 && fragment.permissionIsNotGranted(Manifest.permission.POST_NOTIFICATIONS)) {
            CompassExperiment.ANDROID_PUSH_PRE_PROMPT.postExposure()
            analytics.track(
                Event.NotificationRequest.View(
                    "push_pre_prompt",
                    experimentId = prePromptExperiment.experimentId,
                    variant = prePromptExperiment.variant
                )
            )

            val prePromptDialog = AlertDialog.Builder(fragment.requireContext())
                .setTitle(prePromptExperiment.title)
                .setMessage(prePromptExperiment.message)
                .setPositiveButton(R.string.notification_dialog_cta_positive) { dialog, _ ->
                    track(view = "push_pre_prompt", element = "ok")
                    permissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
                    dialog.dismiss()
                }
                .setNegativeButton(fragment.getString(R.string.notification_dialog_cta_negative)) { dialog, _ ->
                    track(view = "push_pre_prompt", element = "not_now")
                    dialog.dismiss()
                }
                .setOnCancelListener {
                    track(view = "push_pre_prompt", element = "dismiss")
                }
                .create()

            fragment.lifecycleScope.launch {
                delay(ONE_SECOND_IN_MILLIS)
                prePromptDialog.show()
            }
        }
    }

    private fun onPermissionRequestResult(isGranted: Boolean) {
        val element = if (isGranted) "allow" else "dont_allow"
        track(view = "push_pre_prompt_system", element = element)
    }

    private fun track(view: String, element: String) {
        analytics.track(
            Event.NotificationRequest.Click(
                view,
                element,
                experimentId = prePromptExperiment.experimentId,
                variant = prePromptExperiment.variant
            )
        )
    }

    private class PrePromptExperiment {
        val experimentId: String = CompassExperiment.ANDROID_PUSH_PRE_PROMPT.name
        val variant: String = CompassExperiment.ANDROID_PUSH_PRE_PROMPT.variant._name
        val title: String
        val message: String

        init {
            val (title, message) = when (val variant = CompassExperiment.ANDROID_PUSH_PRE_PROMPT.variant) {
                is AndroidPushPrePrompt.AndroidPushPrePromptVariant.CTRL -> Pair(variant.title, variant.subtitle)
                is AndroidPushPrePrompt.AndroidPushPrePromptVariant.A -> Pair(variant.title, variant.subtitle)
            }

            this.title = title
            this.message = message
        }
    }
}

fun Fragment.permissionIsNotGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED
}