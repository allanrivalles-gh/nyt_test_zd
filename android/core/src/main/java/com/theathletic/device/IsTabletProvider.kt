package com.theathletic.device

import android.content.Context
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Named
import com.theathletic.core.R

class IsTabletProvider @AutoKoin constructor(
    @Named("application-context") private val context: Context
) {

    val isTablet get() = context.resources.getBoolean(R.bool.tablet)
}