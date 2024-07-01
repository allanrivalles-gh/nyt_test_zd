package com.theathletic.rooms.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asResourceString
import com.theathletic.ui.asString

data class ScheduledRoomsUi(
    val rooms: List<Room>,
) {
    data class Room(
        val id: String,
        val title: String,
        val subtitle: String,
        val createdAt: ResourceString,
    )

    interface Interactor {
        fun onLiveRoomClicked(roomId: String)
        fun onLiveRoomLongClicked(roomId: String)
    }
}

@Composable
fun ScheduledRoomsScreen(
    uiModel: ScheduledRoomsUi,
    interactor: ScheduledRoomsUi.Interactor,
) {
    if (uiModel.rooms.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = AthTheme.colors.dark600,
            )
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = spacedBy(1.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(uiModel.rooms) { room ->
                RoomListItem(
                    room = room,
                    onClick = interactor::onLiveRoomClicked,
                    onLongClick = interactor::onLiveRoomLongClicked,
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RoomListItem(
    room: ScheduledRoomsUi.Room,
    onClick: (String) -> Unit = {},
    onLongClick: (String) -> Unit = {},
) {
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
            .combinedClickable(
                onClick = { onClick(room.id) },
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLongClick(room.id)
                },
            )
            .padding(16.dp)
    ) {
        Text(
            text = room.title,
            color = AthTheme.colors.dark800,
            style = AthTextStyle.LiveRoom.SheetHeader,
        )
        Text(
            text = room.subtitle,
            color = AthTheme.colors.dark500,
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            modifier = Modifier.padding(top = 2.dp),
        )
        Text(
            text = stringResource(R.string.rooms_created_datetime, room.createdAt.asString()),
            color = AthTheme.colors.dark500,
            style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
@Preview
private fun RoomListItem_Preview() {
    RoomListItem(
        room = ScheduledRoomsUi.Room(
            id = "1",
            title = "Test Room Title",
            subtitle = "Test Room Subtitle",
            createdAt = "3 hours ago".asResourceString()
        )
    )
}