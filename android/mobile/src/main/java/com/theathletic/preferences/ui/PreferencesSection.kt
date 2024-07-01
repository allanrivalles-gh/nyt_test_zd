package com.theathletic.preferences.ui

import androidx.annotation.StringRes
import com.theathletic.R
import com.theathletic.ui.ListSection

sealed class PreferencesSection(
    override val sectionId: String,
    @StringRes override val titleResId: Int
) : ListSection {
    object PushNotifs : PreferencesSection("PUSH_NOTIFS", R.string.preferences_section_push_notifs)
    object Newsletter : PreferencesSection("NEWSLETTER", R.string.preferences_section_newsletters)

    object TopicPushNotifs : PreferencesSection(
        "TOPIC_PUSH_NOTIFS",
        R.string.preferences_section_topic_push_notifs
    )
}