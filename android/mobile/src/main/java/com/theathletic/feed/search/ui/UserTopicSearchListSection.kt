package com.theathletic.feed.search.ui

import androidx.annotation.StringRes
import com.theathletic.R
import com.theathletic.ui.ListSection

sealed class UserTopicSearchListSection(
    override val sectionId: String,
    @StringRes override val titleResId: Int
) : ListSection {
    object Browse : UserTopicSearchListSection("BROWSE", R.string.drawer_more_suggestions)
}