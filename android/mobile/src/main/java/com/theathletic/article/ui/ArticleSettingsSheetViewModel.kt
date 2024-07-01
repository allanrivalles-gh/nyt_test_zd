package com.theathletic.article.ui

import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.ContentTextSize
import com.theathletic.ui.DataState
import com.theathletic.ui.DayNightMode
import com.theathletic.ui.DisplayPreferences

class ArticleSettingsSheetViewModel @AutoKoin constructor(
    private val displayPreferences: DisplayPreferences,
    private val analytics: Analytics
) :
    AthleticViewModel<ArticleSettingsSheetViewModel.State, ArticleSettingsSheetContract.ViewState>(),
    ArticleSettingsSheetContract.Presenter {

    override val initialState: State by lazy {
        State(
            displayTheme = displayPreferences.dayNightMode,
            textSizeValue = displayPreferences.contentTextSize,
            displaySystemThemeButton = displayPreferences.supportsSystemDayNightMode
        )
    }

    override fun transform(data: State): ArticleSettingsSheetContract.ViewState {
        return ArticleSettingsSheetContract.ViewState(
            selectedMode = data.displayTheme,
            textSizeValue = data.textSizeValue,
            displaySystemThemeButton = data.displaySystemThemeButton
        )
    }

    override fun onDayNightToggle(displayTheme: DayNightMode) {
        analytics.track(
            Event.ContentSettings.DisplayThemeClick(
                object_type = when (displayTheme) {
                    DayNightMode.NIGHT_MODE -> "dark"
                    DayNightMode.DAY_MODE -> "light"
                    DayNightMode.SYSTEM -> "system"
                }
            )
        )
        displayPreferences.dayNightMode = displayTheme
        updateState { copy(displayTheme = displayTheme) }
    }

    override fun onTextSizeChange(textSize: ContentTextSize) {
        if (textSize != displayPreferences.contentTextSize) {
            analytics.track(Event.ContentSettings.TextSizeSlide(object_id = textSize.key.toString()))
            displayPreferences.contentTextSize = textSize
            updateState { copy(textSizeValue = textSize) }
        }
    }

    data class State(
        val displayTheme: DayNightMode,
        val textSizeValue: ContentTextSize,
        val displaySystemThemeButton: Boolean = false
    ) : DataState
}