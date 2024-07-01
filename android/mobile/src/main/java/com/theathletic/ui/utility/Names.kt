package com.theathletic.ui.utility

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import com.theathletic.R
import com.theathletic.rooms.local.LiveAudioRoomUserDetails
import com.theathletic.ui.ResourceString
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.ResourceString.StringWrapper
import com.theathletic.ui.asResourceString

fun initials(
    firstname: String,
    lastname: String
): ResourceString {
    val firstInitial = firstname.capitalize(Locale.current).firstOrNull() ?: ""
    val lastInitial = lastname.capitalize(Locale.current).firstOrNull() ?: ""
    return StringWrapper("$firstInitial$lastInitial")
}

fun initializedName(firstname: String, lastname: String): ResourceString {
    return when (lastname.isEmpty()) {
        true -> StringWrapper(firstname)
        else -> StringWithParams(
            R.string.global_name_full_first_initialized_last,
            firstname,
            lastname.first(),
        )
    }
}

val LiveAudioRoomUserDetails.displayName: ResourceString get() = when {
    staffInfo?.verified == true -> name.asResourceString()
    else -> initializedName(firstname, lastname)
}