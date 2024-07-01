package com.theathletic.ui.binding

import androidx.annotation.StringRes
import com.theathletic.core.R
import com.theathletic.ui.ResourceString
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.asResourceString

/**
 * Can be used with android:parameterizedString in XML to specify a string resource with parameters
 * without needing to use Context in the Transformer layer.
 */
@Deprecated(
    "Only use in XML views, for compose views use ResourceString",
    replaceWith = ReplaceWith("com/theathletic/ui/ResourceString.kt")
)
data class ParameterizedString(
    @StringRes val stringRes: Int,
    val parameters: List<Any>
) {
    constructor(
        @StringRes stringRes: Int,
        vararg items: Any
    ) : this(stringRes, items.toList())

    constructor(rawString: String) : this(R.string.core_raw_parameterized_string, rawString)
}

fun String.asParameterized() = ParameterizedString(this)
fun ParameterizedString?.orShortDash() = this ?: "-".asParameterized()
fun ParameterizedString?.orEmpty() = this ?: "".asParameterized()
fun ParameterizedString?.toResourceString(): ResourceString = this?.let {
    StringWithParams(stringRes, parameters)
} ?: "".asResourceString()