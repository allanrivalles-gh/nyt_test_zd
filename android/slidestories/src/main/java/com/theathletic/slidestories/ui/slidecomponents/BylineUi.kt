package com.theathletic.slidestories.ui.slidecomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.theathletic.slidestories.R
import com.theathletic.themes.AthColor.Companion.Gray200
import com.theathletic.themes.AthColor.Companion.Gray400
import com.theathletic.themes.AthColor.Companion.Gray500
import com.theathletic.themes.AthColor.Companion.Gray700
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.preview.DayNightPreview
import com.theathletic.ui.widgets.RemoteImageAsync
import com.theathletic.ui.widgets.SimpleShrinkToFitText

data class ByLineImageInfo(
    val imageOffset: Int,
    val imageSize: Int
)

@Composable
fun Byline(
    byline: String,
    authorImageUrls: List<String>,
    reportingFrom: String?,
    showSmallIcons: Boolean,
    showSmallFont: Boolean,
    byLineTextColor: Color = Gray700
) {
    val byLineImageInfo = if (showSmallIcons) {
        ByLineImageInfo(20, 24)
    } else {
        ByLineImageInfo(32, 40)
    }

    val fontStyle = if (showSmallFont) {
        AthTextStyle.Calibre.Utility.Regular.ExtraSmall
    } else {
        AthTextStyle.Calibre.Utility.Medium.Small
    }

    Row(
        modifier = Modifier
            .height(IntrinsicSize.Max)
            .fillMaxWidth()
            .background(color = Gray200)
    ) {
        AuthorImageStack(
            authorImages = authorImageUrls,
            imageOffset = byLineImageInfo.imageOffset,
            imageSize = byLineImageInfo.imageSize,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Column(
            modifier = Modifier
                .padding(start = 8.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = byline,
                color = byLineTextColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = fontStyle
            )

            reportingFrom?.let {
                Spacer(modifier = Modifier.height(4.dp))
                SimpleShrinkToFitText(
                    text = generateReportingFromTitle(reportingFrom),
                    maxLines = 1,
                    style = AthTextStyle.Calibre.Utility.Regular.Small.copy(color = Gray500),
                )
            }
        }
    }
}

private fun generateReportingFromTitle(location: String) = "\uD83D\uDCCD".plus(location)

@Composable
fun AuthorImageStack(
    modifier: Modifier = Modifier,
    authorImages: List<String>,
    imageOffset: Int,
    imageSize: Int
) {
    Box(
        modifier = modifier
    ) {
        authorImages.forEachIndexed { index, url ->
            val paddingOffset = index * imageOffset
            Box {
                RemoteImageAsync(
                    url = url,
                    placeholder = R.drawable.ic_athletic_a_logo,
                    error = R.drawable.ic_athletic_a_logo,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(start = paddingOffset.dp)
                        .clip(shape = CircleShape)
                        .size(imageSize.dp)
                        .border(width = 1.dp, color = Gray400, RoundedCornerShape(100.dp)),
                    circular = true
                )
            }
        }
    }
}

@DayNightPreview
@Composable
fun Byline_Preview() {
    AthleticTheme(lightMode = isSystemInDarkTheme()) {
        Byline(
            "By Andy McCullough",
            listOf("A", "B"),
            "Reporting from Dallas and a very long location, TX",
            false,
            false
        )
    }
}

@DayNightPreview
@Composable
fun Byline_NoLocation_Preview() {
    AthleticTheme(lightMode = isSystemInDarkTheme()) {
        Byline(
            "By The Athletic MLB Staff",
            listOf("A"),
            null,
            true,
            true
        )
    }
}