package com.theathletic.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ResourceString.asString() = when (this) {
    is ResourceString.StringWrapper -> value
    is ResourceString.StringWithParams -> stringResource(id = stringRes, *parameters.toTypedArray())
    is ResourceString.Plurals -> pluralStringResource(
        id = pluralsRes,
        count = count,
        formatArgs = parameters.toTypedArray()
    )
}

@Composable
fun List<ResourceString>.asString(
    separator: String = ", ",
    ignoreFirstSeparator: Boolean = false
): String {
    val stringBuilder = StringBuilder()
    this.forEachIndexed { index, resourceString ->
        stringBuilder.append(resourceString.asString())
        if (index == 0 && ignoreFirstSeparator) {
            stringBuilder.append(" ")
        } else if (index != this.lastIndex) {
            stringBuilder.append(separator)
        }
    }
    return stringBuilder.toString()
}