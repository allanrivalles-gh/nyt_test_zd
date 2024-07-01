package com.theathletic.viewmodel

import android.os.Bundle
import androidx.databinding.ObservableField
import androidx.lifecycle.LifecycleObserver
import com.theathletic.activity.FullscreenPhotoActivity

class FullscreenPhotoViewModel() : BaseViewModel(), LifecycleObserver {
    val imageUrl: ObservableField<String> = ObservableField("")

    constructor(extras: Bundle?) : this() {
        // handle intent extras
        handleExtras(extras)
    }

    private fun handleExtras(extras: Bundle?) {
        if (extras == null)
            return

        if (extras.get(FullscreenPhotoActivity.EXTRA_URL) is String)
            imageUrl.set(extras.get(FullscreenPhotoActivity.EXTRA_URL) as String)
    }
}