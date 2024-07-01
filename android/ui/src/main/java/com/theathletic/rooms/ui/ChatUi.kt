package com.theathletic.rooms.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R

@Composable
fun LiveRoomChat(
    chatInput: String,
    isLocked: Boolean,
    messages: List<LiveRoomUi.ChatMessage>,
    interactor: LiveRoomUi.Interactor,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        when (messages.isEmpty()) {
            true -> LiveRoomEmptyChat(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            else -> ChatMessageList(
                messages = messages,
                interactor = interactor,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }

        LiveRoomChatInputBar(
            chatInput = chatInput,
            isLocked = isLocked,
            interactor = interactor,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
        )
    }
}

@Composable
fun LiveRoomEmptyChat(
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.rooms_chat_empty_title),
            color = AthTheme.colors.dark800,
            style = AthTextStyle.Slab.Bold.Medium,
        )
        Text(
            text = stringResource(R.string.rooms_chat_empty_subtitle),
            color = AthTheme.colors.dark500,
            style = AthTextStyle.Calibre.Utility.Regular.Large,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

val ChatInputShape = RoundedCornerShape(40.dp)

@Composable
private fun LiveRoomChatInputBar(
    chatInput: String,
    isLocked: Boolean,
    interactor: LiveRoomUi.Interactor,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
            .alpha(if (isLocked) 0.4f else 1.0f)
    ) {
        ChatInputTextField(
            chatInput = chatInput,
            interactor = interactor,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 10.dp, horizontal = 16.dp)
        )

        if (isLocked) {
            Box(
                Modifier
                    .fillMaxSize()
                    .clickable(onClick = interactor::onLockedChatInputClicked)
            )
        }
    }
}

@Composable
fun ChatInputTextField(
    chatInput: String,
    interactor: LiveRoomUi.Interactor,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = chatInput,
        onValueChange = interactor::onChatInputChanged,
        singleLine = true,
        textStyle = AthTextStyle.Calibre.Utility.Regular.Large.copy(color = AthTheme.colors.dark800),
        cursorBrush = SolidColor(AthTheme.colors.dark800),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Send,
        ),
        keyboardActions = KeyboardActions(onSend = { interactor.onSendChatClicked() }),
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = AthTheme.colors.dark300, shape = ChatInputShape)
                    .padding(start = 16.dp)
            ) {
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(end = 40.dp)
                ) {
                    innerTextField()
                }

                if (chatInput.isEmpty()) {
                    Text(
                        text = stringResource(R.string.rooms_chat_input_hint),
                        color = AthTheme.colors.dark500,
                        style = AthTextStyle.Calibre.Utility.Regular.Large,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_arrow_right),
                        contentDescription = null,
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { interactor.onSendChatClicked() }
                            .padding(8.dp)
                            .background(color = AthTheme.colors.dark800.copy(alpha = 0.4f), CircleShape)
                            .padding(2.dp)
                            .align(Alignment.CenterEnd)
                    )
                }
            }
        },
        modifier = modifier
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun LiveRoomChat_Preview() {
    LiveRoomChat(
        chatInput = "",
        messages = LiveRoomPreviewData.Messages,
        isLocked = false,
        interactor = LiveRoomPreviewData.Interactor,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun LiveRoomChat_LightPreview() {
    AthleticTheme(lightMode = true) {
        LiveRoomChat(
            chatInput = "",
            messages = LiveRoomPreviewData.Messages,
            isLocked = false,
            interactor = LiveRoomPreviewData.Interactor,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun LiveRoomChat_EmptyPreview() {
    LiveRoomChat(
        chatInput = "",
        messages = emptyList(),
        isLocked = false,
        interactor = LiveRoomPreviewData.Interactor,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun LiveRoomChat_LightEmptyPreview() {
    AthleticTheme(lightMode = true) {
        LiveRoomChat(
            chatInput = "",
            messages = emptyList(),
            isLocked = false,
            interactor = LiveRoomPreviewData.Interactor,
        )
    }
}