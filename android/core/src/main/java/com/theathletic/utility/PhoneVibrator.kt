package com.theathletic.utility

import android.os.Vibrator
import com.theathletic.annotation.autokoin.AutoKoin

class PhoneVibrator @AutoKoin constructor(
    private val vibrator: Vibrator
) {
    enum class Duration(val vibrationMs: Long) {
        CLICK(10L)
    }

    fun vibrate(duration: Duration) {
        vibrator.vibrate(duration.vibrationMs)
    }
}