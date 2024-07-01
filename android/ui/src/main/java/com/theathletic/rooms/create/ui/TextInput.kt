package com.theathletic.rooms.create.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.R

@Composable
fun CreateRoomTextInput(
    input: String,
    onInputChanged: (String) -> Unit,
    placeholder: String,
    maxLength: Int,
    modifier: Modifier = Modifier,
    minHeight: Dp = Dp.Unspecified,
) {
    Column(
        modifier = modifier
    ) {
        TextInput(
            input = input,
            onInputChanged = onInputChanged,
            placeholder = placeholder,
            maxLength = maxLength,
            minHeight = minHeight,
        )
        MaxLengthIndicator(
            text = input,
            maxLength = maxLength,
            modifier = Modifier
                .padding(top = 4.dp)
                .align(Alignment.End)
        )
    }
}

@Composable
private fun TextInput(
    input: String,
    onInputChanged: (String) -> Unit,
    placeholder: String,
    maxLength: Int,
    minHeight: Dp,
) {
    BasicTextField(
        value = input,
        onValueChange = { value -> onInputChanged(value.take(maxLength)) },
        textStyle = AthTextStyle.Calibre.Utility.Regular.Large.copy(color = AthTheme.colors.dark800),
        cursorBrush = SolidColor(AthTheme.colors.dark800),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Send,
        ),
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.TopStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .border(
                        width = 1.dp,
                        color = AthTheme.colors.dark400,
                        shape = RoundedCornerShape(2.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                innerTextField()

                if (input.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = AthTheme.colors.dark500,
                        style = AthTextStyle.Calibre.Utility.Regular.Large,
                        modifier = Modifier.align(Alignment.TopStart)
                    )
                }
            }
        },
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .defaultMinSize(minHeight = minHeight)
    )
}

@Composable
private fun MaxLengthIndicator(
    text: String,
    maxLength: Int,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(R.string.rooms_create_input_character_limit, text.length, maxLength),
        style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
        color = AthTheme.colors.dark800,
        modifier = modifier
    )
}