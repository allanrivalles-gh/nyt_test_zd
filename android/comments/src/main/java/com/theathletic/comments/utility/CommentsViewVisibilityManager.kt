package com.theathletic.comments.utility

import com.theathletic.annotation.autokoin.AutoKoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CommentsViewVisibilityManager @AutoKoin constructor() {
    private val _isVisible = MutableStateFlow(false)
    val isVisible: StateFlow<Boolean> = _isVisible

    private var isActive = false
    private var isTabSelected = true

    fun onResumed() {
        isActive = true
        onVisibilityMayHaveChanged()
    }

    fun onPaused() {
        isActive = false
        onVisibilityMayHaveChanged()
    }

    fun onTabSelectionChanged(isSelected: Boolean) {
        isTabSelected = isSelected
        onVisibilityMayHaveChanged()
    }

    private fun onVisibilityMayHaveChanged() {
        _isVisible.value = isTabSelected && isActive
    }
}