package com.theathletic.auth.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme

@Composable
fun CreateAuthTextInput(
    input: String,
    modifier: Modifier = Modifier,
    onInputChanged: (String) -> Unit,
    placeholder: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isError: Boolean = false
) = CreateGenericTextInput(
    input = input,
    modifier = modifier,
    onInputChanged = onInputChanged,
    placeholder = placeholder,
    keyboardOptions = keyboardOptions,
    keyboardActions = keyboardActions,
    isError = isError
)

@Composable
fun CreateAuthPasswordInput(
    input: String,
    modifier: Modifier = Modifier,
    onInputChanged: (String) -> Unit,
    placeholder: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isError: Boolean = false
) {
    var showPassword by rememberSaveable { mutableStateOf(false) }
    CreateGenericTextInput(
        input = input,
        modifier = modifier,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        placeholder = placeholder,
        isError = isError,
        visualTransformation =
        if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        onInputChanged = onInputChanged,
        trailingIcon = {
            ShowPasswordButton(showPassword = showPassword) {
                showPassword = !showPassword
            }
        }
    )
}

@Composable
private fun ShowPasswordButton(
    showPassword: Boolean,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = if (showPassword) {
                Icons.Filled.Visibility
            } else {
                Icons.Filled.VisibilityOff
            },
            contentDescription = null
        )
    }
}

@Composable
private fun CreateGenericTextInput(
    input: String,
    modifier: Modifier = Modifier,
    onInputChanged: (String) -> Unit,
    placeholder: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isError: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused = interactionSource.collectIsFocusedAsState()
    TextField(
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .border(
                width = 1.dp,
                color = if (isFocused.value) AthTheme.colors.dark800 else AthTheme.colors.dark300,
                shape = RoundedCornerShape(2.dp)
            )
            .widthIn(min = 0.dp, max = 480.dp),
        textStyle = AthTextStyle.Calibre.Utility.Regular.Large,
        shape = RoundedCornerShape(2.dp),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        value = input,
        placeholder = { Text(text = placeholder, style = AthTextStyle.Calibre.Utility.Regular.Large) },
        colors = textFieldColors(),
        singleLine = true,
        interactionSource = interactionSource,
        onValueChange = onInputChanged,
        isError = isError,
        trailingIcon = trailingIcon
    )
}

@Composable
private fun textFieldColors() = TextFieldDefaults.textFieldColors(
    textColor = AthTheme.colors.dark800,
    backgroundColor = AthTheme.colors.dark100,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent,
    cursorColor = AthTheme.colors.dark800,
    placeholderColor = AthTheme.colors.dark500
)