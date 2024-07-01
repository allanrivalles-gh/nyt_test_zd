package com.theathletic.utility.device

import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import com.theathletic.R
import com.theathletic.extension.deviceID

/**
 * Data class that represents immutable attributes of the device running The Athletic.
 */
data class DeviceInfo(
    val deviceId: String,
    val isTablet: Boolean,
    val osSdkVersion: Int,
    val carrier: String,
    val brand: String,
    val model: String
) {
    companion object {
        private const val UNKNOWN_STRING = "Unknown"

        fun buildFromContext(context: Context): DeviceInfo {
            return DeviceInfo(
                context.deviceID(),
                context.resources.getBoolean(R.bool.tablet),
                Build.VERSION.SDK_INT,
                getCarrierName(context),
                Build.BRAND ?: UNKNOWN_STRING,
                Build.MODEL ?: UNKNOWN_STRING
            )
        }

        private fun getCarrierName(context: Context): String {
            return try {
                val telephonyManager =
                    context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                telephonyManager.simOperatorName
            } catch (e: Exception) {
                UNKNOWN_STRING
            }
        }
    }
}