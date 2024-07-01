package com.theathletic.ui.toaster

import android.app.Activity
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import timber.log.Timber

class ToasterQueue {

    data class Request(
        val activity: Activity,
        @StringRes val textRes: Int,
        @DrawableRes val iconRes: Int? = null,
        @DrawableRes val iconMaskRes: Int? = null,
        val style: ToasterStyle = ToasterStyle.BASE,
    )

    private val queue = mutableListOf<Request>()

    fun add(request: Request) {
        queue.add(request)
    }

    fun getFirstValidRequest(): Request? {
        var request = queue.removeFirstOrNull()
        while (request != null) {
            Timber.v("Queue size: ${queue.size}")
            if (!request.activity.isDestroyed) {
                return request
            }
            request = queue.removeFirstOrNull()
        }
        return request
    }
}