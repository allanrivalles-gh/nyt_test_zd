package com.theathletic.ui.toaster

import androidx.annotation.StyleRes
import com.theathletic.R

enum class ToasterStyle(@StyleRes val themeRes: Int) {
    BASE(R.style.Theme_Toaster),
    GREEN(R.style.Theme_Toaster_Green),
    RED(R.style.Theme_Toaster_Red),
}