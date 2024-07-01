package com.theathletic.liveblog.ui

import com.theathletic.liveblog.data.local.LiveBlogLinks

data class LiveBlogWebViewUiState(
    val webViewLoaded: Boolean = false,
    val hasError: Boolean = false,
    val liveBlogLinks: LiveBlogLinks? = null,
    val adConfig: String? = null,
    val initialPostId: String? = null
) {
    val isLoading: Boolean
        get() = hasError.not() && (liveBlogLinks == null || webViewLoaded.not())
}

sealed class Event : com.theathletic.utility.Event() {
    object OnBackClick : Event()
    data class OnShareClick(val permalink: String) : Event()
}