package com.theathletic.ui.toaster

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.theathletic.utility.Event

open class ToasterEvent(
    @StringRes val textRes: Int,
    @DrawableRes val iconRes: Int? = null,
    @DrawableRes val iconMaskRes: Int? = null,
    val style: ToasterStyle = ToasterStyle.BASE
) : Event()