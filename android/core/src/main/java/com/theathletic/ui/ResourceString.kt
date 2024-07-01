package com.theathletic.ui

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import com.theathletic.ui.binding.ParameterizedString

/**
 * Used to specify a string or plural resource with parameters
 * without needing to use Context in the Transformer layer.
 */
sealed interface ResourceString {
    data class StringWrapper(
        val value: String
    ) : ResourceString

    data class StringWithParams(
        @StringRes val stringRes: Int,
        val parameters: List<Any>
    ) : ResourceString {
        constructor(
            @StringRes stringRes: Int,
            vararg items: Any
        ) : this(stringRes, items.toList())
    }

    data class Plurals(
        @PluralsRes val pluralsRes: Int,
        val count: Int,
        val parameters: List<Any>
    ) : ResourceString {
        constructor(
            @PluralsRes pluralsRes: Int,
            count: Int,
            vararg items: Any
        ) : this(pluralsRes, count, items.toList())
    }
}

fun String.asResourceString(): ResourceString = ResourceString.StringWrapper(this)
fun ResourceString?.orEmpty(): ResourceString = this ?: "".asResourceString()
fun ResourceString?.orShortDash() = this ?: "-".asResourceString()
fun ResourceString?.asParameterizedString() = when (this) {
    is ResourceString.StringWrapper -> ParameterizedString(value)
    is ResourceString.StringWithParams -> ParameterizedString(stringRes, parameters)
    else -> null
}
fun ResourceString?.asString(context: Context, default: String): String = when (this) {
    is ResourceString.StringWrapper -> value
    is ResourceString.StringWithParams -> context.getString(stringRes, *parameters.toTypedArray())
    is ResourceString.Plurals -> context.resources.getQuantityString(
        pluralsRes,
        count,
        *parameters.toTypedArray()
    )
    else -> default
}