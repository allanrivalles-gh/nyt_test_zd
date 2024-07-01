package com.theathletic.rooms.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.widgets.RemoteImage
import com.theathletic.ui.widgets.ResourceIcon

@Composable
fun LiveRoomDetailsScreen(
    roomTitle: String,
    roomDescription: String,
    hosts: List<LiveRoomUi.HostInfo>,
    tags: List<LiveRoomUi.TagInfo>,
    recording: Boolean,
    interactor: LiveRoomUi.Interactor,
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(AthTheme.colors.dark200)
            .verticalScroll(rememberScrollState())
    ) {
        if (recording) {
            RecordingText()
        }
        Text(
            text = roomTitle,
            style = AthTextStyle.Calibre.Headline.SemiBold.Medium,
            color = AthTheme.colors.dark800,
            modifier = Modifier.padding(top = 12.dp, start = 16.dp, end = 16.dp),
        )
        Text(
            text = roomDescription,
            style = AthTextStyle.Calibre.Utility.Regular.Large,
            color = AthTheme.colors.dark800,
            modifier = Modifier.padding(top = 12.dp, start = 16.dp, end = 16.dp),
        )
        Spacer(modifier = Modifier.height(12.dp))
        HostsSection(hosts = hosts)
        TagsSection(
            tags = tags,
            interactor = interactor,
        )
    }
}

@Composable
private fun RecordingText() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color = AthTheme.colors.red, shape = CircleShape)
        )
        Text(
            text = stringResource(R.string.rooms_live_recording).uppercase(),
            style = AthTextStyle.Calibre.Utility.Medium.Small,
            color = AthTheme.colors.red,
            modifier = Modifier.padding(start = 6.dp),
        )
    }
}

@Composable
private fun HostsSection(
    hosts: List<LiveRoomUi.HostInfo>
) {
    MetadataSection(
        title = stringResource(R.string.rooms_hosts),
        rowData = hosts,
    ) { host ->
        MetadataRow(
            name = host.name,
            subtitle = host.subtitle,
            imageContent = {
                RemoteImage(
                    url = host.imageUrl,
                    circular = true,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AthTheme.colors.dark300, CircleShape)
                        .padding(2.dp),
                )
            },
        )
    }
}

@Composable
fun TagsSection(
    tags: List<LiveRoomUi.TagInfo>,
    interactor: LiveRoomUi.Interactor,
) {
    MetadataSection(
        title = stringResource(R.string.rooms_tags),
        rowData = tags,
    ) { tag ->
        MetadataRow(
            name = tag.name,
            imageContent = {
                RemoteImage(
                    url = tag.imageUrl,
                    modifier = Modifier.fillMaxSize(),
                )
            },
            onClick = {
                interactor.onTagClicked(
                    id = tag.name,
                    deeplink = tag.deeplink,
                )
            },
        )
    }
}

@Composable
private fun <T> MetadataSection(
    title: String,
    rowData: List<T>,
    rowContent: @Composable (T) -> Unit,
) {
    Column {
        Text(
            text = title.uppercase(),
            style = AthTextStyle.Calibre.Tag.Medium.Large,
            color = AthTheme.colors.dark500,
            modifier = Modifier.padding(top = 12.dp, start = 16.dp, end = 16.dp),
        )
        Spacer(modifier = Modifier.height(6.dp))

        for (item in rowData) {
            rowContent(item)
        }
    }
}

@Composable
private fun MetadataRow(
    name: String,
    imageContent: @Composable BoxScope.() -> Unit,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 16.dp)
    ) {
        Box(
            content = imageContent,
            modifier = Modifier.size(40.dp),
        )

        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f),
        ) {
            Text(
                text = name,
                style = AthTextStyle.Calibre.Utility.Medium.ExtraLarge,
                color = AthTheme.colors.dark700,
            )

            if (!subtitle.isNullOrEmpty()) {
                Text(
                    text = subtitle,
                    style = AthTextStyle.Calibre.Utility.Medium.ExtraSmall,
                    color = AthTheme.colors.dark500,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }

        if (onClick != null) {
            ResourceIcon(
                resourceId = R.drawable.ic_chevron_right,
                tint = AthTheme.colors.dark500,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Preview
@Composable
fun LiveRoomDetailsScreen_Preview() {
    LiveRoomDetailsScreen(
        roomTitle = LiveRoomPreviewData.RoomTitle,
        roomDescription = LiveRoomPreviewData.RoomDescription,
        hosts = LiveRoomPreviewData.HostInfo,
        tags = LiveRoomPreviewData.TagInfo,
        recording = true,
        interactor = LiveRoomPreviewData.Interactor,
    )
}

@Preview
@Composable
fun LiveRoomDetailsScreen_LightPreview() {
    AthleticTheme(lightMode = true) {
        LiveRoomDetailsScreen(
            roomTitle = LiveRoomPreviewData.RoomTitle,
            roomDescription = LiveRoomPreviewData.RoomDescription,
            hosts = LiveRoomPreviewData.HostInfo,
            tags = LiveRoomPreviewData.TagInfo,
            recording = true,
            interactor = LiveRoomPreviewData.Interactor,
        )
    }
}