package com.theathletic.feed.compose.ui

internal data class NextPageTriggerConfiguration(val threshold: Int, val currentItemsCount: Int) {
    fun shouldTrigger(index: Int): Boolean = index >= (currentItemsCount - threshold)
}