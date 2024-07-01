package com.theathletic.rooms.create.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.R
import com.theathletic.ui.widgets.ResourceIcon

data class CreateLiveRoomUi(
    val isEditing: Boolean,
    val titleInput: String,
    val titleMaxLength: Int,
    val descriptionInput: String,
    val descriptionMaxLength: Int,
    val topicTags: List<String>,
    val hosts: List<String>,
    val categories: List<String>,
    val selfAsHost: Boolean,
    val record: Boolean,
    val sendAutoPush: Boolean,
    val disableChat: Boolean,
    val submitButtonEnabled: Boolean,
    val isCreatingRoom: Boolean,
) {
    interface Interactor {
        fun onBackClicked()
        fun onTitleInputChanged(title: String)
        fun onDescriptionInputChanged(description: String)
        fun onAddTagsClicked()
        fun onAddHostsClicked()
        fun onAddRoomTypeClicked()

        fun onRecordingToggled(recordingOn: Boolean)
        fun onSendAutoPushToggled(autoPushOn: Boolean)
        fun onCurrentUserHostToggled(userIsHost: Boolean)
        fun onDisableChatToggled(disableChat: Boolean)

        fun onCreateRoomClicked()
    }
}

@Composable
fun CreateLiveRoomScreen(
    uiModel: CreateLiveRoomUi,
    interactor: CreateLiveRoomUi.Interactor,
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(AthTheme.colors.dark100)
            .verticalScroll(state = rememberScrollState())
    ) {
        Toolbar(uiModel = uiModel, interactor = interactor)
        TextInputs(uiModel = uiModel, interactor = interactor)
        TagInputs(uiModel = uiModel, interactor = interactor)
        ToggleInputs(uiModel = uiModel, interactor = interactor)

        Divider(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 5.dp)
                .background(AthTheme.colors.dark300)
        )

        AdvancedOptions(uiModel = uiModel, interactor = interactor)
        SubmitButton(uiModel = uiModel, interactor = interactor)
    }
}

@Composable
private fun Toolbar(
    uiModel: CreateLiveRoomUi,
    interactor: CreateLiveRoomUi.Interactor,
) {
    Box(modifier = Modifier.height(56.dp)) {
        Text(
            text = stringResource(
                id = when {
                    uiModel.isEditing -> R.string.rooms_edit_title
                    else -> R.string.rooms_create_title
                }
            ),
            textAlign = TextAlign.Center,
            color = AthTheme.colors.dark800,
            style = AthTextStyle.Slab.Bold.Small,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.Center)
        )

        Icon(
            imageVector = Icons.Default.ArrowBack,
            tint = AthTheme.colors.dark800,
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.CenterStart)
                .padding(start = 20.dp)
                .clickable(onClick = interactor::onBackClicked)
        )
    }
}

@Composable
private fun TextInputs(
    uiModel: CreateLiveRoomUi,
    interactor: CreateLiveRoomUi.Interactor,
) {
    CreateRoomTextInput(
        input = uiModel.titleInput,
        onInputChanged = interactor::onTitleInputChanged,
        placeholder = stringResource(R.string.rooms_create_input_hint_title),
        maxLength = uiModel.titleMaxLength,
        modifier = Modifier
            .wrapContentHeight()
            .padding(top = 12.dp, start = 16.dp, end = 16.dp)
    )
    CreateRoomTextInput(
        input = uiModel.descriptionInput,
        onInputChanged = interactor::onDescriptionInputChanged,
        placeholder = stringResource(R.string.rooms_create_input_hint_description),
        maxLength = uiModel.descriptionMaxLength,
        minHeight = 96.dp,
        modifier = Modifier
            .wrapContentHeight()
            .padding(top = 12.dp, start = 16.dp, end = 16.dp)
    )
}

@Composable
private fun TagInputs(
    uiModel: CreateLiveRoomUi,
    interactor: CreateLiveRoomUi.Interactor,
) {
    CreateRoomChipContainer(
        chips = uiModel.topicTags,
        hint = stringResource(id = R.string.rooms_create_add_tags),
        onAddClick = interactor::onAddTagsClicked,
    )
}

@Composable
private fun ToggleInputs(
    uiModel: CreateLiveRoomUi,
    interactor: CreateLiveRoomUi.Interactor,
) {
    Spacer(Modifier.height(12.dp))
    ToggleOption(
        text = stringResource(id = R.string.rooms_create_add_self_host),
        isOn = uiModel.selfAsHost,
        onValueChanged = interactor::onCurrentUserHostToggled,
    )
    ToggleOption(
        text = stringResource(id = R.string.rooms_create_input_record),
        isOn = uiModel.record,
        onValueChanged = interactor::onRecordingToggled,
    )
    ToggleOption(
        text = stringResource(id = R.string.rooms_create_input_auto_push),
        isOn = uiModel.sendAutoPush,
        onValueChanged = interactor::onSendAutoPushToggled,
    )
    ToggleOption(
        text = stringResource(id = R.string.rooms_create_input_disable_chat),
        isOn = uiModel.disableChat,
        onValueChanged = interactor::onDisableChatToggled,
    )
}

@Composable
private fun ToggleOption(
    text: String,
    isOn: Boolean,
    onValueChanged: (Boolean) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
    ) {
        Text(
            text = text,
            style = AthTextStyle.Calibre.Utility.Medium.Large,
            color = AthTheme.colors.dark800,
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = isOn,
            onCheckedChange = onValueChanged,
            colors = SwitchDefaults.colors(
                uncheckedThumbColor = AthTheme.colors.dark800,
                uncheckedTrackColor = AthTheme.colors.dark800,
                checkedThumbColor = AthTheme.colors.green,
                checkedTrackColor = AthTheme.colors.green,
            ),
        )
    }
}

@Composable
private fun AdvancedOptions(
    uiModel: CreateLiveRoomUi,
    interactor: CreateLiveRoomUi.Interactor,
) {
    var showAdvancedOptions by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showAdvancedOptions = !showAdvancedOptions }
            .padding(horizontal = 16.dp, vertical = 26.dp)
    ) {
        Text(
            text = stringResource(id = R.string.rooms_create_advanced_options),
            style = AthTextStyle.Calibre.Utility.Medium.Large,
            color = AthTheme.colors.dark800,
            modifier = Modifier.align(Alignment.CenterStart)
        )

        ResourceIcon(
            resourceId = R.drawable.ic_chevron_right,
            tint = AthTheme.colors.dark800,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .rotate(degrees = if (showAdvancedOptions) 270f else 90f)
        )
    }

    AnimatedVisibility(
        visible = showAdvancedOptions,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut() + slideOutVertically(),
    ) {
        Column(Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            CreateRoomChipContainer(
                chips = uiModel.categories,
                hint = stringResource(id = R.string.rooms_create_add_room_type),
                onAddClick = interactor::onAddRoomTypeClicked,
            )
            CreateRoomChipContainer(
                chips = uiModel.hosts,
                hint = stringResource(id = R.string.rooms_create_add_hosts),
                onAddClick = interactor::onAddHostsClicked,
            )
        }
    }
}

@Composable
private fun SubmitButton(
    uiModel: CreateLiveRoomUi,
    interactor: CreateLiveRoomUi.Interactor,
) {
    Button(
        enabled = uiModel.submitButtonEnabled || uiModel.isCreatingRoom,
        onClick = {
            if (!uiModel.isCreatingRoom) {
                interactor.onCreateRoomClicked()
            }
        },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = AthTheme.colors.dark800,
            contentColor = AthTheme.colors.dark100,
            disabledBackgroundColor = AthTheme.colors.dark800.copy(alpha = 0.3f),
            disabledContentColor = AthTheme.colors.dark200,
        ),
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(2.dp),
        modifier = Modifier
            .padding(vertical = 24.dp, horizontal = 16.dp)
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
    ) {
        if (uiModel.isCreatingRoom) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = LocalContentColor.current,
                strokeWidth = 2.dp,
            )
        } else {
            Text(
                text = stringResource(
                    id = when {
                        uiModel.isEditing -> R.string.rooms_save_room
                        else -> R.string.rooms_create_room
                    }
                ),
                style = AthTextStyle.Calibre.Utility.Medium.Large,
                modifier = Modifier.padding(vertical = 14.dp)
            )
        }
    }
}