package com.theathletic.comments.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandCircleDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.theathletic.comments.ui.CommentsDrawerState
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.utility.KeyboardOpenCloseListener
import com.theathletic.ui.utility.conditional
import com.theathletic.ui.utility.ternary

// region Legacy implementation - To be deleted once comment-drawer feature switch is removed
@Composable
fun LegacyInputArea(
    inputText: String,
    isEnabled: Boolean,
    enableSend: Boolean,
    focusRequester: FocusRequester,
    focusManager: FocusManager,
    onCommentInputClick: () -> Unit,
    onTextChanged: (String) -> Unit,
    onSendClick: (() -> Unit) -> Unit
) {
    val textStyle = AthTextStyle.Calibre.Utility.Regular.Large
    val selectionColors = TextSelectionColors(
        handleColor = AthTheme.colors.dark500,
        backgroundColor = AthTheme.colors.dark500.copy(.45f)
    )
    val trailingIconColor = if (enableSend) AthTheme.colors.dark800 else AthTheme.colors.dark500

    val scrollingState = rememberScrollState()
    LaunchedEffect(scrollingState.maxValue) {
        scrollingState.animateScrollTo(scrollingState.maxValue)
    }

    CompositionLocalProvider(
        LocalTextSelectionColors provides selectionColors,
        LocalTextStyle provides textStyle
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .pointerInput(Unit) { detectTapGestures(onTap = { onCommentInputClick() }) }
                .clip(RoundedCornerShape(18.dp))
                .background(AthTheme.colors.dark300)
                .fillMaxWidth()
        ) {
            LegacyInputField(
                inputText = inputText,
                isEnabled = isEnabled,
                onTextChanged = onTextChanged,
                focusRequester = focusRequester,
                scrollingState = scrollingState
            )
            IconButton(
                onClick = {
                    onSendClick {
                        focusManager.clearFocus()
                    }
                },
                enabled = enableSend
            ) {
                Icon(
                    imageVector = Icons.Filled.ExpandCircleDown,
                    contentDescription = null,
                    tint = trailingIconColor,
                    modifier = Modifier
                        .rotate(270f)
                        .size(32.dp)
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun RowScope.LegacyInputField(
    inputText: String,
    isEnabled: Boolean,
    focusRequester: FocusRequester,
    onTextChanged: (String) -> Unit,
    scrollingState: ScrollState
) {
    BasicTextField(
        modifier = Modifier
            .heightIn(min = 48.dp, max = 200.dp)
            .weight(1f)
            .verticalScroll(scrollingState)
            .focusRequester(focusRequester),
        value = inputText,
        enabled = isEnabled,
        onValueChange = { onTextChanged(it) },
        cursorBrush = SolidColor(AthTheme.colors.dark800),
        textStyle = LocalTextStyle.current.copy(color = AthTheme.colors.dark800),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
    ) { innerTextField ->
        TextFieldDefaults.TextFieldDecorationBox(
            value = inputText,
            placeholder = {
                Text(
                    text = stringResource(id = R.string.comments_hint_add_comment),
                    style = AthTextStyle.Calibre.Utility.Regular.Large.copy(
                        color = AthTheme.colors.dark500
                    )
                )
            },
            singleLine = true,
            visualTransformation = VisualTransformation.None,
            contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 6.dp, bottom = 8.dp),
            interactionSource = MutableInteractionSource(),
            innerTextField = innerTextField,
            enabled = true
        )
    }
}

@Composable
internal fun LegacyCommentsInput(
    inputText: String = "",
    isEnabled: Boolean = false,
    enableSend: Boolean = true,
    focusRequester: FocusRequester = FocusRequester(),
    focusManager: FocusManager = LocalFocusManager.current,
    inputHeaderData: InputHeaderData = InputHeaderData.EmptyHeaderData,
    onCommentInputClick: () -> Unit = {},
    onTextChanged: (String) -> Unit = {},
    onSendClick: (() -> Unit) -> Unit = {},
    onCancelInput: (InputHeaderData) -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(AthTheme.colors.dark200)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        AnimatedVisibility(visible = inputHeaderData.shouldShow) {
            LegacyInputHeader(
                inputHeader = inputHeaderData,
                onCancel = onCancelInput
            )
        }
        LegacyInputArea(
            inputText = inputText,
            isEnabled = isEnabled,
            enableSend = enableSend,
            focusRequester = focusRequester,
            focusManager = focusManager,
            onCommentInputClick = onCommentInputClick,
            onTextChanged = onTextChanged,
            onSendClick = onSendClick
        )
    }
}

@Composable
fun LegacyInputHeader(
    inputHeader: InputHeaderData,
    onCancel: (InputHeaderData) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp, top = 5.dp)
    ) {
        when (inputHeader) {
            is InputHeaderData.TempBannedHeaderData -> {}
            is InputHeaderData.TopLevelCommentHeaderData -> {}
            is InputHeaderData.ReplyHeaderData -> LegacyInputHeaderReply(inputHeader.replyingToText) {
                onCancel(inputHeader)
            }
            is InputHeaderData.EditHeaderData -> LegacyInputHeaderEdit(inputHeader.editingText) {
                onCancel(inputHeader)
            }
            is InputHeaderData.EmptyHeaderData -> {}
        }
    }
}

@Composable
fun LegacyInputHeaderReply(headerText: String, onCancel: () -> Unit) {
    Text(
        text = headerText,
        style = AthTextStyle.Calibre.Utility.Medium.Large.copy(
            color = AthTheme.colors.blue
        )
    )
    LegacyInputHeaderCancelBtn(onCancel)
}

@Composable
fun LegacyInputHeaderEdit(headerText: String, onCancel: () -> Unit) {
    Text(
        text = headerText,
        style = AthTextStyle.Calibre.Utility.Medium.Large.copy(
            color = AthTheme.colors.red
        )
    )
    LegacyInputHeaderCancelBtn(onCancel)
}

@Composable
private fun LegacyInputHeaderCancelBtn(onCancel: () -> Unit) {
    IconButton(
        onClick = onCancel,
        modifier = Modifier
            .size(28.dp)
            .padding(start = 5.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Cancel,
            contentDescription = null,
            tint = AthTheme.colors.dark800,
            modifier = Modifier
                .rotate(270f)
        )
    }
}
// endregion Legacy implementation - To be deleted once comment-drawer feature switch is removed

// region Comment-drawer feature enabled
@Composable
private fun InputArea(
    inputText: String,
    drawerState: CommentsDrawerState,
    isEnabled: Boolean,
    focusRequester: FocusRequester,
    interactor: CommentInputInteractor
) {
    KeyboardOpenCloseListener(interactor::onKeyboardOpenChanged)

    val textStyle = AthTextStyle.Calibre.Utility.Regular.Large
    val selectionColors = TextSelectionColors(
        handleColor = AthTheme.colors.dark500,
        backgroundColor = AthTheme.colors.dark500.copy(.45f)
    )

    val scrollingState = rememberScrollState()
    LaunchedEffect(scrollingState.maxValue) {
        scrollingState.animateScrollTo(scrollingState.maxValue)
    }

    CompositionLocalProvider(
        LocalTextSelectionColors provides selectionColors,
        LocalTextStyle provides textStyle
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .conditional(drawerState == CommentsDrawerState.CLOSED) {
                    clip(RoundedCornerShape(18.dp))
                }
                .background(AthTheme.colors.dark300)
                .fillMaxWidth()
        ) {
            InputField(
                inputText = inputText,
                isEnabled = isEnabled,
                drawerState = drawerState,
                focusRequester = focusRequester,
                onTextChanged = interactor::onTextChanged,
                scrollingState = scrollingState,
                interactor = interactor
            )
        }
    }
}

@Composable
private fun InputControlBar(
    enableSend: Boolean,
    focusManager: FocusManager,
    onSendClick: (() -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = modifier) {
        Row(modifier = Modifier.weight(1f)) {
            Spacer(modifier = Modifier.width(1.dp)) // TODO: Actions will go here as those features are added
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            SendButton(enableSend, focusManager, onSendClick)
        }
    }
}

@Composable
private fun SendButton(
    enableSend: Boolean,
    focusManager: FocusManager,
    onSendClick: (() -> Unit) -> Unit
) {
    Button(
        onClick = {
            onSendClick {
                focusManager.clearFocus() // Close the keyboard
            }
        },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = AthTheme.colors.dark700,
            contentColor = AthTheme.colors.dark200
        ),
        shape = RoundedCornerShape(size = 19.dp),
        contentPadding = PaddingValues(
            start = 8.dp,
            top = 4.dp,
            end = 8.dp,
            bottom = 4.dp
        ),
        enabled = enableSend
    ) {
        Text(
            style = AthTextStyle.Calibre.Utility.Medium.Small,
            text = stringResource(id = R.string.comments_send),
        )
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            modifier = Modifier
                .size(16.dp)
        )
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun RowScope.InputField(
    inputText: String,
    isEnabled: Boolean,
    drawerState: CommentsDrawerState,
    focusRequester: FocusRequester,
    onTextChanged: (String) -> Unit,
    scrollingState: ScrollState,
    interactor: CommentInputInteractor
) {
    val textStyle = LocalTextStyle.current
    val textFieldMaxHeight: Dp = calculateTextFieldMaxHeight(textStyle, drawerState)
    val currentInteractor by rememberUpdatedState(interactor)

    BasicTextField(
        modifier = Modifier
            .pointerInput(currentInteractor) {
                // This detects taps only if this BasicTextField is disabled, which is fine for our current purposes,
                // but something you might be surprised by if you're expecting calls to onCommentInputClick() while
                // commenting is enabled
                detectTapGestures(onTap = { currentInteractor.onCommentInputClick() })
            }
            .heightIn(min = 48.dp, max = textFieldMaxHeight)
            .weight(1f)
            .verticalScroll(scrollingState)
            .focusRequester(focusRequester),
        value = inputText,
        enabled = isEnabled,
        onValueChange = onTextChanged,
        cursorBrush = SolidColor(AthTheme.colors.dark800),
        textStyle = LocalTextStyle.current.copy(color = AthTheme.colors.dark800),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
    ) { innerTextField ->
        TextFieldDefaults.TextFieldDecorationBox(
            value = inputText,
            placeholder = {
                Text(
                    text = stringResource(id = R.string.comments_hint_add_comment),
                    style = textStyle.copy(
                        color = AthTheme.colors.dark500
                    )
                )
            },
            singleLine = true,
            visualTransformation = VisualTransformation.None,
            contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 6.dp, bottom = 8.dp),
            interactionSource = MutableInteractionSource(),
            innerTextField = innerTextField,
            enabled = true
        )
    }
}

@Composable
private fun calculateTextFieldMaxHeight(
    textStyle: TextStyle,
    drawerState: CommentsDrawerState
): Dp {
    val lineHeight = textStyle.lineHeight
    val collapsedMaxHeight: Dp = with(LocalDensity.current) {
        lineHeight.toDp().times(2).plus(8.dp) // Two lines plus a bit of padding
    }
    val openMaxHeight = 132.dp
    return if (drawerState == CommentsDrawerState.COLLAPSED) {
        collapsedMaxHeight
    } else {
        openMaxHeight
    }
}

@Composable
internal fun CommentsInput(
    inputText: String = "",
    drawerState: CommentsDrawerState = CommentsDrawerState.CLOSED,
    isEnabled: Boolean = false,
    enableSend: Boolean = true,
    focusRequester: FocusRequester = FocusRequester(),
    focusManager: FocusManager = LocalFocusManager.current,
    inputHeaderData: InputHeaderData = InputHeaderData.EmptyHeaderData,
    interactor: CommentInputInteractor = object : CommentInputInteractor {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(AthTheme.colors.dark200)
            .padding(start = 16.dp, end = 12.dp, bottom = 4.dp)
    ) {
        Spacer(
            Modifier
                .ternary(
                    drawerState == CommentsDrawerState.CLOSED,
                    ifTrue = { height(12.dp) },
                    ifFalse = { height(2.dp) }
                )
        )
        AnimatedVisibility(
            visible = (drawerState == CommentsDrawerState.CLOSED).not()
        ) {
            InputHeader(
                inputHeader = inputHeaderData,
                onCancel = interactor::onCancelInput,
                onCodeOfConductClick = interactor::onCodeOfConductClick
            )
        }
        InputArea(
            inputText = inputText,
            drawerState = drawerState,
            isEnabled = isEnabled,
            focusRequester = focusRequester,
            interactor = interactor
        )
        AnimatedVisibility(
            visible = drawerState == CommentsDrawerState.OPEN
        ) {
            InputControlBar(
                enableSend = enableSend,
                focusManager = focusManager,
                onSendClick = interactor::onSendClick
            )
        }
    }
}
// endregion Comment-drawer feature enabled

// region Previews
@Composable
@Preview
fun CommentsInput_EditingCommentLightPreview() {
    AthleticTheme(lightMode = true) {
        CommentsInput(
            inputText = "Hello world",
            drawerState = CommentsDrawerState.OPEN,
            enableSend = true,
            inputHeaderData = InputHeaderData.EditHeaderData(true)
        )
    }
}

@Composable
@Preview
fun CommentsInput_ReplyingToCommentDarkPreview() {
    AthleticTheme(lightMode = false) {
        CommentsInput(
            inputText = "Yeah but...",
            drawerState = CommentsDrawerState.OPEN,
            enableSend = true,
            inputHeaderData = InputHeaderData.ReplyHeaderData("Reginald Q.")
        )
    }
}

@Composable
@Preview
fun CommentsInput_DisabledSendLightPreview() {
    AthleticTheme(lightMode = false) {
        CommentsInput(drawerState = CommentsDrawerState.OPEN, enableSend = false)
    }
}

@Composable
@Preview
fun CommentsInput_ClosedDrawerWithTextDarkPreview() {
    AthleticTheme(lightMode = false) {
        CommentsInput(inputText = "Hello world", drawerState = CommentsDrawerState.COLLAPSED)
    }
}

@Composable
@Preview
fun CommentsInput_ClosedDrawerDarkPreview() {
    AthleticTheme(lightMode = false) {
        CommentsInput(drawerState = CommentsDrawerState.CLOSED)
    }
}
// endregion Previews