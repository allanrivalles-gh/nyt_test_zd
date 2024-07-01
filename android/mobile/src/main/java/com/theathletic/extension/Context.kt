package com.theathletic.extension

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.media.AudioManager
import android.provider.Settings
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.fragment.app.FragmentActivity

val Context.notificationManager: NotificationManager get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
val Context.audioManager get() = getSystemService(Context.AUDIO_SERVICE) as AudioManager

@SuppressLint("HardwareIds")
fun Context.deviceID(): String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID) ?: "NoAndroidId"

/**
 * Return true if this [Context] is available.
 * Availability is defined as the following:
 * + [Context] is not null
 * + [Context] is not destroyed (tested with [FragmentActivity.isDestroyed] or [Activity.isDestroyed])
 * + [Context] is not finishing (tested with [FragmentActivity.isFinishing] or [Activity.isFinishing])
 */
fun Context?.extIsAvailable(): Boolean {
    if (this == null) {
        return false
    } else if (this !is Application) {
        if (this is FragmentActivity) {
            return !(this.isDestroyed || this.isFinishing)
        } else if (this is Activity) {
            return !(this.isDestroyed || this.isFinishing)
        }
    }
    return true
}

fun Context?.extGetActivityContext(): Context? {
    var context = this
    var failSafeMaxDepth = 15

    while (context is ContextWrapper && failSafeMaxDepth > 0) {
        if (context is Activity || context is FragmentActivity) {
            return context
        }
        context = context.baseContext
        failSafeMaxDepth--
    }

    return null
}

/**
 * When invoking this, use the most-specific Context available to ensure the appropriate
 * theme is applied to retrieve the color. E.g. from within a View, use that context. If in an
 * Activity or Fragment, use that context.
 */
@ColorInt
fun Context.getColorFromAttr(
    @AttrRes attrColor: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Int {
    theme.resolveAttribute(attrColor, typedValue, resolveRefs)
    return typedValue.data
}

fun Context.dpToPx(dp: Int): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        resources.displayMetrics
    )
}