package com.theathletic.ui

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.appcompat.app.AppCompatDelegate
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Exposes
import com.theathletic.annotation.autokoin.Named
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.extension.get
import com.theathletic.extension.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

enum class DayNightMode(val key: String) {
    NIGHT_MODE("N"),
    DAY_MODE("D"),
    SYSTEM("S");

    companion object {
        fun fromKey(key: String?) = values().firstOrNull { it.key == key } ?: NIGHT_MODE
    }
}

enum class ContentTextSize(val key: Int) {
    DEFAULT(0),
    MEDIUM(1),
    LARGE(2),
    EXTRA_LARGE(3);

    companion object {
        fun fromKey(key: Int?) = values().firstOrNull { it.key == key } ?: DEFAULT
    }
}

interface DisplayPreferences {
    var dayNightMode: DayNightMode
    var contentTextSize: ContentTextSize
    val dayNightModeState: Flow<DayNightMode>
    val contentTextSizeState: Flow<ContentTextSize>
    val supportsSystemDayNightMode: Boolean
    fun initializeDayNightPreferences()
    fun shouldDisplayDayMode(context: Context): Boolean
}

@Exposes(DisplayPreferences::class)
class DisplayPreferencesImpl @AutoKoin(Scope.SINGLE) constructor(
    @Named("application-context") appContext: Context
) : DisplayPreferences {

    private val prefKeyDayNight = "pref_display_day_night_mode"
    private val prefKeyTextSize = "pref_display_content_text_size"

    private val sharedPreferences = appContext.getSharedPreferences(
        "displayPreferences", Context.MODE_PRIVATE
    )

    private val _contentTextSizeState = MutableStateFlow(contentTextSize)
    private val _dayNightMode = MutableStateFlow(dayNightMode)

    override val contentTextSizeState: Flow<ContentTextSize>
        get() = _contentTextSizeState

    override val dayNightModeState: Flow<DayNightMode>
        get() = _dayNightMode

    override fun initializeDayNightPreferences() {
        val currentMode = dayNightMode
        dayNightMode = currentMode
    }

    override var dayNightMode: DayNightMode
        get() {
            val defaultKey = if (supportsSystemDayNightMode) {
                DayNightMode.SYSTEM.key
            } else {
                DayNightMode.NIGHT_MODE.key
            }
            return DayNightMode.fromKey(sharedPreferences[prefKeyDayNight, defaultKey])
        }
        set(value) {
            when {
                value == DayNightMode.DAY_MODE -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                value == DayNightMode.SYSTEM && supportsSystemDayNightMode ->
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            sharedPreferences[prefKeyDayNight] = value.key
            _dayNightMode.value = value
        }

    override var contentTextSize: ContentTextSize
        get() = ContentTextSize.fromKey(sharedPreferences[prefKeyTextSize, ContentTextSize.DEFAULT.key])
        set(value) {
            sharedPreferences[prefKeyTextSize] = value.key
            _contentTextSizeState.value = value
        }

    override fun shouldDisplayDayMode(context: Context): Boolean {
        return when (context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_NO -> true
            else -> false
        }
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.P)
    override val supportsSystemDayNightMode = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
}