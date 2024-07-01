package com.theathletic.ui.binding

data class LinkableTag(
    val id: String,
    val title: String,
    val deeplink: String,
) {
    interface Interactor {
        fun onTagClicked(id: String, deeplink: String)
    }
}