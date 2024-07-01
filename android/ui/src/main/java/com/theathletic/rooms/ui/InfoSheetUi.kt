package com.theathletic.rooms.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.R
import com.theathletic.ui.binding.LinkableTag
import com.theathletic.utility.safeLet

private val BottomSheetBackgroundShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
private val BottomSheetHandleShape = RoundedCornerShape(20.dp)

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun LiveRoomInfoSheet(
    roomTitle: String,
    roomDescription: String,
    roomTags: List<LinkableTag>,
    onTagClick: (id: String, deeplink: String) -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    var sheetHeaderHeight by remember { mutableStateOf(0f) }

    BottomSheetScaffold(
        sheetContent = {
            LiveRoomInfoSheetContent(
                title = roomTitle,
                description = roomDescription,
                tags = roomTags,
                onTagClick = onTagClick,
                onPeekHeightMeasured = { height ->
                    if (height != sheetHeaderHeight) {
                        sheetHeaderHeight = height
                    }
                }
            )
        },
        sheetPeekHeight = sheetHeaderHeight.dp,
        backgroundColor = AthTheme.colors.dark100,
        sheetShape = BottomSheetBackgroundShape,
        sheetBackgroundColor = AthTheme.colors.dark200,
        content = content,
    )
}

@Composable
private fun LiveRoomInfoSheetContent(
    title: String,
    description: String,
    tags: List<LinkableTag>,
    onPeekHeightMeasured: (Float) -> Unit,
    onTagClick: (id: String, deeplink: String) -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 16.dp)
    ) {
        LiveRoomInfoSheetHeader(
            title = title,
            onHeightMeasured = onPeekHeightMeasured,
        )
        Text(
            text = description,
            color = AthTheme.colors.dark600,
            style = AthTextStyle.Calibre.Utility.Regular.Large,
            letterSpacing = .25.sp,
            lineHeight = 18.sp,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        if (tags.isNotEmpty()) {
            LiveRoomInfoSheetTags(
                tags = tags,
                onTagClick = onTagClick,
            )
            Spacer(Modifier.fillMaxWidth().height(16.dp))
        }
    }
}

@Composable
private fun LiveRoomInfoSheetHeader(
    title: String,
    onHeightMeasured: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current.density

    Column(
        modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 8.dp)
            .onGloballyPositioned {
                onHeightMeasured(it.size.height / density)
            }
    ) {
        Box(
            Modifier
                .size(width = 32.dp, height = 4.dp)
                .background(color = AthTheme.colors.dark300, shape = BottomSheetHandleShape)
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp, bottom = 4.dp)
        )

        Row(horizontalArrangement = Arrangement.Absolute.spacedBy(6.dp)) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(color = AthTheme.colors.red, shape = CircleShape)
                    .align(Alignment.CenterVertically)
            )
            Text(
                text = stringResource(R.string.feed_live),
                color = AthTheme.colors.red,
                style = AthTextStyle.Calibre.Utility.Medium.Small,
            )
        }

        Text(
            text = title,
            color = AthTheme.colors.dark700,
            style = AthTextStyle.LiveRoom.SheetHeader,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp, bottom = 20.dp),
        )
    }
}

@Composable
private fun LiveRoomInfoSheetTags(
    tags: List<LinkableTag>,
    onTagClick: (id: String, deeplink: String) -> Unit,
) {
    val underlinedStyle = SpanStyle(
        color = AthTheme.colors.dark700,
        textDecoration = TextDecoration.Underline,
    )
    val nonUnderlinedStyle = SpanStyle(color = AthTheme.colors.dark700)

    val annotatedText = buildAnnotatedString {
        tags.forEachIndexed { index, linkableTag ->
            val idIndex = pushStringAnnotation(
                tag = "ID",
                annotation = linkableTag.id,
            )
            val deeplinkIndex = pushStringAnnotation(
                tag = "URL",
                annotation = linkableTag.deeplink,
            )

            withStyle(style = underlinedStyle) {
                append(linkableTag.title)
            }

            pop(deeplinkIndex)
            pop(idIndex)

            if (index != tags.lastIndex) {
                withStyle(style = nonUnderlinedStyle) {
                    append("  •  ")
                }
            }
        }
    }

    ClickableText(
        text = annotatedText,
        style = AthTextStyle.Calibre.Utility.Regular.Large,
        onClick = { offset ->
            val id = annotatedText.getStringAnnotations(
                tag = "ID",
                start = offset,
                end = offset
            ).firstOrNull()?.item
            val deeplink = annotatedText.getStringAnnotations(
                tag = "URL",
                start = offset,
                end = offset
            ).firstOrNull()?.item

            safeLet(id, deeplink) { safeId, safeDeeplink ->
                onTagClick(safeId, safeDeeplink)
            }
        }
    )
}

@Composable
@Preview
private fun LiveRoomInfoSheet_Preview() {
    LiveRoomInfoSheetContent(
        title = LiveRoomPreviewData.RoomTitle,
        description = LiveRoomPreviewData.RoomDescription,
        tags = LiveRoomPreviewData.Tags,
        onTagClick = { _, _ -> },
        onPeekHeightMeasured = {},
    )
}