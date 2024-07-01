package com.theathletic.main.ui

import androidx.annotation.StringRes
import com.theathletic.core.R

enum class BottomTabItem(
    @StringRes val titleId: Int,
) {
    FEED(R.string.main_navigation_feed),
    SCORES(R.string.main_navigation_scores),
    FRONTPAGE(R.string.main_navigation_news),
    DISCOVER(R.string.main_navigation_discover),
    LISTEN(R.string.main_navigation_listen),
    ACCOUNT(R.string.main_navigation_account),
}