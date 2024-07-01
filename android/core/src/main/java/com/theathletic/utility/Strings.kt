package com.theathletic.utility

import com.theathletic.ui.binding.ParameterizedString
import java.util.Locale

fun String?.orShortDash(): String = this ?: "-"

fun String?.orLongDash(): String = this ?: "\u2014"

fun String.toTitleCase() = lowercase(Locale.ROOT)
    .split(" ")
    .joinToString(" ") { char ->
        char.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
    }

fun String.asParameterized() = ParameterizedString(this)