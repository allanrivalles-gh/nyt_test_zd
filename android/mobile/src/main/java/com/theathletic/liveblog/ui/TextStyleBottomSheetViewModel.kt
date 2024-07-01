package com.theathletic.liveblog.ui

import androidx.compose.runtime.Stable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.ContentTextSize
import com.theathletic.ui.DataState
import com.theathletic.ui.DayNightMode
import com.theathletic.ui.DisplayPreferences
import com.theathletic.utility.coroutines.collectIn

class TextStyleBottomSheetViewModel @AutoKoin constructor(
    @Assisted val params: Params,
    private val liveBlogAnalytics: LiveBlogAnalytics,
    private val displayPreferences: DisplayPreferences
) : AthleticViewModel<
    TextStyleBottomSheetState,
    TextStyleBottomSheetContract.ViewState>(),
    TextStyleBottomSheetContract.Presenter {

    @Stable
    data class Params(
        val liveBlogId: String
    )

    override val initialState by lazy {
        TextStyleBottomSheetState(
            displaySystemThemeButton = displayPreferences.supportsSystemDayNightMode
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun initialize() {
        displayPreferences.contentTextSizeState.collectIn(viewModelScope) { contentTextSize ->
            updateState { copy(textSize = contentTextSize) }
        }
        displayPreferences.dayNightModeState.collectIn(viewModelScope) { dayNightMode ->
            updateState { copy(dayNightMode = dayNightMode) }
        }
    }

    override fun transform(data: TextStyleBottomSheetState) = TextStyleBottomSheetContract.ViewState(
        textSize = data.textSize,
        dayNightMode = data.dayNightMode,
        displaySystemThemeButton = data.displaySystemThemeButton
    )

    override fun onTextSizeChange(textSize: ContentTextSize) {
        displayPreferences.contentTextSize = textSize
    }

    override fun trackTextSizeChange() {
        liveBlogAnalytics.trackSlide(
            blogId = params.liveBlogId,
            element = "text_size",
            objectId = "${displayPreferences.contentTextSize.key}"
        )
    }

    override fun onDayNightToggle(dayNightMode: DayNightMode) {
        displayPreferences.dayNightMode = dayNightMode
        liveBlogAnalytics.trackClick(
            blogId = params.liveBlogId,
            element = "display_theme",
            view = "settings_drawer",
            objectId = "",
            objectType = when (dayNightMode) {
                DayNightMode.NIGHT_MODE -> "dark"
                DayNightMode.DAY_MODE -> "light"
                DayNightMode.SYSTEM -> "system"
            }
        )
    }
}

data class TextStyleBottomSheetState(
    val textSize: ContentTextSize = ContentTextSize.DEFAULT,
    val dayNightMode: DayNightMode = DayNightMode.NIGHT_MODE,
    val displaySystemThemeButton: Boolean = false
) : DataState