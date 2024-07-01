package com.theathletic.liveblog.ui

import com.theathletic.presenter.Interactor
import com.theathletic.ui.ContentTextSize
import com.theathletic.ui.DayNightMode

interface TextStyleBottomSheetContract {
    interface Presenter : Interactor, LiveBlogUi.BottomSheetInteractor

    data class ViewState(
        val textSize: ContentTextSize,
        val dayNightMode: DayNightMode,
        val displaySystemThemeButton: Boolean = false
    ) : com.theathletic.ui.ViewState
}