package com.theathletic.liveblog.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.ContentTextSize
import com.theathletic.ui.DayNightMode
import com.theathletic.ui.R
import kotlin.math.round

@Composable
fun LiveBlogTextSettingsBottomSheet(
    dayNightMode: DayNightMode,
    contentTextSize: ContentTextSize,
    displaySystemThemeButton: Boolean,
    interactor: LiveBlogUi.BottomSheetInteractor
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        DisplayThemePicker(
            dayNightMode = dayNightMode,
            displaySystemThemeButton = displaySystemThemeButton,
            onDayNightToggle = interactor::onDayNightToggle
        )
        Divider(
            color = AthTheme.colors.dark300,
            thickness = 1.dp
        )
        TextSizePicker(
            contentTextSize = contentTextSize,
            interactor = interactor
        )
    }
}

@Composable
private fun DisplayThemePicker(
    dayNightMode: DayNightMode,
    displaySystemThemeButton: Boolean,
    onDayNightToggle: (DayNightMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.profile_display_theme),
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp),
            color = AthTheme.colors.dark800,
            style = AthTextStyle.Navigation
        )
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .clip(RoundedCornerShape(4.dp)),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GroupTextButton(
                stringResourceId = R.string.profile_daynight_light,
                onClick = {
                    onDayNightToggle(DayNightMode.DAY_MODE)
                },
                enabled = dayNightMode != DayNightMode.DAY_MODE
            )
            GroupTextButton(
                stringResourceId = R.string.profile_daynight_dark,
                onClick = {
                    onDayNightToggle(DayNightMode.NIGHT_MODE)
                },
                enabled = dayNightMode != DayNightMode.NIGHT_MODE
            )
            if (displaySystemThemeButton) {
                GroupTextButton(
                    stringResourceId = R.string.profile_daynight_system,
                    onClick = {
                        onDayNightToggle(DayNightMode.SYSTEM)
                    },
                    enabled = dayNightMode != DayNightMode.SYSTEM
                )
            }
        }
    }
}

@Composable
private fun GroupTextButton(
    stringResourceId: Int,
    onClick: () -> Unit,
    enabled: Boolean
) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(0.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = AthTheme.colors.dark300,
            contentColor = AthTheme.colors.dark700,
            disabledBackgroundColor = AthTheme.colors.dark800,
            disabledContentColor = AthTheme.colors.dark200
        )
    ) {
        Text(
            text = stringResource(id = stringResourceId),
            style = AthTextStyle.Calibre.Utility.Medium.Small
        )
    }
}

@Composable
private fun TextSizePicker(
    contentTextSize: ContentTextSize,
    interactor: LiveBlogUi.BottomSheetInteractor
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.article_font_selector_character),
            color = AthTheme.colors.dark800,
            style = AthTextStyle.Navigation
        )
        TextSizePickerSlider(
            contentTextSize = contentTextSize,
            onTextSizeChange = interactor::onTextSizeChange,
            trackTextSizeChange = interactor::trackTextSizeChange
        )
        Text(
            text = stringResource(id = R.string.article_font_selector_character),
            fontSize = 28.sp,
            color = AthTheme.colors.dark800,
            style = AthTextStyle.Navigation
        )
    }
}

@Composable
private fun RowScope.TextSizePickerSlider(
    contentTextSize: ContentTextSize,
    onTextSizeChange: (ContentTextSize) -> Unit,
    trackTextSizeChange: () -> Unit
) {
    Slider(
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 12.dp),
        value = contentTextSize.toFloat(),
        onValueChange = { value ->
            onTextSizeChange(parseSliderValue(round(value)))
        },
        onValueChangeFinished = trackTextSizeChange,
        valueRange = 0f..3f,
        steps = 2,
        colors = SliderDefaults.colors(
            thumbColor = AthTheme.colors.dark800,
            activeTrackColor = AthTheme.colors.dark800,
            inactiveTrackColor = AthTheme.colors.dark300,
            activeTickColor = AthTheme.colors.dark300,
            inactiveTickColor = AthTheme.colors.dark800
        )
    )
}

private fun parseSliderValue(value: Float) = when (value) {
    3f -> ContentTextSize.EXTRA_LARGE
    2f -> ContentTextSize.LARGE
    1f -> ContentTextSize.MEDIUM
    else -> ContentTextSize.DEFAULT
}

private fun ContentTextSize.toFloat() = when (this) {
    ContentTextSize.EXTRA_LARGE -> 3f
    ContentTextSize.LARGE -> 2f
    ContentTextSize.MEDIUM -> 1f
    else -> 0f
}