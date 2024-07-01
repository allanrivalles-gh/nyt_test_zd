package com.theathletic.article.ui

import com.theathletic.presenter.Interactor
import com.theathletic.ui.ContentTextSize
import com.theathletic.ui.DayNightMode

interface ArticleSettingsSheetContract {
    interface Presenter : Interactor {
        fun onDayNightToggle(displayTheme: DayNightMode)
        fun onTextSizeChange(textSize: ContentTextSize)
    }

    data class ViewState(
        val selectedMode: DayNightMode,
        val textSizeValue: ContentTextSize,
        val displaySystemThemeButton: Boolean = false
    ) : com.theathletic.ui.ViewState
}