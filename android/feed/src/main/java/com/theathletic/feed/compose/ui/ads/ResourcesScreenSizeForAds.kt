package com.theathletic.feed.compose.ui.ads

import android.content.res.Resources
import android.util.Size
import com.theathletic.extension.toDp
import com.theathletic.feed.R

internal val Resources.screenSizeForAds: Size
    get() {
        val padding = getDimension(R.dimen.global_list_gutter_padding).toInt().toDp
        val width = displayMetrics.widthPixels.toDp - (padding * 2)
        val height = displayMetrics.heightPixels.toDp
        return Size(width, height)
    }