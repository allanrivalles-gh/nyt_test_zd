package com.theathletic.feed.compose.ui.reusables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.theathletic.feed.R
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.preview.DayNightPreview
import com.theathletic.ui.widgets.OverlappingRow
import com.theathletic.ui.widgets.RemoteImageAsync

@Composable
fun Facepile(
    avatarUrls: List<String?>,
    modifier: Modifier = Modifier,
    maxDisplayedAvatars: Int,
    borderColor: Color,
    imageDiameterDp: Int = 24,
    borderWidthDp: Int = 2,
    overlapDp: Int = 10
) {
    if (avatarUrls.isEmpty()) {
        return
    }

    var overflowCount = 0
    var displayedAvatarCount: Int = avatarUrls.size

    if (displayedAvatarCount > maxDisplayedAvatars) {
        // If max is three, for example, and we have four avatar URLs, show TWO faces and use the third
        // avatar slot to show the overflow count
        overflowCount = avatarUrls.size - maxDisplayedAvatars + 1
        displayedAvatarCount -= overflowCount
    }

    OverlappingRow(
        overlap = overlapDp.dp,
        modifier = modifier
    ) {
        val itemModifier = Modifier
            .size(imageDiameterDp.dp)
            .border(
                width = borderWidthDp.dp,
                color = borderColor,
                shape = CircleShape
            )
            .padding(borderWidthDp.dp)
            .clip(CircleShape)
            .background(
                color = AthTheme.colors.dark800,
                shape = CircleShape
            )

        avatarUrls.take(displayedAvatarCount).forEach { url ->
            RemoteImageAsync(
                url = url,
                placeholder = R.drawable.ic_profile_v2,
                error = R.drawable.ic_profile_v2,
                fallbackImage = R.drawable.ic_profile_v2,
                circular = true,
                modifier = itemModifier
            )
        }

        if (overflowCount > 0) {
            Box(contentAlignment = Alignment.CenterEnd, modifier = itemModifier) {
                Text(
                    color = AthTheme.colors.dark200,
                    style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
                    text = "+$overflowCount"
                )
            }
        }
    }
}

@DayNightPreview
@Composable
private fun FacepilePreview(
    @PreviewParameter(FacepilePreviewParamProvider::class)
    avatarUrls: List<String>
) {
    return AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        Box(
            Modifier
                .wrapContentSize()
                .background(AthTheme.colors.dark300)
                .padding(6.dp)
        ) {
            Facepile(
                avatarUrls = avatarUrls,
                maxDisplayedAvatars = 3,
                borderColor = AthTheme.colors.dark300,
                imageDiameterDp = 24,
                borderWidthDp = 2,
                overlapDp = 10
            )
        }
    }
}

private class FacepilePreviewParamProvider :
    PreviewParameterProvider<List<String?>> {
    override val values: Sequence<List<String?>> = sequenceOf(
        List(1) { null },
        List(2) { null },
        List(3) { null },
        List(4) { null },
        List(9) { null },
    )
}